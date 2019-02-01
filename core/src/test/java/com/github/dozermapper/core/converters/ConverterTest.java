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

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Test;

public class ConverterTest extends AbstractDozerTest {

    @Test(expected = ConversionException.class)
    public void testInvalidDateInput() throws Exception {
        DateConverter dc = new DateConverter(DateFormat.getDateInstance(DateFormat.LONG));
        dc.convert(java.util.Date.class, "jfdlajf");
    }

    @Test(expected = ConversionException.class)
    public void testInvalidDateInput_String() throws Exception {
        // no long constructor
        DateConverter dc = new DateConverter(null);
        dc.convert(String.class, "123");
    }

    @Test(expected = ConversionException.class)
    public void testInvalidCalendarInput() throws Exception {
        CalendarConverter dc = new CalendarConverter(DateFormat.getDateInstance(DateFormat.LONG));
        dc.convert(java.util.GregorianCalendar.class, "jfdlajf");
    }
}
