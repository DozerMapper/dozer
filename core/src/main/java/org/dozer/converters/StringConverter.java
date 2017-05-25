/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer.converters;


import org.apache.commons.beanutils.Converter;

/**
 * Internal class for converting Supported Data Types to String. Uses date formatter for Date and Calendar source
 * objects. Calls toString() on the source object for all other types. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public class StringConverter implements Converter {
  private DateFormatContainer dateFormatContainer;

  public StringConverter(DateFormatContainer dateFormatContainer) {
    this.dateFormatContainer = dateFormatContainer;
  }

  public Object convert(Class destClass, Object srcObj) {
    String result;
    Class srcClass = srcObj.getClass();
    if (dateFormatContainer != null && java.util.Date.class.isAssignableFrom(srcClass)
        && dateFormatContainer.getDateFormat() != null) {
      result = dateFormatContainer.getDateFormat().format((java.util.Date) srcObj);
    } else if (dateFormatContainer != null && java.util.Calendar.class.isAssignableFrom(srcClass)
        && dateFormatContainer.getDateFormat() != null) {
      result = dateFormatContainer.getDateFormat().format(((java.util.Calendar) srcObj).getTime());
    } else {
      result = srcObj.toString();
    }

    return result;
  }

  public DateFormatContainer getDateFormatContainer() {
    return dateFormatContainer;
  }

  public void setDateFormatContainer(DateFormatContainer dateFormat) {
    this.dateFormatContainer = dateFormat;
  }
}
