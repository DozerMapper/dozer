/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.spring;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperModelContext;
import com.github.dozermapper.core.events.EventListener;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DozerBeanMapperFactoryBeanTest {

    private DozerBeanMapperFactoryBean factory;
    private Resource mockResource;
    private ApplicationContext mockContext;

    @Before
    public void setUp() {
        factory = new DozerBeanMapperFactoryBean();
        mockResource = mock(Resource.class);
        mockContext = mock(ApplicationContext.class);
        factory.setApplicationContext(mockContext);
    }

    @Test
    public void testOk() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("mappings/mappingSpring.xml");
        when(mockResource.getURL()).thenReturn(url);

        factory.setCustomConverters(Collections.emptyList());
        factory.setCustomConvertersWithId(Collections.emptyMap());
        factory.setEventListeners(Collections.emptyList());
        factory.setFactories(Collections.emptyMap());
        factory.setMappingFiles(new Resource[] {mockResource});
        factory.setMappingBuilders(Collections.emptyList());
        factory.setCustomFieldMapper(null);
        factory.setBuilderCustomizers(Collections.emptyList());

        factory.afterPropertiesSet();

        assertEquals(Mapper.class, factory.getObjectType());
        assertTrue(factory.isSingleton());

        Mapper mapper = factory.getObject();
        assertNotNull(mapper);

        MapperModelContext mapperModelContext = mapper.getMapperModelContext();
        assertNotNull(mapperModelContext);

        List<?> files = mapperModelContext.getMappingFiles();

        assertEquals(1, files.size());
        assertEquals("file:" + url.getFile(), files.iterator().next());
    }

    @Test
    public void testEmpty() {
        factory.afterPropertiesSet();
    }

    @Test
    public void shouldInjectBeans() {
        HashMap<String, CustomConverter> converterHashMap = new HashMap<>();
        converterHashMap.put("a", mock(CustomConverter.class));

        HashMap<String, BeanFactory> beanFactoryMap = new HashMap<>();
        beanFactoryMap.put("a", mock(BeanFactory.class));

        HashMap<String, EventListener> eventListenerMap = new HashMap<>();
        eventListenerMap.put("a", mock(EventListener.class));

        DozerBeanMapperBuilderCustomizer customizer = mock(DozerBeanMapperBuilderCustomizer.class);
        HashMap<String, DozerBeanMapperBuilderCustomizer> customizerMap = new HashMap<>();
        customizerMap.put("a", customizer);
        customizerMap.put("b", customizer);

        when(mockContext.getBeansOfType(CustomConverter.class)).thenReturn(converterHashMap);
        when(mockContext.getBeansOfType(BeanFactory.class)).thenReturn(beanFactoryMap);
        when(mockContext.getBeansOfType(EventListener.class)).thenReturn(eventListenerMap);
        when(mockContext.getBeansOfType(DozerBeanMapperBuilderCustomizer.class)).thenReturn(customizerMap);

        factory.setBuilderCustomizers(Arrays.asList(customizer, customizer));

        factory.afterPropertiesSet();

        Mapper mapper = factory.getObject();
        assertNotNull(mapper);

        MapperModelContext mapperModelContext = mapper.getMapperModelContext();
        assertNotNull(mapperModelContext);

        assertEquals(1, mapperModelContext.getCustomConverters().size());
        assertEquals(1, mapperModelContext.getCustomConverters().size());
        assertEquals(1, mapperModelContext.getCustomConvertersWithId().size());
        assertEquals(1, mapperModelContext.getEventListeners().size());

        verify(customizer, times(4)).customize(any());
    }
}
