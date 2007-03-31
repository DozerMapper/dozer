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
package net.sf.dozer.util.mapping.jmx;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.stats.GlobalStatistics;
import net.sf.dozer.util.mapping.stats.StatisticEntry;
import net.sf.dozer.util.mapping.stats.StatisticTypeConstants;
import net.sf.dozer.util.mapping.stats.StatisticsManagerIF;


/**
 * @author tierney.matt
 */
public class DozerStatisticsController implements DozerStatisticsControllerMBean {
  private final StatisticsManagerIF statsMgr = GlobalStatistics.getInstance().getStatsMgr();
  
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
    return getStatisticValue(StatisticTypeConstants.MAPPING_SUCCESS_COUNT);
  }
  
  public long getMappingFailureCount() {
    return getStatisticValue(StatisticTypeConstants.MAPPING_FAILURE_COUNT);
  }
  
  public long getMapperInstancesCount() {
    return getStatisticValue(StatisticTypeConstants.MAPPER_INSTANCES_COUNT);
  }
  
  public long getMappingOverallTime() {
    return getStatisticValue(StatisticTypeConstants.MAPPING_TIME);
  }
  
  public Set getMappingFailureExceptionTypes() {
    return getStatisticEntries(StatisticTypeConstants.MAPPING_FAILURE_EX_TYPE_COUNT); 
  }
  
  public Set getMappingFailureTypes() {
    return getStatisticEntries(StatisticTypeConstants.MAPPING_FAILURE_TYPE_COUNT); 
  }
  
  public Set getCacheHitCount() {
    return getStatisticEntries(StatisticTypeConstants.CACHE_HIT_COUNT); 
  }
  
  public Set getCacheMissCount() {
    return getStatisticEntries(StatisticTypeConstants.CACHE_MISS_COUNT); 
  }

  public long getFieldMappingSuccessCount() {
    return getStatisticValue(StatisticTypeConstants.FIELD_MAPPING_SUCCESS_COUNT);
  }
  
  public long getFieldMappingFailureCount() {
    return getStatisticValue(StatisticTypeConstants.FIELD_MAPPING_FAILURE_COUNT);
  }
  
  public long getFieldMappingFailureIgnoredCount() {
    return getStatisticValue(StatisticTypeConstants.FIELD_MAPPING_FAILURE_IGNORED_COUNT); 
  }
  
  public Set getStatisticTypes() {
    return statsMgr.getStatisticTypes();
  }
  
  public double getMappingAverageTime() {
    double totalTime = getStatisticValue(StatisticTypeConstants.MAPPING_TIME);
    double totalCount = getStatisticValue(StatisticTypeConstants.MAPPING_SUCCESS_COUNT);
    return totalTime/totalCount;
  }
  
  public Set getStatisticEntries(String statisticType) {
    Set result = new TreeSet();
    if (statsMgr.statisticExists(statisticType)) {
      Set entries = statsMgr.getStatisticEntries(statisticType);
      Iterator iter = entries.iterator();
      while(iter.hasNext()) {
        StatisticEntry entry = (StatisticEntry) iter.next();
        result.add(entry.getKey().toString() + ":Count " + entry.getValue());
      }
    }
    return result;
  }
  
  public void logStatistics() {
    statsMgr.logStatistics();
  }
  
  public String dumpStatistics() {
    return statsMgr.getStatistics().toString();
  }
  
  protected long getStatisticValue(String statisticType) {
    long result = 0;
    if(statsMgr.statisticExists(statisticType)) {
      result = statsMgr.getStatisticValue(statisticType);
    }
    return result;
  }
}
