/*
 * Copyright 2005-2010 the original author or authors.
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
package org.dozer.stats;

import org.dozer.config.GlobalSettings;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Internal class that manages the Dozer runtime statistics. Only intended for internal use.
 *
 * @author tierney.matt
 */
public final class StatisticsManagerImpl implements StatisticsManager {

  private static final Logger log = LoggerFactory.getLogger(StatisticsManagerImpl.class);

  private final ConcurrentMap<StatisticType, Statistic> statisticsMap = new ConcurrentHashMap<StatisticType, Statistic>();
  private boolean isStatisticsEnabled = GlobalSettings.getInstance().isStatisticsEnabled();

  public void clearAll() {
    statisticsMap.clear();
  }

  @SuppressWarnings("unchecked")
  public Set<StatisticEntry> getStatisticEntries(StatisticType statisticType) {
    Statistic statistic = statisticsMap.get(statisticType);
    return statistic != null ? statistic.getEntries() : Collections.EMPTY_SET;
  }

  public Set<Statistic> getStatistics() {
    return new HashSet<Statistic>(statisticsMap.values());
  }

  public boolean isStatisticsEnabled() {
    return isStatisticsEnabled;
  }

  public void setStatisticsEnabled(boolean statisticsEnabled) {
    this.isStatisticsEnabled = statisticsEnabled;
    GlobalSettings.getInstance().setStatisticsEnabled(statisticsEnabled);
  }

  public Set<StatisticType> getStatisticTypes() {
    Set<StatisticType> results = new HashSet<StatisticType>();
    for (Entry<StatisticType, Statistic> entry : statisticsMap.entrySet()) {
      results.add(entry.getKey());
    }
    return results;
  }

  /*
   * Convenience method that should only be used for statistic types that will only ever have 1 statistic entry(value).
   * For stats that only have one entry, it is assumed that the single entry's key is the same as the stat type name
   */

  public Statistic increment(StatisticType statisticType) {
    return increment(statisticType, 1);
  }

  public Statistic increment(StatisticType statisticType, long value) {
    return increment(statisticType, statisticType, value);
  }

  public Statistic increment(StatisticType statisticType, Object statisticEntryKey) {
    return increment(statisticType, statisticEntryKey, 1);
  }

  protected Statistic increment(StatisticType statisticType, Object statisticEntryKey, long value) {
    // If statistics are not enabled, just return and do nothing.
    if (!isStatisticsEnabled()) {
      return null;
    }

    if (statisticType == null) {
      throw new IllegalArgumentException("statistic type must be specified");
    }

    // Get Statistic object for the specified type. If it doesn't already exist, create it
    Statistic statistic = statisticsMap.get(statisticType);
    if (statistic == null) {
      Statistic newStatistic = new Statistic(statisticType);
      statistic = statisticsMap.putIfAbsent(statisticType, newStatistic);
      if (statistic == null) {
        statistic = newStatistic;
      }
    }

    // increment the statistic
    statistic.increment(statisticEntryKey, value);
    return statistic;
  }

  protected Statistic getStatistic(StatisticType statisticType) {
    return statisticsMap.get(statisticType);
  }

  /*
   * Convenience method that should only be used for statistic types that will only ever have 1 statistic entry(value).
   * getStatisticEntries() should be used for statistic types that have more than 1 statistic entry(value)
   */

  public long getStatisticValue(StatisticType statisticType) {
    return getStatisticValue(statisticType, statisticType);
  }

  public long getStatisticValue(StatisticType statisticType, Object entryKey) {
    Statistic statistic = statisticsMap.get(statisticType);
    return statistic != null ? statistic.getStatisticValue(entryKey) : 0;
  }

  public void logStatistics() {
    log.info(getStatistics().toString());
  }

}
