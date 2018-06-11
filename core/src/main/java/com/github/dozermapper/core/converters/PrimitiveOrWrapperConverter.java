/*
 * Copyright 2005-2018 Dozer Project
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.lang3.ClassUtils;

/**
 * Internal class for converting between wrapper types(including primitives). Only intended for internal use.
 */
public class PrimitiveOrWrapperConverter {

    private static final Map<Class, Converter> CONVERTER_MAP = new HashMap<>();

    static {
        CONVERTER_MAP.put(Integer.class, new IntegerConverter());
        CONVERTER_MAP.put(Double.class, new DoubleConverter());
        CONVERTER_MAP.put(Short.class, new ShortConverter());
        CONVERTER_MAP.put(Character.class, new CharacterConverter());
        CONVERTER_MAP.put(Long.class, new LongConverter());
        CONVERTER_MAP.put(Boolean.class, new BooleanConverter());
        CONVERTER_MAP.put(Byte.class, new ByteConverter());
        CONVERTER_MAP.put(Float.class, new FloatConverter());
        CONVERTER_MAP.put(BigDecimal.class, new BigDecimalConverter());
        CONVERTER_MAP.put(BigInteger.class, new BigIntegerConverter());
        CONVERTER_MAP.put(Class.class, new ClassConverter());
    }

    private final BeanContainer beanContainer;

    public PrimitiveOrWrapperConverter(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public Object convert(Object srcFieldValue, Class destFieldClass, DateFormatContainer dateFormatContainer) {
        return convert(srcFieldValue, destFieldClass, dateFormatContainer, null, null);
    }

    public Object convert(Object srcFieldValue, Class destFieldClass, DateFormatContainer dateFormatContainer, String destFieldName, Object destObj) {
        if (srcFieldValue == null || destFieldClass == null || (srcFieldValue.equals("") && !destFieldClass.equals(String.class))) {
            return null;
        }
        Converter converter = getPrimitiveOrWrapperConverter(destFieldClass, dateFormatContainer, destFieldName, destObj);
        try {
            return converter.convert(destFieldClass, unwrapSrcFieldValue(srcFieldValue));
        } catch (org.apache.commons.beanutils.ConversionException e) {
            throw new com.github.dozermapper.core.converters.ConversionException(e);
        }
    }

    private Object unwrapSrcFieldValue(Object srcFieldValue) {
        if (JAXBElement.class.isAssignableFrom(srcFieldValue.getClass())) {
            return JAXBElement.class.cast(srcFieldValue).getValue();
        }
        return srcFieldValue;
    }

    private Converter getPrimitiveOrWrapperConverter(Class destClass, DateFormatContainer dateFormatContainer, String destFieldName, Object destObj) {
        if (String.class.equals(destClass)) {
            return new StringConverter(dateFormatContainer);
        }

        Converter result = CONVERTER_MAP.get(ClassUtils.primitiveToWrapper(destClass));

        if (result == null) {
            if (java.util.Date.class.isAssignableFrom(destClass)) {
                result = new DateConverter(dateFormatContainer.getDateFormat());
            } else if (Calendar.class.isAssignableFrom(destClass)) {
                result = new CalendarConverter(dateFormatContainer.getDateFormat());
            } else if (XMLGregorianCalendar.class.isAssignableFrom(destClass)) {
                result = new XMLGregorianCalendarConverter(dateFormatContainer.getDateFormat());
            } else if (MappingUtils.isEnumType(destClass)) {
                result = new EnumConverter();
            } else if (JAXBElement.class.isAssignableFrom(destClass) && destFieldName != null) {
                result = new JAXBElementConverter(destObj.getClass().getCanonicalName(), destFieldName, dateFormatContainer.getDateFormat(), beanContainer);
            }
        }
        return result == null ? new StringConstructorConverter(dateFormatContainer) : result;
    }

    public boolean accepts(Class<?> aClass) {
        return aClass.isPrimitive()
               || Number.class.isAssignableFrom(aClass)
               || String.class.equals(aClass)
               || Character.class.equals(aClass)
               || Boolean.class.equals(aClass)
               || java.util.Date.class.isAssignableFrom(aClass)
               || java.util.Calendar.class.isAssignableFrom(aClass)
               || aClass.isEnum();
    }

}
