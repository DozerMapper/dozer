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

import com.github.dozermapper.core.MappingException;

/**
 * Cache manager which is used as the top level interaction when dealing with cached objects
 */
public interface CacheManager {

    /**
     * Clears all available caches. Should not be used in production. Can be applied on "soft" application restart.
     */
    void clearAllEntries();

    /**
     * Returns a collection of all cache names
     *
     * @return Collection of all cache names
     */
    Collection<String> getCacheNames();

    /**
     * Returns a collection of all caches
     *
     * @return collection of all caches
     */
    Collection<Cache> getCaches();

    /**
     * Get cache object by name.
     *
     * @param cacheName unique cache name
     * @return Cache object found
     * @throws MappingException exception is thrown if no cache found
     */
    Cache getCache(String cacheName) throws MappingException;

    /**
     * Puts cache object into store
     *
     * @param cacheName   cache name to store
     * @param maximumSize maximum number of objects to store in cache
     * @return Cache object created
     * @throws MappingException exception is thrown if cache already exists
     */
    Cache putCache(String cacheName, int maximumSize) throws MappingException;

    /**
     * Checks if cache name exists
     *
     * @param cacheName cache name to find
     * @return true if found, false if doesn't exist
     */
    boolean cacheExists(String cacheName);
}
