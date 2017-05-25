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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Internal class used as a container to determine the date format to use for a particular field mapping. Only intended
 * for internal use.  
 * 
 * @author tierney.matt
 */
public class DateFormatContainer {
  private String dfStr;
  private DateFormat dateFormat;

  public DateFormatContainer(String dfStr) {
    this.dfStr = dfStr;
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
    return dfStr == null ? null : new SimpleDateFormat(dfStr, Locale.getDefault());
  }
}
