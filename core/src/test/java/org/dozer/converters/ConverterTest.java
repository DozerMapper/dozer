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

import org.dozer.AbstractDozerTest;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class ConverterTest extends AbstractDozerTest {
  /*
   * See PrimitiveOrWrapperConverterTest for more thorough data conversion unit tests
   */
  @Test
  public void testAccessors() throws Exception {
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

    CalendarConverter cc = new CalendarConverter(dateFormat);
    assertEquals(dateFormat, cc.getDateFormat());

    StringConverter sc = new StringConverter(null);
    DateFormatContainer dfc = new DateFormatContainer(null);
    sc.setDateFormatContainer(dfc);
    assertEquals(dfc, sc.getDateFormatContainer());
  }

  @Test(expected = ConversionException.class)
  public void testInvalidDateInput() throws Exception {
    DateConverter dc = new DateConverter(DateFormat.getDateInstance(DateFormat.LONG));
    dc.convert(java.util.Date.class, "jfdlajf");
  }

  @Test(expected = ConversionException.class)
  public void testInvalidDateInput_String() throws Exception {
    DateConverter dc = new DateConverter(DateFormat.getDateInstance(DateFormat.LONG));
    // no long constructor
    dc = new DateConverter(null);
    dc.convert(String.class, "123");
  }

  @Test(expected = ConversionException.class)
  public void testInvalidCalendarInput() throws Exception {
    CalendarConverter dc = new CalendarConverter(DateFormat.getDateInstance(DateFormat.LONG));
    dc.convert(java.util.GregorianCalendar.class, "jfdlajf");
  }
}
