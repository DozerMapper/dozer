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

/**
 * Cache which stores a single {@link com.github.dozermapper.core.cache.DozerCacheType} type
 *
 * @param <KeyType>   type of key being stored
 * @param <ValueType> java of value being stored
 */
public interface Cache<KeyType, ValueType> {

    /**
     * Removes all of the mappings from this map. The map will be empty after this call returns.
     */
    void clear();

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    void put(KeyType key, ValueType value);

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param key key with which the specified value is to be associated
     * @return value to be associated with the specified key
     */
    ValueType get(KeyType key);

    /**
     * Returns the name of the overall cache store
     *
     * @return name of the overall cache store
     */
    String getName();

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    long getSize();

    /**
     * Returns the maximum number of entries which the cache can hold
     *
     * @return maximum number of entries which the cache can hold
     */
    int getMaxSize();

    /**
     * Returns true if this map contains a mapping for the
     * specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key.
     */
    boolean containsKey(KeyType key);
}
