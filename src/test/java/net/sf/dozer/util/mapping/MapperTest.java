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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.util.mapping.util.TestDataFactory;
import net.sf.dozer.util.mapping.vo.AnotherTestObject;
import net.sf.dozer.util.mapping.vo.AnotherTestObjectPrime;
import net.sf.dozer.util.mapping.vo.Apple;
import net.sf.dozer.util.mapping.vo.Car;
import net.sf.dozer.util.mapping.vo.CustomConverterWrapper;
import net.sf.dozer.util.mapping.vo.CustomConverterWrapperPrime;
import net.sf.dozer.util.mapping.vo.CustomDoubleObject;
import net.sf.dozer.util.mapping.vo.CustomDoubleObjectIF;
import net.sf.dozer.util.mapping.vo.DehydrateTestObject;
import net.sf.dozer.util.mapping.vo.FurtherTestObject;
import net.sf.dozer.util.mapping.vo.FurtherTestObjectPrime;
import net.sf.dozer.util.mapping.vo.HintedOnly;
import net.sf.dozer.util.mapping.vo.HydrateTestObject;
import net.sf.dozer.util.mapping.vo.LoopObjectChild;
import net.sf.dozer.util.mapping.vo.LoopObjectParent;
import net.sf.dozer.util.mapping.vo.LoopObjectParentPrime;
import net.sf.dozer.util.mapping.vo.MethodFieldTestObject;
import net.sf.dozer.util.mapping.vo.MethodFieldTestObject2;
import net.sf.dozer.util.mapping.vo.NoCustomMappingsObject;
import net.sf.dozer.util.mapping.vo.NoCustomMappingsObjectPrime;
import net.sf.dozer.util.mapping.vo.NoDefaultConstructor;
import net.sf.dozer.util.mapping.vo.NoReadMethod;
import net.sf.dozer.util.mapping.vo.NoReadMethodPrime;
import net.sf.dozer.util.mapping.vo.NoSuperClass;
import net.sf.dozer.util.mapping.vo.NoWriteMethod;
import net.sf.dozer.util.mapping.vo.NoWriteMethodPrime;
import net.sf.dozer.util.mapping.vo.OneWayObject;
import net.sf.dozer.util.mapping.vo.OneWayObjectPrime;
import net.sf.dozer.util.mapping.vo.Orange;
import net.sf.dozer.util.mapping.vo.SubClass;
import net.sf.dozer.util.mapping.vo.SubClassPrime;
import net.sf.dozer.util.mapping.vo.TestCustomConverterHashMapObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterHashMapPrimeObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterObjectPrime;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;
import net.sf.dozer.util.mapping.vo.TestReferenceFoo;
import net.sf.dozer.util.mapping.vo.TestReferenceFooPrime;
import net.sf.dozer.util.mapping.vo.TestReferenceObject;
import net.sf.dozer.util.mapping.vo.TestReferencePrimeObject;
import net.sf.dozer.util.mapping.vo.TheFirstSubClass;
import net.sf.dozer.util.mapping.vo.Van;
import net.sf.dozer.util.mapping.vo.Vehicle;
import net.sf.dozer.util.mapping.vo.WeirdGetter;
import net.sf.dozer.util.mapping.vo.WeirdGetter2;
import net.sf.dozer.util.mapping.vo.WeirdGetterPrime;
import net.sf.dozer.util.mapping.vo.WeirdGetterPrime2;
import net.sf.dozer.util.mapping.vo.deep.DestDeepObj;
import net.sf.dozer.util.mapping.vo.deep.HomeDescription;
import net.sf.dozer.util.mapping.vo.deep.House;
import net.sf.dozer.util.mapping.vo.deep.Person;
import net.sf.dozer.util.mapping.vo.deep.Room;
import net.sf.dozer.util.mapping.vo.deep.SrcDeepObj;
import net.sf.dozer.util.mapping.vo.deep.SrcNestedDeepObj;
import net.sf.dozer.util.mapping.vo.inheritance.AnotherSubClass;
import net.sf.dozer.util.mapping.vo.inheritance.AnotherSubClassPrime;
import net.sf.dozer.util.mapping.vo.inheritance.BaseSubClassCombined;
import net.sf.dozer.util.mapping.vo.inheritance.GenericAbstractSuper;
import net.sf.dozer.util.mapping.vo.inheritance.GenericIF;
import net.sf.dozer.util.mapping.vo.inheritance.S2Class;
import net.sf.dozer.util.mapping.vo.inheritance.S2ClassPrime;
import net.sf.dozer.util.mapping.vo.inheritance.SClass;
import net.sf.dozer.util.mapping.vo.inheritance.SClassPrime;
import net.sf.dozer.util.mapping.vo.inheritance.Specific3;
import net.sf.dozer.util.mapping.vo.inheritance.SpecificObject;
import net.sf.dozer.util.mapping.vo.inheritance.WrapperSpecific;
import net.sf.dozer.util.mapping.vo.inheritance.WrapperSpecificPrime;
import net.sf.dozer.util.mapping.vo.map.CustomMap;
import net.sf.dozer.util.mapping.vo.map.CustomMapIF;
import net.sf.dozer.util.mapping.vo.map.MapTestObject;
import net.sf.dozer.util.mapping.vo.map.MapTestObjectPrime;
import net.sf.dozer.util.mapping.vo.map.MapToProperty;
import net.sf.dozer.util.mapping.vo.map.PropertyToMap;
import net.sf.dozer.util.mapping.vo.self.Account;
import net.sf.dozer.util.mapping.vo.self.SimpleAccount;

