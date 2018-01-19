/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer.loader;

import org.dozer.classmap.ClassMappings;
import org.dozer.classmap.Configuration;

/**
 * Internal class that contains the results of the loadMappings operation. Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author tierney.matt
 * 
 */
public class LoadMappingsResult {

  private ClassMappings customMappings;
  private Configuration globalConfiguration;

  public LoadMappingsResult(ClassMappings customMappings, Configuration globalConfiguration) {
    this.customMappings = customMappings;
    this.globalConfiguration = globalConfiguration;
  }

  public ClassMappings getCustomMappings() {
    return customMappings;
  }

  public Configuration getGlobalConfiguration() {
    return globalConfiguration;
  }

}
