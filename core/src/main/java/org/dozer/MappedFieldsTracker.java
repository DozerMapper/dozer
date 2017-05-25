/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Keeps track of mapped object during this mapping process execution.
 * Objects, which are referenced multiple types in object hierarchy will be fetched from here
 * to retain referential integrity of resulting object graph.
 *
 * @author dmitry.buzdin
 */
public class MappedFieldsTracker {

  // Hash Code is ignored as it can serve application specific needs
  // <srcObject, <hashCodeOfDestination, mappedDestinationMapIdField>>
  private final Map<Object, Map<Integer, MapIdField>> mappedFields = new IdentityHashMap<Object, Map<Integer, MapIdField>>();


  public void put(Object src, Object dest, String mapId) {
    int destId = System.identityHashCode(dest);

    Map<Integer, MapIdField> mappedTo = mappedFields.get(src);
    if (mappedTo == null) {
      mappedTo = new HashMap<Integer, MapIdField>();
      mappedFields.put(src, mappedTo);
    }

    MapIdField destMapIdField = mappedTo.get(destId);
    if (destMapIdField == null) {
      destMapIdField = new MapIdField();
      mappedTo.put(destId, destMapIdField);
    }

    if (!destMapIdField.containsMapId(mapId)) {
      destMapIdField.put(mapId, dest);
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
