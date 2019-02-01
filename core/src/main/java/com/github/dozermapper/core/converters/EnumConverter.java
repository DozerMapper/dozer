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
import org.apache.commons.lang3.EnumUtils;

/**
 * Internal class for converting Supported Data Types to Enum. Only intended for internal use.
 */
public class EnumConverter implements Converter {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object convert(Class destClass, Object srcObj) {
        if (null == srcObj) {
            MappingUtils.throwMappingException("Cannot convert null to enum of type " + destClass);
        }

        try {
            if ((srcObj.getClass().equals(Byte.class)) || (srcObj.getClass().equals(Byte.TYPE))) {
                return EnumUtils.getEnumList(destClass).get(((Byte)srcObj).intValue());
            } else if ((srcObj.getClass().equals(Short.class)) || (srcObj.getClass().equals(Short.TYPE))) {
                return EnumUtils.getEnumList(destClass).get(((Short)srcObj).intValue());
            } else if ((srcObj.getClass().equals(Integer.class)) || (srcObj.getClass().equals(Integer.TYPE))) {
                return EnumUtils.getEnumList(destClass).get((Integer)srcObj);
            } else if ((srcObj.getClass().equals(Long.class)) || (srcObj.getClass().equals(Long.TYPE))) {
                return EnumUtils.getEnumList(destClass).get(((Long) srcObj).intValue());
            } else if (Enum.class.isAssignableFrom(srcObj.getClass())) {
                return Enum.valueOf(destClass, ((Enum)srcObj).name());
            } else {
                return Enum.valueOf(destClass, srcObj.toString());
            }
        } catch (Exception e) {
            MappingUtils.throwMappingException("Cannot convert [" + srcObj + "] to enum of type " + destClass, e);
        }
        return srcObj;
    }
}
