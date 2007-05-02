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

import org.apache.commons.beanutils.Converter;

import net.sf.dozer.util.mapping.util.DateFormatContainer;


/**
 * @author tierney.matt
 */
public class StringConverter implements Converter {
  private DateFormatContainer dateFormatContainer;

  public StringConverter(DateFormatContainer dateFormatContainer) {
    this.dateFormatContainer = dateFormatContainer;
  }

  public Object convert(Class destClass, Object sourceObj) {
    String result = null;
    Class sourceClass = sourceObj.getClass();
    if (dateFormatContainer != null && dateFormatContainer.getDateFormat() != null && java.util.Date.class.isAssignableFrom(sourceClass)) {
      result = dateFormatContainer.getDateFormat().format((java.util.Date) sourceObj);
    } else if (dateFormatContainer != null && dateFormatContainer.getDateFormat() != null && java.util.Calendar.class.isAssignableFrom(sourceClass)) {
      result = dateFormatContainer.getDateFormat().format(((java.util.Calendar)sourceObj).getTime());
    } else {
      result = sourceObj.toString();
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
