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

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting Supported Data Types to String. Uses date formatter for Date and Calendar source
 * objects. Calls toString() on the source object for all other types. Only intended for internal use.
 */
public class StringConverter implements Converter {

    private DateFormatContainer dateFormatContainer;

    public StringConverter(DateFormatContainer dateFormatContainer) {
        this.dateFormatContainer = dateFormatContainer;
    }

    public Object convert(Class destClass, Object srcObj) {
        Class srcClass = srcObj.getClass();
        if (hasDateFormat()) {
            if (Date.class.isAssignableFrom(srcClass)) {
                return dateFormatContainer.getDateFormat().format((Date)srcObj);
            }
            if (Calendar.class.isAssignableFrom(srcClass)) {
                return dateFormatContainer.getDateFormat().format(((Calendar)srcObj).getTime());
            } else if (TemporalAccessor.class.isAssignableFrom(srcClass)) {
                return dateFormatContainer.getDateTimeFormatter().format((TemporalAccessor)srcObj);
            } else {
                return srcObj.toString();
            }
        } else {
            return srcObj.toString();
        }
    }

    /**
     * Whether date format is provided or not
     *
     * @return true - date format provided. Otherwise false.
     */
    private boolean hasDateFormat() {
        return dateFormatContainer != null && dateFormatContainer.isPresent();
    }
}
