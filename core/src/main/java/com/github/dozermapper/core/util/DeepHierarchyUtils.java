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
package com.github.dozermapper.core.util;

import java.util.Collection;
import java.util.StringTokenizer;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.propertydescriptor.DozerPropertyDescriptor;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

public final class DeepHierarchyUtils {

    private DeepHierarchyUtils() {

    }

    // Copy-paste from GetterSetterPropertyDescriptor
    public static Object getDeepFieldValue(Object srcObj, String fieldName, boolean isIndexed, int index, HintContainer srcDeepIndexHintContainer,
                                           BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        // follow deep field hierarchy. If any values are null along the way, then return null
        Object parentObj = srcObj;
        Object hierarchyValue = parentObj;
        DozerPropertyDescriptor[] hierarchy = getDeepFieldHierarchy(srcObj.getClass(), fieldName, srcDeepIndexHintContainer, beanContainer,
                                                                    destBeanCreator, propertyDescriptorFactory);

        for (DozerPropertyDescriptor hierarchyElement : hierarchy) {
            hierarchyValue = hierarchyElement.getPropertyValue(parentObj);
            parentObj = hierarchyValue;
            if (hierarchyValue == null) {
                break;
            }
        }

        if (isIndexed) {
            hierarchyValue = MappingUtils.getIndexedValue(hierarchyValue, index);
        }

        return hierarchyValue;
    }

    public static Class<?> getDeepFieldType(Class<?> clazz, String fieldName, HintContainer deepIndexHintContainer, BeanContainer beanContainer,
                                            DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        // follow deep field hierarchy. If any values are null along the way, then return null
        DozerPropertyDescriptor[] hierarchy = getDeepFieldHierarchy(clazz, fieldName, deepIndexHintContainer, beanContainer, destBeanCreator, propertyDescriptorFactory);
        return hierarchy[hierarchy.length - 1].getPropertyType();
    }

    public static Class<?> getDeepGenericType(Class<?> clazz, String fieldName, HintContainer deepIndexHintContainer, BeanContainer beanContainer,
                                              DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        // follow deep field hierarchy. If any values are null along the way, then return null
        DozerPropertyDescriptor[] hierarchy = getDeepFieldHierarchy(clazz, fieldName, deepIndexHintContainer, beanContainer, destBeanCreator, propertyDescriptorFactory);
        return hierarchy[hierarchy.length - 1].genericType();
    }

    private static DozerPropertyDescriptor[] getDeepFieldHierarchy(Class<?> parentClass, String field, HintContainer deepIndexHintContainer,
                                                                   BeanContainer beanContainer, DestBeanCreator destBeanCreator,
                                                                   PropertyDescriptorFactory propertyDescriptorFactory) {
        if (!MappingUtils.isDeepMapping(field)) {
            MappingUtils.throwMappingException("Field does not contain deep field delimiter");
        }

        StringTokenizer toks = new StringTokenizer(field, DozerConstants.DEEP_FIELD_DELIMITER);
        Class<?> latestClass = parentClass;
        DozerPropertyDescriptor[] hierarchy = new DozerPropertyDescriptor[toks.countTokens()];
        int index = 0;
        int hintIndex = 0;
        while (toks.hasMoreTokens()) {
            String aFieldName = toks.nextToken();
            String theFieldName = aFieldName;
            int collectionIndex = -1;

            if (aFieldName.contains("[")) {
                theFieldName = aFieldName.substring(0, aFieldName.indexOf("["));
                collectionIndex = Integer.parseInt(aFieldName.substring(aFieldName.indexOf("[") + 1, aFieldName.indexOf("]")));
            }

            DozerPropertyDescriptor propDescriptor = propertyDescriptorFactory.getPropertyDescriptor(latestClass, null, null, null, null, false,
                                                                                                     collectionIndex > -1, collectionIndex, theFieldName, null,
                                                                                                     false, null, null, null,
                                                                                                     null, beanContainer,
                                                                                                     destBeanCreator); //we can pass null as a hint container -
                                                                                                                        // if genericType return null - we will use hintContainer
                                                                                                                        // in the underlying if
            if (propDescriptor == null) {
                MappingUtils.throwMappingException("Exception occurred determining deep field hierarchy for Class --> "
                                                   + parentClass.getName() + ", Field --> " + field + ".  Unable to determine property descriptor for Class --> "
                                                   + latestClass.getName() + ", Field Name: " + aFieldName);
            }

            latestClass = propDescriptor.getPropertyType();
            if (toks.hasMoreTokens()) {
                if (latestClass.isArray()) {
                    latestClass = latestClass.getComponentType();
                } else if (Collection.class.isAssignableFrom(latestClass)) {
                    Class<?> genericType = propDescriptor.genericType();

                    if (genericType == null && deepIndexHintContainer == null) {
                        MappingUtils
                                .throwMappingException(
                                        "Hint(s) or Generics not specified.  Hint(s) or Generics must be specified for deep mapping with indexed field(s). "
                                        + "Exception occurred determining deep field hierarchy for Class --> "
                                        + parentClass.getName()
                                        + ", Field --> "
                                        + field
                                        + ".  Unable to determine property descriptor for Class --> "
                                        + latestClass.getName() + ", Field Name: " + aFieldName);
                    }
                    if (genericType != null) {
                        latestClass = genericType;
                    } else {
                        latestClass = deepIndexHintContainer.getHint(hintIndex);
                        hintIndex += 1;
                    }
                }
            }
            hierarchy[index++] = propDescriptor;
        }

        return hierarchy;
    }
}