import org.apache.commons.lang.SerializationUtils;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 * Having good unit tests is very important for the bean mapper since there are so many mapping combinations.
 */
public class MapperTest extends AbstractDozerTest {
  private static MapperIF mapper;

  protected void setUp() throws Exception {
    super.setUp();
    if (mapper == null) {
      mapper = getNewMapper(new String[] { "dozerBeanMapping.xml" });
    }
  }

  public void testNoSourceObject() throws Exception {
    try {
      mapper.map(null, TestObjectPrime.class);
    } catch (MappingException e) {
      assertEquals("source object must not be null", e.getMessage());
    }
  }

  public void testNoDestinationClass() throws Exception {
    try {
      mapper.map(new TestObjectPrime(), null);
    } catch (MappingException e) {
      assertEquals("destination class must not be null", e.getMessage());
    }
  }

  public void testNoDefaultConstructor() throws Exception {
    try {
      mapper.map("test", NoDefaultConstructor.class);
    } catch (MappingException e) {

      assertEquals("java.lang.InstantiationException: net.sf.dozer.util.mapping.vo.NoDefaultConstructor", e
          .getMessage());

    }
  }

  public void testNoSourceValueIterateFieldMap() throws Exception {
    DehydrateTestObject inputDto = new DehydrateTestObject();
    HydrateTestObject hto = (HydrateTestObject) mapper.map(inputDto, HydrateTestObject.class);
    assertEquals(TestDataFactory.getExpectedTestNoSourceValueIterateFieldMapHydrateTestObject(), hto);
  }

  public void testCustomGetterSetterMap() throws Exception {
    WeirdGetter inputDto = new WeirdGetter();
    inputDto.placeValue("theValue");
    inputDto.setWildValue("wild");

    WeirdGetterPrime prime = (WeirdGetterPrime) mapper.map(inputDto, WeirdGetterPrime.class);
    assertNull(prime.getWildValue()); // testing global wildcard, expect this to be null
    assertEquals(inputDto.buildValue(), prime.getValue());

    inputDto = (WeirdGetter) mapper.map(prime, WeirdGetter.class);
    assertEquals(inputDto.buildValue(), prime.getValue());

    WeirdGetterPrime2 inputDto2 = new WeirdGetterPrime2();
    inputDto2.placeValue("theValue");
    WeirdGetter2 prime2 = (WeirdGetter2) mapper.map(inputDto2, WeirdGetter2.class);
    assertEquals(inputDto2.buildValue(), prime2.getValue());

    inputDto2 = (WeirdGetterPrime2) mapper.map(prime2, WeirdGetterPrime2.class);
    assertEquals(inputDto2.buildValue(), prime2.getValue());

  }

  public void testIncompatibleMapping() throws Exception {
    mapper.map(new Vehicle(), TestObject.class);
    // can not assert anything...no exception should be thrown and we should have a log statement
  }

