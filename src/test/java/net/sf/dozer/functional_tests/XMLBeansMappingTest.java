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
package net.sf.dozer.functional_tests;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import net.pmonks.xml.dozer.test.ChildType;
import net.sf.dozer.util.mapping.vo.AnotherTestObject;
import net.sf.dozer.util.mapping.vo.Child;
import net.sf.dozer.util.mapping.vo.GetWeatherByZipCodeDocument;
import net.sf.dozer.util.mapping.vo.GetWeatherByZipCodeResponseDocument;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.WeatherData;
import net.sf.dozer.util.mapping.vo.GetWeatherByZipCodeDocument.GetWeatherByZipCode;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class XMLBeansMappingTest extends AbstractMapperTest {

  public void testXmlBeans() throws Exception {
    mapper = getMapper(new String[] { "xmlBeansMapping.xml" });
    // Map from TestObject to XMLBeans
    TestObject to = (TestObject) newInstance(TestObject.class);
    to.setOne("one");
    GetWeatherByZipCodeDocument doc = (GetWeatherByZipCodeDocument) mapper.map(to, GetWeatherByZipCodeDocument.class);
    assertEquals(to.getOne(), doc.getGetWeatherByZipCode().getZipCode());

    // Map from XMLBeans to TestObject
    GetWeatherByZipCodeDocument res = GetWeatherByZipCodeDocument.Factory.newInstance();
    GetWeatherByZipCode zipCode = res.addNewGetWeatherByZipCode();
    zipCode.setZipCode("one");
    TestObject to2 = (TestObject) mapper.map(res, TestObject.class);
    assertEquals(res.getGetWeatherByZipCode().getZipCode(), to2.getOne());
    
    Set set = new HashSet();
    AnotherTestObject ato = new AnotherTestObject();
    ato.setDay("day");
    set.add(ato);
    to.setSetToArray(set);
    GetWeatherByZipCodeResponseDocument responseDoc = (GetWeatherByZipCodeResponseDocument) mapper.map(to, GetWeatherByZipCodeResponseDocument.class);
    WeatherData[] weatherDataArray = responseDoc.getGetWeatherByZipCodeResponse().getWeatherDataArray(); 
    assertEquals(ato.getDay(), weatherDataArray[0].getDay());    
  }

  /*
   * Test Case Submitted by Peter Monks 1/2007
   */
  public void testInterfaceInheritanceViaXmlBeans_PojoToXmlBean() {
    mapper = getMapper(new String[] { "xmlBeansMapping.xml" });
    Child pojo = (Child) newInstance(Child.class);

    pojo.setId(BigInteger.valueOf(42));
    pojo.setName("Ernie");
    pojo.setFu("Fu");
    pojo.setBar("Bar");

    ChildType xmlBean = (ChildType) mapper.map(pojo, ChildType.class);

    assertNotNull("dest obj should not be null", xmlBean);
    assertNotNull("fu value should not be null", xmlBean.getFu());
    assertEquals("invalid fu value", pojo.getFu(), xmlBean.getFu());
    assertNotNull("bar field should not be null", xmlBean.getBar());
    assertEquals("invalid bar value", pojo.getBar(), xmlBean.getBar());
    assertNotNull("name value should not be null", xmlBean.getName());
    assertEquals("invalid name value", pojo.getName(), xmlBean.getName());
    assertNotNull("id field should not be null", xmlBean.getId());
    assertEquals("invalid id value", pojo.getId(), xmlBean.getId());
  }

  /*
   * Test Case Submitted by Peter Monks 1/2007
   */
  public void testInterfaceInheritanceViaXmlBeans_XmlBeanToPojo() {
    mapper = getMapper(new String[] { "xmlBeansMapping.xml" });
    ChildType xmlBean = ChildType.Factory.newInstance();

    xmlBean.setId(BigInteger.valueOf(7236));
    xmlBean.setName("Bert");
    xmlBean.setFu("Uf");
    xmlBean.setBar("Rab");

    Child pojo = (Child) mapper.map(xmlBean, Child.class);

    assertNotNull("dest obj should not be null", pojo);
    assertNotNull("fu should not be null", pojo.getFu());
    assertEquals("invalid fu value", xmlBean.getFu(), pojo.getFu());
    assertNotNull("bar should not be null", pojo.getBar());
    assertEquals("invalid bar value", xmlBean.getBar(), pojo.getBar());
    assertNotNull("id should not be null", pojo.getId());
    assertEquals("invalid id value", xmlBean.getId(), pojo.getId());
    assertNotNull("name should not be null", pojo.getName());
    assertEquals("invalid name value", xmlBean.getName(), pojo.getName());
  }

  public void testXmlBeansWithNullFields() throws Exception {

    mapper = getMapper(new String[] { "xmlBeansMapping.xml" });
    ChildType xmlBean = ChildType.Factory.newInstance();

    Child pojo = (Child) mapper.map(xmlBean, Child.class);

    assertNotNull("dest obj should not be null", pojo);
    assertNull("fu should be null", pojo.getFu());
    assertNull("bar should be null", pojo.getBar());
    assertNull("id should be null", pojo.getId());
    assertNull("name should be null", pojo.getName());
    assertNull("testBoolean should be null", pojo.getTestBoolean());
    assertNull("testInt should be null", pojo.getTestInt());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }
}