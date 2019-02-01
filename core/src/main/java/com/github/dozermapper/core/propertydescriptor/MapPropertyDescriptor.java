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

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

/**
 * Internal class used to read and write values for Map backed objects that use key/value pairs. The specified "key" is
 * used when invoking getter/setter. It is assumed that Map setter method has two parameters (for "key" and "value"),
 * but getter method one parameter (for "key").
 * <p>
 * Overloaded methods are supported. Map class can have two set methods with different signatures, but class will
 * choose the one with appropriate number of parameters.
 * <p>
 * Only intended for internal use.
 */
public class MapPropertyDescriptor extends GetterSetterPropertyDescriptor {

    private final String setMethodName;
    private final String getMethodName;
    private final String key;

    private SoftReference<Method> writeMethod;
    private SoftReference<Method> readMethod;

    public MapPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index, String setMethod, String getMethod,
                                 String key, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer,
                                 BeanContainer beanContainer, DestBeanCreator destBeanCreator) {
        super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);
        this.setMethodName = setMethod;
        this.getMethodName = getMethod;
        this.key = key;
    }

    @Override
    public Method getWriteMethod() {
        if (MappingUtils.isBlankOrNull(setMethodName)) {
            throw new MappingException("Custom Map set method not specified for field mapping to class: " + clazz
                                       + ".  Perhaps the map-set-method wasn't specified in the dozer mapping file?");
        }
        if (writeMethod == null || writeMethod.get() == null) {
            Method method = findMapMethod(clazz, setMethodName, 2);
            writeMethod = new SoftReference<>(method);
        }
        return writeMethod.get();
    }

    private Method findMapMethod(Class clazz, String methodName, int parameterCount) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()) && method.getParameterTypes().length == parameterCount) {
                return method;
            }
        }
        throw new MappingException("No map method found for class:" + clazz + " and method name:" + methodName);
    }

    @Override
    public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
        if (MappingUtils.isDeepMapping(fieldName)) {
            writeDeepDestinationValue(bean, value, fieldMap);
        } else {
            if (!getPropertyType().isPrimitive() || value != null) {
                // Check if dest value is already set and is equal to src value. If true, no need to rewrite the dest value
                try {
                    // We should map null values to create a new key in the map
                    if (value != null && getPropertyValue(bean) == value) {
                        return;
                    }
                } catch (Exception e) {
                    // if we failed to read the value, assume we must write, and continue...
                }
                invokeWriteMethod(bean, value);
            }
        }
    }

    @Override
    protected Method getReadMethod() {
        if (MappingUtils.isBlankOrNull(getMethodName)) {
            throw new MappingException("Custom Map get method not specified for field mapping to class: " + clazz
                                       + ".  Perhaps the map-get-method wasn't specified in the dozer mapping file?");
        }
        if (readMethod == null || readMethod.get() == null) {
            Method method = findMapMethod(clazz, getMethodName, 1);
            readMethod = new SoftReference<>(method);
        }
        return readMethod.get();
    }

    @Override
    protected String getSetMethodName() {
        return setMethodName;
    }

    @Override
    protected boolean isCustomSetMethod() {
        return true;
    }

    @Override
    protected void invokeWriteMethod(Object target, Object value) {
        if (key == null) {
            throw new MappingException("key must be specified");
        }

        ReflectionUtils.invoke(getWriteMethod(), target, new Object[] {key, value});
    }

    @Override
    protected Object invokeReadMethod(Object target) {
        if (key == null) {
            throw new MappingException("key must be specified");
        }

        Object result = ReflectionUtils.invoke(getReadMethod(), target, new Object[] {key});
        return result;
    }
}
