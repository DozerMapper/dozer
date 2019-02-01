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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.apache.commons.beanutils.Converter;

/**
 * Base converter to Java 8 date and time types.
 * <pre>
 * Supported basic conversions:
 *  Any child of TemporalAccessor &#45;&gt; LocalDateTime
 *  String &#45;&gt; LocalDateTime
 * </pre>
 */
public abstract class AbstractJava8DateTimeConverter implements Converter {

    private final DateTimeFormatter formatter;

    AbstractJava8DateTimeConverter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Class destClass, Object srcObject) {
        Class<?> srcObjectClass = srcObject.getClass();
        try {
            if (TemporalAccessor.class.isAssignableFrom(srcObjectClass)) {
                Method method = destClass.getDeclaredMethod("from", TemporalAccessor.class);
                return method.invoke(null, (TemporalAccessor)srcObject);
            } else if (String.class.isAssignableFrom(srcObjectClass) && formatter != null) {
                Method method = destClass.getDeclaredMethod("parse", CharSequence.class, DateTimeFormatter.class);
                return method.invoke(null, srcObject, formatter);
            } else {
                throw new ConversionException(String.format("Unsupported source object type: %s", srcObjectClass), null);
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ConversionException(
                    String.format("Failed to create %s from %s", destClass.getSimpleName(), srcObject.getClass().getSimpleName()), e);
        } catch (InvocationTargetException e) {
            throw new ConversionException(
                    String.format("Failed to create %s from %s", destClass.getSimpleName(), srcObject.getClass().getSimpleName()), e.getTargetException());
        }
    }
}
