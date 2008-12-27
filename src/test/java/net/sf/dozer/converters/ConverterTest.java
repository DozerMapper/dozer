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
package net.sf.dozer.converters;

import java.text.DateFormat;

import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.util.DateFormatContainer;

/**
 * @author tierney.matt
 */
public class ConverterTest extends AbstractDozerTest {
  /*
   * See DataConvertionTest for more thorough data conversion unit tests
   */
  public void testAccessors() throws Exception {
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

    CalendarConverter cc = new CalendarConverter(dateFormat);
    assertEquals(dateFormat, cc.getDateFormat());

    StringConverter sc = new StringConverter(null);
    DateFormatContainer dfc = new DateFormatContainer(null);
    sc.setDateFormatContainer(dfc);
    assertEquals(dfc, sc.getDateFormatContainer());
  }

  public void testInvalidDateInput() throws Exception {
    DateConverter dc = new DateConverter(DateFormat.getDateInstance(DateFormat.LONG));
    try {
      dc.convert(java.util.Date.class, "jfdlajf");
      fail("should have thrown ConversionException");
    } catch (ConversionException e) {
    }

    try {
      // no long constructor
      dc = new DateConverter(null);
      dc.convert(String.class, "123");
      fail("should have thrown ConversionException");
    } catch (ConversionException e) {
    }
  }

  public void testInvalidCalendarInput() throws Exception {
    CalendarConverter dc = new CalendarConverter(DateFormat.getDateInstance(DateFormat.LONG));
    try {
      dc.convert(java.util.GregorianCalendar.class, "jfdlajf");
      fail("should have thrown ConversionException");
    } catch (ConversionException e) {
    }
  }
}
