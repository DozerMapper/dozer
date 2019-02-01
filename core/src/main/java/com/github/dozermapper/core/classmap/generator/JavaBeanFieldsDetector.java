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
package com.github.dozermapper.core.classmap.generator;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import com.github.dozermapper.core.util.ReflectionUtils;

public class JavaBeanFieldsDetector implements BeanFieldsDetector {
    public boolean accepts(Class<?> clazz) {
        return true;
    }

    public Set<String> getReadableFieldNames(Class<?> clazz) {
        Set<String> srcFieldNames = new HashSet<>();
        PropertyDescriptor[] srcProperties = ReflectionUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor srcPropertyDescriptor : srcProperties) {
            String fieldName = srcPropertyDescriptor.getName();

            if (srcPropertyDescriptor.getReadMethod() == null) {
                continue;
            }

            srcFieldNames.add(fieldName);
        }
        return srcFieldNames;
    }

    public Set<String> getWritableFieldNames(Class<?> clazz) {
        Set<String> destFieldNames = new HashSet<>();
        PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor destPropertyDescriptor : destProperties) {
            String fieldName = destPropertyDescriptor.getName();

            // If destination field does not have a write method, then skip
            if (destPropertyDescriptor.getWriteMethod() == null && ReflectionUtils.getNonStandardSetter(clazz, fieldName) == null) {
                continue;
            }

            destFieldNames.add(fieldName);
        }
        return destFieldNames;
    }
}
