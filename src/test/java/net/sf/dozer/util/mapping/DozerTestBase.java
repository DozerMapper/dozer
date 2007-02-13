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
package net.sf.dozer.util.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.dozer.util.mapping.util.MapperConstants;

import junit.framework.TestCase;

/**
 * @author tierney.matt
 */
public abstract class DozerTestBase extends TestCase {
  private static Random rand = new Random(System.currentTimeMillis());
  protected MapperIF mapper = null;
  
  protected void setUp() throws Exception {
    System.setProperty("log4j.debug","true");
    System.setProperty(MapperConstants.DEBUG_SYS_PROP,"true");
    mapper = new DozerBeanMapper();
  }
  
  protected MapperIF getNewMapper(String[] mappingFiles) {
    List list = new ArrayList();
    if (mappingFiles != null) {
      for (int i = 0; i < mappingFiles.length; i++) {
        list.add(mappingFiles[i]);
      }
    }
    MapperIF mapper = new DozerBeanMapper();
    ((DozerBeanMapper) mapper).setMappingFiles(list);
    return mapper;
  }
  
  protected String getRandomString() {
    return String.valueOf(rand.nextInt());
  }
  

}