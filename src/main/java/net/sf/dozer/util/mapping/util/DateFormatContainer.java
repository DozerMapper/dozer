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
package net.sf.dozer.util.mapping.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;


/**
 * Internal class used as a container to determine the date format to use for a particular field mapping.  Only intended for internal use. 
 * 
 * @author tierney.matt
 */
public class DateFormatContainer {
  private ClassMap classMap;
  private FieldMap fieldMap;
  private DateFormat dateFormat;
  
  public DateFormatContainer(ClassMap classMap, FieldMap fieldMap) {
    this.classMap = classMap;
    this.fieldMap = fieldMap;
  }
  
  public DateFormatContainer(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  public DateFormat getDateFormat() {
    if (dateFormat == null) {
      dateFormat = determineDateFormat();
    }
    return dateFormat;
  }
  
  public void setDateFormat(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  private DateFormat determineDateFormat() {
    if (classMap == null || fieldMap == null) {
      return null;
    }
    // try to use field mapping date format
    String dfStr = fieldMap.getSourceField().getDateFormat() == null ? fieldMap.getDestField().getDateFormat()
        : fieldMap.getSourceField().getDateFormat();

    // if field level date format is not specified, try using class mapping
    // default date format
    if (MappingUtils.isBlankOrNull(dfStr)) {
      dfStr = classMap.getDateFormat();
    }

    return dfStr == null ? null : new SimpleDateFormat(dfStr, Locale.getDefault());
  }
}
