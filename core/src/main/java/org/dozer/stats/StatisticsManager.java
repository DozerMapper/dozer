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

  Set<Statistic> getStatistics();

  long getStatisticValue(StatisticType statisticType);

  long getStatisticValue(StatisticType statisticType, Object entryKey);

  void clearAll();

  Set<StatisticEntry> getStatisticEntries(StatisticType statisticType);

  Set<StatisticType> getStatisticTypes();

  boolean isStatisticsEnabled();

  void setStatisticsEnabled(boolean statisticsEnabled);

  void logStatistics();

  Statistic increment(StatisticType statisticType);

  Statistic increment(StatisticType statisticType, long value);

  Statistic increment(StatisticType statisticType, Object statisticEntryKey);

}
