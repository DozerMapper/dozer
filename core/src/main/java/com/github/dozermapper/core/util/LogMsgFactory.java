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
package com.github.dozermapper.core.util;

import java.util.Collection;

import com.github.dozermapper.core.fieldmap.FieldMap;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class used to build various types of log messages. Only intended for internal use.
 */
public final class LogMsgFactory {

    private static final Logger log = LoggerFactory.getLogger(LogMsgFactory.class);

    public String createFieldMappingErrorMsg(Object srcObj, FieldMap fieldMapping, Object srcFieldValue, Object destObj) {
        String srcClassName = null;
        if (srcObj != null) {
            srcClassName = srcObj.getClass().getName();
        }

        String srcValueType;
        String srcFieldValueString = null;
        if (srcFieldValue != null) {
            srcValueType = srcFieldValue.getClass().toString();
            //1797808 - If an error occurs calling src value toString(), eat the error and still log the dozer error msg
            try {
                srcFieldValueString = srcFieldValue.toString();
            } catch (Exception e) {
                log.error("An exception occurred invoking toString() on the source field value: " + srcFieldValue.getClass().getName(), e);
                srcFieldValueString = "Unable to determine source field value";
            }
        } else {
            srcValueType = fieldMapping.getSrcFieldType();
        }

        String destClassName = null;
        if (destObj != null) {
            destClassName = destObj.getClass().getName();
        }

        String destFieldTypeName = null;
        try {
            if (destObj != null) {
                destFieldTypeName = fieldMapping.getDestFieldType(destObj.getClass()).getName();
            }
        } catch (Exception e) {
            log.warn("Unable to determine dest field type when build log.error message");
        }

        return "Field mapping error -->" + "\n  MapId: " + fieldMapping.getMapId() + "\n  Type: " + fieldMapping.getType()
               + "\n  Source parent class: " + srcClassName + "\n  Source field name: " + fieldMapping.getSrcFieldName()
               + "\n  Source field type: " + srcValueType + "\n  Source field value: " + srcFieldValueString + "\n  Dest parent class: "
               + destClassName + "\n  Dest field name: " + fieldMapping.getDestFieldName() + "\n  Dest field type: " + destFieldTypeName;
    }

    public String createFieldMappingSuccessMsg(Class<?> srcClass, Class<?> destClass, String srcFieldName, String destFieldName,
                                               Object srcFieldValue, Object destFieldValue, String classMapId) {
        String srcClassStr = MappingUtils.getClassNameWithoutPackage(srcClass);
        String destClassStr = MappingUtils.getClassNameWithoutPackage(destClass);

        return "MAPPED: " + srcClassStr + "." + srcFieldName + " --> " + destClassStr + "." + destFieldName + "    VALUES: "
               + getLogOutput(srcFieldValue) + " --> " + getLogOutput(destFieldValue) + "    MAPID: "
               + (classMapId != null ? classMapId : "");
    }

    private String getLogOutput(Object object) {
        String output = "NULL";
        if (object == null) {
            return output;
        }
        try {
            if (object.getClass().isArray() || Collection.class.isAssignableFrom(object.getClass())) {
                output = ReflectionToStringBuilder.toString(object, ToStringStyle.MULTI_LINE_STYLE);
            } else {
                output = object.toString();
            }
        } catch (RuntimeException e) {
            output = object.toString();
        }
        return output;
    }

}
