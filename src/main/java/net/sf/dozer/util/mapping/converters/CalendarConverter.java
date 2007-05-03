/*
 * Copyright 2005-2007 the original author or authors.
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
 package net.sf.dozer.util.mapping.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting from Supported Data Types --> Calendar.
 * Supported source data types include Date, Calendar, String, Objects that return a long from their toString()
 * Only intended for internal use. 
 * 
 * @author tierney.matt
 */
public class CalendarConverter implements Converter {

  private DateFormat dateFormat;

  public CalendarConverter(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  public Object convert(Class destClass, Object sourceObj) {
    Calendar result = new GregorianCalendar();
    Class sourceFieldClass = sourceObj.getClass();
    //Convert from Date to Calendar
    if (java.util.Date.class.isAssignableFrom(sourceFieldClass)) {
      result.setTime( (java.util.Date) sourceObj);
    }
    //Convert from Calendar to Calendar
    else if (Calendar.class.isAssignableFrom(sourceFieldClass)) {
      Calendar c = (Calendar) sourceObj;
      result.setTime(c.getTime());
    }
    //String to Calendar
    else if (dateFormat != null && String.class.isAssignableFrom(sourceFieldClass)) {
      try {
        result.setTime(new Date(dateFormat.parse( (String) sourceObj).getTime()));
      } catch (ParseException e) {
        throw new ConversionException("Unable to parse source object using specified date format", e);
      }
    //Default conversion
    } else {
        try {
          result.setTime(new Date(Long.parseLong(sourceObj.toString())));
        }
        catch (NumberFormatException e) {
          throw new ConversionException("Unable to determine time in millis of source object", e);
        }
    }
    return result;
  }

  public DateFormat getDateFormat() {
    return dateFormat;
  }
}
