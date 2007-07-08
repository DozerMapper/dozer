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

import java.util.Set;

/**
 * Internal interface for managing caches. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public interface CacheManagerIF {
  public void clearAllEntries();
  public Set getCaches();
  public Cache getCache(String cacheName);
  public Set getCacheNames();
  public void addCache(String cacheName, long maximumSize);
  public void addCache(Cache cache);
  public boolean cacheExists(String cacheName);
  public void logCaches();
}
