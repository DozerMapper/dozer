/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.cache;

import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.cache.Cache;
import net.sf.dozer.cache.CacheKeyFactory;
import net.sf.dozer.cache.DozerCache;

/**
 * @author tierney.matt
 */
public class DozerCacheTest extends AbstractDozerTest {

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
    assertEquals("invlid cache hit count", numCacheEntriesToAdd, cache.getHitCount());
    assertEquals("invlid cache miss count", numCacheEntriesToAdd, cache.getMissCount());
  }

  public void testMaximumCacheSize() throws Exception {
    int maxSize = 25;
    Cache cache = new DozerCache(getRandomString(), maxSize);
    // Add a bunch of entries to cache to verify the cache doesnt grow larger than specified max size
    for (int i = 0; i < maxSize + 125; i++) {
      cache.put("testkey" + i, "testvalue" + i);
    }
    assertEquals("cache size should not exceed max size", maxSize, cache.getSize());
  }

  public void testMaximumCacheSize_Zero() throws Exception {
    int maxSize = 0;
    Cache cache = new DozerCache(getRandomString(), maxSize);
    // Add a bunch of entries to cache to verify the cache doesnt grow larger than specified max size
    for (int i = 0; i < maxSize + 5; i++) {
      cache.put("testkey" + i, "testvalue" + i);
    }
    assertEquals("cache size should not exceed max size", maxSize, cache.getSize());
  }

  public void testClear() throws Exception {
    Cache cache = new DozerCache(getRandomString(), 50);
    Object key = CacheKeyFactory.createKey(String.class, Integer.class);
    cache.put(key, "testvalue");

    assertEquals("cache should contain entry", 1, cache.getSize());
    cache.clear();
    assertEquals("cache should have been cleared", 0, cache.getSize());
  }

  public void testGetMaxSize() {
    int maxSize = 550;
    Cache cache = new DozerCache(getRandomString(), maxSize);

    assertEquals("invalid max size", maxSize, cache.getMaxSize());
  }

  public void testGetNull() {
    Cache cache = new DozerCache(getRandomString(), 5);
    try {
      cache.get(null);
      fail("should have thrown exception");
    } catch (IllegalArgumentException e) {
    }
  }

  public void testPutNull() {
    Cache cache = new DozerCache(getRandomString(), 5);
    try {
      cache.put(null, null);
      fail("should have thrown exception");
    } catch (IllegalArgumentException e) {
    }
  }
}
