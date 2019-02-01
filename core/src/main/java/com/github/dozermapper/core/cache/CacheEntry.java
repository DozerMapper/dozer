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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A single entry in the cache which holds the cache value and unique key for lookup.
 */
public class CacheEntry<KeyType, ValueType> {

    private final KeyType key;
    private final ValueType value;

    /**
     * A single entry in the cache which holds the cache value and unique key for lookup.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public CacheEntry(KeyType key, ValueType value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key stored
     *
     * @return key stored
     */
    public KeyType getKey() {
        return key;
    }

    /**
     * Returns the value stored
     *
     * @return value stored
     */
    public ValueType getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CacheEntry)) {
            return false;
        }

        CacheEntry entry = (CacheEntry)object;
        return this.getKey().equals(entry.getKey());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
