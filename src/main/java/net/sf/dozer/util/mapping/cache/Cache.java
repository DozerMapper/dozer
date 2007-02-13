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

import java.util.Collection;
import java.util.Map;

import net.sf.dozer.util.mapping.stats.GlobalStatistics;
import net.sf.dozer.util.mapping.stats.StatisticTypeConstants;
import net.sf.dozer.util.mapping.stats.StatisticsManagerIF;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author tierney.matt
 */
public class Cache {
  private final String name;

  private long maximumSize;
  private final Map cacheMap;
  private long hitCount;
  private long missCount;

  public Cache(String name, long maximumSize) {
    this.name = name;
    this.maximumSize = maximumSize;
    this.cacheMap = new CacheLinkedHashMap();
  }
  
  public void clear() {
    cacheMap.clear();
  }

  public synchronized void put(CacheEntry cacheEntry) {
    if (cacheEntry == null) {
      throw new IllegalArgumentException("Cache Entry cannot be null");
    }
    cacheMap.put(cacheEntry.getKey(), cacheEntry);
    if (cacheMap.size() > maximumSize) {
      // remove eldest entry
      ((LinkedMap) cacheMap).remove(cacheMap.size() - 1);
    }
  }

  public CacheEntry get(Object key) {
    if (key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
    CacheEntry result = (CacheEntry) cacheMap.get(key);
    StatisticsManagerIF statMgr = GlobalStatistics.getInstance().getStatsMgr();
    if (result != null) {
      hitCount++;
      statMgr.increment(StatisticTypeConstants.CACHE_HIT_COUNT, name);
    } else {
      missCount++;
      statMgr.increment(StatisticTypeConstants.CACHE_MISS_COUNT, name);
    }
    return result;
  }
  
  public Collection getEntries() {
    return cacheMap.values();
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return cacheMap.size();
  }
  
  public long getMaxSize() {
    return maximumSize;
  }
  
  public void setMaxSize(long maximumSize) {
    this.maximumSize = maximumSize;
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

  private class CacheLinkedHashMap extends LinkedMap {
    // Order the map by which its entries were last accessed, from least-recently accessed to most-recently
    // (access-order)
    public CacheLinkedHashMap() {
      super(100, 0.75F);
    }
  }

}
