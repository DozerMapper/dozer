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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XMLGregorianCalendarConverterTest extends AbstractDozerTest {
    private XMLGregorianCalendarConverter converter;
    private static final int YEAR = 1983;
    private static final int MONTH = 8;
    private static final int DAY = 4;

    @Before
    public void setUp() throws Exception {
        converter = new XMLGregorianCalendarConverter(new SimpleDateFormat("dd.MM.yyyy"));
    }

    @Test
    public void testConvert_Date() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(YEAR, MONTH, DAY);
        Date date = calendar.getTime();

        Object result = converter.convert(XMLGregorianCalendar.class, date);
        XMLGregorianCalendar xmlCalendar = (XMLGregorianCalendar)result;

        assertEquals(YEAR, xmlCalendar.getYear());
        assertEquals(MONTH + 1, xmlCalendar.getMonth());
        assertEquals(DAY, xmlCalendar.getDay());
    }

    @Test
    public void testConvert_Calendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(YEAR, MONTH, DAY);

        Object result = converter.convert(XMLGregorianCalendar.class, calendar);
        XMLGregorianCalendar xmlCalendar = (XMLGregorianCalendar)result;

        assertEquals(YEAR, xmlCalendar.getYear());
        assertEquals(MONTH + 1, xmlCalendar.getMonth());
        assertEquals(DAY, xmlCalendar.getDay());
    }

    @Test
    public void testConvert_String() {
        Object result = converter.convert(XMLGregorianCalendar.class, "04.07.1983");
        XMLGregorianCalendar xmlCalendar = (XMLGregorianCalendar)result;

        assertEquals(YEAR, xmlCalendar.getYear());
        assertEquals(MONTH - 1, xmlCalendar.getMonth());
        assertEquals(DAY, xmlCalendar.getDay());
    }

    @Test
    public void testConvert_XmlGregorianCalendar() throws Exception {
        DatatypeFactory instance = DatatypeFactory.newInstance();
        XMLGregorianCalendar calendar = instance.newXMLGregorianCalendar(new GregorianCalendar(YEAR, MONTH, DAY));

        Object result = converter.convert(XMLGregorianCalendar.class, calendar);
        XMLGregorianCalendar xmlCalendar = (XMLGregorianCalendar)result;

        assertEquals(YEAR, xmlCalendar.getYear());
        assertEquals(MONTH + 1, xmlCalendar.getMonth());
        assertEquals(DAY, xmlCalendar.getDay());
    }

    @Test
    public void testConvert_EmptyString() {
        Object result = converter.convert(XMLGregorianCalendar.class, "");

        assertNull(result);
    }

    @Test
    public void testConvert_Format() throws Exception {
        DatatypeFactory instance = DatatypeFactory.newInstance();
        XMLGregorianCalendar calendar = instance.newXMLGregorianCalendar(new GregorianCalendar(YEAR, MONTH, DAY));
        Object result = converter.convert(String.class, calendar);
        String stringCalendar = (String)result;

        assertEquals(stringCalendar, "04.09.1983");
    }
}
