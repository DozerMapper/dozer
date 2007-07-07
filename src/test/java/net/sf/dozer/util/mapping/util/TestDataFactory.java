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

import org.apache.commons.lang.RandomStringUtils;

import net.sf.dozer.util.mapping.vo.*;
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

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public abstract class TestDataFactory {


  public static SubClass getSubClass()
  {
      SubClass obj = new SubClass();

      obj.setAttribute( "subclass" );
      obj.setSuperAttribute( "superclass" );

      List superList = new ArrayList();
      superList.add( "one" );
      superList.add( "two" );
      superList.add( "three" );

      obj.setSuperList( superList );
      obj.setSuperSuperAttribute("supersuperattribute");
      obj.setSuperSuperSuperAttr( "toplevel" );

      obj.setTestObject( TestDataFactory.getInputGeneralMappingTestObject() );
      HydrateTestObject2 sourceObj = new HydrateTestObject2();

      TestCustomConverterObject cobj = new TestCustomConverterObject();
      CustomDoubleObjectIF doub = new CustomDoubleObject();
      doub.setTheDouble(15);
      cobj.setAttribute(doub);

      obj.setCustomConvert( cobj );

      obj.setHydrate( sourceObj );

      obj.setSuperFieldToExclude("superFieldToExclude");

      return obj;
  }

  public static SrcDeepObj getSrcDeepObj() {
    SrcDeepObj result = new SrcDeepObj();
    SrcNestedDeepObj srcNested = new SrcNestedDeepObj();
    SrcNestedDeepObj2 srcNested2 = new SrcNestedDeepObj2();
    FurtherTestObjectPrime furtherObjectPrime = new FurtherTestObjectPrime();

    srcNested2.setSrc5("nestedsrc2field5");
    furtherObjectPrime.setOne("fjd");

    srcNested.setSrc1("nestedsrc1");
    srcNested.setSrc2(Integer.valueOf("5"));
    srcNested.setSrc3(90);
    srcNested.setSrc4(new String[] { "item1", "item2", "item3" });
    srcNested.setSrcNestedObj2(srcNested2);
    srcNested.setSrc6(furtherObjectPrime);

    //List to List.  String to Integer
    List hintList = new ArrayList();
    hintList.add("1");
    hintList.add("2");
    srcNested.setHintList(hintList);

    //List to List.  TheFirstSubClass to TheFirstSubClassPrime
    TheFirstSubClass hintList2Obj = new TheFirstSubClass();
    hintList2Obj.setS("test");

    TheFirstSubClass hintList2Obj2 = new TheFirstSubClass();
    hintList2Obj.setS("test2");

    List hintList2 = new ArrayList();
    hintList2.add(hintList2Obj);
    hintList2.add(hintList2Obj2);
    srcNested.setHintList2(hintList2);

    result.setSrcNestedObj(srcNested);
    result.setSameNameField("sameNameField");

    return result;
  }

  public static House getHouse() {
    House house = new House();
    Address address = new Address();
    address.setStreet("1234 street");
    City city = new City();
    city.setName("Denver");
    address.setCity(city);

    house.setAddress(address);

    Person person = new Person();
    person.setName("Franz");

    house.setOwner(person);

    house.setPrice(1000000);

    Van van = new Van();
    van.setName("van");
    van.setTestValue("testValue");
    house.setVan(van);

    Room living = new Room();
    living.setName("Living");
    Room kitchen = new Room();
    kitchen.setName("kitchen");

    List rooms = new ArrayList();
    rooms.add(living);
    rooms.add(kitchen);

    house.setRooms(rooms);
    List custom = new ArrayList();
    Van van2 = new Van();
    van2.setName("van2");
    custom.add(van2);
    house.setCustomSetGetMethod(custom);

    return house;
  }

  public static HydrateTestObject getExpectedTestNoSourceValueIterateFieldMapHydrateTestObject() {
    Car car = new Car();
    car.setName("Build by buildCar");
    HydrateTestObject hto = new HydrateTestObject();
    //Problem - Destination Field is array of 'cars' - but getMethod() is buildCar() which returns a Car. MapCollection method can not handle this...
    //DestinationType is a Car and it should be an array.
    //hto.addCar(car);
    hto.setCarArray(new Car[0]);
    return hto;
  }

  public static NoCustomMappingsObject getInputTestNoClassMappingsNoCustomMappingsObject() {
    NoCustomMappingsObject custom = new NoCustomMappingsObject();
    custom.setStringDataType("stringDataType");
    custom.setDate(new Date());
    custom.setFive(55);
    custom.setFour(44);
    custom.setSeven(77);
    custom.setSix(new Double(87.62));
    custom.setThree(new Integer(9876));
    
    return custom;
  }

  public static NoCustomMappingsObject getInputTestMapFieldWithMapNoCustomMappingsObject() {
    NoCustomMappingsObject custom = new NoCustomMappingsObject();
    Map map = new HashMap();
    map.put("1", "1value");
    map.put("2", "2value");
    custom.setMapDataType(map);
    return custom;
  }

  public static NoCustomMappingsObject getInputTestMapFieldWithEmptyMapNoCustomMappingsObject() {
    NoCustomMappingsObject custom = new NoCustomMappingsObject();
    Map map = new HashMap();
    custom.setMapDataType(map);
    return custom;
  }

  public static NoCustomMappingsObject getInputTestSetFieldWithSetNoCustomMappingsObject() {
    NoCustomMappingsObject custom = new NoCustomMappingsObject();
    Set set = new HashSet();
    set.add("1value");
    set.add("2value");
    custom.setSetDataType(set);
    return custom;
  }

  public static NoCustomMappingsObject getInputTestSetFieldWithSetEmptyCustomMappingsObject() {
    NoCustomMappingsObject custom = new NoCustomMappingsObject();
    Set set = new HashSet();
    custom.setSetDataType(set);
    return custom;
  }

  public static NoCustomMappingsObject getInputTestSetFieldComplexSetNoCustomMappingsObject() {
    NoCustomMappingsObject custom = new NoCustomMappingsObject();
    Set set = new HashSet();
    set.add(getInputTestNoClassMappingsNoCustomMappingsObject());
    custom.setSetDataType(set);
    return custom;
  }

  public static TestObject getInputTestListFieldEmptyListTestObject() {
    TestObject custom = new TestObject();
    custom.setEqualNamedList(new ArrayList());
    return custom;
  }

  public static TestObject getInputTestListFieldArrayListTestObject() {
    TestObject custom = new TestObject();
    Integer[] array = { new Integer(1) };
    custom.setArrayForLists(array);
    return custom;
  }

  public static TestObject getInputTestListUsingDestHintTestObject() {
    TestObject custom = new TestObject();
    List list = new ArrayList();
    list.add(new TheFirstSubClass());
    custom.setHintList(list);
    return custom;
  }

  public static TestObject getInputGeneralMappingTestObject() {
    TestObject custom = new TestObject();
    custom.setOne("one");
    custom.setTwo(new Integer(2));

    int[] pa = { 0 , 1, 2, 3,4};
    custom.setPrimArray( pa );

    InsideTestObject ito = new InsideTestObject();
    ito.setLabel("label");
    ito.setWrapper(new Integer(1));
    ito.setToWrapper(1);

    custom.setThree(ito);

    //testing if it will map two custom objects that are different types but same names //
    InsideTestObject ito2 = new InsideTestObject();
    ito2.setLabel("label");
    custom.setInsideTestObject(ito2);

    List list1 = new ArrayList();
    list1.add("1value");
    list1.add("2value");
    List list2 = new ArrayList();
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
    Van van = new Van();
    van.setName("van");
    van.setTestValue("testValue");
    custom.setVan(van);
    custom.setExcludeMe("takemeout");

    // testing interfaces
    MetalThingyIF car = new Car();
    car.setName("metalthingy");
    custom.setCarMetalThingy(car);

    List hintList = new ArrayList();
    TheFirstSubClass fsc = new TheFirstSubClass();
    TheFirstSubClass fsc2 = new TheFirstSubClass();
    fsc.setS("s");
    fsc2.setS("s");
    hintList.add(fsc);
    hintList.add(fsc2);

    custom.setHintList(hintList);
    
    custom.setBlankDate("");
    custom.setBlankStringToLong("");
    NoExtendBaseObject nebo = new NoExtendBaseObject();
    custom.setCopyByReference(nebo);
    NoExtendBaseObject nebo2 = new NoExtendBaseObject();
    custom.setCopyByReferenceDeep(nebo2);
    NoExtendBaseObjectGlobalCopyByReference globalNebo = new NoExtendBaseObjectGlobalCopyByReference();
    custom.setGlobalCopyByReference(globalNebo);
    
    String[] stringArray = new String[] { null, "one", "two" };
    custom.setStringArrayWithNullValue(stringArray);
    return custom;
  }

  public static FurtherTestObject getInputTestNoWildcardsFurtherTestObject() {
    FurtherTestObject custom = new FurtherTestObject();
    custom.setOne("label");
    custom.setTwo("another");
    return custom;
  }

  public static DehydrateTestObject getInputTestHydrateAndMoreDehydrateTestObject() {
    DehydrateTestObject custom = new DehydrateTestObject();
    Car car = new Car();
    car.setName("name");
    List carList = new ArrayList();
    carList.add(car);
    custom.setCars(carList);

    Apple apple = new Apple();
    apple.setName("name");
    Orange orange = new Orange();
    orange.setName("name");
    List fruitList = new ArrayList();
    fruitList.add(apple);
    fruitList.add(orange);
    custom.setFruit(fruitList);

    Van van = new Van();
    van.setName("name");
    List vanList = new ArrayList();
    vanList.add(van);
    custom.setVans(vanList);

    AppleComputer apple1 = new AppleComputer();
    apple1.setName("name");
    AppleComputer apple2 = new AppleComputer();
    apple2.setName("name");
    List compList = new ArrayList();
    compList.add(apple1);
    compList.add(apple2);
    custom.setAppleComputers(compList);
    
    Car iterateCar = new Car();
    iterateCar.setName("name");
    List iterateCarList = new ArrayList();
    iterateCarList.add(car);
    custom.setIterateCars(iterateCarList);

    iterateCar.setName("name");
    List iterateMoreCarList = new ArrayList();
    iterateMoreCarList.add(car);
    custom.setIterateMoreCars(iterateMoreCarList);

    return custom;
  }

  public static HydrateTestObject getExpectedTestHydrateAndMoreHydrateTestObject() {
    HydrateTestObject hto = new HydrateTestObject();
    Car car = new Car();
    car.setName("name");
    Car buildByCar = new Car();
    buildByCar.setName("Build by buildCar");
    Van van = new Van();
    van.setName("name");

    AppleComputer apple1 = new AppleComputer();
    apple1.setName("name");
    AppleComputer apple2 = new AppleComputer();
    apple2.setName("name");
    List compList = new ArrayList();
    compList.add(apple1);
    compList.add(apple2);
    hto.setComputers(compList);
    List iterateCars = new ArrayList();
    iterateCars.add(car);
    hto.setIterateCars(iterateCars);
    Car[] carArray = { car };
    hto.setCarArray(carArray);
    return hto;
  }



  public static HydrateTestObject getInputTestHydrateAndMoreHydrateTestObject() {
    HydrateTestObject hto = new HydrateTestObject();
    Car car = new Car();
    car.setName("name");
    Van van = new Van();
    van.setName("name");
    List vehicles = new ArrayList();
    vehicles.add(car);
    vehicles.add(van);
    hto.setVehicles(vehicles);

    Apple apple = new Apple();
    apple.setName("name");
    Orange orange = new Orange();
    orange.setName("name");
    List apples = new ArrayList();
    apples.add(apple);
    List oranges = new ArrayList();
    oranges.add(orange);

    hto.setApples(apples);
    hto.setOranges(oranges);
    
    AppleComputer apple1 = new AppleComputer();
    apple1.setName("name");
    AppleComputer apple2 = new AppleComputer();
    apple2.setName("name");
    List compList = new ArrayList();
    compList.add(apple1);
    compList.add(apple2);
    hto.setComputers(compList);

    List iterateCars = new ArrayList();
    iterateCars.add(car);
    hto.setIterateCars(iterateCars);
    return hto;

  }

  public static DehydrateTestObject getExpectedTestHydrateAndMoreDehydrateTestObject() {
    DehydrateTestObject custom = new DehydrateTestObject();
    Car car = new Car();
    car.setName("name");

    AppleComputer apple1 = new AppleComputer();
    apple1.setName("name");
    AppleComputer apple2 = new AppleComputer();
    apple2.setName("name");
    List compList = new ArrayList();
    compList.add(apple1);
    compList.add(apple2);
    custom.setAppleComputers(compList);
    List iterateCars = new ArrayList();
    iterateCars.add(car);
    custom.setIterateCars(iterateCars);    
    return custom;
  }
  
  public static SimpleObj getSimpleObj() {
    SimpleObj result = new SimpleObj();
    result.setField1("one");
    result.setField2(Integer.valueOf("2"));
    result.setField3(BigDecimal.valueOf(3));
    result.setField4(new Double(44.44));
    result.setField5(Calendar.getInstance());
    result.setField6("66");
    
    return result;
  }
  
  public static AnotherSubClass getAnotherSubClass() {
    AnotherSubClass asub = new AnotherSubClass();
    asub.setBaseAttribute("base");
    asub.setSubAttribute("sub");
    List list = new ArrayList();
    SClass s = new SClass();
    s.setBaseSubAttribute("sBase");
    s.setSubAttribute("s");
    S2Class s2 = new S2Class();
    s2.setBaseSubAttribute("s2Base");
    s2.setSub2Attribute("s2");
    list.add(s2);
    list.add(s);
    asub.setSubList(list);

    List list2 = new ArrayList();
    SClass sclass = new SClass();
    sclass.setBaseSubAttribute("sBase");
    sclass.setSubAttribute("s");
    S2Class s2class = new S2Class();
    s2class.setBaseSubAttribute("s2Base");
    s2class.setSub2Attribute("s2");
    SClass sclass2 = new SClass();
    sclass2.setBaseSubAttribute("sclass2");
    sclass2.setSubAttribute("sclass2");
    list2.add(s2class);
    list2.add(sclass);
    list2.add(sclass2);
    asub.setListToArray(list2);

    SClass sclassA = new SClass();
    SClass sclassB = new SClass();
    sclassA.setBaseSubAttribute("sBase");
    sclassA.setSubAttribute("s");
    sclassB.setBaseSubAttribute("sBase");
    sclassB.setSubAttribute("s");
    asub.setSClass(sclassA);
    asub.setSClass2(sclassB);

    return asub;    
  }
  
  public static MyClassA getRandomMyClassA() { 
    MyClassA myClassAObj = new MyClassA(); 
    myClassAObj.setAStringList(getRandomStringList(500)); 
   
    return myClassAObj; 
  } 
   
  private static List getRandomStringList(int listSize) { 
    List stringList = new ArrayList(listSize); 
   
    for (int count = 0; count < listSize; count = count + 1) { 
      stringList.add(RandomStringUtils.randomAlphabetic(255)); 
    } 
   
    return stringList; 
  } 
  
}
