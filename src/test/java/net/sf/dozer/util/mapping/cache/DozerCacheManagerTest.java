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
package net.sf.dozer.util.mapping.cache;

import java.util.HashSet;
import java.util.Set;

import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.MappingException;

/**
 * @author tierney.matt
 */
public class DozerCacheManagerTest extends AbstractDozerTest {
  private DozerCacheManager cacheMgr;

  protected void setUp() throws Exception {
    super.setUp();
    cacheMgr = new DozerCacheManager();
  }

  public void testCreateNew() throws Exception {
    DozerCacheManager cacheMgr2 = new DozerCacheManager();

    assertFalse("cache mgrs should not be equal", cacheMgr.equals(cacheMgr2));
    assertNotSame("cache mgrs should not be same instance", cacheMgr, cacheMgr2);
  }

  public void testAddGetExistsCache() throws Exception {
    String cacheName = getRandomString();
    cacheMgr.addCache(cacheName, 1);

    boolean cacheExists = cacheMgr.cacheExists(cacheName);
    assertTrue("cache should exist", cacheExists);

    Cache cache = cacheMgr.getCache(cacheName);
    assertNotNull("cache should not be null", cache);
    assertEquals("cache should be empty", cache.getSize(), 0);
    assertEquals("invalid cache name", cacheName, cache.getName());
  }

  public void testGetUnknownCache() throws Exception {
    String cacheName = getRandomString();
    boolean cacheExists = cacheMgr.cacheExists(cacheName);
    assertFalse("cache should not exist", cacheExists);

    try {
      cacheMgr.getCache(cacheName);
      fail("trying to get an unknown cache should have thrown a MappingException");
    } catch (MappingException e) {
    }
  }

  public void testAddDuplicateCachesSingleton() throws Exception {
    String cacheName = getRandomString();
    cacheMgr.addCache(cacheName, 1);

    try {
      // try adding it again
      cacheMgr.addCache(cacheName, 1);
      fail("trying to add duplicate caches should have thrown an ObjectExistsException");
    } catch (MappingException e) {
    }
  }

  public void testAddDuplicateCachesNonSingleton() throws Exception {
    // You should be able to add caches with the same name to non singleton instances
    // of the cache manager because they each have their own copies of caches to manage.
    // The caches are uniquely identified by the cache managers by using the instance id.
    DozerCacheManager cacheMgr2 = new DozerCacheManager();

    // add cache to each cache mgr instance
    String cacheName = getRandomString();
    cacheMgr.addCache(cacheName, 1);
    cacheMgr2.addCache(cacheName, 1);

    assertTrue("cache should exist in cache mgr1", cacheMgr.cacheExists(cacheName));
    assertTrue("cache should also exist in cache mgr2", cacheMgr2.cacheExists(cacheName));

    Cache cache1 = cacheMgr.getCache(cacheName);
    Cache cache2 = cacheMgr2.getCache(cacheName);

    assertFalse("caches should not be the same instance", cache1 == cache2);
    assertEquals("invalid cache name", cacheName, cache1.getName());
    assertEquals("invalid cache name for cache2", cacheName, cache2.getName());
  }

  public void testGetStatisticTypes() {
    String name = getRandomString();
    String name2 = name + "-2";
    cacheMgr.addCache(name, 100);
    cacheMgr.addCache(name2, 100);

    Set expected = new HashSet();
    expected.add(name);
    expected.add(name2);

    assertEquals("invalid cache names types found", expected, cacheMgr.getCacheNames());
  }

  public void testClearAllCacheEntries() {
    String name = getRandomString();
    Cache cache = new Cache(name, 5);
    CacheEntry entry = new CacheEntry(getRandomString(), "value");
    cache.put(entry);
    cacheMgr.addCache(cache);

    assertEquals("invalid initial cache entry size", 1, cacheMgr.getCache(name).getEntries().size());
    cacheMgr.clearAllEntries();
    assertEquals("invalid cache entry size after clearAll", 0, cacheMgr.getCache(name).getEntries().size());
  }

  public void testGetCaches() {
    String name = getRandomString();
    Cache cache = new Cache(name, 5);
    Cache cache2 = new Cache(name + "2", 5);
    cacheMgr.addCache(cache);
    cacheMgr.addCache(cache2);

    Set expected = new HashSet();
    expected.add(cache);
    expected.add(cache2);

    assertEquals("invalid caches found", expected, cacheMgr.getCaches());
  }
}
