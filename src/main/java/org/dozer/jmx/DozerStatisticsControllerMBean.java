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

import java.util.Set;

/**
 * Public Dozer JMX Bean interface
 * 
 * @author tierney.matt
 */
public interface DozerStatisticsControllerMBean {
  public double getMappingAverageTimeInMillis();
  public long getMappingSuccessCount();
  public long getMappingFailureCount();
  public Set<String> getMappingFailureExceptionTypes();
  public Set<String> getMappingFailureTypes();
  public long getMappingOverallTimeInMillis();
  public Set<String> getCacheHitCount();
  public Set<String> getCacheMissCount();
  public long getMapperInstancesCount();
  public long getFieldMappingSuccessCount();
  public long getFieldMappingFailureCount();
  public long getFieldMappingFailureIgnoredCount();
  public long getCustomConverterOverallTimeInMillis();
  public long getCustomConverterSuccessCount();
  public double getCustomConverterPercentageOfMappingTime();
  public double getCustomConverterAverageTimeInMillis();
  public String dumpStatistics();
  public void clearAll();
  public boolean isStatisticsEnabled();
  public void setStatisticsEnabled(boolean statisticsEnabled);
  public void logStatistics();
}
