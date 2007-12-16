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
package net.sf.dozer.util.mapping.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.functional_tests.DataObjectInstantiator;
import net.sf.dozer.util.mapping.vo.Apple;
import net.sf.dozer.util.mapping.vo.AppleComputer;
import net.sf.dozer.util.mapping.vo.Car;
import net.sf.dozer.util.mapping.vo.CustomDoubleObject;
import net.sf.dozer.util.mapping.vo.CustomDoubleObjectIF;
import net.sf.dozer.util.mapping.vo.DehydrateTestObject;
import net.sf.dozer.util.mapping.vo.FurtherTestObject;
import net.sf.dozer.util.mapping.vo.FurtherTestObjectPrime;
import net.sf.dozer.util.mapping.vo.HydrateTestObject;
import net.sf.dozer.util.mapping.vo.HydrateTestObject2;
import net.sf.dozer.util.mapping.vo.InsideTestObject;
import net.sf.dozer.util.mapping.vo.MetalThingyIF;
import net.sf.dozer.util.mapping.vo.NoCustomMappingsObject;
import net.sf.dozer.util.mapping.vo.NoExtendBaseObject;
import net.sf.dozer.util.mapping.vo.NoExtendBaseObjectGlobalCopyByReference;
import net.sf.dozer.util.mapping.vo.Orange;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.SubClass;
import net.sf.dozer.util.mapping.vo.TestCustomConverterObject;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TheFirstSubClass;
import net.sf.dozer.util.mapping.vo.Van;
import net.sf.dozer.util.mapping.vo.deep.Address;
import net.sf.dozer.util.mapping.vo.deep.City;
import net.sf.dozer.util.mapping.vo.deep.House;
import net.sf.dozer.util.mapping.vo.deep.Person;
import net.sf.dozer.util.mapping.vo.deep.Room;
import net.sf.dozer.util.mapping.vo.deep.SrcDeepObj;
import net.sf.dozer.util.mapping.vo.deep.SrcNestedDeepObj;
import net.sf.dozer.util.mapping.vo.deep.SrcNestedDeepObj2;
import net.sf.dozer.util.mapping.vo.inheritance.AnotherSubClass;
import net.sf.dozer.util.mapping.vo.inheritance.S2Class;
import net.sf.dozer.util.mapping.vo.inheritance.SClass;
import net.sf.dozer.util.mapping.vo.perf.MyClassA;

