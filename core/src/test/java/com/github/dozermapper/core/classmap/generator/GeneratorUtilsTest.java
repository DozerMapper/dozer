/*
 * Copyright 2005-2024 Dozer Project
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
package com.github.dozermapper.core.classmap.generator;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.functional_tests.runner.ProxyDataObjectInstantiator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeneratorUtilsTest {

    @Test
    public void ignoreTechnicalFields() {
        Object proxy = ProxyDataObjectInstantiator.INSTANCE.newInstance(Object.class);
        BeanContainer beanContainer = new BeanContainer();

        assertTrue(GeneratorUtils.shouldIgnoreField("callback", proxy.getClass(), Object.class, beanContainer));
        assertFalse(GeneratorUtils.shouldIgnoreField("callback", Object.class, Object.class, beanContainer));
        assertTrue(GeneratorUtils.shouldIgnoreField("callbacks", proxy.getClass(), Object.class, beanContainer));
        assertFalse(GeneratorUtils.shouldIgnoreField("callbacks", Object.class, Object.class, beanContainer));
        assertTrue(GeneratorUtils.shouldIgnoreField("class", Object.class, Object.class, beanContainer));
        assertFalse(GeneratorUtils.shouldIgnoreField("a", proxy.getClass(), Object.class, beanContainer));
    }
}
