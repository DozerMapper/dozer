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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import net.pmonks.xml.dozer.test.ChildType;
import net.sf.dozer.util.mapping.converters.StringAppendCustomConverter;
import net.sf.dozer.util.mapping.fieldmapper.TestCustomFieldMapper;
import net.sf.dozer.util.mapping.util.CollectionUtils;
import net.sf.dozer.util.mapping.vo.AnotherTestObject;
import net.sf.dozer.util.mapping.vo.AnotherTestObjectPrime;
import net.sf.dozer.util.mapping.vo.ArrayCustConverterObj;
import net.sf.dozer.util.mapping.vo.ArrayCustConverterObjPrime;
import net.sf.dozer.util.mapping.vo.Car;
import net.sf.dozer.util.mapping.vo.Child;
import net.sf.dozer.util.mapping.vo.FieldValue;
import net.sf.dozer.util.mapping.vo.GetWeatherByZipCodeDocument;
import net.sf.dozer.util.mapping.vo.InsideTestObject;
import net.sf.dozer.util.mapping.vo.InsideTestObjectPrime;
import net.sf.dozer.util.mapping.vo.MetalThingyIF;
import net.sf.dozer.util.mapping.vo.PrimitiveArrayObj;
import net.sf.dozer.util.mapping.vo.PrimitiveArrayObjPrime;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.SimpleObjPrime;
import net.sf.dozer.util.mapping.vo.SimpleObjPrime2;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;
import net.sf.dozer.util.mapping.vo.TestObjectPrime2;
import net.sf.dozer.util.mapping.vo.GetWeatherByZipCodeDocument.GetWeatherByZipCode;
import net.sf.dozer.util.mapping.vo.allowedexceptions.TestException;
import net.sf.dozer.util.mapping.vo.allowedexceptions.ThrowException;
import net.sf.dozer.util.mapping.vo.allowedexceptions.ThrowExceptionPrime;
import net.sf.dozer.util.mapping.vo.context.ContextMapping;
import net.sf.dozer.util.mapping.vo.context.ContextMappingNested;
import net.sf.dozer.util.mapping.vo.context.ContextMappingNestedPrime;
import net.sf.dozer.util.mapping.vo.context.ContextMappingPrime;
import net.sf.dozer.util.mapping.vo.deep.House;
import net.sf.dozer.util.mapping.vo.deep2.Dest;
import net.sf.dozer.util.mapping.vo.deep2.Src;
import net.sf.dozer.util.mapping.vo.km.SomeVo;
import net.sf.dozer.util.mapping.vo.km.Sub;
import net.sf.dozer.util.mapping.vo.km.Super;
import net.sf.dozer.util.mapping.vo.map.MapToMap;
import net.sf.dozer.util.mapping.vo.map.MapToMapPrime;
import net.sf.dozer.util.mapping.vo.set.NamesArray;
import net.sf.dozer.util.mapping.vo.set.NamesSet;
import net.sf.dozer.util.mapping.vo.set.NamesSortedSet;
import net.sf.dozer.util.mapping.vo.set.SomeDTO;
import net.sf.dozer.util.mapping.vo.set.SomeOtherDTO;
import net.sf.dozer.util.mapping.vo.set.SomeVO;
import net.sf.dozer.util.mapping.vo.iface.ApplicationUser;
import net.sf.dozer.util.mapping.vo.iface.UpdateMember;
import net.sf.dozer.util.mapping.vo.index.Mccoy;
import net.sf.dozer.util.mapping.vo.index.MccoyPrime;
import net.sf.dozer.util.mapping.vo.isaccessible.Foo;
import net.sf.dozer.util.mapping.vo.isaccessible.FooPrime;
import net.sf.dozer.util.mapping.vo.isaccessible.PrivateConstructorBean;
import net.sf.dozer.util.mapping.vo.isaccessible.PrivateConstructorBeanPrime;

/**
 * @author garsombke.franz
 */
public class GranularDozerBeanMapperTest extends AbstractDozerTest {

