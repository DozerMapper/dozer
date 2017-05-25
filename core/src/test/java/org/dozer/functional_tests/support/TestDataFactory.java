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
package org.dozer.functional_tests.support;

import java.io.Serializable;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.dozer.functional_tests.DataObjectInstantiator;
import org.dozer.vo.Apple;
import org.dozer.vo.AppleComputer;
import org.dozer.vo.Car;
import org.dozer.vo.CustomDoubleObject;
import org.dozer.vo.CustomDoubleObjectIF;
import org.dozer.vo.DehydrateTestObject;
import org.dozer.vo.Fruit;
import org.dozer.vo.FurtherTestObject;
import org.dozer.vo.FurtherTestObjectPrime;
import org.dozer.vo.HydrateTestObject;
import org.dozer.vo.HydrateTestObject2;
import org.dozer.vo.InsideTestObject;
import org.dozer.vo.MetalThingyIF;
import org.dozer.vo.NoCustomMappingsObject;
import org.dozer.vo.NoExtendBaseObject;
import org.dozer.vo.NoExtendBaseObjectGlobalCopyByReference;
import org.dozer.vo.Orange;
import org.dozer.vo.SimpleObj;
import org.dozer.vo.SubClass;
import org.dozer.vo.TestCustomConverterObject;
import org.dozer.vo.TestObject;
import org.dozer.vo.TheFirstSubClass;
import org.dozer.vo.Van;
import org.dozer.vo.Vehicle;
import org.dozer.vo.deep.Address;
import org.dozer.vo.deep.City;
import org.dozer.vo.deep.House;
import org.dozer.vo.deep.Person;
import org.dozer.vo.deep.Room;
import org.dozer.vo.deep.SrcDeepObj;
import org.dozer.vo.deep.SrcNestedDeepObj;
import org.dozer.vo.deep.SrcNestedDeepObj2;
import org.dozer.vo.inheritance.AnotherSubClass;
import org.dozer.vo.inheritance.BaseSubClass;
import org.dozer.vo.inheritance.Main;
import org.dozer.vo.inheritance.S2Class;
import org.dozer.vo.inheritance.SClass;
import org.dozer.vo.inheritance.Sub;
import org.dozer.vo.perf.MyClassA;

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

  private <T> T newInstance(Class<T> classToInstantiate) {
    return dataObjectInstantiator.newInstance(classToInstantiate);
  }

  public SubClass getSubClass() {
    SubClass obj = newInstance(SubClass.class);

    obj.setAttribute("subclass");
    obj.setSuperAttribute("superclass");

    List<String> superList = newInstance(ArrayList.class);
    superList.add("one");
    superList.add("two");
    superList.add("three");

    obj.setSuperList(superList);
    obj.setSuperSuperAttribute("supersuperattribute");
    obj.setSuperSuperSuperAttr("toplevel");

    obj.setTestObject(getInputGeneralMappingTestObject());
    HydrateTestObject2 sourceObj = newInstance(HydrateTestObject2.class);

    TestCustomConverterObject cobj = newInstance(TestCustomConverterObject.class);
    CustomDoubleObjectIF doub = newInstance(CustomDoubleObject.class);
    doub.setTheDouble(15);
    cobj.setAttribute(doub);

    obj.setCustomConvert(cobj);
    obj.setHydrate(sourceObj);
    obj.setSuperFieldToExclude("superFieldToExclude");

    return obj;
  }

  public SrcDeepObj getSrcDeepObj() {
    SrcDeepObj result = newInstance(SrcDeepObj.class);
    SrcNestedDeepObj srcNested = newInstance(SrcNestedDeepObj.class);
    SrcNestedDeepObj2 srcNested2 = newInstance(SrcNestedDeepObj2.class);
    FurtherTestObjectPrime furtherObjectPrime = newInstance(FurtherTestObjectPrime.class);

    srcNested2.setSrc5("nestedsrc2field5");
    furtherObjectPrime.setOne("fjd");

    srcNested.setSrc1("nestedsrc1");
    srcNested.setSrc2(Integer.valueOf("5"));
    srcNested.setSrc3(90);
    srcNested.setSrc4(new String[] { "item1", "item2", "item3" });
    srcNested.setSrcNestedObj2(srcNested2);
    srcNested.setSrc6(furtherObjectPrime);

    // List to List. String to Integer
    List<String> hintList = newInstance(ArrayList.class);
    hintList.add("1");
    hintList.add("2");
    srcNested.setHintList(hintList);

    // List to List. TheFirstSubClass to TheFirstSubClassPrime
    TheFirstSubClass hintList2Obj = newInstance(TheFirstSubClass.class);
    hintList2Obj.setS("test");

    TheFirstSubClass hintList2Obj2 = newInstance(TheFirstSubClass.class);
    hintList2Obj.setS("test2");

    List<TheFirstSubClass> hintList2 = newInstance(ArrayList.class);
    hintList2.add(hintList2Obj);
    hintList2.add(hintList2Obj2);
    srcNested.setHintList2(hintList2);

    result.setSrcNestedObj(srcNested);
    result.setSameNameField("sameNameField");

    return result;
  }

  public House getHouse() {
    House house = newInstance(House.class);
    Address address = newInstance(Address.class);
    address.setStreet("1234 street");
    City city = newInstance(City.class);
    city.setName("Denver");
    address.setCity(city);

    house.setAddress(address);

    Person person = newInstance(Person.class);
    person.setName("Franz");

    house.setOwner(person);

    house.setPrice(1000000);

    Van van = newInstance(Van.class);
    van.setName("van");
    van.setTestValue("testValue");
    house.setVan(van);

    Room living = newInstance(Room.class);
    living.setName("Living");
    Room kitchen = newInstance(Room.class);
    kitchen.setName("kitchen");

    List<Room> rooms = newInstance(ArrayList.class);
    rooms.add(living);
    rooms.add(kitchen);

    house.setRooms(rooms);
    List<Van> custom = newInstance(ArrayList.class);
    Van van2 = newInstance(Van.class);
    van2.setName("van2");
    custom.add(van2);
    house.setCustomSetGetMethod(custom);

    return house;
  }

  public HydrateTestObject getExpectedTestNoSourceValueIterateFieldMapHydrateTestObject() {
    Car car = newInstance(Car.class);
    car.setName("Build by buildCar");
    HydrateTestObject hto = newInstance(HydrateTestObject.class);
    // Problem - Destination Field is array of 'cars' - but getMethod() is buildCar() which returns a Car. MapCollection
    // method can not handle this...
    // DestinationType is a Car and it should be an array.
    // hto.addCar(car);
    hto.setCarArray(new Car[0]);
    return hto;
  }

  public NoCustomMappingsObject getInputTestNoClassMappingsNoCustomMappingsObject() {
    NoCustomMappingsObject custom = newInstance(NoCustomMappingsObject.class);
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
    NoCustomMappingsObject custom = newInstance(NoCustomMappingsObject.class);
    Map<String, String> map = newInstance(HashMap.class);
    map.put("1", "1value");
    map.put("2", "2value");
    custom.setMapDataType(map);
    return custom;
  }

  public NoCustomMappingsObject getInputTestMapFieldWithEmptyMapNoCustomMappingsObject() {
    NoCustomMappingsObject custom = newInstance(NoCustomMappingsObject.class);
    Map<String, String> map = newInstance(HashMap.class);
    custom.setMapDataType(map);
    return custom;
  }

  public NoCustomMappingsObject getInputTestSetFieldWithSetNoCustomMappingsObject() {
    NoCustomMappingsObject custom = newInstance(NoCustomMappingsObject.class);
    Set<Serializable> set = newInstance(HashSet.class);
    set.add("1value");
    set.add("2value");
    custom.setSetDataType(set);
    return custom;
  }

  public NoCustomMappingsObject getInputTestSetFieldWithSetEmptyCustomMappingsObject() {
    NoCustomMappingsObject custom = newInstance(NoCustomMappingsObject.class);
    Set<Serializable> set = newInstance(HashSet.class);
    custom.setSetDataType(set);
    return custom;
  }

  public NoCustomMappingsObject getInputTestSetFieldComplexSetNoCustomMappingsObject() {
    NoCustomMappingsObject custom = newInstance(NoCustomMappingsObject.class);
    Set<Serializable> set = newInstance(HashSet.class);
    set.add(getInputTestNoClassMappingsNoCustomMappingsObject());
    custom.setSetDataType(set);
    return custom;
  }

  public TestObject getInputTestListFieldEmptyListTestObject() {
    TestObject custom = newInstance(TestObject.class);
    custom.setEqualNamedList(newInstance(ArrayList.class));
    return custom;
  }

  public TestObject getInputTestListFieldArrayListTestObject() {
    TestObject custom = newInstance(TestObject.class);
    Integer[] array = { new Integer(1) };
    custom.setArrayForLists(array);
    return custom;
  }

  public TestObject getInputTestListUsingDestHintTestObject() {
    TestObject custom = newInstance(TestObject.class);
    List<TheFirstSubClass> list = newInstance(ArrayList.class);
    list.add(newInstance(TheFirstSubClass.class));
    custom.setHintList(list);
    return custom;
  }

  public TestObject getInputGeneralMappingTestObject() {
    TestObject custom = newInstance(TestObject.class);
    custom.setOne("one");
    custom.setTwo(new Integer(2));

    int[] pa = { 0, 1, 2, 3, 4 };
    custom.setPrimArray(pa);

    InsideTestObject ito = newInstance(InsideTestObject.class);
    ito.setLabel("label");
    ito.setWrapper(new Integer(1));
    ito.setToWrapper(1);

    custom.setThree(ito);

    // testing if it will map two custom objects that are different types but same names //
    InsideTestObject ito2 = newInstance(InsideTestObject.class);
    ito2.setLabel("label");
    custom.setInsideTestObject(ito2);

    List<String> list1 = newInstance(ArrayList.class);
    list1.add("1value");
    list1.add("2value");
    List<String> list2 = newInstance(ArrayList.class);
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
    Van van = newInstance(Van.class);
    van.setName("van");
    van.setTestValue("testValue");
    custom.setVan(van);
    custom.setExcludeMe("takemeout");

    // testing interfaces
    MetalThingyIF car = newInstance(Car.class);
    car.setName("metalthingy");
    custom.setCarMetalThingy(car);

    List<TheFirstSubClass> hintList = newInstance(ArrayList.class);
    TheFirstSubClass fsc = newInstance(TheFirstSubClass.class);
    TheFirstSubClass fsc2 = newInstance(TheFirstSubClass.class);
    fsc.setS("s");
    fsc2.setS("s");
    hintList.add(fsc);
    hintList.add(fsc2);

    custom.setHintList(hintList);

    custom.setBlankDate("");
    custom.setBlankStringToLong("");
    NoExtendBaseObject nebo = newInstance(NoExtendBaseObject.class);
    custom.setCopyByReference(nebo);
    NoExtendBaseObject nebo2 = newInstance(NoExtendBaseObject.class);
    custom.setCopyByReferenceDeep(nebo2);
    NoExtendBaseObjectGlobalCopyByReference globalNebo = newInstance(NoExtendBaseObjectGlobalCopyByReference.class);
    custom.setGlobalCopyByReference(globalNebo);

    String[] stringArray = new String[] { null, "one", "two" };
    custom.setStringArrayWithNullValue(stringArray);
    return custom;
  }

  public FurtherTestObject getInputTestNoWildcardsFurtherTestObject() {
    FurtherTestObject custom = newInstance(FurtherTestObject.class);
    custom.setOne("label");
    custom.setTwo("another");
    return custom;
  }

  public DehydrateTestObject getInputTestHydrateAndMoreDehydrateTestObject() {
    DehydrateTestObject custom = newInstance(DehydrateTestObject.class);
    Car car = newInstance(Car.class);
    car.setName("name");
    List<Car> carList = newInstance(ArrayList.class);
    carList.add(car);
    custom.setCars(carList);

    Apple apple = newInstance(Apple.class);
    apple.setName("name");
    Orange orange = newInstance(Orange.class);
    orange.setName("name");
    List<Fruit> fruitList = newInstance(ArrayList.class);
    fruitList.add(apple);
    fruitList.add(orange);
    custom.setFruit(fruitList);

    Van van = newInstance(Van.class);
    van.setName("name");
    List<Van> vanList = newInstance(ArrayList.class);
    vanList.add(van);
    custom.setVans(vanList);

    AppleComputer apple1 = newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = newInstance(AppleComputer.class);
    apple2.setName("name");
    List<AppleComputer> compList = newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    custom.setAppleComputers(compList);

    Car iterateCar = newInstance(Car.class);
    iterateCar.setName("name");
    List<Car> iterateCarList = newInstance(ArrayList.class);
    iterateCarList.add(car);
    custom.setIterateCars(iterateCarList);

    iterateCar.setName("name");
    List<Car> iterateMoreCarList = newInstance(ArrayList.class);
    iterateMoreCarList.add(car);
    custom.setIterateMoreCars(iterateMoreCarList);

    return custom;
  }

  public HydrateTestObject getExpectedTestHydrateAndMoreHydrateTestObject() {
    HydrateTestObject hto = newInstance(HydrateTestObject.class);
    Car car = newInstance(Car.class);
    car.setName("name");
    Car buildByCar = newInstance(Car.class);
    buildByCar.setName("Build by buildCar");
    Van van = newInstance(Van.class);
    van.setName("name");

    AppleComputer apple1 = newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = newInstance(AppleComputer.class);
    apple2.setName("name");
    List<AppleComputer> compList = newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    hto.setComputers(compList);
    List<Car> iterateCars = newInstance(ArrayList.class);
    iterateCars.add(car);
    hto.setIterateCars(iterateCars);
    Car[] carArray = { car };
    hto.setCarArray(carArray);
    return hto;
  }

  public HydrateTestObject getInputTestHydrateAndMoreHydrateTestObject() {
    HydrateTestObject hto = newInstance(HydrateTestObject.class);
    Car car = newInstance(Car.class);
    car.setName("name");
    Van van = newInstance(Van.class);
    van.setName("name");
    List<Vehicle> vehicles = newInstance(ArrayList.class);
    vehicles.add(car);
    vehicles.add(van);
    hto.setVehicles(vehicles);

    Apple apple = newInstance(Apple.class);
    apple.setName("name");
    Orange orange = newInstance(Orange.class);
    orange.setName("name");
    List<Apple> apples = newInstance(ArrayList.class);
    apples.add(apple);
    List<Orange> oranges = newInstance(ArrayList.class);
    oranges.add(orange);

    hto.setApples(apples);
    hto.setOranges(oranges);

    AppleComputer apple1 = newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = newInstance(AppleComputer.class);
    apple2.setName("name");
    List<AppleComputer> compList = newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    hto.setComputers(compList);

    List<Car> iterateCars = newInstance(ArrayList.class);
    iterateCars.add(car);
    hto.setIterateCars(iterateCars);
    return hto;

  }

  public DehydrateTestObject getExpectedTestHydrateAndMoreDehydrateTestObject() {
    DehydrateTestObject custom = newInstance(DehydrateTestObject.class);
    Car car = newInstance(Car.class);
    car.setName("name");

    AppleComputer apple1 = newInstance(AppleComputer.class);
    apple1.setName("name");
    AppleComputer apple2 = newInstance(AppleComputer.class);
    apple2.setName("name");
    List<AppleComputer> compList = newInstance(ArrayList.class);
    compList.add(apple1);
    compList.add(apple2);
    custom.setAppleComputers(compList);
    List<Car> iterateCars = newInstance(ArrayList.class);
    iterateCars.add(car);
    custom.setIterateCars(iterateCars);
    return custom;
  }

  public SimpleObj getSimpleObj() {
    SimpleObj result = newInstance(SimpleObj.class);
    result.setField1("one");
    result.setField2(Integer.valueOf("2"));
    result.setField3(BigDecimal.valueOf(3));
    result.setField4(new Double(44.44));
    result.setField5(Calendar.getInstance());
    result.setField6("66");

    return result;
  }

  public AnotherSubClass getAnotherSubClass() {
    AnotherSubClass asub = newInstance(AnotherSubClass.class);
    asub.setBaseAttribute("base");
    asub.setSubAttribute("sub");
    List<BaseSubClass> list = newInstance(ArrayList.class);
    SClass s = newInstance(SClass.class);
    s.setBaseSubAttribute("sBase");
    s.setSubAttribute("s");
    S2Class s2 = newInstance(S2Class.class);
    s2.setBaseSubAttribute("s2Base");
    s2.setSub2Attribute("s2");
    list.add(s2);
    list.add(s);
    asub.setSubList(list);

    List<BaseSubClass> list2 = newInstance(ArrayList.class);
    SClass sclass = newInstance(SClass.class);
    sclass.setBaseSubAttribute("sBase");
    sclass.setSubAttribute("s");
    S2Class s2class = newInstance(S2Class.class);
    s2class.setBaseSubAttribute("s2Base");
    s2class.setSub2Attribute("s2");
    SClass sclass2 = newInstance(SClass.class);
    sclass2.setBaseSubAttribute("sclass2");
    sclass2.setSubAttribute("sclass2");
    list2.add(s2class);
    list2.add(sclass);
    list2.add(sclass2);
    asub.setListToArray(list2);

    SClass sclassA = newInstance(SClass.class);
    SClass sclassB = newInstance(SClass.class);
    sclassA.setBaseSubAttribute("sBase");
    sclassA.setSubAttribute("s");
    sclassB.setBaseSubAttribute("sBase");
    sclassB.setSubAttribute("s");
    asub.setSClass(sclassA);
    asub.setSClass2(sclassB);

    return asub;
  }

  public MyClassA getRandomMyClassA() {
    MyClassA myClassAObj = newInstance(MyClassA.class);
    myClassAObj.setAStringList(getRandomStringList(500));

    return myClassAObj;
  }
  
  public Main getMain() {
    Sub sub = newInstance(Sub.class);
    sub.setName("sName");
    sub.setDetail("sDetail");
    sub.setMarker("sMarker");
    Main result = newInstance(Main.class);
    result.setName("topLevelName");
    result.setSub(sub);
    return result;
  }


  private List<String> getRandomStringList(int listSize) {
    List<String> stringList = newInstance(ArrayList.class);

    for (int count = 0; count < listSize; count = count + 1) {
      stringList.add(RandomStringUtils.randomAlphabetic(255));
    }

    return stringList;
  }

}
