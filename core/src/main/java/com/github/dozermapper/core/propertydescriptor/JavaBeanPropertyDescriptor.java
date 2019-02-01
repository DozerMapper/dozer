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
import java.lang.reflect.Method;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Internal class used to read and write values for fields that follow the java bean spec and have corresponding
 * getter/setter methods for the field that are name accordingly. If the field does not have the necessary
 * getter/setter, an exception will be thrown. Only intended for internal use.
 */
public class JavaBeanPropertyDescriptor extends GetterSetterPropertyDescriptor {
    private PropertyDescriptor pd;
    private boolean propertyDescriptorsRefreshed;

    public JavaBeanPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
                                      HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer,
                                      BeanContainer beanContainer, DestBeanCreator destBeanCreator) {
        super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);
    }

    @Override
    public Method getWriteMethod() throws NoSuchMethodException {
        Method result = getPropertyDescriptor(destDeepIndexHintContainer).getWriteMethod();
        result = result == null ? ReflectionUtils.getNonStandardSetter(clazz, fieldName) : result;
        if (result == null) {
            result = retryMissingMethod(true);
        }

        propertyDescriptorsRefreshed = false;
        return result;
    }

    @Override
    protected String getSetMethodName() throws NoSuchMethodException {
        return getWriteMethod().getName();
    }

    @Override
    protected Method getReadMethod() throws NoSuchMethodException {
        Method result = getPropertyDescriptor(srcDeepIndexHintContainer).getReadMethod();
        if (result == null) {
            result = retryMissingMethod(false);
        }

        propertyDescriptorsRefreshed = false;
        return result;
    }

    @Override
    protected boolean isCustomSetMethod() {
        return false;
    }

    /**
     * PropertyDescriptor may lose the references to the write and read methods during
     * garbage collection. If the methods can't be found, we should retry once to
     * ensure that our PropertyDescriptor hasn't gone bad and the method really
     * isn't there.
     *
     * @param writeMethod {@code true} to look for the write method for a property,
     *                    {@code false} to look for the read method
     * @return the method or {@code null}
     * @throws NoSuchMethodException if we've already retried finding the method once
     * @see <a href="https://github.com/DozerMapper/dozer/issues/118">Dozer mapping stops working</a>
     */
    private Method retryMissingMethod(boolean writeMethod) throws NoSuchMethodException {
        if (propertyDescriptorsRefreshed) {
            throw new NoSuchMethodException(
                    "Unable to determine " + (writeMethod ? "write" : "read") +
                    " method for Field: '" + fieldName + "' in Class: " + clazz);
        } else {
            refreshPropertyDescriptors();
            return writeMethod ? getWriteMethod() : getReadMethod();
        }
    }

    /**
     * Cleans out the {@link PropertyDescriptor} cache; when suspecting that
     * our PropertyDescriptor has lost its references, we want it to be re-built
     * (instead of getting the same instance from the cache).
     */
    private void refreshPropertyDescriptors() {
        PropertyUtils.clearDescriptors();
        pd = null;
        propertyDescriptorsRefreshed = true;
    }

    private PropertyDescriptor getPropertyDescriptor(HintContainer deepIndexHintContainer) {
        if (pd == null) {
            pd = ReflectionUtils.findPropertyDescriptor(clazz, fieldName, deepIndexHintContainer);
            if (pd == null) {
                MappingUtils.throwMappingException("Property: '" + fieldName + "' not found in Class: " + clazz);
            }
        }
        return pd;
    }

}
