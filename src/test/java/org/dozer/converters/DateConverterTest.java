/*
 * Copyright 2005-2009 the original author or authors.
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

import junit.framework.TestCase;

import org.dozer.converters.DateConverter;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author dmitry.buzdin
 */
public class DateConverterTest extends TestCase {

  private DateConverter converter;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    converter = new DateConverter(new SimpleDateFormat());
  }

  @Test
  public void testTimestampConversion() {
    Timestamp timestamp = new java.sql.Timestamp(1L);
    timestamp.setNanos(12345);
    Timestamp result = (Timestamp) converter.convert(Timestamp.class, timestamp);
    assertEquals(timestamp, result);
  }

  @Test
  public void testSqlDateConversion() {
    Date date = new Date(1L);
    Date result = (Date) converter.convert(Date.class, date);
    assertEquals(date, result);
  }

  @Test
  public void testUtilDateConversion() {
    java.util.Date date = new java.util.Date(1L);
    java.util.Date result = (java.util.Date) converter.convert(java.util.Date.class, date);
    assertEquals(date, result);
  }

  @Test
  public void testCalendarConversion() {
    GregorianCalendar calendar = new GregorianCalendar(1, 2, 3);
    GregorianCalendar result = (GregorianCalendar) converter.convert(GregorianCalendar.class, calendar);
    assertEquals(calendar, result);
  }

  @Test
  public void testTimeConversion() {
    Time time = new Time(1L);
    Time result = (Time) converter.convert(Time.class, time);
    assertEquals(result, time);
  }

  //TODO add from and to String conversion tests for each supported date/time type
  //TODO Check XmlGregorianCalendar with Mocks

}
