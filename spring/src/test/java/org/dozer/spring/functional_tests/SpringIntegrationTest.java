/**
 * Copyright 2005-2013 Dozer Project
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
package org.dozer.spring.functional_tests;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerEventListener;
import org.dozer.Mapper;
import org.dozer.spring.functional_tests.support.EventTestListener;
import org.dozer.spring.functional_tests.support.InjectedCustomConverter;
import org.dozer.spring.functional_tests.support.SampleBeanMappingBuilder;
import org.dozer.spring.vo.Destination;
import org.dozer.spring.vo.Source;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dmitry.buzdin
 */
public class SpringIntegrationTest {

  ClassPathXmlApplicationContext context;

  @Before
  public void setUp() throws Exception {
    context = new ClassPathXmlApplicationContext("applicationContext.xml");
  }

  @Test
  public void testCreationByFactory() {
    DozerBeanMapper mapper = context.getBean("byFactory", DozerBeanMapper.class);
    assertTrue(mapper.getMappingFiles().isEmpty());
  }

  @Test
  public void testSpringNoMappingFilesSpecified() throws Exception {
    // Mapper can be used without specifying any mapping files.
    // Fields that have the same name will be mapped automatically.
    Mapper implicitMapper = (Mapper) context.getBean("implicitMapper");

    assertBasicMapping(implicitMapper);
  }

  @Test
  public void testInjectConverter() throws Exception {
    DozerBeanMapper mapper = context.getBean("mapperWithConverter", DozerBeanMapper.class);

    assertNotNull(mapper);
    assertNotNull(mapper.getMappingFiles());

    List<CustomConverter> customConverters = mapper.getCustomConverters();
    assertEquals(1, customConverters.size());

    InjectedCustomConverter converter = context.getBean(InjectedCustomConverter.class);
    converter.setInjectedName("inject");

    assertBasicMapping(mapper);
  }

  @Test
  public void testEventListeners() throws Exception {
    DozerBeanMapper eventMapper = context.getBean("mapperWithEventListener", DozerBeanMapper.class);
    assertNotNull(eventMapper.getEventListeners());
    assertEquals(1, eventMapper.getEventListeners().size());
    DozerEventListener eventListener = eventMapper.getEventListeners().get(0);
    assertEquals(EventTestListener.class, eventListener.getClass());

    EventTestListener listener = context.getBean(EventTestListener.class);
    assertNotNull(listener);

    assertEquals(0, listener.getInvocationCount());

    assertBasicMapping(eventMapper);

    assertEquals(4, listener.getInvocationCount());
  }

  @Test
  public void testBeanMappingBuilder() throws Exception {
    DozerBeanMapper mapper = (DozerBeanMapper) context.getBean("factoryWithMappingBuilder", DozerBeanMapper.class);

    Source source = new Source();
    source.setName("John");
    source.setId(2L);
    Destination destination = mapper.map(source, Destination.class);
    assertEquals("John", destination.getValue());
    assertEquals(2L, destination.getId());
  }

  private void assertBasicMapping(Mapper mapper) {
    Source source = new Source();
    source.setId(1L);
    Destination destination = mapper.map(source, Destination.class);
    assertEquals(1L, destination.getId());
  }

}
