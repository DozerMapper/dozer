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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DateConverterTest extends AbstractDozerTest {

    private DateConverter converter;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        converter = new DateConverter(new SimpleDateFormat("dd.MM.yyyy"));
    }

    @Test
    public void testTimestampConversion() {
        Timestamp timestamp = new java.sql.Timestamp(1L);
        timestamp.setNanos(12345);
        Timestamp result = (Timestamp)converter.convert(Timestamp.class, timestamp);

        assertEquals(timestamp, result);
    }

    @Test
    public void testSqlDateConversion() {
        Date date = new Date(1L);
        Date result = (Date)converter.convert(Date.class, date);

        assertEquals(date, result);
    }

    @Test
    public void testUtilDateConversion() {
        java.util.Date date = new java.util.Date(1L);
        java.util.Date result = (java.util.Date)converter.convert(java.util.Date.class, date);

        assertEquals(date, result);
    }

    @Test
    public void testCalendarConversion() {
        GregorianCalendar calendar = new GregorianCalendar(1, 2, 3);
        GregorianCalendar result = (GregorianCalendar)converter.convert(GregorianCalendar.class, calendar);

        assertEquals(calendar, result);
    }

    @Test
    public void testTimeConversion() {
        Time time = new Time(1L);
        Time result = (Time)converter.convert(Time.class, time);

        assertEquals(result, time);
    }

    @Test
    public void testEmptyString() {
        assertNull(converter.convert(Date.class, ""));
    }

    @Test
    public void testGoodString() {
        GregorianCalendar calendar = new GregorianCalendar(2001, 1, 1);
        java.util.Date expected = calendar.getTime();

        assertEquals(expected, converter.convert(java.util.Date.class, "01.02.2001"));
    }

    @Test
    public void testBadString() {
        try {
            converter.convert(Date.class, "xyz");
            fail();
        } catch (ConversionException e) {
        }
    }

    @Test
    public void testXmlGregorianCalendar() {
        XMLGregorianCalendar xmlCalendar = mock(XMLGregorianCalendar.class);
        GregorianCalendar expected = new GregorianCalendar();
        when(xmlCalendar.toGregorianCalendar()).thenReturn(expected);

        Date date = new Date(expected.getTimeInMillis());

        assertEquals(date, converter.convert(Date.class, xmlCalendar));
    }

    @Test
    public void testConvert_Format() {
        GregorianCalendar calendar = new GregorianCalendar(2001, 1, 1);

        Object result = converter.convert(String.class, calendar);

        String stringCalendar = (String)result;

        assertEquals(stringCalendar, "01.02.2001");
    }

}
