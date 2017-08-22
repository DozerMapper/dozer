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
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * @author tierney.matt
 */
public class DozerCacheTest extends AbstractDozerTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Test
  public void testPutGetFromCache() throws Exception {
    Cache cache = new DozerCache(getRandomString(), 50);
    int numCacheEntriesToAdd = 45;
    for (int i = 0; i < numCacheEntriesToAdd; i++) {
      Object key = String.valueOf(i);

      assertNull("cache entry should not already exist", cache.get(key));

      cache.put(key, "testvalue" + i);

      String value = (String) cache.get(key);
      assertEquals("cache entries should be equal", value, "testvalue" + i);
    }
    assertEquals("invlid cache size", numCacheEntriesToAdd, cache.getSize());
  }

  @Test
  public void testMaximumCacheSize() throws Exception {
    int maxSize = 25;
    Cache cache = new DozerCache(getRandomString(), maxSize);
    // Add a bunch of entries to cache to verify the cache doesnt grow larger than specified max size
    for (int i = 0; i < maxSize + 125; i++) {
      cache.put("testkey" + i, "testvalue" + i);
    }
    assertEquals("cache size should not exceed max size", maxSize, cache.getSize());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMaximumCacheSize_Zero() throws Exception {
    int maxSize = 0;
    Cache cache = new DozerCache(getRandomString(), maxSize);
    // Add a bunch of entries to cache to verify the cache doesnt grow larger than specified max size
    for (int i = 0; i < maxSize + 5; i++) {
      cache.put("testkey" + i, "testvalue" + i);
    }
    assertEquals("cache size should not exceed max size", maxSize, cache.getSize());
  }

  @Test
  public void testClear() throws Exception {
    Cache<Object, String> cache = new DozerCache<Object, String>(getRandomString(), 50);
    Object key = CacheKeyFactory.createKey(String.class, Integer.class);
    cache.put(key, "testvalue");

    assertEquals("cache should contain entry", 1, cache.getSize());
    cache.clear();
    assertEquals("cache should have been cleared", 0, cache.getSize());
  }

  @Test
  public void testGetMaxSize() {
    int maxSize = 550;
    Cache cache = new DozerCache(getRandomString(), maxSize);

    assertEquals("invalid max size", maxSize, cache.getMaxSize());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetNull() {
    Cache cache = new DozerCache(getRandomString(), 5);
    cache.get(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutNull() {
    Cache cache = new DozerCache(getRandomString(), 5);
    cache.put(null, null);
  }

  @Test
  public void testAddEntries() {
    DozerCache cache = new DozerCache(getRandomString(), 5);
    DozerCache<String, String> cache2 = new DozerCache<String, String>(getRandomString(), 5);

    cache2.put("A", "B");
    cache2.put("B", "C");

    assertEquals(0, cache.getSize());

    cache.addEntries(cache2.getEntries());

    assertEquals(2, cache.getSize());
    assertEquals(2, cache2.getSize());
  }

}
