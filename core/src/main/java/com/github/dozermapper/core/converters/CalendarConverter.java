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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting Supported Data Types to Calendar. Supported source data types include Date, Calendar,
 * String, Objects that return a long from their toString(). Only intended for internal use.
 */
public class CalendarConverter implements Converter {

    private DateFormat dateFormat;

    public CalendarConverter(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Object convert(Class destClass, Object srcObj) {
        Calendar result = new GregorianCalendar();
        Class srcFieldClass = srcObj.getClass();
        // Convert from Date to Calendar
        if (java.util.Date.class.isAssignableFrom(srcFieldClass)) {
            result.setTime((java.util.Date)srcObj);
        } else if (Calendar.class.isAssignableFrom(srcFieldClass)) {
            //  Convert from Calendar to Calendar
            Calendar c = (Calendar)srcObj;
            result.setTime(c.getTime());
            result.setTimeZone(c.getTimeZone());
        } else if (XMLGregorianCalendar.class.isAssignableFrom(srcFieldClass)) {
            Calendar c = ((XMLGregorianCalendar)srcObj).toGregorianCalendar();
            result.setTime(c.getTime());
            result.setTimeZone(c.getTimeZone());
        } else if (dateFormat != null && String.class.isAssignableFrom(srcFieldClass)) {
            // String to Calendar
            try {
                result.setTime(new Date(dateFormat.parse((String)srcObj).getTime()));
            } catch (ParseException e) {
                throw new ConversionException("Unable to parse source object using specified date format", e);
            }
        } else {
            // Default conversion
            try {
                result.setTime(new Date(Long.parseLong(srcObj.toString())));
            } catch (NumberFormatException e) {
                throw new ConversionException("Unable to determine time in millis of source object", e);
            }
        }

        // Calendar to String
        if (dateFormat != null && String.class.isAssignableFrom(destClass)) {
            return dateFormat.format(result.getTime());
        }

        return result;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }
}
