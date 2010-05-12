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
package org.dozer.jmx;

import org.dozer.config.GlobalSettings;
import org.dozer.stats.GlobalStatistics;
import org.dozer.stats.StatisticEntry;
import org.dozer.stats.StatisticType;
import org.dozer.stats.StatisticsManager;

import java.util.Set;
import java.util.TreeSet;


/**
 * Public Dozer JMX Bean
 *
 * @author tierney.matt
 */
public class DozerStatisticsController implements DozerStatisticsControllerMBean {

  private final StatisticsManager statsMgr = GlobalStatistics.getInstance().getStatsMgr();

  public void clearAll() {
    statsMgr.clearAll();
  }

  public boolean isStatisticsEnabled() {
    return GlobalSettings.getInstance().isStatisticsEnabled();
  }

  public void setStatisticsEnabled(boolean statisticsEnabled) {
    GlobalSettings.getInstance().setStatisticsEnabled(statisticsEnabled);
  }

  public long getMappingSuccessCount() {
    return getStatisticValue(StatisticType.MAPPING_SUCCESS_COUNT);
  }

  public long getMappingFailureCount() {
    return getStatisticValue(StatisticType.MAPPING_FAILURE_COUNT);
  }

  public long getMapperInstancesCount() {
    return getStatisticValue(StatisticType.MAPPER_INSTANCES_COUNT);
  }

  public long getMappingOverallTimeInMillis() {
    return getStatisticValue(StatisticType.MAPPING_TIME);
  }

  public Set<String> getMappingFailureExceptionTypes() {
    return getStatisticEntries(StatisticType.MAPPING_FAILURE_EX_TYPE_COUNT);
  }

  public Set<String> getMappingFailureTypes() {
    return getStatisticEntries(StatisticType.MAPPING_FAILURE_TYPE_COUNT);
  }

  public Set<String> getCacheHitCount() {
    return getStatisticEntries(StatisticType.CACHE_HIT_COUNT);
  }

  public Set<String> getCacheMissCount() {
    return getStatisticEntries(StatisticType.CACHE_MISS_COUNT);
  }

  public long getFieldMappingSuccessCount() {
    return getStatisticValue(StatisticType.FIELD_MAPPING_SUCCESS_COUNT);
  }

  public long getFieldMappingFailureCount() {
    return getStatisticValue(StatisticType.FIELD_MAPPING_FAILURE_COUNT);
  }

  public long getFieldMappingFailureIgnoredCount() {
    return getStatisticValue(StatisticType.FIELD_MAPPING_FAILURE_IGNORED_COUNT);
  }

  public long getCustomConverterSuccessCount() {
    return getStatisticValue(StatisticType.CUSTOM_CONVERTER_SUCCESS_COUNT);
  }

  public long getCustomConverterOverallTimeInMillis() {
    return getStatisticValue(StatisticType.CUSTOM_CONVERTER_TIME);
  }

  public double getMappingAverageTimeInMillis() {
    double totalTime = getStatisticValue(StatisticType.MAPPING_TIME);
    double totalCount = getStatisticValue(StatisticType.MAPPING_SUCCESS_COUNT);
    return totalTime / totalCount;
  }

  public double getCustomConverterAverageTimeInMillis() {
    double totalTime = getStatisticValue(StatisticType.CUSTOM_CONVERTER_TIME);
    double totalCount = getStatisticValue(StatisticType.CUSTOM_CONVERTER_SUCCESS_COUNT);
    return totalTime / totalCount;
  }

  public double getCustomConverterPercentageOfMappingTime() {
    double ccTotalTime = getStatisticValue(StatisticType.CUSTOM_CONVERTER_TIME);
    double overallTime = getStatisticValue(StatisticType.MAPPING_TIME);
    return (ccTotalTime / overallTime) * 100;
  }

  protected Set<String> getStatisticEntries(StatisticType statisticType) {
    Set<String> result = new TreeSet<String>();
    for (StatisticEntry entry : statsMgr.getStatisticEntries(statisticType)) {
      result.add(entry.getKey().toString() + ": Count " + entry.getValue());
    }
    return result;
  }

  public void logStatistics() {
    statsMgr.logStatistics();
  }

  public String dumpStatistics() {
    return statsMgr.getStatistics().toString();
  }

  protected long getStatisticValue(StatisticType statisticType) {
    return statsMgr.getStatisticValue(statisticType);
  }

}
