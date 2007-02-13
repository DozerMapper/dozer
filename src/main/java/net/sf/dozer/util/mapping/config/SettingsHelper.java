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
package net.sf.dozer.util.mapping.config;

import java.util.Properties;


/**
 * @author tierney.matt
 */
public class SettingsHelper {
  
  private SettingsHelper() {}
  
  public static void populateSettingsFromProperties(Settings result, Properties props) {
    String propValue = props.getProperty(PropertyConstants.STATISTICS_ENABLED);
    if (propValue != null) {
      result.setStatisticsEnabled(Boolean.valueOf(propValue).booleanValue());
    }
    propValue = props.getProperty(PropertyConstants.CONVERTER_CACHE_MAX_SIZE);
    if (propValue != null) {
      result.setConverterByDestTypeCacheMaxSize(Long.parseLong(propValue));
    }
    propValue = props.getProperty(PropertyConstants.SUPERTYPE_CACHE_MAX_SIZE);
    if (propValue != null) {
      result.setSuperTypesCacheMaxSize(Long.parseLong(propValue));
    }
  }
}
