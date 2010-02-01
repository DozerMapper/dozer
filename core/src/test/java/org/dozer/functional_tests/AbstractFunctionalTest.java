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
package org.dozer.functional_tests;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.functional_tests.support.TestDataFactory;
import org.dozer.util.DozerConstants;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class AbstractFunctionalTest {

  protected Mapper mapper;
  protected TestDataFactory testDataFactory = new TestDataFactory(getDataObjectInstantiator());

  // Provides default non-proxy instantiation behavior
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

  @Before
  public void setUp() throws Exception {
    System.setProperty("log4j.debug", "true");
    System.setProperty(DozerConstants.DEBUG_SYS_PROP, "true");
    mapper = new DozerBeanMapper();
  }

  protected Mapper getMapper(String ... mappingFiles) {
    List<String> list = new ArrayList<String>();
    if (mappingFiles != null) {
      list.addAll(Arrays.asList(mappingFiles));
    }
    Mapper result = new DozerBeanMapper();
    ((DozerBeanMapper) result).setMappingFiles(list);
    return result;
  }

  protected <T> T newInstance(Class<T> classToInstantiate) {
    return getDataObjectInstantiator().newInstance(classToInstantiate);
  }
  
  public <T> T newInstance(Class<T> classToInstantiate, Object[] args) {
    return getDataObjectInstantiator().newInstance(classToInstantiate, args);
  }

  protected Object newInstance(Class<?>[] interfacesToProxy, Object target) {
    return getDataObjectInstantiator().newInstance(interfacesToProxy, target);
  }

}