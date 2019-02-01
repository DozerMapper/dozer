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
package com.github.dozermapper.core.converters;

import java.lang.reflect.Method;
import java.text.DateFormat;

import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;

public class JAXBElementConverter implements Converter {

    /**
     * Cache the ObjectFactory because newInstance is very expensive.
     */
    private static Object objectFactory;
    private static Class<?> objectFactoryClass;

    private String destObjClass;
    private String destFieldName;
    private DateFormat dateFormat;
    private final BeanContainer beanContainer;

    public JAXBElementConverter(String destObjClass, String destFieldName, DateFormat dateFormat, BeanContainer beanContainer) {
        this.destObjClass = destObjClass;
        this.destFieldName = destFieldName;
        this.dateFormat = dateFormat;
        this.beanContainer = beanContainer;
    }

    /**
     * Returns a new instance of ObjectFactory, or the cached one if previously created.
     *
     * @param destObjClass type to convert to
     * @return instance of ObjectFactory
     */
    private static Object objectFactory(String destObjClass, BeanContainer beanContainer) {
        String objectFactoryClassName = destObjClass.substring(0, destObjClass.lastIndexOf(".")) + ".ObjectFactory";
        if (objectFactory == null || objectFactoryClass == null || !objectFactoryClass.getCanonicalName().equals(objectFactoryClassName)) {
            objectFactoryClass = MappingUtils.loadClass(objectFactoryClassName, beanContainer);
            objectFactory = ReflectionUtils.newInstance(objectFactoryClass);
        }

        return objectFactory;
    }

    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type  Data type to which this value should be converted
     * @param value The input value to be converted
     * @return The converted value
     * @throws org.apache.commons.beanutils.ConversionException if conversion cannot be performed successfully
     */
    @Override
    public Object convert(Class type, Object value) {

        Object result;
        Object factory = objectFactory(destObjClass, beanContainer);
        Class<?> factoryClass = factory.getClass();
        Class<?> destClass = value.getClass();
        Class<?> valueClass = value.getClass();

        String methodName = "create" + destObjClass.substring(destObjClass.lastIndexOf(".") + 1) + StringUtils.capitalize(destFieldName);
        Method method = null;
        try {
            method = ReflectionUtils.findAMethod(factoryClass, methodName, beanContainer);
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterClass : parameterTypes) {
                if (!valueClass.equals(parameterClass)) {
                    destClass = parameterClass;
                    break;
                }
            }

            Class<?>[] paramTypes = {destClass};
            method = ReflectionUtils.getMethod(factoryClass, methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            MappingUtils.throwMappingException(e);
        }

        Object param = value;
        Converter converter;
        if (java.util.Date.class.isAssignableFrom(valueClass) && !destClass.equals(XMLGregorianCalendar.class)) {
            converter = new DateConverter(dateFormat);
            param = converter.convert(destClass, param);
        } else if (java.util.Calendar.class.isAssignableFrom(valueClass) && !destClass.equals(XMLGregorianCalendar.class)) {
            converter = new CalendarConverter(dateFormat);
            param = converter.convert(destClass, param);
        } else if (XMLGregorianCalendar.class.isAssignableFrom(valueClass) || XMLGregorianCalendar.class.isAssignableFrom(destClass)) {
            converter = new XMLGregorianCalendarConverter(dateFormat);
            param = converter.convert(destClass, param);
        }

        Object[] paramValues = {param};
        result = ReflectionUtils.invoke(method, factory, paramValues);
        return result;
    }

    /**
     * Resolve the beanId associated to destination field name
     *
     * @return bean id
     */
    public String getBeanId() {
        Class<?> factoryClass = objectFactory(destObjClass, beanContainer).getClass();
        Class<?> destClass = null;
        String methodName = "create" + destObjClass.substring(destObjClass.lastIndexOf(".") + 1) + StringUtils.capitalize(destFieldName);

        try {
            Method method = ReflectionUtils.findAMethod(factoryClass, methodName, beanContainer);
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterClass : parameterTypes) {
                destClass = parameterClass;
                break;
            }
        } catch (NoSuchMethodException e) {
            MappingUtils.throwMappingException(e);
        }

        return (destClass != null) ? destClass.getCanonicalName() : null;
    }
}
