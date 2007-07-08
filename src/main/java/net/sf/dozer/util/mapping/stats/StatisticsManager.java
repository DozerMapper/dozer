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
package net.sf.dozer.util.mapping.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.util.MappingUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal class that manages the Dozer statistics. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public final class StatisticsManager implements StatisticsManagerIF {
  private static final Log log = LogFactory.getLog(StatisticsManager.class);

  private final Map statisticsMap = new HashMap();
  private boolean isStatisticsEnabled = GlobalSettings.getInstance().isStatisticsEnabled();

  public void clearAll() {
    statisticsMap.clear();
  }

  public Set getStatisticEntries(String statisticType) {
    return getStatistic(statisticType).getEntries();
  }

  public Set getStatistics() {
    return new HashSet(statisticsMap.values());
  }

  public boolean isStatisticsEnabled() {
    return isStatisticsEnabled;
  }

  public void setStatisticsEnabled(boolean statisticsEnabled) {
    this.isStatisticsEnabled = statisticsEnabled;
    GlobalSettings.getInstance().setStatisticsEnabled(statisticsEnabled);
  }

  public Statistic getStatistic(String statisticType) {
    Statistic result = (Statistic) statisticsMap.get(statisticType);
    if (result == null) {
      MappingUtils.throwMappingException("Unable to find statistic for type: " + statisticType);
    }
    return result;
  }

  public Set getStatisticTypes() {
    Set results = new HashSet();
    Iterator iter = statisticsMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      results.add((String) entry.getKey());
    }
    return results;
  }

  /*
   * Convenience method that should only be used for statistic types that will only ever have 1 statistic entry(value).
   * For stats that only have one entry, it is assumed that the single entrie's key is the same as the stat type name
   */
  public void increment(String statisticType) {
    increment(statisticType, 1);
  }

  public void increment(String statisticType, long value) {
    increment(statisticType, statisticType, value);
  }

  public void increment(String statisticType, Object statisticEntryKey) {
    increment(statisticType, statisticEntryKey, 1);
  }

  public void increment(String statisticType, Object statisticEntryKey, long value) {
    // If statistics are not enabled, just return and do nothing.
    if (!isStatisticsEnabled()) {
      return;
    }

    if (statisticType == null) {
      throw new IllegalArgumentException("statistic type must be specified");
    }

    if (statisticEntryKey == null) {
      throw new IllegalArgumentException("statistic entry key must be specified");
    }

    // Get Statistic object for the specified type. If it doesnt aleady exist, create it
    Statistic statistic = (Statistic) statisticsMap.get(statisticType);
    if (statistic == null) {
      statistic = new Statistic(statisticType);
      addStatistic(statistic);
    }

    // Get the Statistic Entry object which contains the actual value.
    // If it doesnt aleady exist, create it so that it can be incremented
    StatisticEntry statisticEntry = null;
    statisticEntry = statistic.getEntry(statisticEntryKey);
    if (statisticEntry == null) {
      statisticEntry = new StatisticEntry(statisticEntryKey);
      statistic.addEntry(statisticEntry);
    }

    // Increment the actual value
    statisticEntry.increment(value);
  }

  /*
   * Convenience method that should only be used for statistic types that will only ever have 1 statistic entry(value).
   * getStatisticEntries() should be used for statistic types that have more than 1 statistic entry(value)
   */
  public long getStatisticValue(String statisticType) {
    Set entries = getStatistic(statisticType).getEntries();
    if (entries.size() > 1) {
      throw new IllegalArgumentException("More than one value entry found for stat type: " + statisticType);
    }
    return ((StatisticEntry) entries.iterator().next()).getValue();
  }

  public void addStatistic(Statistic statistic) {
    if (statisticExists(statistic.getType())) {
      throw new IllegalArgumentException("Statistic already exists for type: " + statistic.getType());
    }
    statisticsMap.put(statistic.getType(), statistic);
  }

  public boolean statisticExists(String statisticType) {
    return statisticsMap.containsKey(statisticType);
  }

  public void logStatistics() {
    log.info(getStatistics());
  }
}