  public void testNoClassMappings() throws Exception {
    MapperIF mapper = new DozerBeanMapper();
    // Should attempt mapping even though it is not in the beanmapping.xml file
    NoCustomMappingsObjectPrime dest1 = (NoCustomMappingsObjectPrime) mapper.map(TestDataFactory
        .getInputTestNoClassMappingsNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    NoCustomMappingsObject source = (NoCustomMappingsObject) mapper.map(dest1, NoCustomMappingsObject.class);
    NoCustomMappingsObjectPrime dest3 = (NoCustomMappingsObjectPrime) mapper.map(source,
        NoCustomMappingsObjectPrime.class);
    assertEquals(dest1, dest3);
  }  
   
  public void testImplicitInnerObject() {
    // This tests that we implicitly map an inner object to an inner object without defining it in the mapping file
    TestObject to = new TestObject();
    to.setNoMappingsObj(TestDataFactory.getInputTestNoClassMappingsNoCustomMappingsObject());
    TestObjectPrime dest2 = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    TestObject source2 = (TestObject) mapper.map(dest2, TestObject.class);
    TestObjectPrime dest4 = (TestObjectPrime) mapper.map(source2, TestObjectPrime.class);
    assertEquals(dest2, dest4);
  }

  public void testMapField() throws Exception {
    NoCustomMappingsObjectPrime dest = (NoCustomMappingsObjectPrime) mapper.map(TestDataFactory
        .getInputTestMapFieldWithMapNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);

    NoCustomMappingsObject source = (NoCustomMappingsObject) mapper.map(dest, NoCustomMappingsObject.class);
    NoCustomMappingsObjectPrime dest2 = (NoCustomMappingsObjectPrime) mapper.map(source,
        NoCustomMappingsObjectPrime.class);

    assertEquals(dest2, dest);

    dest = (NoCustomMappingsObjectPrime) mapper.map(new NoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = (NoCustomMappingsObject) mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = (NoCustomMappingsObjectPrime) mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // empty Map
    dest = (NoCustomMappingsObjectPrime) mapper.map(TestDataFactory
        .getInputTestMapFieldWithEmptyMapNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = (NoCustomMappingsObject) mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = (NoCustomMappingsObjectPrime) mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);
  }

  public void testSetField() throws Exception {
    // basic set --> set
    NoCustomMappingsObjectPrime dest = (NoCustomMappingsObjectPrime) mapper.map(TestDataFactory
        .getInputTestSetFieldWithSetNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    NoCustomMappingsObject source = (NoCustomMappingsObject) mapper.map(dest, NoCustomMappingsObject.class);
    NoCustomMappingsObjectPrime dest2 = (NoCustomMappingsObjectPrime) mapper.map(source,
        NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // null set --> set
    dest = (NoCustomMappingsObjectPrime) mapper.map(new NoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = (NoCustomMappingsObject) mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = (NoCustomMappingsObjectPrime) mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // empty set --> set
    dest = (NoCustomMappingsObjectPrime) mapper.map(TestDataFactory
        .getInputTestSetFieldWithSetEmptyCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = (NoCustomMappingsObject) mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = (NoCustomMappingsObjectPrime) mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // complex type set -->
    dest = (NoCustomMappingsObjectPrime) mapper.map(TestDataFactory
        .getInputTestSetFieldComplexSetNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = (NoCustomMappingsObject) mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = (NoCustomMappingsObjectPrime) mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);
  }

  public void testListField() throws Exception {
    // test empty list --> empty list
    TestObjectPrime dest = (TestObjectPrime) mapper.map(TestDataFactory.getInputTestListFieldEmptyListTestObject(),
        TestObjectPrime.class);
    TestObject source = (TestObject) mapper.map(dest, TestObject.class);
    TestObjectPrime dest2 = (TestObjectPrime) mapper.map(source, TestObjectPrime.class);
    assertEquals(dest2, dest);

    // test empty array --> empty list
    dest = (TestObjectPrime) mapper.map(TestDataFactory.getInputTestListFieldArrayListTestObject(),
        TestObjectPrime.class);
    source = (TestObject) mapper.map(dest, TestObject.class);
    dest2 = (TestObjectPrime) mapper.map(source, TestObjectPrime.class);
    assertEquals(dest2, dest);
  }

  public void testListUsingDestHint() throws Exception {
    TestObjectPrime dest = (TestObjectPrime) mapper.map(TestDataFactory.getInputTestListUsingDestHintTestObject(),
        TestObjectPrime.class);
    TestObject source = (TestObject) mapper.map(dest, TestObject.class);
    TestObjectPrime dest2 = (TestObjectPrime) mapper.map(source, TestObjectPrime.class);
    assertEquals(dest, dest2);
  }

  public void testExcludeFields() throws Exception {
    // Map
    TestObjectPrime prime = (TestObjectPrime) mapper.map(TestDataFactory.getInputGeneralMappingTestObject(),
        TestObjectPrime.class);
    assertEquals("excludeMe", prime.getExcludeMe());
    assertEquals("excludeMeOneWay", prime.getExcludeMeOneWay());
    // map back
    TestObject to = (TestObject) mapper.map(prime, TestObject.class);
    assertNull(to.getExcludeMe());
    assertEquals("excludeMeOneWay", to.getExcludeMeOneWay());
  }

  public void testGeneralMapping() throws Exception {
    // Map
    TestObject to = TestDataFactory.getInputGeneralMappingTestObject();
    TestObjectPrime prime = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    // valdidate that we copied by object reference -
    TestObject source = (TestObject) mapper.map(prime, TestObject.class);
    TestObjectPrime prime2 = (TestObjectPrime) mapper.map(source, TestObjectPrime.class);
    assertEquals(prime2, prime);
  }

  public void testMappingNoDestSpecified() throws Exception {
    // Map
    House src = TestDataFactory.getHouse();
    HomeDescription dest = (HomeDescription) mapper.map(src, HomeDescription.class);
    House src2 = (House) mapper.map(dest, House.class);
    HomeDescription dest2 = (HomeDescription) mapper.map(src2, HomeDescription.class);

    long[] prim = { 1, 2, 3, 1, 2, 3 };
    // cumulative relationship
    dest.setPrim(prim);
    assertEquals(dest, dest2);

    // By reference
    src = TestDataFactory.getHouse();
    House houseClone = (House) SerializationUtils.clone(src);
    dest = (HomeDescription) mapper.map(src, HomeDescription.class);
    mapper.map(dest, House.class);
    assertEquals(houseClone, src);
  }

  public void testGeneralMappingPassByReference() throws Exception {
    // Map
    TestObject to = TestDataFactory.getInputGeneralMappingTestObject();
    TestObject toClone = (TestObject) SerializationUtils.clone(to);
    TestObjectPrime prime = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    mapper.map(prime, to);
    // more objects should be added to the clone from the ArrayList
    TheFirstSubClass fsc = new TheFirstSubClass();
    fsc.setS("s");
    toClone.getHintList().add(fsc);
    toClone.getHintList().add(fsc);
    toClone.getEqualNamedList().add("1value");
    toClone.getEqualNamedList().add("2value");
    int[] pa = { 0, 1, 2, 3, 4, 0, 1, 2, 3, 4 };
    int[] intArray = { 1, 1, 1, 1 };
    Integer[] integerArray = { new Integer(1), new Integer(1), new Integer(1), new Integer(1) };
    toClone.setAnArray(intArray);
    toClone.setArrayForLists(integerArray);
    toClone.setPrimArray(pa);
    toClone.setBlankDate(null);
    toClone.setBlankStringToLong(null);
    // since we copy by reference the attribute copyByReference we need to null it out. The clone method above creates
    // two versions of it...
    // which is incorrect
    to.setCopyByReference(null);
    toClone.setCopyByReference(null);
    to.setCopyByReferenceDeep(null);
    toClone.setCopyByReferenceDeep(null);
    to.setGlobalCopyByReference(null);
    toClone.setGlobalCopyByReference(null);
    // null out string array because we get NPE since a NULL value in the String []
    to.setStringArrayWithNullValue(null);
    toClone.setStringArrayWithNullValue(null);
    toClone.setExcludeMeOneWay("excludeMeOneWay");
    assertEquals(toClone, to);
  }

  public void testLongToLongMapping() throws Exception {
    // Map
    TestObject source = TestDataFactory.getInputGeneralMappingTestObject();
    source.setAnotherLongValue(42);
    TestObjectPrime prime2 = (TestObjectPrime) mapper.map(source, TestObjectPrime.class);
    Long value = prime2.getTheLongValue();
    assertEquals(value.longValue(), 42);
  }

  public void testNoWildcards() throws Exception {
    // Map
    FurtherTestObjectPrime prime = (FurtherTestObjectPrime) mapper.map(TestDataFactory
        .getInputTestNoWildcardsFurtherTestObject(), FurtherTestObjectPrime.class);
    FurtherTestObject source = (FurtherTestObject) mapper.map(prime, FurtherTestObject.class);
    FurtherTestObjectPrime prime2 = (FurtherTestObjectPrime) mapper.map(source, FurtherTestObjectPrime.class);
    assertEquals(prime2, prime);
  }

  public void testHydrateAndMore() throws Exception {
    HydrateTestObject dest = (HydrateTestObject) mapper.map(TestDataFactory
        .getInputTestHydrateAndMoreDehydrateTestObject(), HydrateTestObject.class);
    // validate results
    assertEquals(TestDataFactory.getExpectedTestHydrateAndMoreHydrateTestObject(), dest);

    // map it back
    DehydrateTestObject dhto = (DehydrateTestObject) mapper.map(TestDataFactory
        .getInputTestHydrateAndMoreHydrateTestObject(), DehydrateTestObject.class);
    assertEquals(TestDataFactory.getExpectedTestHydrateAndMoreDehydrateTestObject(), dhto);
  }

  public void testDeepProperties() throws Exception {
    House src = TestDataFactory.getHouse();
    HomeDescription dest = (HomeDescription) mapper.map(src, HomeDescription.class);
    House src2 = (House) mapper.map(dest, House.class);
    HomeDescription dest2 = (HomeDescription) mapper.map(src2, HomeDescription.class);

    long[] prim = { 1, 2, 3, 1, 2, 3 };
    // cumulative relationship
    dest.setPrim(prim);
    assertEquals(dest, dest2);

    // By reference
    src = TestDataFactory.getHouse();
    House houseClone = (House) SerializationUtils.clone(src);
    dest = (HomeDescription) mapper.map(src, HomeDescription.class);
    mapper.map(dest, src);
    // cumulative relationship
    int[] prims = { 1, 2, 3, 1, 2, 3 };
    houseClone.getOwner().setPrim(prims);
    // add two more rooms
    Room room1 = new Room();
    room1.setName("Living");
    Room room2 = new Room();
    room2.setName("kitchen");
    Van van = new Van();
    van.setName("van2");
    houseClone.getRooms().add(room1);
    houseClone.getRooms().add(room2);
    houseClone.getCustomSetGetMethod().add(van);
    assertEquals(houseClone, src);
  }

  public void testDeepMapping() throws Exception {
    SrcDeepObj src = TestDataFactory.getSrcDeepObj();
    DestDeepObj dest = (DestDeepObj) mapper.map(src, DestDeepObj.class);
    SrcDeepObj src2 = (SrcDeepObj) mapper.map(dest, SrcDeepObj.class);
    DestDeepObj dest2 = (DestDeepObj) mapper.map(src2, DestDeepObj.class);

    assertEquals(src, src2);
    assertEquals(dest, dest2);

  }

  public void testMethodMapping() throws Exception {
    MethodFieldTestObject sourceObj = new MethodFieldTestObject();
    sourceObj.setIntegerStr("1500");
    sourceObj.setPriceItem("3500");
    sourceObj.setFieldOne("fieldOne");
    MethodFieldTestObject2 result = (MethodFieldTestObject2) mapper.map(sourceObj, MethodFieldTestObject2.class);
    assertEquals("invalid result object size", 1, result.getIntegerList().size());
    assertEquals("invalid result object value", 3500, result.getTotalPrice());
    assertEquals("invalid result object value", "fieldOne", result.getFieldOne());
    // map back
    MethodFieldTestObject result2 = (MethodFieldTestObject) mapper.map(result, MethodFieldTestObject.class);
    // if no exceptions we thrown we are good. stopOnErrors = true. both values will be null
    // since this is a one-way mapping we shouldn't have a value
    assertNull(result2.getFieldOne());
  }

  public void testCustomConverterMapping() throws Exception {
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

  public void testStringToDateMapping() throws Exception {
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS");
    String dateStr = "01/29/1975 10:45:13:25";
    TestObject sourceObj = new TestObject();
    sourceObj.setDateStr(dateStr);
    TestObjectPrime result = (TestObjectPrime) mapper.map(sourceObj, TestObjectPrime.class);
    assertEquals(df.parse(dateStr), result.getDateFromStr());
    assertEquals(dateStr, df.format(result.getDateFromStr()));

    TestObject result2 = (TestObject) mapper.map(result, TestObject.class);
    assertEquals(df.format(result.getDateFromStr()), result2.getDateStr());
    assertEquals(result.getDateFromStr(), df.parse(result2.getDateStr()));
  }

  public void testNullDestClass() throws Exception {
    try {
      mapper.map(new TestObject(), null);
      fail("should have thrown mapping exception");
    } catch (MappingException e) {
    }
  }

  public void testNullDestObj() throws Exception {
    try {
      Object destObj = null;
      mapper.map(new TestObject(), destObj);
      fail("should have thrown mapping exception");
    } catch (MappingException e) {
    }
  }

  public void testNoReadMethod() throws Exception {
    //If the field doesnt have a getter/setter, dont add it is a default field to be mapped.
    NoReadMethod src = new NoReadMethod();
    src.setNoReadMethod("somevalue");
    
    NoReadMethodPrime dest = (NoReadMethodPrime) mapper.map(src, NoReadMethodPrime.class);
    assertNull("field should be null because no read method exists for field", dest.getXXXXX());
  }

  public void testNoReadMethodSameClassTypes() throws Exception {
    //If the field doesnt have a getter/setter, dont add it is a default field to be mapped.
    NoReadMethod src = new NoReadMethod();
    src.setNoReadMethod("somevalue");
    
    NoReadMethod dest = (NoReadMethod) mapper.map(src, NoReadMethod.class);
    assertNull("field should be null because no read method exists for field", dest.getXXXXX());
  }
  
  public void testNoReadMethod_GetterOnlyWithParams() throws Exception {
    //Dont use getter methods that have a param when discovering default fields to be mapped.
    NoReadMethod src = new NoReadMethod();
    src.setOtherNoReadMethod("someValue");
    
    NoReadMethod dest = (NoReadMethod) mapper.map(src, NoReadMethod.class);
    assertNull("field should be null because no read method exists for field", dest.getOtherNoReadMethod(-1));
  }
  
  public void testNoWriteMethod() throws Exception {
    NoWriteMethod src = new NoWriteMethod();
    src.setXXXXXX("someValue");
    
    NoWriteMethodPrime dest = (NoWriteMethodPrime) mapper.map(src, NoWriteMethodPrime.class);
    assertNull("field should be null because no write method exists for field", dest.getNoWriteMethod());
    
  }
  
  public void testNoWriteMethodSameClassTypes() throws Exception {
    //When mapping between identical types, if the field doesnt have a getter/setter, dont
    //add it is a default field to be mapped.
    NoWriteMethod src = new NoWriteMethod();
    src.setXXXXXX("someValue");
    
    mapper.map(new NoReadMethod(), NoReadMethod.class);
    
    NoWriteMethod dest =  (NoWriteMethod) mapper.map(src, NoWriteMethod.class);
    assertNull("field should be null because no write method exists for field", dest.getNoWriteMethod());
  }
  

  public void testOneWayMapping() throws Exception {
    // Map
    OneWayObject owo = new OneWayObject();
    OneWayObjectPrime owop = new OneWayObjectPrime();
    SrcNestedDeepObj nested = new SrcNestedDeepObj();
    nested.setSrc1("src1");
    owo.setNested(nested);
    owop.setOneWayPrimeField("oneWayField");
    owop.setSetOnlyField("setOnly");
    List list = new ArrayList();
    list.add("stringToList");
    list.add("src1");
    owop.setStringList(list);
    owo.setOneWayField("oneWayField");
    owo.setStringToList("stringToList");
    OneWayObjectPrime prime = (OneWayObjectPrime) mapper.map(owo, OneWayObjectPrime.class);

    assertEquals(owop, prime);

    OneWayObject source = (OneWayObject) mapper.map(prime, OneWayObject.class);
    // should have not mapped this way
    assertEquals(null, source.getOneWayField());
  }

  public void testNullField() throws Exception {
    AnotherTestObject src = new AnotherTestObject();
    src.setField2(null);
    AnotherTestObjectPrime dest = new AnotherTestObjectPrime();
    dest.setField2(Integer.valueOf("555"));
    // check that null overrides an existing value
    mapper.map(src, dest);
    assertNull("dest field should be null", dest.getField2());
  }

  public void testNullField2() throws Exception {
    // Test that String --> String with an empty String input value results
    // in the destination field being an empty String and not null.
    String input = "";
    TestObject src = new TestObject();
    src.setOne(input);

    TestObjectPrime dest = (TestObjectPrime) mapper.map(src, TestObjectPrime.class);
    assertNotNull("dest field should not be null", dest.getOnePrime());
    assertEquals("invalid dest field value", input, dest.getOnePrime());
  }

  public void testNullToPrimitive() throws Exception {
    AnotherTestObject src = new AnotherTestObject();
    AnotherTestObjectPrime prime = new AnotherTestObjectPrime();
    TestObject to = new TestObject();
    to.setThePrimitive(AnotherTestObjectPrime.DEFAULT_FIELD1);
    prime.setTo(to);
    mapper.map(src, prime);
    // check primitive on deep field
    // primitive should still be default
    assertEquals("invalid field value", AnotherTestObjectPrime.DEFAULT_FIELD1, prime.getField1());
    assertEquals("invalid field value", AnotherTestObjectPrime.DEFAULT_FIELD1, prime.getTo().getThePrimitive());
  }

  public void testMapByReference() throws Exception {
    // Map
    TestReferenceObject tro = new TestReferenceObject();
    TestReferenceFoo foo1 = new TestReferenceFoo();
    foo1.setA("a");
    TestReferenceFoo foo = new TestReferenceFoo();
    foo.setA("a");
    foo.setB(null);
    foo.setC("c");
    List list2 = new ArrayList();
    list2.add(foo);
    tro.setListA(list2);
    tro.setArrayToArrayCumulative(new Object[] { foo1 });
    TestReferenceFoo foo2 = new TestReferenceFoo();
    foo2.setA("a");
    foo2.setB(null);
    foo2.setC("c");
    TestReferenceFoo foo3 = new TestReferenceFoo();
    foo3.setA("a");
    foo3.setB(null);
    foo3.setC("c");
    tro.setArrayToArrayNoncumulative(new Object[] { foo2 });
    List list3 = new ArrayList();
    list3.add("string1");
    list3.add("string2");
    tro.setListToArray(list3);
    int[] pa = { 1, 2, 3 };
    tro.setPrimitiveArray(pa);
    Integer[] integerArray = { new Integer(1), new Integer(2) };
    tro.setPrimitiveArrayWrapper(integerArray);
    Set set = new HashSet();
    TestReferenceFoo foo4 = new TestReferenceFoo();
    foo4.setA("a");
    set.add(foo4);
    tro.setSetToSet(set);
    Car car = new Car();
    car.setName("myName");
    tro.setCars(new Car[] { car });
    Car car2 = new Car();
    car2.setName("myName");
    List vehicles = new ArrayList();
    vehicles.add(car2);
    tro.setVehicles(vehicles);
    TestReferenceObject toClone = (TestReferenceObject) SerializationUtils.clone(tro);
    TestReferencePrimeObject trop = (TestReferencePrimeObject) mapper.map(tro, TestReferencePrimeObject.class);
    assertEquals("myName", ((Van) trop.getVans()[0]).getName());
    assertEquals("myName", ((Van) trop.getMoreVans()[0]).getName());

    TestReferenceFooPrime fooPrime = (TestReferenceFooPrime) trop.getListAPrime().get(0);
    fooPrime.setB("b");
    TestReferenceFooPrime fooPrime2 = (TestReferenceFooPrime) trop.getArrayToArrayNoncumulative()[0];
    fooPrime2.setB("b");
    mapper.map(trop, tro);
    // make sure we update the array list and didnt lose the value 'c' - non-cumulative
    assertEquals("c", ((TestReferenceFoo) tro.getListA().get(0)).getC());
    assertEquals("c", ((TestReferenceFoo) tro.getArrayToArrayNoncumulative()[0]).getC());
    // cumulative
    toClone.setArrayToArrayCumulative(new Object[] { foo1, foo1 });
    toClone.setCars(new Car[] { car, car });
    Van van = new Van();
    van.setName("myName");
    toClone.getVehicles().add(van);
    // cumulative
    toClone.getListToArray().add("string1");
    toClone.getListToArray().add("string2");
    int[] paClone = { 1, 2, 3, 1, 2, 3 };
    toClone.setPrimitiveArray(paClone);
    Integer[] integerArrayClone = { new Integer(1), new Integer(2), new Integer(1), new Integer(2) };
    toClone.setPrimitiveArrayWrapper(integerArrayClone);
    assertEquals(toClone, tro);
  }

  public void testHintedOnlyConverter() throws Exception {
    String hintStr = "where's my hint?";

    CustomConverterWrapper source = new CustomConverterWrapper();
    HintedOnly hint = new HintedOnly();
    hint.setStr(hintStr);
    source.addHint(hint);

    CustomConverterWrapperPrime dest = (CustomConverterWrapperPrime) mapper.map(source,
        CustomConverterWrapperPrime.class);
    String destHintStr = (String) dest.getNeedsHint().iterator().next();
    assertNotNull(destHintStr);
    assertEquals(hintStr, destHintStr);

    CustomConverterWrapper sourcePrime = (CustomConverterWrapper) mapper.map(dest, CustomConverterWrapper.class);
    String sourcePrimeHintStr = ((HintedOnly) sourcePrime.getNeedsHint().iterator().next()).getStr();
    assertNotNull(sourcePrimeHintStr);
    assertEquals(hintStr, sourcePrimeHintStr);
  }

  public void testDeepPropertyOneWay() throws Exception {
    House house = new House();
    Person owner = new Person();
    owner.setYourName("myName");
    house.setOwner(owner);
    HomeDescription desc = (HomeDescription) mapper.map(house, HomeDescription.class);
    assertEquals(desc.getDescription().getMyName(), "myName");
    // make sure we don't map back
    House house2 = (House) mapper.map(desc, House.class);
    assertNull(house2.getOwner().getYourName());
  }

  public void testSelfMapping() throws Exception {
    SimpleAccount simpleAccount = new SimpleAccount();
    simpleAccount.setName("name");
    simpleAccount.setPostcode(1234);
    simpleAccount.setStreetName("streetName");
    simpleAccount.setSuburb("suburb");
    Account account = (Account) mapper.map(simpleAccount, Account.class);
    assertEquals(account.getAddress().getStreet(), simpleAccount.getStreetName());
    assertEquals(account.getAddress().getSuburb(), simpleAccount.getSuburb());
    assertEquals(account.getAddress().getPostcode(), simpleAccount.getPostcode());

    // try mapping back
    SimpleAccount dest = (SimpleAccount) mapper.map(account, SimpleAccount.class);
    assertEquals(account.getAddress().getStreet(), dest.getStreetName());
    assertEquals(account.getAddress().getSuburb(), dest.getSuburb());
    assertEquals(account.getAddress().getPostcode(), dest.getPostcode());
  }

  public void testSetToArray() throws Exception {
    Orange orange1 = new Orange();
    orange1.setName("orange1");
    Orange orange2 = new Orange();
    orange2.setName("orange2");
    Orange orange3 = new Orange();
    orange3.setName("orange3");
    Orange orange4 = new Orange();
    orange4.setName("orange4");
    Set set = new HashSet();
    set.add(orange1);
    set.add(orange2);
    Set set2 = new HashSet();
    set2.add(orange3);
    set2.add(orange4);
    TestObject to = new TestObject();
    to.setSetToArray(set);
    to.setSetToObjectArray(set2);
    TestObjectPrime top = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    assertEquals("orange1", top.getArrayToSet()[0].getName());
    assertEquals("orange2", top.getArrayToSet()[1].getName());
    // Hashset changes the order
    assertEquals("orange3", ((Apple) top.getObjectArrayToSet()[1]).getName());
    assertEquals("orange4", ((Apple) top.getObjectArrayToSet()[0]).getName());
    Apple apple = new Apple();
    apple.setName("apple1");
    Apple[] appleArray = { apple };
    top.setSetToArrayWithValues(appleArray);
    // now map back
    Apple apple2 = new Apple();
    apple2.setName("apple2");
    TestObject toDest = new TestObject();
    Set hashSet = new HashSet();
    hashSet.add(apple2);
    toDest.setSetToArrayWithValues(hashSet);
    mapper.map(top, toDest);
    assertTrue(toDest.getSetToArray().contains(top.getArrayToSet()[0]));
    assertTrue(toDest.getSetToArray().contains(top.getArrayToSet()[1]));
    assertTrue(toDest.getSetToObjectArray().contains((Apple) top.getObjectArrayToSet()[0]));
    assertTrue(toDest.getSetToObjectArray().contains((Apple) top.getObjectArrayToSet()[1]));
    assertTrue(toDest.getSetToArrayWithValues().contains(apple));
    assertTrue(toDest.getSetToArrayWithValues().contains(apple2));
    assertTrue(toDest.getSetToArrayWithValues() instanceof HashSet);
  }

  public void testSetToList() throws Exception {
    Orange orange1 = new Orange();
    orange1.setName("orange1");
    Orange orange2 = new Orange();
    orange2.setName("orange2");
    Set set = new HashSet();
    set.add(orange1);
    set.add(orange2);
    TestObject to = new TestObject();
    to.setSetToList(set);
    TestObjectPrime top = (TestObjectPrime) mapper.map(to, TestObjectPrime.class);
    assertEquals("orange1", ((Orange) top.getListToSet().get(0)).getName());
    assertEquals("orange2", ((Orange) top.getListToSet().get(1)).getName());
    List list = new ArrayList();
    Orange orange4 = new Orange();
    orange4.setName("orange4");
    list.add(orange4);
    top.setSetToListWithValues(list);
    // Map back
    Orange orange3 = new Orange();
    orange3.setName("orange3");
    Set set2 = new HashSet();
    set2.add(orange3);
    set2.add(orange4);
    TestObject toDest = new TestObject();
    toDest.setSetToListWithValues(set2);
    mapper.map(top, toDest);
    assertTrue(toDest.getSetToList().contains(top.getListToSet().get(0)));
    assertTrue(toDest.getSetToList().contains(top.getListToSet().get(1)));
    assertTrue(toDest.getSetToListWithValues().contains(orange3));
    assertTrue(toDest.getSetToListWithValues().contains(orange4));
  }

}