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

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.HintContainer;

public class XmlBeanPropertyDescriptor implements DozerPropertyDescriptor {

    private static final String IS_SET_PREFIX = "set";

    private static final String DOT = ".";

    private final JavaBeanPropertyDescriptor fieldPropertyDescriptor;

    private final JavaBeanPropertyDescriptor isSetFieldPropertyDescriptor;

    public XmlBeanPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
                                     HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer, BeanContainer beanContainer,
                                     DestBeanCreator destBeanCreator) {

        fieldPropertyDescriptor = new JavaBeanPropertyDescriptor(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer,
                                                                 destDeepIndexHintContainer, beanContainer, destBeanCreator);

        isSetFieldPropertyDescriptor = new JavaBeanPropertyDescriptor(clazz, getIsSetFieldName(fieldName), isIndexed, index,
                                                                      srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);
    }

    public Class<?> getPropertyType() {
        return fieldPropertyDescriptor.getPropertyType();
    }

    public Object getPropertyValue(Object bean) {
        return isFieldSet(bean) ? fieldPropertyDescriptor.getPropertyValue(bean) : null;
    }

    public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
        fieldPropertyDescriptor.setPropertyValue(bean, value, fieldMap);
    }

    private boolean isFieldSet(Object bean) {

        try {
            final Boolean isSetField = (Boolean)isSetFieldPropertyDescriptor.getPropertyValue(bean);
            return isSetField.booleanValue();

        } catch (Throwable e) {
            // The isSetField will not be present for all fields eg. Class.name -
            // that's ok.
            return true;
        }
    }

    private String getIsSetFieldName(final String fieldName) {

        if (fieldName == null) {
            return null;
        }

        // Does the field use dot notation eg. parent.child.field
        int lastDotIndex = fieldName.lastIndexOf(DOT);
        if ((lastDotIndex < 0) || (lastDotIndex == (fieldName.length() - 1))) {
            // No just return the setfield field name
            return IS_SET_PREFIX + fieldName;
        }

        // yes - return the parent.child.setfield field name
        return fieldName.substring(0, lastDotIndex + 1) + IS_SET_PREFIX + fieldName.substring(lastDotIndex + 1);
    }

    public Class<?> genericType() {
        return null;
    }

}
