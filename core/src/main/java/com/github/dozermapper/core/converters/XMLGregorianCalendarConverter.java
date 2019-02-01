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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.MappingException;

import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting Supported Data Types to XMLGregorianCalendar.
 * <p>
 * Supported source data types include
 * <ul>
 * <li>java.util.Date</li>
 * <li>java.sql.Date</li>
 * <li>java.sql.Time</li>
 * <li>java.sql.Timestamp</li>
 * <li>java.util.Calendar</li>
 * <li>java.lang.String</li>
 * <li>any objects that return a number of milliseconds applicable to java.lang.Long format in their toString() form</li>
 * </ul>
 * <p>
 * Only intended for internal use.
 */
public class XMLGregorianCalendarConverter implements Converter {

    private DateFormat dateFormat;

    public XMLGregorianCalendarConverter(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Cache the DatatypeFactory because newInstance is very expensive.
     */
    private static DatatypeFactory dataTypeFactory;

    /**
     * Returns a new instance of DatatypeFactory, or the cached one if previously created.
     *
     * @return instance of DatatypeFactory
     */
    private static DatatypeFactory dataTypeFactory() {
        if (dataTypeFactory == null) {
            try {
                dataTypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new MappingException(e);
            }
        }
        return dataTypeFactory;
    }

    /**
     * {@inheritDoc}
     */
    public Object convert(Class destClass, Object srcObj) {
        Class sourceClass = srcObj.getClass();
        Calendar result = new GregorianCalendar();

        if (java.util.Date.class.isAssignableFrom(sourceClass)) {
            // Date --> XMLGregorianCalendar
            result.setTime(java.util.Date.class.cast(srcObj));
        } else if (Calendar.class.isAssignableFrom(sourceClass)) {
            // Calendar --> XMLGregorianCalendar
            Calendar c = Calendar.class.cast(srcObj);
            result.setTime(c.getTime());
            result.setTimeZone(c.getTimeZone());
        } else if (XMLGregorianCalendar.class.isAssignableFrom(sourceClass)) {
            result = XMLGregorianCalendar.class.cast(srcObj).toGregorianCalendar();
        } else if (dateFormat != null && String.class.isAssignableFrom(sourceClass)) {
            if ("".equals(String.class.cast(srcObj))) {
                return null;
            }

            try {
                long time = dateFormat.parse(String.class.cast(srcObj)).getTime();
                result.setTime(new Date(time));
            } catch (ParseException e) {
                throw new ConversionException("Unable to parse source object using specified date format", e);
            }
        } else {
            try {
                long time = Long.parseLong(srcObj.toString());
                result.setTime(new Date(time));
            } catch (NumberFormatException e) {
                throw new ConversionException("Unable to determine time in millis of source object", e);
            }
        }

        if (dateFormat != null && String.class.isAssignableFrom(destClass)) {
            return dateFormat.format(result.getTime());
        }

        return dataTypeFactory().newXMLGregorianCalendar(GregorianCalendar.class.cast(result));
    }
}
