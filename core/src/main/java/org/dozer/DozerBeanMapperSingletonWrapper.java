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
package org.dozer;

import org.dozer.util.DozerConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * Public Singleton wrapper for the DozerBeanMapper. Only supports a single mapping file named dozerBeanMapping.xml, so
 * configuration is very limited. Instead of using the DozerBeanMapperSingletonWrapper, it is recommended that the
 * DozerBeanMapper(MapperIF) instance is configured via an IOC framework, such as Spring, with singleton property set to
 * "true"
 * 
 * @author garsombke.franz
 */
public class DozerBeanMapperSingletonWrapper {

  private static Mapper instance;

  private DozerBeanMapperSingletonWrapper() {
  }

  public static synchronized Mapper getInstance() {
    if (instance == null) {
      List<String> mappingFiles = new ArrayList<String>();
      mappingFiles.add(DozerConstants.DEFAULT_MAPPING_FILE);
      instance = new DozerBeanMapper(mappingFiles);
    }
    return instance;
  }
}
