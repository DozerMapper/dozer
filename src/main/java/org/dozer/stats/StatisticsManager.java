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

import java.util.Set;

/**
 * Internal interface for managing statistics. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public interface StatisticsManager {
  public Set<Statistic<?>> getStatistics();
  public long getStatisticValue(StatisticType statisticType);
  public boolean statisticExists(StatisticType statisticType);
  public void clearAll();
  public Set<StatisticEntry> getStatisticEntries(StatisticType statisticType);
  public Set<StatisticType> getStatisticTypes();
  public boolean isStatisticsEnabled();
  public void setStatisticsEnabled(boolean statisticsEnabled);
  public void logStatistics();
  public void increment(StatisticType statisticType);
  public void increment(StatisticType statisticType, long value);
  public void increment(StatisticType statisticType, Object statisticEntryKey);
  
  
}
