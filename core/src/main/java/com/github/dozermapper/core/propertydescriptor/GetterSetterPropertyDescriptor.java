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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.util.BridgedMethodFinder;
import com.github.dozermapper.core.util.CollectionUtils;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;
import com.github.dozermapper.core.util.TypeResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class used to read and write values for fields that have a getter and setter method. This class encapsulates
 * underlying dozer specific logic such as index mapping and deep mapping for reading and writing field values. Only
 * intended for internal use.
 */
public abstract class GetterSetterPropertyDescriptor extends AbstractPropertyDescriptor {

    private final Logger log = LoggerFactory.getLogger(GetterSetterPropertyDescriptor.class);

    private Class<?> propertyType;
    protected final BeanContainer beanContainer;
    protected final DestBeanCreator destBeanCreator;

    public GetterSetterPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
                                          HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer,
                                          BeanContainer beanContainer, DestBeanCreator destBeanCreator) {
        super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
        this.beanContainer = beanContainer;
        this.destBeanCreator = destBeanCreator;
    }

    protected abstract Method getWriteMethod() throws NoSuchMethodException;

    protected abstract Method getReadMethod() throws NoSuchMethodException;

    protected abstract String getSetMethodName() throws NoSuchMethodException;

    protected abstract boolean isCustomSetMethod();

    public Class<?> getWriteMethodPropertyType() {
        try {
            return getWriteMethod().getParameterTypes()[0];
        } catch (Exception e) {
            MappingUtils.throwMappingException(e);
            return null;
        }
    }

    public Class<?> getPropertyType() {
        if (propertyType == null) {
            propertyType = determinePropertyType();
        }
        return propertyType;
    }

    public Object getPropertyValue(Object bean) {
        Object result;
        if (MappingUtils.isDeepMapping(fieldName)) {
            result = getDeepSrcFieldValue(bean);
        } else {
            result = invokeReadMethod(bean);
            if (isIndexed) {
                result = MappingUtils.getIndexedValue(result, index);
            }
        }
        return result;
    }

    public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
        if (MappingUtils.isDeepMapping(fieldName)) {
            writeDeepDestinationValue(bean, value, fieldMap);
        } else {
            if (!getPropertyType().isPrimitive() || value != null) {
                //First check if value is indexed. If it's null, then the new array will be created
                if (isIndexed) {
                    writeIndexedValue(bean, value);
                } else {
                    // Check if dest value is already set and is equal to src value. If true, no need to rewrite the dest value
                    try {
                        if (getPropertyValue(bean) == value && !isIndexed) {
                            return;
                        }
                    } catch (Exception e) {
                        // if we failed to read the value, assume we must write, and continue...
                    }
                    invokeWriteMethod(bean, value);
                }
            }
        }
    }

    private Object getDeepSrcFieldValue(Object srcObj) {
        // follow deep field hierarchy. If any values are null along the way, then return null
        Object parentObj = srcObj;
        Object hierarchyValue = parentObj;
        DeepHierarchyElement[] hierarchy = getDeepFieldHierarchy(srcObj, srcDeepIndexHintContainer);
        int size = hierarchy.length;
        for (int i = 0; i < size; i++) {
            DeepHierarchyElement hierarchyElement = hierarchy[i];
            PropertyDescriptor pd = hierarchyElement.getPropDescriptor();
            // If any fields in the deep hierarchy are indexed, get actual value within the collection at the specified index
            if (hierarchyElement.getIndex() > -1) {
                hierarchyValue = MappingUtils.getIndexedValue(ReflectionUtils.invoke(pd.getReadMethod(), hierarchyValue, null),
                                                              hierarchyElement.getIndex());
            } else {
                hierarchyValue = ReflectionUtils.invoke(pd.getReadMethod(), parentObj, null);
            }
            parentObj = hierarchyValue;
            if (hierarchyValue == null) {
                break;
            }
        }

        // If dest field is indexed, get actual value within the collection at the specified index
        if (isIndexed) {
            hierarchyValue = MappingUtils.getIndexedValue(hierarchyValue, index);
        }

        return hierarchyValue;
    }

    protected void writeDeepDestinationValue(Object destObj, Object destFieldValue, FieldMap fieldMap) {
        // follow deep field hierarchy. If any values are null along the way, then create a new instance
        DeepHierarchyElement[] hierarchy = getDeepFieldHierarchy(destObj, fieldMap.getDestDeepIndexHintContainer());
        // first, iteratate through hierarchy and instantiate any objects that are null
        Object parentObj = destObj;
        int hierarchyLength = hierarchy.length - 1;
        int hintIndex = 0;
        for (int i = 0; i < hierarchyLength; i++) {
            DeepHierarchyElement hierarchyElement = hierarchy[i];
            PropertyDescriptor pd = hierarchyElement.getPropDescriptor();
            Object value = ReflectionUtils.invoke(pd.getReadMethod(), parentObj, null);
            Class<?> clazz;
            Class<?> collectionEntryType;
            if (value == null) {
                clazz = pd.getPropertyType();
                if (clazz.isInterface() && (i + 1) == hierarchyLength && fieldMap.getDestHintContainer() != null) {
                    // before setting the property on the destination object we should check for a destination hint. need to know
                    // that we are at the end of the line determine the property type
                    clazz = fieldMap.getDestHintContainer().getHint();
                }
                Object o = null;
                if (clazz.isArray()) {
                    o = MappingUtils.prepareIndexedCollection(clazz, null, destBeanCreator.create(clazz.getComponentType()),
                                                              hierarchyElement.getIndex());
                } else if (Collection.class.isAssignableFrom(clazz)) {

                    Class<?> genericType = ReflectionUtils.determineGenericsType(parentObj.getClass(), pd);
                    if (genericType != null) {
                        collectionEntryType = genericType;
                    } else {
                        collectionEntryType = fieldMap.getDestDeepIndexHintContainer().getHint(hintIndex);
                        //hint index is used to handle multiple hints
                        hintIndex += 1;
                    }

                    o = MappingUtils.prepareIndexedCollection(clazz, null, destBeanCreator.create(collectionEntryType), hierarchyElement
                            .getIndex());
                } else {
                    try {
                        o = destBeanCreator.create(clazz);
                    } catch (Exception e) {
                        //lets see if they have a factory we can try as a last ditch. If not...throw the exception:
                        if (fieldMap.getClassMap().getDestClassBeanFactory() != null) {
                            o = destBeanCreator.create(new BeanCreationDirective(null, fieldMap.getClassMap().getSrcClassToMap(), clazz, clazz, fieldMap.getClassMap()
                                    .getDestClassBeanFactory(), fieldMap.getClassMap().getDestClassBeanFactoryId(), null, null));
                        } else {
                            MappingUtils.throwMappingException(e);
                        }
                    }
                }

                ReflectionUtils.invoke(pd.getWriteMethod(), parentObj, new Object[] {o});
                value = ReflectionUtils.invoke(pd.getReadMethod(), parentObj, null);
            }

            //Check to see if collection needs to be resized
            if (MappingUtils.isSupportedCollection(value.getClass())) {
                int currentSize = CollectionUtils.getLengthOfCollection(value);
                if (currentSize < hierarchyElement.getIndex() + 1) {
                    collectionEntryType = pd.getPropertyType().getComponentType();

                    if (collectionEntryType == null) {
                        collectionEntryType = ReflectionUtils.determineGenericsType(parentObj.getClass(), pd);

                        // if the target list is a List that doesn't have type specified, we can
                        // try to use the deep-index-hint.  If the list has more than 1 element, there is no
                        // way to tell which class to use, so we'll just try the first one.
                        if (collectionEntryType == null) {
                            if (log.isWarnEnabled()) {
                                log.warn(fieldName + " is in a Collection with an unspecified type.");
                            }
                            if (destDeepIndexHintContainer != null && destDeepIndexHintContainer.getHints() != null
                                && destDeepIndexHintContainer.getHints().size() > 0) {
                                collectionEntryType = destDeepIndexHintContainer.getHints().get(0);
                                if (log.isWarnEnabled()) {
                                    log.warn("Using deep-index-hint to predict containing Collection type for field "
                                             + fieldName + " to be " + collectionEntryType);
                                }
                            }
                        }
                    }

                    value = MappingUtils.prepareIndexedCollection(pd.getPropertyType(), value, destBeanCreator.create(collectionEntryType), hierarchyElement.getIndex());
                    //value = MappingUtils.prepareIndexedCollection(pd.getPropertyType(), value, DestBeanCreator.create(collectionEntryType), hierarchyElement.getIndex());
                    ReflectionUtils.invoke(pd.getWriteMethod(), parentObj, new Object[] {value});
                }
            }

            if (value != null && value.getClass().isArray()) {
                parentObj = Array.get(value, hierarchyElement.getIndex());
            } else if (value != null && Collection.class.isAssignableFrom(value.getClass())) {
                parentObj = MappingUtils.getIndexedValue(value, hierarchyElement.getIndex());
            } else {
                parentObj = value;
            }
        }
        // second, set the very last field in the deep hierarchy
        PropertyDescriptor pd = hierarchy[hierarchy.length - 1].getPropDescriptor();

        Class<?> type;
        // For one-way mappings there could be no read method
        if (pd.getReadMethod() != null) {
            type = pd.getReadMethod().getReturnType();
        } else {
            type = pd.getWriteMethod().getParameterTypes()[0];
        }

        if (!type.isPrimitive() || destFieldValue != null) {
            if (!isIndexed) {
                Method method = null;
                if (!isCustomSetMethod()) {
                    method = pd.getWriteMethod();
                } else {
                    try {
                        method = ReflectionUtils.findAMethod(parentObj.getClass(), getSetMethodName(), beanContainer);
                    } catch (NoSuchMethodException e) {
                        MappingUtils.throwMappingException(e);
                    }
                }

                ReflectionUtils.invoke(method, parentObj, new Object[] {destFieldValue});
            } else {
                writeIndexedValue(parentObj, destFieldValue);
            }
        }
    }

    protected Object invokeReadMethod(Object target) {
        Object result = null;
        try {
            result = ReflectionUtils.invoke(getReadMethod(), target, null);
        } catch (NoSuchMethodException e) {
            MappingUtils.throwMappingException(e);
        }
        return result;
    }

    protected void invokeWriteMethod(Object target, Object value) {
        try {
            ReflectionUtils.invoke(getWriteMethod(), target, new Object[] {value});
        } catch (NoSuchMethodException e) {
            MappingUtils.throwMappingException(e);
        }
    }

    private DeepHierarchyElement[] getDeepFieldHierarchy(Object obj, HintContainer deepIndexHintContainer) {
        return ReflectionUtils.getDeepFieldHierarchy(obj.getClass(), fieldName, deepIndexHintContainer);
    }

    private void writeIndexedValue(Object destObj, Object destFieldValue) {
        Object existingValue = invokeReadMethod(destObj);
        Object indexedValue = MappingUtils.prepareIndexedCollection(getPropertyType(), existingValue, destFieldValue, index);
        invokeWriteMethod(destObj, indexedValue);
    }

    private Class determinePropertyType() {
        Method readMethod = getBridgedReadMethod();
        Method writeMethod = getBridgedWriteMethod();

        Class returnType = null;

        try {
            returnType = TypeResolver.resolvePropertyType(clazz, readMethod, writeMethod);
        } catch (Exception ignore) {
        }

        if (returnType != null) {
            return returnType;
        }

        if (readMethod == null && writeMethod == null) {
            throw new MappingException("No read or write method found for field (" + fieldName
                                       + ") in class (" + clazz + ")");
        }

        if (readMethod == null) {
            return determineByWriteMethod(writeMethod);
        } else {
            try {
                return readMethod.getReturnType();
            } catch (Exception e) {
                // let us try the set method - the field might have inacessible 'get' method
                return determineByWriteMethod(writeMethod);
            }
        }
    }

    private Class determineByWriteMethod(Method writeMethod) {
        try {
            return writeMethod.getParameterTypes()[0];
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    private Method getBridgedReadMethod() {
        try {
            return BridgedMethodFinder.findMethod(getReadMethod(), clazz);
        } catch (Exception ignore) {
        }
        return null;
    }

    private Method getBridgedWriteMethod() {
        try {
            return BridgedMethodFinder.findMethod(getWriteMethod(), clazz);
        } catch (Exception ignore) {
        }
        return null;
    }

    public Class<?> genericType() {
        Class<?> genericType = null;
        try {
            Method method = getWriteMethod();
            genericType = ReflectionUtils.determineGenericsType(clazz, method, false);
        } catch (NoSuchMethodException e) {
            log.warn("The destination object: {} does not have a write method for property : {}", e);
        }

        return genericType;
    }

}