  public void testFieldAccessible() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "fieldAttributeMapping.xml" });
    TestObject to = new TestObject();
    to.setFieldAccessible("fieldAccessible");
    to.setFieldAccessiblePrimInt(2);
    InsideTestObject ito = new InsideTestObject();
    ito.setLabel("label");
    to.setFieldAccessibleComplexType(ito);
    String[] stringArray = new String[] { "one", "two" };
    to.setFieldAccessibleArrayToList(stringArray);
    TestObjectPrime top = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    assertEquals("fieldAccessible", top.fieldAccessible);
    assertEquals("label", top.fieldAccessibleComplexType.getLabelPrime());
    assertEquals(2, top.fieldAccessiblePrimInt);
    assertEquals("one", top.fieldAccessibleArrayToList.get(0));
    assertEquals("two", top.fieldAccessibleArrayToList.get(1));

    // Map Back
    TestObject toDest = (TestObject) mapper.map(top, TestObject.class);
    assertEquals("fieldAccessible", toDest.getFieldAccessible());
    assertEquals("label", toDest.getFieldAccessibleComplexType().getLabel());
    assertEquals(2, toDest.getFieldAccessiblePrimInt());
    assertEquals("one", toDest.getFieldAccessibleArrayToList()[0]);
    assertEquals("two", toDest.getFieldAccessibleArrayToList()[1]);
  }

  public void testOverloadGetSetMethods() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "fieldAttributeMapping.xml" });
    TestObject to = new TestObject();
    Date date = new Date();
    to.setOverloadGetField(new Date());
    TestObjectPrime top = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    assertEquals(date, top.getOverloadSetField());

    // Map Back
    TestObject toDest = (TestObject) mapper.map(top, TestObject.class);
    assertEquals(date, toDest.getOverloadGetField());
  }

  public void testFieldCreateMethod() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "fieldAttributeMapping.xml" });
    TestObject to = new TestObject();
    InsideTestObject ito = new InsideTestObject();
    // we did not set any values. this will be set in the 'createMethod'
    to.setCreateMethodType(ito);
    TestObjectPrime top = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    assertEquals("myField", top.getCreateMethodType().getMyField());

    // Map Back
    TestObject toDest = (TestObject) mapper.map(top, TestObject.class);
    assertEquals("testCreateMethod", toDest.getCreateMethodType().getTestCreateMethod());
  }

  public void testDeepInterfaceWithHint() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "fieldAttributeMapping.xml" });
    InsideTestObject ito = new InsideTestObject();
    House house = new House();
    MetalThingyIF thingy = new Car();
    thingy.setName("name");
    house.setThingy(thingy);
    ito.setHouse(house);
    InsideTestObjectPrime itop = (InsideTestObjectPrime) mapper.map(ito, InsideTestObjectPrime.class);
    assertEquals("name", itop.getDeepInterfaceString());

    // Map Back
    InsideTestObject dest = (InsideTestObject) mapper.map(itop, InsideTestObject.class);
    assertEquals("name", ito.getHouse().getThingy().getName());

  }

  public void testIntegerToString() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "fieldAttributeMapping.xml" });
    TestObject to = new TestObject();
    Integer[] array = new Integer[] { new Integer(1), new Integer(2) };
    to.setArrayForLists(array);
    TestObjectPrime top = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    assertEquals("1", top.getListForArray().get(0));
    // Map Back
    // InsideTestObject dest = (InsideTestObject) mapper.map(itop,
    // InsideTestObject.class);
    // assertEquals("name", ito.getHouse().getThingy().getName());

  }

  public void testMapNull_MappingLevel() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "nullFieldMapping.xml" });
    // check that null does not override an existing value when map-null="false"
    AnotherTestObject src = new AnotherTestObject();
    src.setField3(null);
    src.setField4(null);
    AnotherTestObjectPrime dest = new AnotherTestObjectPrime();
    dest.setTo(new TestObject());
    dest.setField3("555");
    dest.getTo().setOne("4641");

    // dest field should remain unchanged
    mapper.map(src, dest);
    assertEquals("invalid dest field value", "555", dest.getField3());
    assertEquals("invalid dest field value2", "4641", dest.getTo().getOne());
  }

  public void testMapNull_MappingLevel2() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "nullFieldMapping.xml" });
    // Reverse mapping
    AnotherTestObjectPrime src = new AnotherTestObjectPrime();
    src.setTo(new TestObject());
    src.setField3(null);
    src.getTo().setOne(null);
    AnotherTestObject dest = new AnotherTestObject();
    dest.setField3("555");
    dest.setField4("4641");

    // dest field should remain unchanged
    mapper.map(src, dest);
    assertEquals("invalid dest field value", "555", dest.getField3());
    assertEquals("invalid dest field value2", "4641", dest.getField4());
  }

  public void testMapEmptyString_MappingLevel() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "nullFieldMapping.xml" });
    // check that "" does not override an existing value when
    // map-empty-string="false"
    AnotherTestObject src = new AnotherTestObject();
    src.setField3("");
    src.setField4("");
    AnotherTestObjectPrime dest = new AnotherTestObjectPrime();
    dest.setTo(new TestObject());
    dest.setField3("555");
    dest.getTo().setOne("4641");

    // dest field should remain unchanged
    mapper.map(src, dest);
    assertEquals("invalid dest field value", "555", dest.getField3());
    assertEquals("invalid dest field value2", "4641", dest.getTo().getOne());
  }

  public void testMapEmptyString_MappingLevel2() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "nullFieldMapping.xml" });
    // reverse mapping
    AnotherTestObjectPrime src = new AnotherTestObjectPrime();
    src.setTo(new TestObject());
    src.setField3("");
    src.getTo().setOne("");
    AnotherTestObject dest = new AnotherTestObject();
    dest.setField3("555");
    dest.setField4("4641");

    // dest field should remain unchanged
    mapper.map(src, dest);
    assertEquals("invalid dest field value", "555", dest.getField3());
    assertEquals("invalid dest field value2", "4641", dest.getField4());
  }

  public void testMapNull_ClassLevel() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "nullFieldMapping.xml" });
    // check that null does not override an existing value when map-null="false"
    TestObject src = new TestObject();
    src.setOne(null);
    TestObjectPrime2 dest = new TestObjectPrime2();
    dest.setOne("555");

    // dest field should remain unchanged
    mapper.map(src, dest);
    assertNotNull("dest should not be null", dest.getOne());
    assertEquals("invalid dest field value", "555", dest.getOne());

    // reverse mapping
    TestObjectPrime2 src2 = new TestObjectPrime2();
    src2.setOne(null);
    TestObject dest2 = new TestObject();
    dest2.setOne("555");

    // dest field should NOT remain unchanged
    mapper.map(src2, dest2);
    assertNull("dest should be null", dest2.getOne());
  }

  public void testMapEmptyString_ClassLevel() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "nullFieldMapping.xml" });
    // check that "" does not override an existing value when
    // map-empty-string="false"
    TestObject src = new TestObject();
    src.setOne("");
    TestObjectPrime2 dest = new TestObjectPrime2();
    dest.setOne("555");

    // dest field should remain unchanged
    mapper.map(src, dest);
    assertNotNull("dest should not be null", dest.getOne());
    assertEquals("invalid dest field value", "555", dest.getOne());

    // reverse mapping
    TestObjectPrime2 src2 = new TestObjectPrime2();
    src2.setOne("");
    TestObject dest2 = new TestObject();
    dest2.setOne("555");

    // dest field should NOT remain unchanged
    mapper.map(src2, dest2);
    assertNotNull("dest should not be null", dest2.getOne());
    assertEquals("dest should be an empty string", "", dest2.getOne());

  }

  public void testContextMappingWithNestedContext() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "contextMapping.xml" });

    ContextMappingNested cmn = new ContextMappingNested();
    cmn.setLoanNo("loanNoNested");
    List list = new ArrayList();
    list.add(cmn);
    ContextMapping cm = new ContextMapping();
    cm.setLoanNo("loanNo");
    cm.setContextList(list);

    ContextMappingPrime cmpA = (ContextMappingPrime) mapper.map(cm, ContextMappingPrime.class, "caseA");
    assertNull(cmpA.getLoanNo());
    assertNull(((ContextMappingNestedPrime) cmpA.getContextList().get(0)).getLoanNo());

    ContextMappingPrime cmpB = (ContextMappingPrime) mapper.map(cm, ContextMappingPrime.class, "caseB");
    assertEquals("loanNo", cmpB.getLoanNo());
    assertEquals("loanNoNested", ((ContextMappingNested) cmpB.getContextList().get(0)).getLoanNo());

    ContextMappingNestedPrime cmn2 = new ContextMappingNestedPrime();
    cmn2.setLoanNo("loanNoNested");
    List list2 = new ArrayList();
    list2.add(cmn2);
    ContextMappingPrime prime = new ContextMappingPrime();
    prime.setLoanNo("loanNo");
    prime.setContextList(list2);

    ContextMapping cmDest = (ContextMapping) mapper.map(prime, ContextMapping.class, "caseA");
    assertNull(cmDest.getLoanNo());
    assertNull(((ContextMappingNested) cmDest.getContextList().get(0)).getLoanNo());

    ContextMapping cmpBDest = (ContextMapping) mapper.map(prime, ContextMapping.class, "caseB");
    assertEquals("loanNo", cmpBDest.getLoanNo());
    assertEquals("loanNoNested", ((ContextMappingNestedPrime) cmpBDest.getContextList().get(0)).getLoanNo());
  }

  public void testXmlBeans() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "xmlBeansMapping.xml" });
    // Map from TestObject to XMLBeans
    TestObject to = new TestObject();
    to.setOne("one");
    GetWeatherByZipCodeDocument doc = (GetWeatherByZipCodeDocument) mapper.map(to, GetWeatherByZipCodeDocument.class);
    assertEquals(to.getOne(), doc.getGetWeatherByZipCode().getZipCode());

    // Map from XMLBeans to TestObject
    GetWeatherByZipCodeDocument res = GetWeatherByZipCodeDocument.Factory.newInstance();
    GetWeatherByZipCode zipCode = res.addNewGetWeatherByZipCode();
    zipCode.setZipCode("one");
    TestObject to2 = (TestObject) mapper.map(res, TestObject.class);
    assertEquals(res.getGetWeatherByZipCode().getZipCode(), to2.getOne());
  }

  public void testArrayToSortedSet() {
    NamesArray from = new NamesArray();
    NamesSortedSet to = null;
    String[] names = { "John", "Bill", "Tony", "Fred", "Bruce" };

    from.setNames(names);

    to = (NamesSortedSet) mapper.map(from, NamesSortedSet.class);

    assertNotNull(to);
    assertEquals(names.length, to.getNames().size());
  }

  public void testSortedSetToArray() {
    NamesSortedSet from = new NamesSortedSet();
    NamesArray to = null;
    SortedSet names = new TreeSet();

    names.add("Jen");
    names.add("Sue");
    names.add("Sally");
    names.add("Jill");
    from.setNames(names);

    to = (NamesArray) mapper.map(from, NamesArray.class);

    assertNotNull(to);
    assertEquals(names.size(), to.getNames().length);
  }

  public void testSetToSortedSet() {
    NamesSet from = new NamesSet();
    NamesSortedSet to = null;
    Set names = new HashSet();

    names.add("Red");
    names.add("Blue");
    names.add("Green");
    from.setNames(names);

    to = (NamesSortedSet) mapper.map(from, NamesSortedSet.class);

    assertNotNull(to);
    assertEquals(names.size(), to.getNames().size());
  }

  public void testSortedSetToSet() {
    NamesSortedSet from = new NamesSortedSet();
    NamesSet to = null;
    SortedSet names = new TreeSet();

    names.add("Bone");
    names.add("White");
    names.add("Beige");
    names.add("Ivory");
    names.add("Cream");
    names.add("Off white");
    from.setNames(names);

    to = (NamesSet) mapper.map(from, NamesSet.class);

    assertNotNull(to);
    assertEquals(names.size(), to.getNames().size());
  }

  public void testSetPrivateField() {
    mapper = super.getNewMapper(new String[] { "isaccessiblemapping.xml" });
    Foo src = new Foo();
    List list = new ArrayList();
    list.add("test1");
    list.add("test2");
    src.setCategories(list);

    FooPrime dest = (FooPrime) mapper.map(src, FooPrime.class);
    assertNotNull(dest);
  }

  public void testStringToIndexedSet_UsingMapSetMethod() {
    mapper = getNewMapper(new String[] { "indexMapping.xml" });
    Mccoy src = new Mccoy();
    src.setStringProperty(String.valueOf(System.currentTimeMillis()));

    MccoyPrime dest = (MccoyPrime) mapper.map(src, MccoyPrime.class);
    Set destSet = dest.getFieldValueObjects();
    assertNotNull("dest set should not be null", destSet);
    assertEquals("dest set should contain 1 entry", 1, destSet.size());
    Object entry = destSet.iterator().next();
    assertTrue("dest set entry should be instance of FieldValue", entry instanceof FieldValue);
    assertEquals("invalid value for dest object", src.getStringProperty(), ((FieldValue) entry).getValue());
    assertEquals("invalid key for dest object", "stringProperty", ((FieldValue) entry).getKey());
  }

  public void testStringToIndexedSet_UsingHint() {
    mapper = getNewMapper(new String[] { "indexMapping.xml" });
    Mccoy src = new Mccoy();
    src.setStringProperty(String.valueOf(System.currentTimeMillis()));
    src.setField2("someValue");

    MccoyPrime dest = (MccoyPrime) mapper.map(src, MccoyPrime.class, "usingDestHint");
    Set destSet = dest.getFieldValueObjects();
    assertNotNull("dest set should not be null", destSet);
    assertEquals("dest set should contain 1 entry", 1, destSet.size());
    Object entry = destSet.iterator().next();
    assertTrue("dest set entry should be instance of FieldValue", entry instanceof FieldValue);
    assertEquals("invalid value for dest object", src.getStringProperty(), ((FieldValue) entry).getValue());
    assertNull("FieldValue key field should be null", ((FieldValue) entry).getKey());

  }

  public void testAllowedExceptions() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "allowedExceptionsMapping.xml" });
    TestObject to = new TestObject();
    to.setThrowAllowedExceptionOnMap("throw me");
    try {
      TestObjectPrime top = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
      fail("We should have thrown TestException");
    } catch (RuntimeException e) {
      if (e instanceof TestException) {
        assertTrue(true);
      } else {
        fail("This should be an instance of TestException");
      }
    }
    TestObject to2 = new TestObject();
    to2.setThrowNonAllowedExceptionOnMap("do not throw me");
    try {
      TestObjectPrime top = (TestObjectPrime) mapper.map(to2, TestObjectPrime.class);
    } catch (RuntimeException e) {
      fail("This should not have been thrown");
    }
  }
  
  public void testAllowedExceptions_Implicit() throws Exception {
    MapperIF mapper = getNewMapper(new String[] {"implicitAllowedExceptionsMapping.xml"});
    ThrowException to = new ThrowException();
    to.setThrowAllowedException("throw me");
    try {
      ThrowExceptionPrime top = (ThrowExceptionPrime) mapper.map(to, ThrowExceptionPrime.class);
      fail("We should have thrown TestException");
    } catch (RuntimeException e) {
      if (e instanceof TestException) {
        assertTrue(true);
      } else {
        fail("This should be an instance of TestException");
      }
    }
    ThrowException to2 = new ThrowException();
    to2.setThrowNotAllowedException("do not throw me");
    try {
      ThrowExceptionPrime top = (ThrowExceptionPrime) mapper.map(to2, ThrowExceptionPrime.class);
    } catch (RuntimeException e) {
      fail("This should not have been thrown");
    }
  }
  
  
  public void testPrimitiveArrayToList() throws Exception {
    mapper = getNewMapper(new String[]{"primitiveArrayToListMapping.xml"});

    int[] i = new int[] {1,2,3};
    PrimitiveArrayObj src = new PrimitiveArrayObj();
    src.setField1(i);
    
    PrimitiveArrayObjPrime dest = (PrimitiveArrayObjPrime) mapper.map(src, PrimitiveArrayObjPrime.class);
    
    assertNotNull("dest list field should not be null", dest.getField1());
    assertEquals("invalid dest field size", i.length, dest.getField1().size());

    List srcObjectList = CollectionUtils.convertPrimitiveArrayToList(i);
    assertEquals("invalid dest field value", srcObjectList, dest.getField1());
  }
  
  public void testPrimitiveArrayToList_UsingHint() throws Exception {
    mapper = getNewMapper(new String[]{"primitiveArrayToListMapping.xml"});

    int[] srcArray = new int[] {1,2,3};
    PrimitiveArrayObj src = new PrimitiveArrayObj();
    src.setField1(srcArray);
    
    PrimitiveArrayObjPrime dest = (PrimitiveArrayObjPrime) mapper.map(src, PrimitiveArrayObjPrime.class, "primitiveToArrayUsingHint");
    
    assertNotNull("dest list field should not be null", dest.getField1());
    assertEquals("invalid dest field size", srcArray.length, dest.getField1().size());

    for(int i = 0; i < srcArray.length; i++) {
      String srcValue = String.valueOf(srcArray[i]);
      String resultValue = (String) dest.getField1().get(i);
      assertEquals("invalid result entry value", srcValue, resultValue);
    }
  }

  public void testInterface() throws Exception {
    mapper = getNewMapper(new String[]{"interfaceMapping.xml"});
    ApplicationUser user = new ApplicationUser();
    user.setSubscriberNumber("123");
  
    //Mapping works
    UpdateMember destObject = (UpdateMember) mapper.map(user, UpdateMember.class);
    
    assertEquals("invalid value for subsriber #", user.getSubscriberNumber(), destObject.getSubscriberKey().getSubscriberNumber());
  
    //Clear value
    destObject = new UpdateMember();
  
    //Mapping doesn't work
    mapper.map(user, destObject);
    
    assertNotNull("dest field should not be null", destObject.getSubscriberKey());
    assertEquals("invalid value for subsriber #", user.getSubscriberNumber(), destObject.getSubscriberKey().getSubscriberNumber());
  }
  
  
  public void testCustomFieldMapper() throws Exception {
    CustomFieldMapperIF customFieldMapper = new TestCustomFieldMapper();
    ((DozerBeanMapper) mapper).setCustomFieldMapper(customFieldMapper);
    
    String currentTime = String.valueOf(System.currentTimeMillis()); 
    SimpleObj src = new SimpleObj();
    src.setField1(currentTime);
    src.setField6("field6Value" + currentTime);
    
    SimpleObjPrime dest = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    
    assertNotNull("dest field1 should not be null", dest.getField1());
    assertNotNull("dest field6 should not be null", dest.getField6());
    assertEquals("dest field1 should have been set by custom field mapper", TestCustomFieldMapper.FIELD_VALUE, dest.getField1());
    assertEquals("dest field6 should NOT have been set by custom field mapper", src.getField6(), dest.getField6());
  }
  
  public void testPrivateConstructor() throws Exception {
	  PrivateConstructorBean src = PrivateConstructorBean.newInstance();
	  src.setField1("someValue");
	  
	  PrivateConstructorBeanPrime dest = (PrivateConstructorBeanPrime) mapper.map(src, PrivateConstructorBeanPrime.class);
	  
	  assertNotNull("dest bean should not be null", dest);
	  assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
  }
  
  
  /*
   * Related to feature request #1456486.  Deep mapping with custom getter/setter does not work
   */
  public void testDeepMapping_UsingCustomGetSetMethods() {
    mapper = super.getNewMapper(new String[]{"deepMappingUsingCustomGetSet.xml"});
    
    Src src = new Src();
    src.setSrcField("srcFieldValue");
    
    Dest dest = (Dest) mapper.map(src, Dest.class);
    
    assertNotNull(dest.getDestField().getNestedDestField().getNestedNestedDestField());
    assertEquals(src.getSrcField(), dest.getDestField().getNestedDestField().getNestedNestedDestField());
    
    Src dest2 = (Src)mapper.map(dest, Src.class);
    
    assertNotNull(dest2.getSrcField());
    assertEquals(dest.getDestField().getNestedDestField().getNestedNestedDestField(), dest2.getSrcField());
  }  
  
  /*
   * Bug #1549738 
   */  
  public void testSetMapping_UppercaseFieldNameInXML() throws Exception {
    //For some reason the resulting SomeVO contains a Set with 4 objects.  2 SomeOtherDTO's and 2 SomeOtherVO's.  I believe it
    //should only contain 2 SomeOtherVO's.  It has something to do with specifying the field name starting with cap in the mapping file.  If
    //you change the field mapping to start with lower case it seems to map correctly.
    MapperIF mapper = getNewMapper(new String[] { "setMappingWithUpperCaseFieldName.xml" }); 
    
    SomeDTO someDto = new SomeDTO(); 
    someDto.setField1(new Integer("1"));
    
    SomeOtherDTO someOtherDto = new SomeOtherDTO();
    someOtherDto.setOtherField2(someDto);
    someOtherDto.setOtherField3("value1"); 
    
    SomeDTO someDto2 = new SomeDTO(); 
    someDto2.setField1(new Integer("2")); 
    
    SomeOtherDTO someOtherDto2 = new SomeOtherDTO();
    someOtherDto2.setOtherField2(someDto2); 
    someOtherDto2.setOtherField3("value2");
    
    SomeDTO src = new SomeDTO();
    src.setField2(new SomeOtherDTO[] { someOtherDto2,someOtherDto });
    
    SomeVO dest = (SomeVO) mapper.map(src, SomeVO.class);
    
    assertEquals("incorrect resulting set size", src.getField2().length, dest.getField2().size());
    //TODO: add more asserts
  } 
  
  public void testGlobalBeanFactoryAppliedToDefaultMappings() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "global-configuration.xml" });
    TestObjectPrime dest = (TestObjectPrime) mapper.map(new TestObject(), TestObjectPrime.class);
    
    assertNotNull("created by factory name should not be null", dest.getCreatedByFactoryName());
    assertEquals("", "net.sf.dozer.util.mapping.factories.SampleDefaultBeanFactory", dest.getCreatedByFactoryName());
  }  
  
 
}