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

import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting Supported Data Types to Integer. Only intended for internal use.
 */
public class IntegerConverter implements Converter {

    private static org.apache.commons.beanutils.converters.IntegerConverter commonsConverter = new org.apache.commons.beanutils.converters.IntegerConverter();

    @SuppressWarnings("rawtypes")
    public Object convert(Class destClass, Object srcObj) {
        // Boolean to Int not supported in apache common's int converter and this is why this class is req'd
        if (Boolean.class.isAssignableFrom(srcObj.getClass())) {
            boolean value = (Boolean)srcObj;
            return value ? 1 : 0;
        } else if (MappingUtils.isEnumType(srcObj.getClass())) {
            return ((Enum)srcObj).ordinal();
        } else {
            return commonsConverter.convert(destClass, srcObj);
        }
    }
}
