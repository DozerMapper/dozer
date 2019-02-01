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

import java.util.Collection;
import java.util.Collections;

import com.github.dozermapper.core.builder.BeanBuilderCreationStrategy;
import com.github.dozermapper.core.classmap.generator.BeanFieldsDetector;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorCreationStrategy;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

public interface DozerModule {

    /**
     * This is a temporal solution to resolve dependencies to Dozer internals.
     *
     * @param beanContainer             factory
     * @param destBeanCreator           factory
     * @param propertyDescriptorFactory factory
     * @deprecated requires completere design of Dozer Modules
     */
    @Deprecated
    void init(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory);

    void init();

    /**
     * To be implemented by module if it provides any additional strategies for beans creation.
     *
     * @return collection of bean creation strategies; or empty collection if module does not provide this.
     */
    default Collection<BeanBuilderCreationStrategy> getBeanBuilderCreationStrategies() {
        return Collections.emptyList();
    }

    /**
     * To be implemented by module if it provides any additional detectors of bean fields.
     *
     * @return collection of bean field detectors; or empty collection if module does not provide this.
     */
    default Collection<BeanFieldsDetector> getBeanFieldsDetectors() {
        return Collections.emptyList();
    }

    /**
     * To be implemented by module if it provides any additional strategies to create property description.
     *
     * @return collection of property creation strategies; or empty collection if module does not provide this.
     */
    default Collection<PropertyDescriptorCreationStrategy> getPropertyDescriptorCreationStrategies() {
        return Collections.emptyList();
    }
}
