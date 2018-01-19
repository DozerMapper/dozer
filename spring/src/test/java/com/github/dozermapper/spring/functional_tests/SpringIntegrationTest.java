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
package com.github.dozermapper.spring.functional_tests;

import java.util.List;

import com.github.dozermapper.spring.functional_tests.support.EventTestListener;
import com.github.dozermapper.spring.functional_tests.support.InjectedCustomConverter;
import com.github.dozermapper.spring.vo.Destination;
import com.github.dozermapper.spring.vo.Source;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerEventListener;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpringIntegrationTest {

    private ClassPathXmlApplicationContext context;

    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    @Test
    public void testCreationByFactory() {
        Mapper mapper = context.getBean("byFactory", Mapper.class);

        assertNotNull(mapper);
        assertNotNull(mapper.getMappingMetadata());
    }

    @Test
    public void testSpringNoMappingFilesSpecified() throws Exception {
        // Mapper can be used without specifying any mapping files.
        // Fields that have the same name will be mapped automatically.
        Mapper implicitMapper = context.getBean("implicitMapper", Mapper.class);

        assertNotNull(implicitMapper);
        assertBasicMapping(implicitMapper);
    }

    @Test
    public void testInjectConverter() throws Exception {
        DozerBeanMapper mapper = (DozerBeanMapper)context.getBean("mapperWithConverter", Mapper.class);

        assertNotNull(mapper);
        assertNotNull(mapper.getMappingMetadata());

        List<CustomConverter> customConverters = mapper.getCustomConverters();
        assertEquals(1, customConverters.size());

        InjectedCustomConverter converter = context.getBean(InjectedCustomConverter.class);
        converter.setInjectedName("inject");

        assertBasicMapping(mapper);
    }

    @Test
    public void testEventListeners() throws Exception {
        DozerBeanMapper eventMapper = (DozerBeanMapper)context.getBean("mapperWithEventListener", Mapper.class);
        EventTestListener listener = context.getBean(EventTestListener.class);

        assertNotNull(eventMapper);
        assertNotNull(listener);
        assertNotNull(eventMapper.getEventListeners());
        assertEquals(1, eventMapper.getEventListeners().size());

        DozerEventListener eventListener = eventMapper.getEventListeners().get(0);
        assertTrue(eventListener instanceof EventTestListener);

        assertEquals(0, listener.getInvocationCount());

        assertBasicMapping(eventMapper);

        assertEquals(4, listener.getInvocationCount());
    }

    @Test
    public void testBeanMappingBuilder() throws Exception {
        Mapper mapper = context.getBean("factoryWithMappingBuilder", Mapper.class);

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
