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

/**
 * Internal constants file that defines the types of supported statistic keys. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public enum StatisticType {
  
  MAPPER_INSTANCES_COUNT,
  MAPPING_SUCCESS_COUNT,
  MAPPING_FAILURE_COUNT,
  MAPPING_FAILURE_EX_TYPE_COUNT,
  MAPPING_FAILURE_TYPE_COUNT,
  MAPPING_TIME,
  FIELD_MAPPING_SUCCESS_COUNT,
  FIELD_MAPPING_FAILURE_COUNT,
  FIELD_MAPPING_FAILURE_IGNORED_COUNT,
  CUSTOM_CONVERTER_SUCCESS_COUNT,
  CUSTOM_CONVERTER_TIME,
  CACHE_HIT_COUNT,
  CACHE_MISS_COUNT
  
}
