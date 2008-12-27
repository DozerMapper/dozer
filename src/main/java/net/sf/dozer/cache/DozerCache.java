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
package net.sf.dozer.cache;

import java.util.Collection;
import java.util.Map;

import net.sf.dozer.stats.GlobalStatistics;
import net.sf.dozer.stats.StatisticTypeConstants;
import net.sf.dozer.stats.StatisticsManager;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * Map backed Cache implementation.
 *
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public class DozerCache implements Cache {

  private final String name;

  // Order the map by which its entries were last accessed, from least-recently accessed to most-recently (access-order)
  private final Map<Object, Object> cacheMap = new LinkedMap(100, 0.75F);
  private long maximumSize;
  private long hitCount;
  private long missCount;

  StatisticsManager statMgr = GlobalStatistics.getInstance().getStatsMgr();

  public DozerCache(String name, long maximumSize) {
    this.name = name;
    this.maximumSize = maximumSize;
  }

  public void clear() {
    cacheMap.clear();
  }

  public synchronized void put(Object key, Object value) {
    if (key == null) {
      throw new IllegalArgumentException("Cache entry key cannot be null");
    }
    CacheEntry cacheEntry = new CacheEntry(key, value);
    cacheMap.put(cacheEntry.getKey(), cacheEntry);
    if (cacheMap.size() > maximumSize) {
      // remove eldest entry
      ((LinkedMap) cacheMap).remove(cacheMap.size() - 1);
    }
  }

  public Object get(Object key) {
    if (key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
    CacheEntry result = (CacheEntry) cacheMap.get(key);
    if (result != null) {
      hitCount++;
      statMgr.increment(StatisticTypeConstants.CACHE_HIT_COUNT, name);
      return result.getValue();
    } else {
      missCount++;
      statMgr.increment(StatisticTypeConstants.CACHE_MISS_COUNT, name);
      return null;
    }
  }

  public Collection<Object> getEntries() {
    return cacheMap.values();
  }

  public String getName() {
    return name;
  }

  public long getSize() {
    return cacheMap.size();
  }

  public long getMaxSize() {
    return maximumSize;
  }

  public long getHitCount() {
    return hitCount;
  }

  public long getMissCount() {
    return missCount;
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }


}
