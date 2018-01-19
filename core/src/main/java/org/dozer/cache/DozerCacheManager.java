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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dozer.util.MappingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class that manages the Dozer caches. Only intended for internal use.
 *
 * @author tierney.matt
 */
public final class DozerCacheManager implements CacheManager {

    private final Logger log = LoggerFactory.getLogger(DozerCacheManager.class);

    private final Map<String, Cache> cachesMap = new HashMap<String, Cache>();

    public DozerCacheManager() {

    }

    public Collection<Cache> getCaches() {
        return new HashSet<Cache>(cachesMap.values());
    }

    public Cache getCache(String name) {
        Cache cache = cachesMap.get(name);
        if (cache == null) {
            MappingUtils.throwMappingException("Unable to find cache with name: " + name);
        }
        return cache;
    }

    public void addCache(String name, int maxElementsInMemory) {
        addCache(new DozerCache(name, maxElementsInMemory));
    }

    public void addCache(Cache cache) {
        synchronized (cachesMap) {
            String name = cache.getName();
            if (cacheExists(name)) {
                MappingUtils.throwMappingException("Cache already exists with name: " + name);
            }
            cachesMap.put(name, cache);
        }
    }

    public Collection<String> getCacheNames() {
        Set<String> results = new HashSet<String>();
        for (Entry<String, Cache> entry : cachesMap.entrySet()) {
            results.add(entry.getKey());
        }
        return results;
    }

    /*
     * Dont clear keys in caches map because these are only added 1 time at startup. Only clear cache entries for each cache
     */
    public void clearAllEntries() {
        for (Cache cache : cachesMap.values()) {
            cache.clear();
        }
    }

    public boolean cacheExists(String name) {
        return cachesMap.containsKey(name);
    }

    public void logCaches() {
        log.info(getCaches().toString());
    }
}
