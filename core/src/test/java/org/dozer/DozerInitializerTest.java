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
package org.dozer;

import org.dozer.builder.DestBeanBuilderCreator;
import org.dozer.classmap.generator.BeanMappingGenerator;
import org.dozer.config.BeanContainer;
import org.dozer.config.GlobalSettings;
import org.dozer.factory.DestBeanCreator;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.stats.StatisticsManager;
import org.dozer.stats.StatisticsManagerImpl;
import org.dozer.util.DefaultClassLoader;
import org.dozer.util.DozerConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DozerInitializerTest extends AbstractDozerTest {

    private GlobalSettings globalSettings;

    private DozerInitializer instance;
    private StatisticsManager statisticsManager;
    private BeanContainer beanContainer;
    private DestBeanBuilderCreator destBeanBuilderCreator;
    private BeanMappingGenerator beanMappingGenerator;
    private PropertyDescriptorFactory propertyDescriptorFactory;
    private DestBeanCreator destBeanCreator;

    @Before
    public void setUp() throws Exception {
        beanContainer = new BeanContainer();
        globalSettings = new GlobalSettings(new DefaultClassLoader(DozerInitializerTest.class.getClassLoader()));
        statisticsManager = new StatisticsManagerImpl(globalSettings);
        destBeanBuilderCreator = new DestBeanBuilderCreator();
        propertyDescriptorFactory = new PropertyDescriptorFactory();
        destBeanCreator = new DestBeanCreator(beanContainer);
        beanMappingGenerator = new BeanMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory);
        instance = new DozerInitializer();
        instance.destroy(globalSettings);
    }

    @After
    public void tearDown() throws Exception {
        instance.destroy(globalSettings);
    }

    @Test
    public void testIsInitialized() {
        assertFalse(instance.isInitialized());

        instance.init(globalSettings, statisticsManager, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        assertTrue(instance.isInitialized());

        instance.destroy(globalSettings);
        assertFalse(instance.isInitialized());
    }

    @Test
    public void testDoubleCalls() {
        instance.destroy(globalSettings);
        assertFalse(instance.isInitialized());

        instance.init(globalSettings, statisticsManager, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        instance.init(globalSettings, statisticsManager, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        assertTrue(instance.isInitialized());

        instance.destroy(globalSettings);
        instance.destroy(globalSettings);
        assertFalse(instance.isInitialized());
    }

    @Test(expected = MappingException.class)
    public void testBeanIsMissing() {
        GlobalSettings settings = mock(GlobalSettings.class);
        when(settings.getClassLoaderName()).thenReturn(DozerConstants.DEFAULT_CLASS_LOADER_BEAN);
        when(settings.getProxyResolverName()).thenReturn("no.such.class.Found");

        instance.initialize(settings, getClass().getClassLoader(), statisticsManager, beanContainer, destBeanBuilderCreator,
                beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        fail();
    }

    @Test(expected = MappingException.class)
    public void testBeanIsNotAssignable() {
        GlobalSettings settings = mock(GlobalSettings.class);
        when(settings.getClassLoaderName()).thenReturn("java.lang.String");
        when(settings.getProxyResolverName()).thenReturn(DozerConstants.DEFAULT_PROXY_RESOLVER_BEAN);

        instance.initialize(settings, getClass().getClassLoader(), statisticsManager, beanContainer, destBeanBuilderCreator,
                beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
        fail();
    }
}
