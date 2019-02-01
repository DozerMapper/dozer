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
package com.github.dozermapper.core.functional_tests;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.config.SettingsDefaults;
import com.github.dozermapper.core.config.SettingsKeys;
import com.github.dozermapper.core.el.DefaultELEngine;
import com.github.dozermapper.core.el.ELExpressionFactory;
import com.github.dozermapper.core.functional_tests.runner.InstantiatorHolder;
import com.github.dozermapper.core.functional_tests.runner.Proxied;
import com.github.dozermapper.core.functional_tests.support.TestDataFactory;
import com.github.dozermapper.core.loader.xml.ExpressionElementReader;
import com.github.dozermapper.core.util.DozerConstants;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * This class should be inherited by all functional tests
 */
@RunWith(Proxied.class)
public abstract class AbstractFunctionalTest {

    protected Mapper mapper;
    protected TestDataFactory testDataFactory = new TestDataFactory(getDataObjectInstantiator());

    // Provides default non-proxy instantiation behavior
    protected DataObjectInstantiator getDataObjectInstantiator() {
        return InstantiatorHolder.get();
    }

    @Before
    public void setUp() throws Exception {
        System.setProperty("log4j.debug", "true");
        System.setProperty(DozerConstants.DEBUG_SYS_PROP, "true");
        System.setProperty(SettingsKeys.CONFIG_FILE_SYS_PROP, SettingsDefaults.LEGACY_PROPERTIES_FILE);

        mapper = DozerBeanMapperBuilder.buildDefault();
    }

    protected Mapper getMapper(String... mappingFiles) {
        return DozerBeanMapperBuilder.create()
                .withMappingFiles(mappingFiles)
                .build();
    }

    protected Mapper getMapperWithEL(String... mappingFiles) {
        DefaultELEngine elEngine = new DefaultELEngine(ELExpressionFactory.newInstance());

        return DozerBeanMapperBuilder.create()
                .withMappingFiles(mappingFiles)
                .withELEngine(elEngine)
                .withElementReader(new ExpressionElementReader(elEngine))
                .build();
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
