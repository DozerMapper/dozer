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
package com.github.dozermapper.core.propertydescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.util.DozerConstants;
import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Internal factory responsible for determining which property descriptor should
 * be used. Only intended for internal use.
 */
public final class PropertyDescriptorFactory {

    private final List<PropertyDescriptorCreationStrategy> pluggedDescriptorCreationStrategies =
            new ArrayList<>();

    public PropertyDescriptorFactory() {

    }

    public DozerPropertyDescriptor getPropertyDescriptor(Class<?> clazz, String theGetMethod, String theSetMethod,
                                                         String mapGetMethod, String mapSetMethod, boolean isAccessible,
                                                         boolean isIndexed, int index, String name, String key, boolean isSelfReferencing,
                                                         String oppositeFieldName, HintContainer srcDeepIndexHintContainer,
                                                         HintContainer destDeepIndexHintContainer, String beanFactory,
                                                         BeanContainer beanContainer, DestBeanCreator destBeanCreator) {
        DozerPropertyDescriptor desc = null;

        // Raw Map types or custom map-get-method/set specified
        boolean isMapProperty = MappingUtils.isSupportedMap(clazz);
        if (name.equals(DozerConstants.SELF_KEYWORD) &&
            (mapSetMethod != null || mapGetMethod != null || isMapProperty)) {

            // If no mapSetMethod is defined, default to "put"
            String setMethod = StringUtils.isBlank(mapSetMethod) ? "put" : mapSetMethod;

            // If no mapGetMethod is defined, default to "get".
            String getMethod = StringUtils.isBlank(mapGetMethod) ? "get" : mapGetMethod;

            desc = new MapPropertyDescriptor(clazz, name, isIndexed, index, setMethod,
                                             getMethod, key != null ? key : oppositeFieldName,
                                             srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);

            // Copy by reference(Not mapped backed properties which also use 'this'
            // identifier for a different purpose)
        } else if (isSelfReferencing) {
            desc = new SelfPropertyDescriptor(clazz);

            // Access field directly and bypass getter/setters
        } else if (isAccessible) {
            desc = new FieldPropertyDescriptor(clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer, destBeanCreator);

            // Custom get-method/set specified
        } else if (theSetMethod != null || theGetMethod != null) {
            desc = new CustomGetSetPropertyDescriptor(clazz, name, isIndexed, index, theSetMethod, theGetMethod,
                                                      srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);

            // If this object is an XML Bean - then use the XmlBeanPropertyDescriptor
        } else if (beanFactory != null && beanFactory.equals(DozerConstants.XML_BEAN_FACTORY)) {
            desc = new XmlBeanPropertyDescriptor(clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);
        }

        if (desc != null) {
            return desc;
        }

        for (PropertyDescriptorCreationStrategy propertyDescriptorBuilder :
                new CopyOnWriteArrayList<>(pluggedDescriptorCreationStrategies)) {
            if (propertyDescriptorBuilder.isApplicable(clazz, name)) {
                desc = propertyDescriptorBuilder.buildFor(
                        clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
                if (desc != null) {
                    break;
                }
            }
        }

        if (desc == null) {
            // Everything else. It must be a normal bean with normal custom get/set methods
            desc = new JavaBeanPropertyDescriptor(clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);
        }

        return desc;
    }

    public void addPluggedPropertyDescriptorCreationStrategies(Collection<PropertyDescriptorCreationStrategy> strategies) {
        pluggedDescriptorCreationStrategies.addAll(strategies);
    }
}
