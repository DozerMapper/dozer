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

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting Supported Data Types --> Date.
 * Supported source data types include Date, Calendar, String, Objects that return a long from their toString().
 * Only intended for internal use. 
 * 
 * @author tierney.matt
 */
public class DateConverter implements Converter {
  private DateFormat dateFormat;

  public DateConverter(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  public Object convert(Class destClass, Object srcObj) {
    Object result = null;

    Class srcFieldClass = srcObj.getClass();
    long time = -1;
    //Calendar to Date
    if (Calendar.class.isAssignableFrom(srcFieldClass)) {
      Calendar inVal = (Calendar) srcObj;
      time = inVal.getTime().getTime();
    //Date to Date
    } else if (java.util.Date.class.isAssignableFrom(srcFieldClass)) {
        time = ( (java.util.Date) srcObj).getTime();
    //String to Date
    } else if (dateFormat != null && String.class.isAssignableFrom(srcObj.getClass())) {
        try {
          if("".equals(srcObj)){
            return null;
          }
          time = dateFormat.parse( (String) srcObj).getTime();
        } catch (ParseException e) {
          throw new ConversionException("Unable to parse source object using specified date format", e);
        }
    //Default conversion
    } else {
      try {
        time = Long.parseLong(srcObj.toString());
      } catch (NumberFormatException e) {
        throw new ConversionException("Unable to determine time in millis of source object",e);
      }
    }

    try {
      Constructor constructor = destClass.getConstructor(new Class[] {Long.TYPE});
      result = constructor.newInstance(new Object[] {new Long(time)});
    } catch (Exception e) {
      throw new ConversionException(e);
    }

    return result;

  }
}
