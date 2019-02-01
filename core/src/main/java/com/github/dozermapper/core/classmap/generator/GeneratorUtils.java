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

import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.DozerField;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.GenericFieldMap;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.MappingUtils;

public final class GeneratorUtils {
    private static final String CLASS = "class";
    private static final String CALLBACK = "callback";
    private static final String CALLBACKS = "callbacks";

    private GeneratorUtils() {
    }

    public static boolean shouldIgnoreField(String fieldName, Class<?> srcType, Class<?> destType, BeanContainer beanContainer) {
        if (CLASS.equals(fieldName)) {
            return true;
        }
        return (CALLBACK.equals(fieldName) || CALLBACKS.equals(fieldName))
               && (MappingUtils.isProxy(srcType, beanContainer) || MappingUtils.isProxy(destType, beanContainer));
    }

    public static void addGenericMapping(MappingType mappingType, ClassMap classMap,
                                         Configuration configuration, String srcName, String destName, BeanContainer beanContainer,
                                         DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        FieldMap fieldMap = new GenericFieldMap(classMap, beanContainer, destBeanCreator, propertyDescriptorFactory);
        DozerField srcField = new DozerField(srcName, null);
        DozerField destField = new DozerField(destName, null);
        fieldMap.setSrcField(srcField);
        fieldMap.setDestField(destField);

        switch (mappingType) {
            case FIELD_TO_FIELD:
                srcField.setAccessible(true);
                destField.setAccessible(true);
                break;
            case FIELD_TO_SETTER:
                srcField.setAccessible(true);
                break;
            case GETTER_TO_SETTER:
            default:
                break;
        }

        // add CopyByReferences per defect #1728159
        MappingUtils.applyGlobalCopyByReference(configuration, fieldMap, classMap);
        classMap.addFieldMapping(fieldMap);
    }
}