import org.apache.commons.lang.RandomStringUtils;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class TestDataFactory {
  private DataObjectInstantiator dataObjectInstantiator;

  public TestDataFactory(DataObjectInstantiator dataObjectInstantiator) {
    this.dataObjectInstantiator = dataObjectInstantiator;
  }

  public TestDataFactory() {
  }

  private Object newInstance(Class classToInstantiate) {
    return dataObjectInstantiator.newInstance(classToInstantiate);
  }

  public SubClass getSubClass() {
    SubClass obj = (SubClass) newInstance(SubClass.class);

    obj.setAttribute("subclass");
    obj.setSuperAttribute("superclass");

    List superList = (List) newInstance(ArrayList.class);
    superList.add("one");
    superList.add("two");
    superList.add("three");

    obj.setSuperList(superList);
    obj.setSuperSuperAttribute("supersuperattribute");
    obj.setSuperSuperSuperAttr("toplevel");

    obj.setTestObject(getInputGeneralMappingTestObject());
    HydrateTestObject2 sourceObj = (HydrateTestObject2) newInstance(HydrateTestObject2.class);

    TestCustomConverterObject cobj = (TestCustomConverterObject) newInstance(TestCustomConverterObject.class);
    CustomDoubleObjectIF doub = (CustomDoubleObjectIF) newInstance(CustomDoubleObject.class);
    doub.setTheDouble(15);
    cobj.setAttribute(doub);

    obj.setCustomConvert(cobj);

    obj.setHydrate(sourceObj);

    obj.setSuperFieldToExclude("superFieldToExclude");

    return obj;
  }

  public SrcDeepObj getSrcDeepObj() {
    SrcDeepObj result = (SrcDeepObj) newInstance(SrcDeepObj.class);
    SrcNestedDeepObj srcNested = (SrcNestedDeepObj) newInstance(SrcNestedDeepObj.class);
    SrcNestedDeepObj2 srcNested2 = (SrcNestedDeepObj2) newInstance(SrcNestedDeepObj2.class);
    FurtherTestObjectPrime furtherObjectPrime = (FurtherTestObjectPrime) newInstance(FurtherTestObjectPrime.class);

    srcNested2.setSrc5("nestedsrc2field5");
    furtherObjectPrime.setOne("fjd");

    srcNested.setSrc1("nestedsrc1");
    srcNested.setSrc2(Integer.valueOf("5"));
    srcNested.setSrc3(90);
    srcNested.setSrc4(new String[] { "item1", "item2", "item3" });
    srcNested.setSrcNestedObj2(srcNested2);
    srcNested.setSrc6(furtherObjectPrime);

    // List to List. String to Integer
    List hintList = (List) newInstance(ArrayList.class);
    hintList.add("1");
    hintList.add("2");
    srcNested.setHintList(hintList);

    // List to List. TheFirstSubClass to TheFirstSubClassPrime
    TheFirstSubClass hintList2Obj = (TheFirstSubClass) newInstance(TheFirstSubClass.class);
    hintList2Obj.setS("test");

    TheFirstSubClass hintList2Obj2 = (TheFirstSubClass) newInstance(TheFirstSubClass.class);
    hintList2Obj.setS("test2");

    List hintList2 = (List) newInstance(ArrayList.class);
    hintList2.add(hintList2Obj);
    hintList2.add(hintList2Obj2);
    srcNested.setHintList2(hintList2);

    result.setSrcNestedObj(srcNested);
    result.setSameNameField("sameNameField");

    return result;
  }

  public House getHouse() {
    House house = (House) newInstance(House.class);
    Address address = (Address) newInstance(Address.class);
    address.setStreet("1234 street");
    City city = (City) newInstance(City.class);
    city.setName("Denver");
    address.setCity(city);

    house.setAddress(address);

    Person person = (Person) newInstance(Person.class);
    person.setName("Franz");

    house.setOwner(person);

    house.setPrice(1000000);

    Van van = (Van) newInstance(Van.class);
    van.setName("van");
    van.setTestValue("testValue");
    house.setVan(van);

    Room living = (Room) newInstance(Room.class);
    living.setName("Living");
    Room kitchen = (Room) newInstance(Room.class);
    kitchen.setName("kitchen");

    List rooms = (List) newInstance(ArrayList.class);
    rooms.add(living);
    rooms.add(kitchen);

    house.setRooms(rooms);
    List custom = (List) newInstance(ArrayList.class);
    Van van2 = (Van) newInstance(Van.class);
    van2.setName("van2");
    custom.add(van2);
    house.setCustomSetGetMethod(custom);

    return house;
  }

  public HydrateTestObject getExpectedTestNoSourceValueIterateFieldMapHydrateTestObject() {
    Car car = (Car) newInstance(Car.class);
    car.setName("Build by buildCar");
    HydrateTestObject hto = (HydrateTestObject) newInstance(HydrateTestObject.class);
    // Problem - Destination Field is array of 'cars' - but getMethod() is buildCar() which returns a Car. MapCollection
    // method can not handle this...
    // DestinationType is a Car and it should be an array.
    // hto.addCar(car);
    hto.setCarArray(new Car[0]);
    return hto;
  }

  public NoCustomMappingsObject getInputTestNoClassMappingsNoCustomMappingsObject() {
    NoCustomMappingsObject custom = (NoCustomMappingsObject) newInstance(NoCustomMappingsObject.class);
    custom.setStringDataType("stringDataType");
    custom.setDate(new Date());
    custom.setFive(55);
    custom.setFour(44);
    custom.setSeven(77);
    custom.setSix(new Double(87.62));
    custom.setThree(new Integer(9876));

    return custom;
  }

  public NoCustomMappingsObject getInputTestMapFieldWithMapNoCustomMappingsObject() {
    NoCustomMappingsObject custom = (NoCustomMappingsObject) newInstance(NoCustomMappingsObject.class);
    Map map = (Map) newInstance(HashMap.class);
    map.put("1", "1value");
    map.put("2", "2value");
    custom.setMapDataType(map);
    return custom;
  }

  public NoCustomMappingsObject getInputTestMapFieldWithEmptyMapNoCustomMappingsObject() {
    NoCustomMappingsObject custom = (NoCustomMappingsObject) newInstance(NoCustomMappingsObject.class);
    Map map = (Map) newInstance(HashMap.class);
    custom.setMapDataType(map);
    return custom;
  }

  public NoCustomMappingsObject getInputTestSetFieldWithSetNoCustomMappingsObject() {
    NoCustomMappingsObject custom = (NoCustomMappingsObject) newInstance(NoCustomMappingsObject.class);
    Set set = (Set) newInstance(HashSet.class);
    set.add("1value");
    set.add("2value");
    custom.setSetDataType(set);
    return custom;
  }

  public NoCustomMappingsObject getInputTestSetFieldWithSetEmptyCustomMappingsObject() {
    NoCustomMappingsObject custom = (NoCustomMappingsObject) newInstance(NoCustomMappingsObject.class);
    Set set = (Set) newInstance(HashSet.class);
    custom.setSetDataType(set);
    return custom;
  }

  public NoCustomMappingsObject getInputTestSetFieldComplexSetNoCustomMappingsObject() {
    NoCustomMappingsObject custom = (NoCustomMappingsObject) newInstance(NoCustomMappingsObject.class);
    Set set = (Set) newInstance(HashSet.class);
    set.add(getInputTestNoClassMappingsNoCustomMappingsObject());
    custom.setSetDataType(set);
    return custom;
  }

  public TestObject getInputTestListFieldEmptyListTestObject() {
    TestObject custom = (TestObject) newInstance(TestObject.class);
    custom.setEqualNamedList((List) newInstance(ArrayList.class));
    return custom;
  }

  public TestObject getInputTestListFieldArrayListTestObject() {
    TestObject custom = (TestObject) newInstance(TestObject.class);
    Integer[] array = { new Integer(1) };
    custom.setArrayForLists(array);
    return custom;
  }

  public TestObject getInputTestListUsingDestHintTestObject() {
    TestObject custom = (TestObject) newInstance(TestObject.class);
    List list = (List) newInstance(ArrayList.class);
    list.add(newInstance(TheFirstSubClass.class));
    custom.setHintList(list);
    return custom;
  }

  public TestObject getInputGeneralMappingTestObject() {
    TestObject custom = (TestObject) newInstance(TestObject.class);
    custom.setOne("one");
    custom.setTwo(new Integer(2));

    int[] pa = { 0, 1, 2, 3, 4 };
    custom.setPrimArray(pa);

    InsideTestObject ito = (InsideTestObject) newInstance(InsideTestObject.class);
    ito.setLabel("label");
    ito.setWrapper(new Integer(1));
    ito.setToWrapper(1);

    custom.setThree(ito);

    // testing if it will map two custom objects that are different types but same names //
    InsideTestObject ito2 = (InsideTestObject) newInstance(InsideTestObject.class);
    ito2.setLabel("label");
    custom.setInsideTestObject(ito2);

    List list1 = (List) newInstance(ArrayList.class);
    list1.add("1value");
    list1.add("2value");
    List list2 = (List) newInstance(ArrayList.class);
    list2.add("1value");
    list2.add("2value");
    custom.setEqualNamedList(list1);
    custom.setUnequalNamedList(list2);

    custom.setThePrimitive(3);
    custom.setTheMappedPrimitive(4);

    int[] intArray = { 1, 1 };
    Integer[] integerArray = { new Integer(1), new Integer(1) };
    custom.setAnArray(intArray);
    custom.setArrayForLists(integerArray);
    custom.setBigDecimalToInt(new BigDecimal(1));
    custom.setIntToBigDecimal(1);
    Date date = new Date();
    custom.setDate(date);
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    custom.setCalendar(calendar);
    Van van = (Van) newInstance(Van.class);
    van.setName("van");
    van.setTestValue("testValue");
    custom.setVan(van);
    custom.setExcludeMe("takemeout");

    // testing interfaces
    MetalThingyIF car = (MetalThingyIF) newInstance(Car.class);
    car.setName("metalthingy");
    custom.setCarMetalThingy(car);

    List hintList = (List) newInstance(ArrayList.class);
    TheFirstSubClass fsc = (TheFirstSubClass) newInstance(TheFirstSubClass.class);
    TheFirstSubClass fsc2 = (TheFirstSubClass) newInstance(TheFirstSubClass.class);
    fsc.setS("s");
    fsc2.setS("s");
    hintList.add(fsc);
    hintList.add(fsc2);

    custom.setHintList(hintList);

    custom.setBlankDate("");
    custom.setBlankStringToLong("");
    NoExtendBaseObject nebo = (NoExtendBaseObject) newInstance(NoExtendBaseObject.class);
    custom.setCopyByReference(nebo);
    NoExtendBaseObject nebo2 = (NoExtendBaseObject) newInstance(NoExtendBaseObject.class);
    custom.setCopyByReferenceDeep(nebo2);
    NoExtendBaseObjectGlobalCopyByReference globalNebo = (NoExtendBaseObjectGlobalCopyByReference) newInstance(NoExtendBaseObjectGlobalCopyByReference.class);
    custom.setGlobalCopyByReference(globalNebo);

    String[] stringArray = new String[] { null, "one", "two" };
    custom.setStringArrayWithNullValue(stringArray);
    return custom;
  }

  public FurtherTestObject getInputTestNoWildcardsFurtherTestObject() {
    FurtherTestObject custom = (FurtherTestObject) newInstance(FurtherTestObject.class);
    custom.setOne("label");
    custom.setTwo("another");
    return custom;
  }

  public DehydrateTestObject getInputTestHydrateAndMoreDehydrateTestObject() {
    DehydrateTestObject custom = (DehydrateTestObject) newInstance(DehydrateTestObject.class);
    Car car = (Car) newInstance(Car.class);
    car.setName("name");
    List carList = (List) newInstance(ArrayList.class);
    carList.add(car);
    custom.setCars(carList);

    Apple apple = (Apple) newInstance(Apple.class);
    apple.setName("name");
    Orange orange = (Orange) newInstance(Orange.class);
    orange.setName("name");
    List fruitList = (List) newInstance(ArrayList.class);
    fruitList.add(apple);
    fruitList.add(orange);
    custom.setFruit(fruitList);

    Van van = (Van) newInstance(Van.class);
    van.setName("name");
    List vanList = (List) newInstance(ArrayList.class);
    vanList.add(van);
    custom.setVans(vanList);

    AppleComputer apple1 = (AppleComputer) newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = (AppleComputer) newInstance(AppleComputer.class);
    apple2.setName("name");
    List compList = (List) newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    custom.setAppleComputers(compList);

    Car iterateCar = (Car) newInstance(Car.class);
    iterateCar.setName("name");
    List iterateCarList = (List) newInstance(ArrayList.class);
    iterateCarList.add(car);
    custom.setIterateCars(iterateCarList);

    iterateCar.setName("name");
    List iterateMoreCarList = (List) newInstance(ArrayList.class);
    iterateMoreCarList.add(car);
    custom.setIterateMoreCars(iterateMoreCarList);

    return custom;
  }

  public HydrateTestObject getExpectedTestHydrateAndMoreHydrateTestObject() {
    HydrateTestObject hto = (HydrateTestObject) newInstance(HydrateTestObject.class);
    Car car = (Car) newInstance(Car.class);
    car.setName("name");
    Car buildByCar = (Car) newInstance(Car.class);
    buildByCar.setName("Build by buildCar");
    Van van = (Van) newInstance(Van.class);
    van.setName("name");

    AppleComputer apple1 = (AppleComputer) newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = (AppleComputer) newInstance(AppleComputer.class);
    apple2.setName("name");
    List compList = (List) newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    hto.setComputers(compList);
    List iterateCars = (List) newInstance(ArrayList.class);
    iterateCars.add(car);
    hto.setIterateCars(iterateCars);
    Car[] carArray = { car };
    hto.setCarArray(carArray);
    return hto;
  }

  public HydrateTestObject getInputTestHydrateAndMoreHydrateTestObject() {
    HydrateTestObject hto = (HydrateTestObject) newInstance(HydrateTestObject.class);
    Car car = (Car) newInstance(Car.class);
    car.setName("name");
    Van van = (Van) newInstance(Van.class);
    van.setName("name");
    List vehicles = (List) newInstance(ArrayList.class);
    vehicles.add(car);
    vehicles.add(van);
    hto.setVehicles(vehicles);

    Apple apple = (Apple) newInstance(Apple.class);
    apple.setName("name");
    Orange orange = (Orange) newInstance(Orange.class);
    orange.setName("name");
    List apples = (List) newInstance(ArrayList.class);
    apples.add(apple);
    List oranges = (List) newInstance(ArrayList.class);
    oranges.add(orange);

    hto.setApples(apples);
    hto.setOranges(oranges);

    AppleComputer apple1 = (AppleComputer) newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = (AppleComputer) newInstance(AppleComputer.class);
    apple2.setName("name");
    List compList = (List) newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    hto.setComputers(compList);

    List iterateCars = (List) newInstance(ArrayList.class);
    iterateCars.add(car);
    hto.setIterateCars(iterateCars);
    return hto;

  }

  public DehydrateTestObject getExpectedTestHydrateAndMoreDehydrateTestObject() {
    DehydrateTestObject custom = (DehydrateTestObject) newInstance(DehydrateTestObject.class);
    Car car = (Car) newInstance(Car.class);
    car.setName("name");

    AppleComputer apple1 = (AppleComputer) newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = (AppleComputer) newInstance(AppleComputer.class);
    apple2.setName("name");
    List compList = (List) newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    custom.setAppleComputers(compList);
    List iterateCars = (List) newInstance(ArrayList.class);
    iterateCars.add(car);
    custom.setIterateCars(iterateCars);
    return custom;
  }

  public SimpleObj getSimpleObj() {
    SimpleObj result = (SimpleObj) newInstance(SimpleObj.class);
    result.setField1("one");
    result.setField2(Integer.valueOf("2"));
    result.setField3(BigDecimal.valueOf(3));
    result.setField4(new Double(44.44));
    result.setField5(Calendar.getInstance());
    result.setField6("66");

    return result;
  }

  public AnotherSubClass getAnotherSubClass() {
    AnotherSubClass asub = (AnotherSubClass) newInstance(AnotherSubClass.class);
    asub.setBaseAttribute("base");
    asub.setSubAttribute("sub");
    List list = (List) newInstance(ArrayList.class);
    SClass s = (SClass) newInstance(SClass.class);
    s.setBaseSubAttribute("sBase");
    s.setSubAttribute("s");
    S2Class s2 = (S2Class) newInstance(S2Class.class);
    s2.setBaseSubAttribute("s2Base");
    s2.setSub2Attribute("s2");
    list.add(s2);
    list.add(s);
    asub.setSubList(list);

    List list2 = (List) newInstance(ArrayList.class);
    SClass sclass = (SClass) newInstance(SClass.class);
    sclass.setBaseSubAttribute("sBase");
    sclass.setSubAttribute("s");
    S2Class s2class = (S2Class) newInstance(S2Class.class);
    s2class.setBaseSubAttribute("s2Base");
    s2class.setSub2Attribute("s2");
    SClass sclass2 = (SClass) newInstance(SClass.class);
    sclass2.setBaseSubAttribute("sclass2");
    sclass2.setSubAttribute("sclass2");
    list2.add(s2class);
    list2.add(sclass);
    list2.add(sclass2);
    asub.setListToArray(list2);

    SClass sclassA = (SClass) newInstance(SClass.class);
    SClass sclassB = (SClass) newInstance(SClass.class);
    sclassA.setBaseSubAttribute("sBase");
    sclassA.setSubAttribute("s");
    sclassB.setBaseSubAttribute("sBase");
    sclassB.setSubAttribute("s");
    asub.setSClass(sclassA);
    asub.setSClass2(sclassB);

    return asub;
  }

  public MyClassA getRandomMyClassA() {
    MyClassA myClassAObj = (MyClassA) newInstance(MyClassA.class);
    myClassAObj.setAStringList(getRandomStringList(500));

    return myClassAObj;
  }

  private List getRandomStringList(int listSize) {
    List stringList = (List) newInstance(ArrayList.class);

    for (int count = 0; count < listSize; count = count + 1) {
      stringList.add(RandomStringUtils.randomAlphabetic(255));
    }

    return stringList;
  }

}
