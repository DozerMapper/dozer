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
import static org.junit.Assert.assertNotEquals;

public class CacheKeyFactoryTest extends AbstractDozerTest {

    @Test
    public void canCreateKey() {
        Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class);
        Object cacheKey2 = CacheKeyFactory.createKey(String.class, Long.class);

        assertEquals(cacheKey, cacheKey2);
        assertEquals(cacheKey.hashCode(), cacheKey2.hashCode());
    }

    @Test
    public void canCreateKeyInReverse() {
        Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class);
        Object cacheKey2 = CacheKeyFactory.createKey(Long.class, String.class);

        assertNotEquals(cacheKey, cacheKey2);
        assertNotEquals(cacheKey2, cacheKey);
        assertNotEquals(cacheKey.hashCode(), cacheKey2.hashCode());
    }

    @Test
    public void canCreateKeyForAllParameters() {
        Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class, "A");
        Object cacheKey2 = CacheKeyFactory.createKey(String.class, Long.class, "B");

        assertNotEquals(cacheKey, cacheKey2);
        assertNotEquals(cacheKey2, cacheKey);
        assertNotEquals(cacheKey.hashCode(), cacheKey2.hashCode());
    }

    @Test
    public void canCreateKeyAndHandlesNullGracefullyInEquals() {
        Object nullCacheKey = null;
        Object normalCacheKey = CacheKeyFactory.createKey(String.class, Long.class);

        assertNotEquals(normalCacheKey, nullCacheKey);
    }

    @Test
    public void canCreateKeyEquivalenceOfSelf() {
        Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class);
        assertEquals(cacheKey, cacheKey);
    }
}
