/*
 * Copyright 2005-2008 the original author or authors.
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
package org.dozer.spring;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author <a href="mailto:buzdin@gmail.com">Dmitry Buzdin</a>
 */
public class DozerBeanMapperFactoryBeanTest {

  DozerBeanMapperFactoryBean factory;
  Resource mockResource;

  @Before
  public void setUp() throws Exception {
    factory = new DozerBeanMapperFactoryBean();
    mockResource = mock(Resource.class);
  }

  @Test
  public void testOk() throws Exception {
    factory.setCustomConverters(Collections.EMPTY_LIST);
    factory.setCustomConvertersWithId(Collections.EMPTY_MAP);
    factory.setEventListeners(Collections.EMPTY_LIST);
    factory.setFactories(Collections.EMPTY_MAP);
    factory.setMappingFiles(new Resource[] { mockResource });

    URL url = this.getClass().getClassLoader().getResource("mappingSpring.xml");
    when(mockResource.getURL()).thenReturn(url);

    factory.afterPropertiesSet();

    assertEquals(Mapper.class, factory.getObjectType());
    Assert.assertTrue(factory.isSingleton());

    DozerBeanMapper mapper = (DozerBeanMapper) factory.getObject();
    List<?> files = mapper.getMappingFiles();
    assertEquals(1, files.size());
    assertEquals("file:" + url.getFile(), files.iterator().next());
  }

  @Test
  public void testEmpty() throws Exception {
    factory.afterPropertiesSet();
  }

  @Test
  public void testDestroy() throws Exception {
    factory.beanMapper = mock(DozerBeanMapper.class);
    factory.destroy();
    verify(factory.beanMapper).destroy();
  }

}
