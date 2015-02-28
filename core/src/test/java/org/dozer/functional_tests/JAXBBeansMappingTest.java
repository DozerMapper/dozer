/**
 * Copyright 2005-2013 Dozer Project
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

import org.apache.commons.beanutils.Converter;
import org.dozer.DozerBeanMapper;
import org.dozer.converters.XMLGregorianCalendarConverter;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.util.MappingUtils;
import org.dozer.vo.TestObject;
import org.dozer.vo.jaxb.employee.EmployeeType;
import org.dozer.vo.jaxb.employee.EmployeeWithInnerClass;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.dozer.loader.api.TypeMappingOptions.*;
import static org.dozer.converters.custom.XmlDateConverter.*;

/**
 * @author dmitry.buzdin
 */
public class JAXBBeansMappingTest extends AbstractFunctionalTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("jaxbBeansMapping.xml");
  }

  @Test
  public void testTrivial() {
    Class<?> type = MappingUtils.loadClass("org.dozer.vo.jaxb.employee.EmployeeType");
    assertNotNull(type);
  }

  @Test
  public void testSimple() {
    TestObject source = new TestObject();
    source.setOne("ABC");
    EmployeeWithInnerClass result = mapper.map(source, EmployeeWithInnerClass.class);
    assertNotNull(result);
    assertEquals("ABC", result.getFirstName());
  }
  
  @Test
  public void testNestedInnerClass() {
    TestObject source = new TestObject();
    source.setOne("Name");
    EmployeeWithInnerClass.Address.State result = mapper.map(source, EmployeeWithInnerClass.Address.State.class);
    assertNotNull(result);
    assertEquals("Name", result.getName());
  }
  
  @Test
  public void testDateToXMLGregorianCalendar() {
      TestObject source = new TestObject();
      Date now = new Date();
      source.setDate(now);
      EmployeeWithInnerClass result = mapper.map(source, EmployeeWithInnerClass.class);
      assertNotNull(result);
      assertEquals(now.getTime(), result.getBirthDate().toGregorianCalendar().getTimeInMillis());
  }
  
  @Test
  public void testXMLGregorianCalendarToDate() throws DatatypeConfigurationException {
      Calendar cal = GregorianCalendar.getInstance();
      EmployeeWithInnerClass source = new EmployeeWithInnerClass();
      source.setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal));
      TestObject result = mapper.map(source, TestObject.class);
      assertNotNull(result);
      assertEquals(cal.getTimeInMillis(), result.getDate().getTime());
  }

  @Test
  public void testJAXBListWithNoSetter() {
    ListContainer source = new ListContainer();
    source.getList().add(1);
    source.getList().add(2);

    source.getSubordinates().add(new StringContainer("John"));

    EmployeeType result = mapper.map(source, EmployeeType.class);

    assertNotNull(result);
    assertEquals(2, result.getIds().size());
    assertTrue(result.getIds().contains(1));
    assertTrue(result.getIds().contains(2));
    
    assertEquals(1, result.getSubordinates().size());
    assertEquals("John", result.getSubordinates().get(0).getFirstName());
  }

  @Test
  public void testCopyMapToSimpleJaxbClass() {
    // Test XmlDateConverter with these date time literal values. 
    String JACK_BENNY_BIRTH_DATE     = "1894-02-14T00:00:00.000-00:00";
    String MEL_BLANC_BIRTH_DATE      = "1908-05-30T00:00:00.000-00:00";
    String EDDIE_ANDERSON_BIRTH_DATE = "1905-09-18T00:00:00.000-00:00";
    
    // Create a Map representing data to be copied to EmployeType.
    
    // Populate an Employee subordinate.
    Map<String,Object> subordinate1 = new HashMap<String,Object>();
    subordinate1.put("firstName", "Eddie (Rochester)");
    subordinate1.put("lastName", "Anderson");
    subordinate1.put("ids", new Integer[] {201, 202});
    subordinate1.put("birthDate", EDDIE_ANDERSON_BIRTH_DATE);
    subordinate1.put("age", 28);
    
    // Populate an Employee subordinate.
    Map<String,Object> subordinate2 = new HashMap<String,Object>();
    subordinate2.put("firstName", "Mel (Tweety Bird)");
    subordinate2.put("lastName", "Blanc");
    subordinate2.put("ids", new Integer[] {301, 302});
    subordinate2.put("birthDate", MEL_BLANC_BIRTH_DATE);
    subordinate2.put("age", 25);
    
    // Build a map of subordinates.
    List<Map<String,Object>> subordinates = new ArrayList<Map<String,Object>>();
    subordinates.add(subordinate1);
    subordinates.add(subordinate2);
    
    // Build a map for the manager.
    Map<String,Object> employeeMap = new HashMap<String,Object>();
    employeeMap.put("firstName", "Jack");
    employeeMap.put("lastName", "Benny");
    employeeMap.put("ids", new Integer[] {101, 102});
    employeeMap.put("birthDate", JACK_BENNY_BIRTH_DATE);
    employeeMap.put("age", 39);
    employeeMap.put("subordinates", subordinates);
    
    // Configure a Dozer mapper to use XmlDateConverter.convertXmlDate()
    BeanMappingBuilder builder = new BeanMappingBuilder()
    {
      @Override
      protected void configure()
      {
        // Tests conversion of ISO8601 date time literal values to XmlGregorianCalendar.
        // Any SimpleDateFormat pattern can be used in convertXmlDate(String pattern)
        //
        // Notes:
        // 1) convertXmlDate() is the same as convertXmlDate(ISO8601)
        // 2) Uses org.jvnet.jaxb2.maven2:maven-jaxb2-plugin and -Xsetters
        //    to generate a setter for List properties. When the source is
        //    a Map, Dozer needs a setter on properties of type List.
        // 3) mapNull(false) avoids a NPE on setAll(null) for EmployeeType.setSubordinates(null).
        // 4) mapNull(true) works with JAXB plugin option: -Xsetters-mode=direct
        mapping(Map.class, EmployeeType.class, mapNull(false))
            .fields("birthDate", "birthDate", convertXmlDate());
      }
    };

    DozerBeanMapper mapper = new DozerBeanMapper();
    mapper.addMapping(builder);
    
    // Use Dozer to copy the employee map to an employee object.
    EmployeeType employee = mapper.map(employeeMap, EmployeeType.class);
    
    // Assert expectations...
    
    assertNotNull("Employee cannot be null.", employee);
    assertEquals("Employee first name must match source.", employeeMap.get("firstName"), employee.getFirstName());
    assertEquals("Employee last name must match source.", employeeMap.get("lastName"), employee.getLastName());
    
    assertNotNull("Employee ids cannot be null.", employee.getIds());
    for ( Integer id : ((Integer[]) employeeMap.get("ids")) )
        assertTrue("Employee ids must contain "+id+".", employee.getIds().contains(id));
    assertNotNull("Employee birth date cannot be null.", employee.getBirthDate());
    
    Converter ISO8601Converter = new XMLGregorianCalendarConverter(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
    XMLGregorianCalendar expectBirthDate = ISO8601Converter.convert(XMLGregorianCalendar.class, JACK_BENNY_BIRTH_DATE);
    XMLGregorianCalendar actualBirthDate = employee.getBirthDate();
    assertEquals("Employee birth date must match source.", expectBirthDate, actualBirthDate);
    
    assertNotNull("Employee subordinates cannot be null.", employee.getSubordinates());
    assertEquals("Employee has two subordinates.", 2, employee.getSubordinates().size());
    for ( EmployeeType subordinate : employee.getSubordinates() )
    {
      assertNotNull("Subordinate first name cannot be null.", subordinate.getFirstName());
      assertNotNull("Subordinate last name cannot be null.", subordinate.getLastName());
      assertNotNull("Subordinate ids cannot be null.", subordinate.getIds());
      assertFalse("Subordinate ids cannot be empty.", subordinate.getIds().isEmpty());
      assertNotNull("Subordinate birth date cannot be null.", subordinate.getBirthDate());
      assertNotNull("Subordinate subordinates cannot be null.", subordinate.getSubordinates());
      assertTrue("Subordinate subordinates must be empty.", subordinate.getSubordinates().isEmpty());
    }
  }

  public static class ListContainer {
    private List<Integer> list = new ArrayList<Integer>();
    private List<StringContainer> subordinates = new ArrayList<StringContainer>();

    public List<Integer> getList() {
      return list;
    }

    public List<StringContainer> getSubordinates() {
      return subordinates;
    }

  }

  public static class StringContainer {
    private String value;

    public StringContainer() {

    }
    
    public StringContainer(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

}
