/*
 * Copyright 2005-2008 the original author or authors.
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
import java.util.Map;
import java.util.Set;


import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.dozer.stats.GlobalStatistics;
import org.dozer.stats.StatisticType;
import org.dozer.stats.StatisticsManager;

/**
 *
 * Map backed Cache implementation.
 *
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public class DozerCache <KeyType, ValueType> implements Cache <KeyType, ValueType> {

  private final String name;

  // Via LRUMap implementation, order the map by which its entries were last accessed, from least-recently accessed to most-recently (access-order)
  private Map<KeyType, CacheEntry<KeyType, ValueType> > cacheMap;

  StatisticsManager statMgr = GlobalStatistics.getInstance().getStatsMgr();

  public DozerCache (String name, int maximumSize) {
    if (maximumSize < 1) {
      throw new IllegalArgumentException("Dozer cache max size must be greater than 0");
    }
    this.name = name;
    this.cacheMap = new LRUMap(maximumSize) ; // TODO This should be in Collections.synchronizedMap()
  }

  public void clear() {
    cacheMap.clear();
  }

  public synchronized void put(KeyType key, ValueType value) {
    if (key == null) {
      throw new IllegalArgumentException("Cache entry key cannot be null");
    }
    CacheEntry <KeyType, ValueType> cacheEntry = new CacheEntry <KeyType, ValueType> (key, value);
    cacheMap.put(cacheEntry.getKey(), cacheEntry);
  }

  public ValueType get(KeyType key) {
    if (key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
    CacheEntry <KeyType, ValueType> result = cacheMap.get(key);
    if (result != null) {
      statMgr.increment(StatisticType.CACHE_HIT_COUNT, name);
      return result.getValue();
    } else {
      statMgr.increment(StatisticType.CACHE_MISS_COUNT, name);
      return null;
    }
  }

  public void addEntries(Collection<CacheEntry <KeyType, ValueType> > entries) {
    for (CacheEntry <KeyType, ValueType> entry : entries) {
      cacheMap.put(entry.getKey(), entry);
    }
  }

  public Collection<CacheEntry <KeyType, ValueType> > getEntries() {
    return cacheMap.values();
  }

  public String getName() {
    return name;
  }

  public long getSize() {
    return cacheMap.size();
  }

  public long getMaxSize() {
    return ((LRUMap) cacheMap).maxSize();
  }

  public boolean containsKey(KeyType key) {
    return cacheMap.containsKey(key);
  }

  public Set<KeyType> keySet() {
    return cacheMap.keySet();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}
