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

import java.util.HashSet;
import java.util.Set;

import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * @author tierney.matt
 */
public class DozerCacheManagerTest extends AbstractDozerTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  private DozerCacheManager cacheMgr;

  @Override
  @Before
  public void setUp() throws Exception {
    cacheMgr = new DozerCacheManager();
  }

  @Test
  public void testCreateNew() throws Exception {
    DozerCacheManager cacheMgr2 = new DozerCacheManager();

    assertFalse("cache mgrs should not be equal", cacheMgr.equals(cacheMgr2));
    assertNotSame("cache mgrs should not be same instance", cacheMgr, cacheMgr2);
  }

  @Test
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

  @Test(expected = MappingException.class)
  public void testGetUnknownCache() throws Exception {
    String cacheName = getRandomString();
    boolean cacheExists = cacheMgr.cacheExists(cacheName);
    assertFalse("cache should not exist", cacheExists);
    cacheMgr.getCache(cacheName);
  }

  @Test(expected = MappingException.class)
  public void testAddDuplicateCachesSingleton() throws Exception {
    String cacheName = getRandomString();
    cacheMgr.addCache(cacheName, 1);
    // try adding it again
    cacheMgr.addCache(cacheName, 1);
  }

  @Test
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

  @Test
  public void testGetStatisticTypes() {
    String name = getRandomString();
    String name2 = name + "-2";
    cacheMgr.addCache(name, 100);
    cacheMgr.addCache(name2, 100);

    Set<String> expected = new HashSet<String>();
    expected.add(name);
    expected.add(name2);

    assertEquals("invalid cache names types found", expected, cacheMgr.getCacheNames());
  }

  @Test
  public void testClearAllCacheEntries() {
    String name = getRandomString();
    Cache<String, String> cache = new DozerCache<String, String>(name, 5);
    cache.put(getRandomString(), "value");
    cacheMgr.addCache(cache);

    assertEquals("invalid initial cache entry size", 1, ((DozerCache) cacheMgr.getCache(name)).getEntries().size());
    cacheMgr.clearAllEntries();
    assertEquals("invalid cache entry size after clearAll", 0, ((DozerCache) cacheMgr.getCache(name)).getEntries().size());
  }

  @Test
  public void testGetCaches() {
    String name = getRandomString();
    Cache<String, String> cache = new DozerCache<String, String>(name, 5);
    Cache<String, String> cache2 = new DozerCache<String, String>(name + "2", 5);
    cacheMgr.addCache(cache);
    cacheMgr.addCache(cache2);

    Set<Cache> expected = new HashSet<Cache>();
    expected.add(cache);
    expected.add(cache2);

    assertEquals("invalid caches found", expected, cacheMgr.getCaches());
  }
}
