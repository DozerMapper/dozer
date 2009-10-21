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
package org.dozer.functional_tests;

import org.dozer.vo.DateContainer;
import org.dozer.vo.DateObjectSource;
import org.dozer.vo.DateObjectDest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author dmitry.buzdin
 */
public class DateFormatTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("dateFormat.xml");
  }

  @Test
  public void testConversion() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2009, 6, 3, 12, 20, 10);
    DateContainer result = mapper.map(calendar.getTime(), DateContainer.class);
    assertNotNull(result);
    assertEquals("2009-07-03 12:20:10", result.getDate());
    assertEquals("2009-07-03 12:20:10", result.getSet().iterator().next());
  }

  @Test
  public void testConversion_Calendar() {
    GregorianCalendar cal = new GregorianCalendar(2009, 2, 3);
    Date sourceValue = cal.getTime();
    Calendar result = mapper.map(sourceValue, Calendar.class);
    assertEquals(cal.getTime(), result.getTime());
  }

  @Test
  public void testConversion_CalendarInsideBean() {
    GregorianCalendar cal = new GregorianCalendar(2009, 2, 3);
    Date sourceValue = cal.getTime();
    Source source = new Source();
    source.setDate(sourceValue);
    Destination result = mapper.map(source, Destination.class);

    assertEquals(cal.getTime(), result.getDate().getTime());
  }

  @Test
  public void testGregorianCalendar() {

    DateObjectSource source = new DateObjectSource();
    source.setRecalledDate(new XMLGregorianCalendarImpl(new GregorianCalendar()));
    DateObjectDest dest = mapper.map(source, DateObjectDest.class);
    assertEquals(source.getRecalledDate(), dest.getRecDate());

    //Test when instance exist
    dest = new DateObjectDest();
    dest.setRecDate(new XMLGregorianCalendarImpl(new GregorianCalendar()));
    mapper.map(source, dest);
    assertEquals(source.getRecalledDate(), dest.getRecDate());
  }


  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

  public static class Source {
    private Date date;

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }
  }


  public static class Destination {

    private Calendar calendar;

    public Calendar getDate() {
      return calendar;
    }

    public void setDate(Calendar calendar) {
      this.calendar = calendar;
    }
  }

}
