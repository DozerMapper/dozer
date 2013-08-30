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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.vo.Apple;
import org.dozer.vo.Car;
import org.dozer.vo.CustomConverterWrapper;
import org.dozer.vo.CustomConverterWrapperPrime;
import org.dozer.vo.DehydrateTestObject;
import org.dozer.vo.FurtherTestObject;
import org.dozer.vo.FurtherTestObjectPrime;
import org.dozer.vo.HintedOnly;
import org.dozer.vo.HydrateTestObject;
import org.dozer.vo.NoCustomMappingsObject;
import org.dozer.vo.NoCustomMappingsObjectPrime;
import org.dozer.vo.OneWayObject;
import org.dozer.vo.OneWayObjectPrime;
import org.dozer.vo.Orange;
import org.dozer.vo.TestObject;
import org.dozer.vo.TestObjectPrime;
import org.dozer.vo.TestReferenceFoo;
import org.dozer.vo.TestReferenceFooPrime;
import org.dozer.vo.TestReferenceObject;
import org.dozer.vo.TestReferencePrimeObject;
import org.dozer.vo.TheFirstSubClass;
import org.dozer.vo.Van;
import org.dozer.vo.WeirdGetter;
import org.dozer.vo.WeirdGetter2;
import org.dozer.vo.WeirdGetterPrime;
import org.dozer.vo.WeirdGetterPrime2;
import org.dozer.vo.deep.HomeDescription;
import org.dozer.vo.deep.House;
import org.dozer.vo.deep.Room;
import org.dozer.vo.deep.SrcNestedDeepObj;
import org.dozer.vo.self.Account;
import org.dozer.vo.self.SimpleAccount;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class MapperTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("dozerBeanMapping.xml");
  }

  @Test
  public void testNoSourceValueIterateFieldMap() throws Exception {
    DehydrateTestObject inputDto = newInstance(DehydrateTestObject.class);
    HydrateTestObject hto = mapper.map(inputDto, HydrateTestObject.class);
    assertEquals(testDataFactory.getExpectedTestNoSourceValueIterateFieldMapHydrateTestObject(), hto);
  }

  @Test
  public void testCustomGetterSetterMap() throws Exception {
    WeirdGetter inputDto = newInstance(WeirdGetter.class);
    inputDto.placeValue("theValue");
    inputDto.setWildValue("wild");

    WeirdGetterPrime prime = mapper.map(inputDto, WeirdGetterPrime.class);
    assertNull(prime.getWildValue()); // testing global wildcard, expect this to be null
    assertEquals(inputDto.buildValue(), prime.getValue());

    inputDto = mapper.map(prime, WeirdGetter.class);
    assertEquals(inputDto.buildValue(), prime.getValue());

    WeirdGetterPrime2 inputDto2 = newInstance(WeirdGetterPrime2.class);
    inputDto2.placeValue("theValue");
    WeirdGetter2 prime2 = mapper.map(inputDto2, WeirdGetter2.class);
    assertEquals(inputDto2.buildValue(), prime2.getValue());

    inputDto2 = mapper.map(prime2, WeirdGetterPrime2.class);
    assertEquals(inputDto2.buildValue(), prime2.getValue());

  }

  @Test
  public void testNoClassMappings() throws Exception {
    Mapper mapper = new DozerBeanMapper();
    // Should attempt mapping even though it is not in the beanmapping.xml file
    NoCustomMappingsObjectPrime dest1 = mapper.map(testDataFactory.getInputTestNoClassMappingsNoCustomMappingsObject(),
        NoCustomMappingsObjectPrime.class);
    NoCustomMappingsObject source = mapper.map(dest1, NoCustomMappingsObject.class);
    NoCustomMappingsObjectPrime dest3 = mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest1, dest3);
  }

  @Test
  public void testImplicitInnerObject() {
    // This tests that we implicitly map an inner object to an inner object without defining it in the mapping file
    TestObject to = newInstance(TestObject.class);
    to.setNoMappingsObj(testDataFactory.getInputTestNoClassMappingsNoCustomMappingsObject());
    TestObjectPrime dest2 = mapper.map(to, TestObjectPrime.class);
    TestObject source2 = mapper.map(dest2, TestObject.class);
    TestObjectPrime dest4 = mapper.map(source2, TestObjectPrime.class);
    assertEquals(dest2, dest4);
  }

  @Test
  public void testMapField() throws Exception {
    NoCustomMappingsObjectPrime dest = mapper.map(testDataFactory.getInputTestMapFieldWithMapNoCustomMappingsObject(),
        NoCustomMappingsObjectPrime.class);

    NoCustomMappingsObject source = mapper.map(dest, NoCustomMappingsObject.class);
    NoCustomMappingsObjectPrime dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);

    assertEquals(dest2, dest);

    dest = mapper.map(new NoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // empty Map
    dest = mapper.map(testDataFactory.getInputTestMapFieldWithEmptyMapNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);
  }

  @Test
  public void testSetField() throws Exception {
    // basic set --> set
    NoCustomMappingsObjectPrime dest = mapper.map(testDataFactory.getInputTestSetFieldWithSetNoCustomMappingsObject(),
        NoCustomMappingsObjectPrime.class);
    NoCustomMappingsObject source = mapper.map(dest, NoCustomMappingsObject.class);
    NoCustomMappingsObjectPrime dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // null set --> set
    dest = mapper.map(new NoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // empty set --> set
    dest = mapper.map(testDataFactory.getInputTestSetFieldWithSetEmptyCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);

    // complex type set -->
    dest = mapper.map(testDataFactory.getInputTestSetFieldComplexSetNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
    source = mapper.map(dest, NoCustomMappingsObject.class);
    dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
    assertEquals(dest2, dest);
  }

  @Test
  public void testListField() throws Exception {
    // test empty list --> empty list
    TestObjectPrime dest = mapper.map(testDataFactory.getInputTestListFieldEmptyListTestObject(), TestObjectPrime.class);
    TestObject source = mapper.map(dest, TestObject.class);
    TestObjectPrime dest2 = mapper.map(source, TestObjectPrime.class);
    assertEquals(dest2, dest);

    // test empty array --> empty list
    dest = mapper.map(testDataFactory.getInputTestListFieldArrayListTestObject(), TestObjectPrime.class);
    source = mapper.map(dest, TestObject.class);
    dest2 = mapper.map(source, TestObjectPrime.class);
    assertEquals(dest2, dest);
  }

  @Test
  public void testListUsingDestHint() throws Exception {
    TestObjectPrime dest = mapper.map(testDataFactory.getInputTestListUsingDestHintTestObject(), TestObjectPrime.class);
    TestObject source = mapper.map(dest, TestObject.class);
    TestObjectPrime dest2 = mapper.map(source, TestObjectPrime.class);
    assertEquals(dest, dest2);
  }

  @Test
  public void testExcludeFields() throws Exception {
    // Map
    TestObjectPrime prime = mapper.map(testDataFactory.getInputGeneralMappingTestObject(), TestObjectPrime.class);
    assertEquals("excludeMe", prime.getExcludeMe());
    assertEquals("excludeMeOneWay", prime.getExcludeMeOneWay());
    // map back
    TestObject to = mapper.map(prime, TestObject.class);
    assertNull(to.getExcludeMe());
    assertEquals("excludeMeOneWay", to.getExcludeMeOneWay());
  }

  @Test
  public void testGeneralMapping() throws Exception {
    // Map
    TestObject to = testDataFactory.getInputGeneralMappingTestObject();
    TestObjectPrime prime = mapper.map(to, TestObjectPrime.class);
    // valdidate that we copied by object reference -
    TestObject source = mapper.map(prime, TestObject.class);
    TestObjectPrime prime2 = mapper.map(source, TestObjectPrime.class);
    assertEquals(prime2, prime);
  }

  @Test
  public void testMappingNoDestSpecified() throws Exception {
    // Map
    House src = testDataFactory.getHouse();
    HomeDescription dest = mapper.map(src, HomeDescription.class);
    House src2 = mapper.map(dest, House.class);
    HomeDescription dest2 = mapper.map(src2, HomeDescription.class);

    long[] prim = { 1, 2, 3, 1, 2, 3 };
    // cumulative relationship
    dest.setPrim(prim);
    assertEquals(dest, dest2);

    // By reference
    src = testDataFactory.getHouse();
    House houseClone = SerializationUtils.clone(src);
    dest = mapper.map(src, HomeDescription.class);
    mapper.map(dest, House.class);
    assertEquals(houseClone, src);
  }

  @Test
  public void testGeneralMappingPassByReference() throws Exception {
    // Map
    TestObject to = testDataFactory.getInputGeneralMappingTestObject();
    TestObject toClone = SerializationUtils.clone(to);
    TestObjectPrime prime = mapper.map(to, TestObjectPrime.class);
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

  @Test
  public void testLongToLongMapping() throws Exception {
    // Map
    TestObject source = testDataFactory.getInputGeneralMappingTestObject();
    source.setAnotherLongValue(42);
    TestObjectPrime prime2 = mapper.map(source, TestObjectPrime.class);
    Long value = prime2.getTheLongValue();
    assertEquals(value.longValue(), 42);
  }

  @Test
  public void testNoWildcards() throws Exception {
    // Map
    FurtherTestObjectPrime prime = mapper.map(testDataFactory.getInputTestNoWildcardsFurtherTestObject(),
        FurtherTestObjectPrime.class);
    FurtherTestObject source = mapper.map(prime, FurtherTestObject.class);
    FurtherTestObjectPrime prime2 = mapper.map(source, FurtherTestObjectPrime.class);
    assertEquals(prime2, prime);
  }

  @Test
  public void testHydrateAndMore() throws Exception {
    HydrateTestObject dest = mapper.map(testDataFactory.getInputTestHydrateAndMoreDehydrateTestObject(), HydrateTestObject.class);
    // validate results
    assertEquals(testDataFactory.getExpectedTestHydrateAndMoreHydrateTestObject(), dest);

    // map it back
    DehydrateTestObject dhto = mapper.map(testDataFactory.getInputTestHydrateAndMoreHydrateTestObject(), DehydrateTestObject.class);
    assertEquals(testDataFactory.getExpectedTestHydrateAndMoreDehydrateTestObject(), dhto);
  }

  @Test
  public void testDeepProperties() throws Exception {
    House src = testDataFactory.getHouse();
    HomeDescription dest = mapper.map(src, HomeDescription.class);
    House src2 = mapper.map(dest, House.class);
    HomeDescription dest2 = mapper.map(src2, HomeDescription.class);

    long[] prim = { 1, 2, 3, 1, 2, 3 };
    // cumulative relationship
    dest.setPrim(prim);
    assertEquals(dest, dest2);

    // By reference
    src = testDataFactory.getHouse();
    House houseClone = SerializationUtils.clone(src);
    dest = mapper.map(src, HomeDescription.class);
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

  @Test
  public void testOneWayMapping() throws Exception {
    // Map
    OneWayObject owo = newInstance(OneWayObject.class);
    OneWayObjectPrime owop = newInstance(OneWayObjectPrime.class);
    SrcNestedDeepObj nested = newInstance(SrcNestedDeepObj.class);
    nested.setSrc1("src1");
    owo.setNested(nested);
    owop.setOneWayPrimeField("oneWayField");
    owop.setSetOnlyField("setOnly");
    List<String> list = new ArrayList<String>();
    list.add("stringToList");
    list.add("src1");
    owop.setStringList(list);
    owo.setOneWayField("oneWayField");
    owo.setStringToList("stringToList");
    OneWayObjectPrime prime = mapper.map(owo, OneWayObjectPrime.class);

    assertEquals(owop, prime);

    OneWayObject source = mapper.map(prime, OneWayObject.class);
    // should have not mapped this way
    assertEquals(null, source.getOneWayField());
  }

  @Ignore("Failing after 4.3 release")
  @Test
  public void testMapByReference() throws Exception {
    // Map
    TestReferenceObject tro = newInstance(TestReferenceObject.class);
    TestReferenceFoo foo1 = newInstance(TestReferenceFoo.class);
    foo1.setA("a");
    TestReferenceFoo foo = newInstance(TestReferenceFoo.class);
    foo.setA("a");
    foo.setB(null);
    foo.setC("c");
    List<TestReferenceFoo> list2 = newInstance(ArrayList.class);
    list2.add(foo);
    tro.setListA(list2);
    tro.setArrayToArrayCumulative(new Object[] { foo1 });
    TestReferenceFoo foo2 = newInstance(TestReferenceFoo.class);
    foo2.setA("a");
    foo2.setB(null);
    foo2.setC("c");
    TestReferenceFoo foo3 = newInstance(TestReferenceFoo.class);
    foo3.setA("a");
    foo3.setB(null);
    foo3.setC("c");
    tro.setArrayToArrayNoncumulative(new Object[] { foo2 });
    List<String> list3 = newInstance(ArrayList.class);
    list3.add("string1");
    list3.add("string2");
    tro.setListToArray(list3);
    int[] pa = { 1, 2, 3 };
    tro.setPrimitiveArray(pa);
    Integer[] integerArray = { new Integer(1), new Integer(2) };
    tro.setPrimitiveArrayWrapper(integerArray);
    Set<TestReferenceFoo> set = newInstance(HashSet.class);
    TestReferenceFoo foo4 = newInstance(TestReferenceFoo.class);
    foo4.setA("a");
    set.add(foo4);
    tro.setSetToSet(set);
    Car car = new Car();
    car.setName("myName");
    tro.setCars(new Car[] { car });
    Car car2 = new Car();
    car2.setName("myName");
    List<Car> vehicles = newInstance(ArrayList.class);
    vehicles.add(car2);
    tro.setVehicles(vehicles);
    TestReferenceObject toClone = SerializationUtils.clone(tro);
    TestReferencePrimeObject trop = mapper.map(tro, TestReferencePrimeObject.class);
    assertEquals("myName", (trop.getVans()[0]).getName());
    assertEquals("myName", (trop.getMoreVans()[0]).getName());

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

  @Test
  public void testHintedOnlyConverter() throws Exception {
    String hintStr = "where's my hint?";

    CustomConverterWrapper source = newInstance(CustomConverterWrapper.class);
    HintedOnly hint = newInstance(HintedOnly.class);
    hint.setStr(hintStr);
    source.addHint(hint);

    CustomConverterWrapperPrime dest = mapper.map(source, CustomConverterWrapperPrime.class);
    String destHintStr = (String) dest.getNeedsHint().iterator().next();
    assertNotNull(destHintStr);
    assertEquals(hintStr, destHintStr);

    CustomConverterWrapper sourcePrime = mapper.map(dest, CustomConverterWrapper.class);
    String sourcePrimeHintStr = ((HintedOnly) sourcePrime.getNeedsHint().iterator().next()).getStr();
    assertNotNull(sourcePrimeHintStr);
    assertEquals(hintStr, sourcePrimeHintStr);
  }

  @Test
  public void testSelfMapping() throws Exception {
    SimpleAccount simpleAccount = newInstance(SimpleAccount.class);
    simpleAccount.setName("name");
    simpleAccount.setPostcode(1234);
    simpleAccount.setStreetName("streetName");
    simpleAccount.setSuburb("suburb");
    Account account = mapper.map(simpleAccount, Account.class);
    assertEquals(account.getAddress().getStreet(), simpleAccount.getStreetName());
    assertEquals(account.getAddress().getSuburb(), simpleAccount.getSuburb());
    assertEquals(account.getAddress().getPostcode(), simpleAccount.getPostcode());

    // try mapping back
    SimpleAccount dest = mapper.map(account, SimpleAccount.class);
    assertEquals(account.getAddress().getStreet(), dest.getStreetName());
    assertEquals(account.getAddress().getSuburb(), dest.getSuburb());
    assertEquals(account.getAddress().getPostcode(), dest.getPostcode());
  }

  @Test
  public void testSetToArray() throws Exception {
    Orange orange1 = newInstance(Orange.class);
    orange1.setName("orange1");
    Orange orange2 = newInstance(Orange.class);
    orange2.setName("orange2");
    Orange orange3 = newInstance(Orange.class);
    orange3.setName("orange3");
    Orange orange4 = newInstance(Orange.class);
    orange4.setName("orange4");
    Set<Orange> set = newInstance(HashSet.class);
    set.add(orange1);
    set.add(orange2);
    Set<Orange> set2 = newInstance(HashSet.class);
    set2.add(orange3);
    set2.add(orange4);
    TestObject to = newInstance(TestObject.class);
    to.setSetToArray(set);
    to.setSetToObjectArray(set2);

    TestObjectPrime top = mapper.map(to, TestObjectPrime.class);

    Set<String> fruitNames = new HashSet<String>();
    fruitNames.add(top.getArrayToSet()[0].getName());
    fruitNames.add(top.getArrayToSet()[1].getName());
    assertTrue(fruitNames.remove("orange1"));
    assertTrue(fruitNames.remove("orange2"));

    fruitNames.add(((Apple) top.getObjectArrayToSet()[0]).getName());
    fruitNames.add(((Apple) top.getObjectArrayToSet()[1]).getName());
    assertTrue(fruitNames.remove("orange3"));
    assertTrue(fruitNames.remove("orange4"));

    Apple apple = newInstance(Apple.class);
    apple.setName("apple1");
    Apple[] appleArray = { apple };
    top.setSetToArrayWithValues(appleArray);
    // now map back
    Apple apple2 = newInstance(Apple.class);
    apple2.setName("apple2");
    TestObject toDest = newInstance(TestObject.class);
    Set<Apple> hashSet = newInstance(HashSet.class);
    hashSet.add(apple2);
    toDest.setSetToArrayWithValues(hashSet);
    mapper.map(top, toDest);
    assertTrue(toDest.getSetToArray().contains(top.getArrayToSet()[0]));
    assertTrue(toDest.getSetToArray().contains(top.getArrayToSet()[1]));
    assertTrue(toDest.getSetToObjectArray().contains(top.getObjectArrayToSet()[0]));
    assertTrue(toDest.getSetToObjectArray().contains(top.getObjectArrayToSet()[1]));
    assertTrue(toDest.getSetToArrayWithValues().contains(apple));
    assertTrue(toDest.getSetToArrayWithValues().contains(apple2));
    assertTrue(toDest.getSetToArrayWithValues() instanceof HashSet);
  }

  @Test
  public void testSetToList() throws Exception {
    Orange orange1 = newInstance(Orange.class);
    orange1.setName("orange1");
    Orange orange2 = newInstance(Orange.class);
    orange2.setName("orange2");
    Set<Orange> set = newInstance(HashSet.class);
    set.add(orange1);
    set.add(orange2);
    TestObject to = newInstance(TestObject.class);
    to.setSetToList(set);
    TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
    assertEquals("orange1", ((Orange) top.getListToSet().get(0)).getName());
    assertEquals("orange2", ((Orange) top.getListToSet().get(1)).getName());
    List<Orange> list = newInstance(ArrayList.class);
    Orange orange4 = newInstance(Orange.class);
    orange4.setName("orange4");
    list.add(orange4);
    top.setSetToListWithValues(list);
    // Map back
    Orange orange3 = newInstance(Orange.class);
    orange3.setName("orange3");
    Set<Orange> set2 = newInstance(HashSet.class);
    set2.add(orange3);
    set2.add(orange4);
    TestObject toDest = newInstance(TestObject.class);
    toDest.setSetToListWithValues(set2);
    mapper.map(top, toDest);
    assertTrue(toDest.getSetToList().contains(top.getListToSet().get(0)));
    assertTrue(toDest.getSetToList().contains(top.getListToSet().get(1)));
    assertTrue(toDest.getSetToListWithValues().contains(orange3));
    assertTrue(toDest.getSetToListWithValues().contains(orange4));
  }

	// one way
	@Test
	public void testMapValuesToList() throws Exception {
		Orange orange1 = newInstance(Orange.class);
		orange1.setName("orange1");
		Orange orange2 = newInstance(Orange.class);
		orange2.setName("orange2");
		Map<String, Orange> map = newInstance(HashMap.class);
		map.put(orange1.getName(), orange1);
		map.put(orange2.getName(), orange2);
		TestObject to = newInstance(TestObject.class);
		to.setCollectionToList(map.values());
		TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
		assertTrue(top.getListToCollection().contains(orange1));
		assertTrue(top.getListToCollection().contains(orange2));
	}

}