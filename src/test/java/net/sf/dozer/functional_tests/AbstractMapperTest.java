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
package net.sf.dozer.functional_tests;

import java.util.ArrayList;
import java.util.List;

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.DozerBeanMapper;
import net.sf.dozer.Mapper;
import net.sf.dozer.util.MapperConstants;
import net.sf.dozer.util.TestDataFactory;

import org.junit.Before;
/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class AbstractMapperTest  {
  protected Mapper mapper;
  protected TestDataFactory testDataFactory = new TestDataFactory(getDataObjectInstantiator());

  protected abstract DataObjectInstantiator getDataObjectInstantiator();

  @Before
  public void setUp() throws Exception {
    System.setProperty("log4j.debug", "true");
    System.setProperty(MapperConstants.DEBUG_SYS_PROP, "true");
    mapper = new DozerBeanMapper();
  }

  protected Mapper getMapper(String mappingFile) {
      return getMapper(new String[] {mappingFile});
  }

  protected Mapper getMapper(String[] mappingFiles) {
    List list = new ArrayList();
    if (mappingFiles != null) {
      for (int i = 0; i < mappingFiles.length; i++) {
        list.add(mappingFiles[i]);
      }
    }
    Mapper result = new DozerBeanMapper();
    ((DozerBeanMapper) result).setMappingFiles(list);
    return result;
  }

  protected Object newInstance(Class classToInstantiate) {
    return getDataObjectInstantiator().newInstance(classToInstantiate);
  }
  
  protected Object newInstance(Class[] interfacesToProxy, Object target) {
    return getDataObjectInstantiator().newInstance(interfacesToProxy, target);
  }

}