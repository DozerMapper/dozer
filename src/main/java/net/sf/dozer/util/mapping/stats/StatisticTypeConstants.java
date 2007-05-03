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

/**
 * Internal constants file that defines the types of supported statistic keys.  Only intended for internal use.
 * 
 * @author tierney.matt
 */
public abstract class StatisticTypeConstants {
  public static final String MAPPER_INSTANCES_COUNT = "Mapper Instances Count";
  public static final String MAPPING_SUCCESS_COUNT = "Mapping Success Count";
  public static final String MAPPING_FAILURE_COUNT = "Mapping Failure Count";
  public static final String MAPPING_FAILURE_EX_TYPE_COUNT = "Mapping Failure Exception Type Count";
  public static final String MAPPING_FAILURE_TYPE_COUNT = "Mapping Failure Type Count";
  public static final String MAPPING_TIME = "Mapping Overall Time(ms)";
  public static final String FIELD_MAPPING_SUCCESS_COUNT = "Field Mapping Success Count";
  public static final String FIELD_MAPPING_FAILURE_COUNT = "Field Mapping Failure Count";
  public static final String FIELD_MAPPING_FAILURE_IGNORED_COUNT = "Field Mapping Failure Ignored Count";
  public static final String CUSTOM_CONVERTER_SUCCESS_COUNT = "Custom Converter Success Count";
  public static final String CUSTOM_CONVERTER_TIME = "Custom Converter Overall Time(ms)";
  public static final String CACHE_HIT_COUNT = "Cache Hit Count";
  public static final String CACHE_MISS_COUNT = "Cache Miss Count";
}
