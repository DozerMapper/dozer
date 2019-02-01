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
package com.github.dozermapper.core;

import com.github.dozermapper.core.config.BeanContainer;

/**
 * Public custom bean factory interface.
 * <p>
 * You can configure Dozer to use custom bean factories to create new instances of destination data objects during the
 * mapping process. By default Dozer just creates a new instance of any destination objects using a default constructor.
 * This is sufficient for most use cases, but if you need more flexibility you can specify your own bean factories to
 * instantiate the data objects.
 * <p>
 * Your custom bean factory must implement the com.github.dozermapper.core.BeanFactory interface.
 * <p>
 * Note: By default the Dozer mapping engine will use the destination object class name for the bean id when invoking
 * the factory.
 * <p>
 * <a
 * href="https://dozermapper.github.io/gitbook/documentation/custombeanfactories.html">
 * https://dozermapper.github.io/gitbook/documentation/custombeanfactories.html</a>
 */
public interface BeanFactory {

    // Need sourceObjClass in case sourceObj is null
    default Object createBean(Object source, Class<?> sourceClass, String targetBeanId, BeanContainer beanContainer) {
        return createBean(source, sourceClass, targetBeanId);
    }

    /**
     * Will be removed in 6.2. Please use {@link BeanFactory#createBean(Object, Class, String, BeanContainer)}.
     *
     * @param source       obj
     * @param sourceClass  obj
     * @param targetBeanId id
     * @return bean
     */
    @Deprecated
    default Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
        return null;
    }
}
