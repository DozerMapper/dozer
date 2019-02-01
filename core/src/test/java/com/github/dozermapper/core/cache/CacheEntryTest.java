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

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheEntryTest extends AbstractDozerTest {

    @Test
    public void canConstructor() {
        String key = getRandomString();
        String value = getRandomString();
        CacheEntry<String, String> cacheEntry = new CacheEntry<>(key, value);

        assertEquals(key, cacheEntry.getKey());
        assertEquals(value, cacheEntry.getValue());
    }

    @Test
    public void hashCodeAndEqualsAreSame() {
        String key = getRandomString();
        String value = getRandomString();
        CacheEntry<String, String> cacheEntry = new CacheEntry<>(key, value);
        CacheEntry<String, String> cacheEntry2 = new CacheEntry<>(key, value);

        assertEquals(cacheEntry.hashCode(), cacheEntry2.hashCode());
        assertEquals(cacheEntry, cacheEntry2);
    }
}
