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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.util.MappingUtils;

/**
 * {@inheritDoc}
 */
public final class DefaultCacheManager implements CacheManager {

    private final Map<String, Cache> cachesMap = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    public DefaultCacheManager() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Cache> getCaches() {
        return new HashSet<>(cachesMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cache getCache(String name) throws MappingException {
        Cache cache = cachesMap.get(name);
        if (cache == null) {
            MappingUtils.throwMappingException("Unable to find cache with name: " + name);
        }

        return cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cache putCache(String name, int maxElementsInMemory) {
        return putCache(new DefaultCache(name, maxElementsInMemory));
    }

    private Cache putCache(Cache cache) throws MappingException {
        synchronized (cachesMap) {
            String name = cache.getName();
            if (cacheExists(name)) {
                MappingUtils.throwMappingException("Cache already exists with name: " + name);
            }

            cachesMap.put(name, cache);
            return cache;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getCacheNames() {
        Set<String> results = new HashSet<>();
        for (Entry<String, Cache> entry : cachesMap.entrySet()) {
            results.add(entry.getKey());
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAllEntries() {
        //NOTE: Don't clear keys in caches map because these are only added 1 time at startup by default.
        //      Only clear cache entries for each cache.
        for (Cache cache : cachesMap.values()) {
            cache.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cacheExists(String name) {
        return cachesMap.containsKey(name);
    }
}
