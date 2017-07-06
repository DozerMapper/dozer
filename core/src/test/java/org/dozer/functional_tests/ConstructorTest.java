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
package org.dozer.functional_tests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * @author Dmitry Buzdin
 */
public class ConstructorTest extends AbstractFunctionalTest {

  private Mapper beanMapper;

  @Before
  public void setUp() {
    beanMapper = DozerBeanMapperBuilder.buildDefault();
  }

  @Test
  public void dateFormat() {
    DateFormat instance = SimpleDateFormat.getInstance();
    GregorianCalendar calendar = new GregorianCalendar(1, 1, 1);
    instance.setCalendar(calendar);
    Container source = new Container();
    source.setDateFormat(instance);
    Container target = new Container();

    beanMapper.map(source, target);

    assertNotSame(source.getDateFormat(), target.getDateFormat());
    assertNotNull(target.getDateFormat());
    assertEquals(calendar, target.getDateFormat().getCalendar());
  }

  @Test
  public void xmlCalendar() {
    Container source = new Container();
    XMLGregorianCalendarImpl calendar = new XMLGregorianCalendarImpl();
    source.setCalendar(calendar);
    Container target = new Container();

    beanMapper.map(source, target);

    assertNotSame(source.getCalendar(), target.getCalendar());
    assertEquals(calendar, target.getCalendar());
  }

  public static class Container {
    DateFormat dateFormat;
    XMLGregorianCalendar calendar;

    public DateFormat getDateFormat() {
      return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
      this.dateFormat = dateFormat;
    }

    public XMLGregorianCalendar getCalendar() {
      return calendar;
    }

    public void setCalendar(XMLGregorianCalendar calendar) {
      this.calendar = calendar;
    }
  }

}
