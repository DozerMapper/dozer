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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.exception.NotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tierney.matt
*/
public final class DozerCacheManager implements CacheManagerIF {
  private static final Log log = LogFactory.getLog(DozerCacheManager.class);
  private static DozerCacheManager singleton = createNew();
  private final Map cachesMap = new HashMap();

  /*
   * The createNew() factory method does not act as a singleton. Callers must maintain their own reference to it. 
   * NOTE: that if the getInstance() method is called, the singleton instance will be created/returned, 
   * separate from any instances created in this method
   */
  public static DozerCacheManager createNew() {
    return new DozerCacheManager();
  }
  
  /*
   * Use singleton cache manager for cache(s) that can be shared by all processes within a VM
   */
  public static DozerCacheManager getInstance() {
    return singleton;
  }
  
  private DozerCacheManager() {
  }

  public Set getCaches() {
    return new HashSet(cachesMap.values());
  }
  
  public Cache getCache(String name) {
    Cache cache = (Cache)cachesMap.get(name);
    if (cache == null) {
      throw new NotFoundException("Unable to find cache with name: " + name);
    }
    return cache;
  }
  
  public void addCache(String name, long maxElementsInMemory) {
    addCache(new Cache(name, maxElementsInMemory));
  }
  
  public void addCache(Cache cache) {
    synchronized(cachesMap) {
      String name = cache.getName();
      if (cacheExists(name)) {
        throw new MappingException("Cache already exists with name: " + name);
      }
      cachesMap.put(name, cache);
    }
  }
  
  public Set getCacheNames() {
    Set results = new HashSet();
    Iterator iter = cachesMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      results.add((String)entry.getKey());
    }
    return results;
  }
  
  /*
   * Dont clear keys in caches map because these are only added 1 time at startup.
   * Only clear cache entries for each cache 
   */
  public void clearAllEntries() {
    Iterator iter = cachesMap.values().iterator();
    while (iter.hasNext()){
      Cache cache = (Cache) iter.next();
      cache.clear();
    }
  }
  
  public boolean cacheExists(String name) {
    return cachesMap.containsKey(name);
  }
  
  public void logCaches() {
    log.info(getCaches());
  }
}
