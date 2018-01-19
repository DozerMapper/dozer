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
package org.dozer.cache;

import org.dozer.AbstractDozerTest;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class CacheKeyFactoryTest extends AbstractDozerTest {

  @Test
  public void testCreateKey() throws Exception {
    Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class);
    Object cacheKey2 = CacheKeyFactory.createKey(String.class, Long.class);

    assertEquals("cache keys should have been equal", cacheKey, cacheKey2);
    assertEquals("cache key hash codes should have been equal", cacheKey.hashCode(), cacheKey2.hashCode());
  }

  @Test
  public void testCreateKey_Reverse() throws Exception {
    Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class);
    Object cacheKey2 = CacheKeyFactory.createKey(Long.class, String.class);

    assertFalse(cacheKey.equals(cacheKey2));
    assertFalse(cacheKey2.equals(cacheKey));
    assertFalse(cacheKey.hashCode() == cacheKey2.hashCode());
  }

  @Test
  public void testCreateKey_AllParameters() throws Exception {
    Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class, "A");
    Object cacheKey2 = CacheKeyFactory.createKey(String.class, Long.class, "B");

    assertFalse(cacheKey.equals(cacheKey2));
    assertFalse(cacheKey2.equals(cacheKey));
    assertFalse(cacheKey.hashCode() == cacheKey2.hashCode());
  }

  @Test
  public void testCreateKey_handlesNullGracefullyInEquals() throws Exception {
      Object nullCacheKey = null;
      Object normalCacheKey = CacheKeyFactory.createKey(String.class, Long.class);

      assertFalse("Null isn't handled properly!", normalCacheKey.equals(nullCacheKey));
  }

  @Test
  public void testCreateKey_equivalenceOfSelf() throws Exception {
    Object cacheKey = CacheKeyFactory.createKey(String.class, Long.class);
    assertTrue("Key isn't equivalent to self!", cacheKey.equals(cacheKey));
  }
}
