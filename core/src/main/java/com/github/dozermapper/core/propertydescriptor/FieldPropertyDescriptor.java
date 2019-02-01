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

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.util.DozerConstants;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

/**
 * Internal class that directly accesses the field via reflection. The getter/setter methods for the field are bypassed
 * and will NOT be invoked. Private fields are accessible by Dozer. Only intended for internal use.
 */
public class FieldPropertyDescriptor extends AbstractPropertyDescriptor implements DozerPropertyDescriptor {

    private final DozerPropertyDescriptor[] descriptorChain;
    private final DestBeanCreator destBeanCreator;

    public FieldPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
                                   HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer,
                                   DestBeanCreator destBeanCreator) {
        super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
        this.destBeanCreator = destBeanCreator;

        String[] tokens = fieldName.split(DozerConstants.DEEP_FIELD_DELIMITER_REGEXP);
        descriptorChain = new DozerPropertyDescriptor[tokens.length];

        Class<?> currentType = clazz;
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            String token = tokens[i];
            descriptorChain[i] = new ChainedPropertyDescriptor(currentType, token, isIndexed, index);
            if (i < tokensLength) {
                Field field = ReflectionUtils.getFieldFromBean(currentType, tokens[i]);
                currentType = field.getType();
            }
        }
    }

    public Class<?> getPropertyType() {
        return descriptorChain[descriptorChain.length - 1].getPropertyType();
    }

    public Class<?> genericType() {
        return descriptorChain[descriptorChain.length - 1].genericType();
    }

    public Object getPropertyValue(Object bean) {
        Object intermediateResult = bean;
        for (DozerPropertyDescriptor descriptor : descriptorChain) {
            intermediateResult = descriptor.getPropertyValue(intermediateResult);
            if (intermediateResult == null) {
                return null;
            }
        }
        return intermediateResult;
    }

    public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
        Object intermediateResult = bean;
        for (int i = 0; i < descriptorChain.length; i++) {
            DozerPropertyDescriptor descriptor = descriptorChain[i];
            if (i != descriptorChain.length - 1) {
                Object currentValue = descriptor.getPropertyValue(intermediateResult);
                if (currentValue == null) {
                    currentValue = destBeanCreator.create(descriptor.getPropertyType());
                    descriptor.setPropertyValue(intermediateResult, currentValue, fieldMap);
                }
                intermediateResult = currentValue;
            } else { // last one
                descriptor.setPropertyValue(intermediateResult, value, fieldMap);
            }
        }
    }

    static class ChainedPropertyDescriptor implements DozerPropertyDescriptor {

        private Field field;
        private boolean indexed;
        private int index;

        ChainedPropertyDescriptor(Class<?> clazz, String fieldName, boolean indexed, int index) {
            this.indexed = indexed;
            this.index = index;
            field = ReflectionUtils.getFieldFromBean(clazz, fieldName);
        }

        public Class<?> getPropertyType() {
            return field.getType();
        }

        public Object getPropertyValue(Object bean) {
            Object result = null;
            try {
                result = field.get(bean);
            } catch (IllegalArgumentException e) {
                MappingUtils.throwMappingException(e);
            } catch (IllegalAccessException e) {
                MappingUtils.throwMappingException(e);
            }
            if (indexed) {
                result = MappingUtils.getIndexedValue(result, index);
            }

            return result;
        }

        public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
            if (value == null && getPropertyType().isPrimitive()) {
                return; // do nothing
            }

            // Check if dest value is already set and is equal to src value. If true, no need to rewrite the dest value
            if (getPropertyValue(bean) == value) {
                return;
            }

            try {
                if (indexed) {
                    Object existingValue = field.get(bean);
                    Object collection = MappingUtils.prepareIndexedCollection(getPropertyType(), existingValue, value, index);
                    field.set(bean, collection);
                } else {
                    field.set(bean, value);
                }
            } catch (IllegalAccessException e) {
                MappingUtils.throwMappingException(e);
            }
        }

        public Class<?> genericType() {
            Type type = field.getGenericType();
            return ReflectionUtils.determineGenericsType(type);
        }
    }

}
