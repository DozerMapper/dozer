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
package net.sf.dozer.util.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import net.sf.dozer.util.mapping.converters.StringAppendCustomConverter;
import net.sf.dozer.util.mapping.vo.ArrayCustConverterObj;
import net.sf.dozer.util.mapping.vo.ArrayCustConverterObjPrime;
import net.sf.dozer.util.mapping.vo.CustomDoubleObject;
import net.sf.dozer.util.mapping.vo.CustomDoubleObjectIF;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.SimpleObjPrime2;
import net.sf.dozer.util.mapping.vo.TestCustomConverterHashMapObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterHashMapPrimeObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterObjectPrime;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;


/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class CustomConverterMappingTest extends AbstractDozerTest {
  
  public void testSimpleCustomConverter() throws Exception {
    mapper = getNewMapper(new String[]{"simpleCustomConverter.xml"});
    SimpleObj src = new SimpleObj();
    src.setField1(String.valueOf(System.currentTimeMillis()));
    
    SimpleObjPrime2 dest = (SimpleObjPrime2) mapper.map(src, SimpleObjPrime2.class);

    //Custom converter specified for the field1 mapping, so verify custom converter was actually used
    assertNotNull("dest field1 should not be null", dest.getField1Prime());
    StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
    assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
    String token1 = st.nextToken();
    assertEquals("1st portion of dest field1 value should equal src field value", src.getField1(), token1);
    String token2 = st.nextToken();
    assertEquals("dest field1 value should have been appended to by the cust converter", 
        StringAppendCustomConverter.APPENDED_VALUE, token2);
  }

  public void testSimpleCustomConverter_NullSrcValue() throws Exception {
    //Test that custom converter gets invoked even if the src field value is NULL
    mapper = getNewMapper(new String[]{"simpleCustomConverter.xml"});
    SimpleObj src = new SimpleObj();
    src.setField1(null);
    
    SimpleObjPrime2 dest = (SimpleObjPrime2) mapper.map(src, SimpleObjPrime2.class);

    //Custom converter specified for the field1 mapping, so verify custom converter was actually used
    assertNotNull("dest field1 should not be null", dest.getField1Prime());
    StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
    assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
    String token1 = st.nextToken();
    assertEquals("dest field1 value should contain the explicit null string", "null", token1);
    String token2 = st.nextToken();
    assertEquals("dest field1 value should have been appended to by the cust converter", 
        StringAppendCustomConverter.APPENDED_VALUE, token2);
  }
  
  public void testArrayToStringCustomConverter() throws Exception {
    //Test that custom converter is used when src is an Array and dest is a String
    mapper = getNewMapper(new String[]{"arrayToStringCustomConverter.xml"});
    SimpleObj simple = new SimpleObj();
    simple.setField1(String.valueOf(System.currentTimeMillis()));
    
    ArrayCustConverterObj src = new ArrayCustConverterObj();
    src.setField1(new SimpleObj[] {simple});
    
    ArrayCustConverterObjPrime dest = (ArrayCustConverterObjPrime) mapper.map(src, ArrayCustConverterObjPrime.class);

    //Custom converter specified for the field1 mapping, so verify custom converter was actually used
    assertNotNull("dest field1 should not be null", dest.getField1Prime());
    StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
    assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
    String token1 = st.nextToken();
    String token2 = st.nextToken();
    assertEquals("dest field1 value should have been appended to by the cust converter", 
        StringAppendCustomConverter.APPENDED_VALUE, token2);
  }
  
  public void testCustomConverterMapping() throws Exception {
    mapper = getNewMapper(new String[] { "dozerBeanMapping.xml" });
    TestCustomConverterObject obj = new TestCustomConverterObject();
    CustomDoubleObjectIF doub = new CustomDoubleObject();
    doub.setTheDouble(15);
    CustomDoubleObjectIF doub2 = new CustomDoubleObject();
    doub2.setTheDouble(15);
    obj.setAttribute(doub);

    Collection list = new ArrayList();
    list.add(doub2);

    obj.setNames(list);

    TestCustomConverterObjectPrime dest = (TestCustomConverterObjectPrime) mapper.map(obj,
        TestCustomConverterObjectPrime.class);

    assertEquals("Custom Converter failed", dest.getDoubleAttribute().doubleValue() + "", "15.0");
    assertEquals("Custom Converter failed", ((Double) dest.getNames().iterator().next()).doubleValue() + "", "15.0");

    TestCustomConverterObjectPrime objp = new TestCustomConverterObjectPrime();

    objp.setDoubleAttribute(new Double(15));

    Collection list2 = new ArrayList();
    objp.setNames(list2);
    objp.getNames().add(new Double(10));

    TestCustomConverterObject destp = (TestCustomConverterObject) mapper.map(objp, TestCustomConverterObject.class);

    assertEquals("Custom Converter failed", destp.getAttribute().getTheDouble() + "", "15.0");
    assertEquals("Custom Converter failed", ((CustomDoubleObjectIF) destp.getNames().iterator().next()).getTheDouble()
        + "", "10.0");

    destp.getAttribute().setName("testName");

    // pass by reference
    mapper.map(objp, destp);

    assertEquals("Custom Converter failed", destp.getAttribute().getTheDouble() + "", "15.0");
    assertEquals("testName", destp.getAttribute().getName());

    // test primitive double
    TestCustomConverterObjectPrime prime = new TestCustomConverterObjectPrime();
    prime.setPrimitiveDoubleAttribute(25.00);
    TestCustomConverterObject obj2 = (TestCustomConverterObject) mapper.map(prime, TestCustomConverterObject.class);
    CustomDoubleObjectIF customDouble = obj2.getPrimitiveDoubleAttribute();
    assertNotNull(customDouble);
    assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

    // test conversion in the other direction
    prime = (TestCustomConverterObjectPrime) mapper.map(obj2, TestCustomConverterObjectPrime.class);
    assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

  }

  public void testCustomConverterWithPrimitive() throws Exception {
    // test primitive double
    mapper = getNewMapper(new String[] { "dozerBeanMapping.xml" });
    TestCustomConverterObjectPrime prime = new TestCustomConverterObjectPrime();
    prime.setPrimitiveDoubleAttribute(25.00);
    prime.setDoubleAttribute(new Double(30.00));
    TestCustomConverterObject obj2 = (TestCustomConverterObject) mapper.map(prime, TestCustomConverterObject.class);
    CustomDoubleObjectIF customDouble = obj2.getPrimitiveDoubleAttribute();
    assertNotNull(customDouble);
    assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

    // test conversion in the other direction
    prime = (TestCustomConverterObjectPrime) mapper.map(obj2, TestCustomConverterObjectPrime.class);
    assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

  }

  public void testCustomConverterHashMapMapping() throws Exception {
    mapper = getNewMapper(new String[] { "dozerBeanMapping.xml" });
    TestCustomConverterHashMapObject testCustomConverterHashMapObject = new TestCustomConverterHashMapObject();
    TestObject to = new TestObject();
    to.setOne("one");
    testCustomConverterHashMapObject.setTestObject(to);
    TestObjectPrime top = new TestObjectPrime();
    top.setOnePrime("onePrime");
    testCustomConverterHashMapObject.setTestObjectPrime(top);
    TestCustomConverterHashMapPrimeObject dest = (TestCustomConverterHashMapPrimeObject) mapper.map(
        testCustomConverterHashMapObject, TestCustomConverterHashMapPrimeObject.class);
    assertEquals(to, dest.getTestObjects().get("object1"));
    assertEquals(top, dest.getTestObjects().get("object2"));

  }

  public void testFieldCustomConverter() throws Exception {
    mapper = getNewMapper(new String[]{"fieldCustomConverter.xml"});
    SimpleObj src = new SimpleObj();
    src.setField1(String.valueOf(System.currentTimeMillis()));
    
    SimpleObjPrime2 dest = (SimpleObjPrime2) mapper.map(src, SimpleObjPrime2.class);

    //Custom converter specified for the field1 mapping, so verify custom converter was actually used
    assertNotNull("dest field1 should not be null", dest.getField1Prime());
    StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
    assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
    String token1 = st.nextToken();
    assertEquals("1st portion of dest field1 value should equal src field value", src.getField1(), token1);
    String token2 = st.nextToken();
    assertEquals("dest field1 value should have been appended to by the cust converter", 
        StringAppendCustomConverter.APPENDED_VALUE, token2);
  }
  
  public void testFieldCustomConverter_NullSrcValue() throws Exception {
    //Test that custom converter gets invoked even if the src field value is NULL
    mapper = getNewMapper(new String[]{"fieldCustomConverter.xml"});
    SimpleObj src = new SimpleObj();
    src.setField1(null);
    
    SimpleObjPrime2 dest = (SimpleObjPrime2) mapper.map(src, SimpleObjPrime2.class);

    //Custom converter specified for the field1 mapping, so verify custom converter was actually used
    assertNotNull("dest field1 should not be null", dest.getField1Prime());
    StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
    assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
    String token1 = st.nextToken();
    assertEquals("dest field1 value should contain the explicit null string", "null", token1);
    String token2 = st.nextToken();
    assertEquals("dest field1 value should have been appended to by the cust converter", 
        StringAppendCustomConverter.APPENDED_VALUE, token2);
  }
  
  
}