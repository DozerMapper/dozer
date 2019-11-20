/*
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Keeps track of mapped object during this mapping process execution.
 * Objects, which are referenced multiple types in object hierarchy will be fetched from here
 * to retain referential integrity of resulting object graph.
 */
public class MappedFieldsTracker {

    private static class UndoLogEntry {

        private final MapIdField destMapIdField;
        private final String mapId;

        UndoLogEntry(MapIdField destMapIdField, String mapId) {
            this.destMapIdField = destMapIdField;
            this.mapId = mapId;
        }

        void revert() {
            destMapIdField.remove(mapId);
        }
    }

    private static class UndoLog {

        private List<UndoLogEntry> ops = new ArrayList<>();

        void track(MapIdField destMapIdField, String mapId) {
            this.ops.add(new UndoLogEntry(destMapIdField, mapId));
        }

        void revert() {
            for (UndoLogEntry op : this.ops) {
                op.revert();
            }
        }
    }

    private static final int NO_TX_ID = -1;

    // Counter used for generation of transaction IDs.
    private final AtomicInteger txId = new AtomicInteger(NO_TX_ID);

    // Hash Code is ignored as it can serve application specific needs
    // <srcObject, <hashCodeOfDestination, mappedDestinationMapIdField>>
    private final Map<Object, Map<Integer, MapIdField>> mappedFields = new IdentityHashMap<>();

    // Map with the Undo-Logs of pending transactions indexed and sorted by transaction IDs.
    private SortedMap<Integer, UndoLog> pendingTransactions = new TreeMap<>();

    /**
     * Start a new transaction which supports commit or rollback. Nested transaction are also supported and may be
     * individually rollbacked. Even if nested transactions are commited they may still be rollbacked by the rollback
     * of an outer transaction until the root transaction is committed as well.
     * @return transaction ID that can be used for commit or rollback of the transaction
     * @see #commitTransaction(Integer)
     * @see #rollbackTransaction(Integer)
     */
    public Integer startTransaction() {
        int curTxId = this.txId.incrementAndGet();
        this.pendingTransactions.put(curTxId, new UndoLog());
        return curTxId;
    }

    /**
     * Commit transaction with the given ID. The operations executed under this transaction my still be reverted by the
     * rollback of a parent transaction (if any).
     * @param txId - transaction ID as returned by {@link #startTransaction()}
     */
    public void commitTransaction(Integer txId) {
        final UndoLog undoLog = pendingTransactions.get(txId);
        if (undoLog == null) {
            throw new IllegalStateException("No transaction with ID " + txId);
        }
        if (pendingTransactions.firstKey().equals(txId)) {
            // commit of root transaction
            pendingTransactions.clear();
        }
    }

    /**
     * Rollback transaction with the given ID. The operations of this transactions as well as those of nested
     * transactions are reverted.
     * @param txId - transaction ID as returned by {@link #startTransaction()}
     */
    public void rollbackTransaction(Integer txId) {
        final UndoLog undoLog = pendingTransactions.get(txId);
        if (undoLog == null) {
            throw new IllegalStateException("No transaction with ID " + txId);
        }

        // rollback nested transactions
        SortedMap<Integer, UndoLog> undoLogs = pendingTransactions.tailMap(txId);
        Iterator<UndoLog> undoLogIterator = undoLogs.values().iterator();
        while (undoLogIterator.hasNext()) {
            UndoLog curUndoLog = undoLogIterator.next();
            curUndoLog.revert();
            undoLogIterator.remove();
        }
    }

    /**
     * Checks if there is a transaction active.
     * @return <code>true</code> if a transaction is active, <code>false</code> otherwise
     */
    public boolean hasTransaction() {
        return pendingTransactions.size() > 0;
    }

    public void put(Object src, Object dest, String mapId) {
        int destId = System.identityHashCode(dest);

        Map<Integer, MapIdField> mappedTo = mappedFields.get(src);
        if (mappedTo == null) {
            mappedTo = new HashMap<>();
            mappedFields.put(src, mappedTo);
        }

        MapIdField destMapIdField = mappedTo.get(destId);
        if (destMapIdField == null) {
            destMapIdField = new MapIdField();
            mappedTo.put(destId, destMapIdField);
        }

        if (!destMapIdField.containsMapId(mapId)) {
            destMapIdField.put(mapId, dest);
            if (hasTransaction()) {
                UndoLog undoLog = this.pendingTransactions.get(txId.get());
                undoLog.track(destMapIdField, mapId);
            }
        }
    }

    public void put(Object src, Object dest) {
        put(src, dest, null);
    }

    public Object getMappedValue(Object src, Class<?> destType, String mapId) {
        Map<Integer, MapIdField> alreadyMappedFields = mappedFields.get(src);
        if (alreadyMappedFields != null) {
            for (MapIdField alreadyMappedField : alreadyMappedFields.values()) {
                Object mappedValue = alreadyMappedField.get(mapId);
                // 1664984 - bi-directionnal mapping with sets & subclasses
                if (mappedValue != null && destType.isAssignableFrom(mappedValue.getClass())) {
                    // Source value has already been mapped to the required destFieldType.
                    return mappedValue;
                }
            }
        }
        return null;
    }

    public Object getMappedValue(Object src, Class<?> destType) {
        return getMappedValue(src, destType, null);
    }
}
