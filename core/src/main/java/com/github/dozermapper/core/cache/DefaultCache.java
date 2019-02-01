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
package com.github.dozermapper.core.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Default cache manager implementation backed by {@link LRUMap}
 *
 * @param <KeyType>   type of key being stored
 * @param <ValueType> java of value being stored
 */
public class DefaultCache<KeyType, ValueType> implements Cache<KeyType, ValueType> {

    private final String name;
    private final LRUMap cacheMap;

    /**
     * Default cache manager implementation backed by {@link LRUMap}
     *
     * @param name        unique cache name
     * @param maximumSize maximum cache size
     */
    public DefaultCache(final String name, final int maximumSize) {
        if (maximumSize < 1) {
            throw new IllegalArgumentException("Dozer cache max size must be greater than 0");
        }

        this.name = name;
        this.cacheMap = new LRUMap(maximumSize); //Should be: Collections.synchronizedMap
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        cacheMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void put(KeyType key, ValueType value) {
        if (key == null) {
            throw new IllegalArgumentException("Cache entry key cannot be null");
        }

        CacheEntry<KeyType, ValueType> cacheEntry = new CacheEntry<>(key, value);
        cacheMap.put(cacheEntry.getKey(), cacheEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueType get(KeyType key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        CacheEntry<KeyType, ValueType> result = cacheMap.get(key);
        if (result == null) {
            return null;
        } else {
            return result.getValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return cacheMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxSize() {
        return cacheMap.getMaximumSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(KeyType key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * NOTE: Look if its worth while in replacing with:
     * https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/map/LRUMap.html
     */
    private class LRUMap extends LinkedHashMap<KeyType, CacheEntry<KeyType, ValueType>> {

        private final int maximumSize;

        LRUMap(int maximumSize) {
            this.maximumSize = maximumSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<KeyType, CacheEntry<KeyType, ValueType>> eldest) {
            return size() > maximumSize;
        }

        private int getMaximumSize() {
            return maximumSize;
        }
    }
}
