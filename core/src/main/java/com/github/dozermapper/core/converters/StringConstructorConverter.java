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

import java.lang.reflect.Constructor;

import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting String to Complex Data Types with a String constructor. Only intended for internal
 * use.
 */
public class StringConstructorConverter implements Converter {

    private StringConverter stringConverter;

    public StringConstructorConverter(DateFormatContainer dateFormatContainer) {
        this.stringConverter = new StringConverter(dateFormatContainer);
    }

    public Object convert(Class destClass, Object srcObj) {
        String result = (String)stringConverter.convert(destClass, srcObj);
        try {
            Constructor constructor = destClass.getConstructor(String.class); // TODO Check, but not catch
            return constructor.newInstance(result);
        } catch (NoSuchMethodException e) {
            // just return the string
            return result;
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }

}
