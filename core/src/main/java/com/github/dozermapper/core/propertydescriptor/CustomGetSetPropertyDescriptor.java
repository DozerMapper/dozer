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

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

/**
 * Internal class used to read and write values for fields that have an explicitly specified getter or setter method.
 * Only intended for internal use.
 */
public class CustomGetSetPropertyDescriptor extends JavaBeanPropertyDescriptor {

    private final String customSetMethod;
    private final String customGetMethod;

    private SoftReference<Method> writeMethod;
    private SoftReference<Method> readMethod;

    public CustomGetSetPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index, String customSetMethod,
                                          String customGetMethod, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer,
                                          BeanContainer beanContainer, DestBeanCreator destBeanCreator) {
        super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);
        this.customSetMethod = customSetMethod;
        this.customGetMethod = customGetMethod;
    }

    @Override
    public Method getWriteMethod() throws NoSuchMethodException {
        if (writeMethod == null || writeMethod.get() == null) {
            if (customSetMethod != null && !MappingUtils.isDeepMapping(fieldName)) {
                Method method = ReflectionUtils.findAMethod(clazz, customSetMethod, beanContainer);
                writeMethod = new SoftReference<>(method);
            } else {
                return super.getWriteMethod();
            }
        }
        return writeMethod.get();
    }

    @Override
    protected Method getReadMethod() throws NoSuchMethodException {
        if (readMethod == null || readMethod.get() == null) {
            if (customGetMethod != null) {
                Method method = ReflectionUtils.findAMethod(clazz, customGetMethod, beanContainer);
                readMethod = new SoftReference<>(method);
            } else {
                return super.getReadMethod();
            }
        }
        return readMethod.get();
    }

    @Override
    protected String getSetMethodName() throws NoSuchMethodException {
        return customSetMethod != null ? customSetMethod : super.getSetMethodName();
    }

    @Override
    protected boolean isCustomSetMethod() {
        return customSetMethod != null;
    }

}
