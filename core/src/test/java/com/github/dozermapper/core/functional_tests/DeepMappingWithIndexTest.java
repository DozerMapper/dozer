/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import com.github.dozermapper.core.vo.AnotherTestObject;
import com.github.dozermapper.core.vo.SimpleObj;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;
import com.github.dozermapper.core.vo.deep.DestDeepObj;
import com.github.dozermapper.core.vo.deep.SrcDeepObj;
import com.github.dozermapper.core.vo.deep.SrcNestedDeepObj;
import com.github.dozermapper.core.vo.deep.SrcNestedDeepObj2;
import com.github.dozermapper.core.vo.deepindex.A;
import com.github.dozermapper.core.vo.deepindex.B;
import com.github.dozermapper.core.vo.deepindex.Family;
import com.github.dozermapper.core.vo.deepindex.HeadOfHouseHold;
import com.github.dozermapper.core.vo.deepindex.HeadOfHouseHolds;
import com.github.dozermapper.core.vo.deepindex.HeadOfHouseHoldsContainer;
import com.github.dozermapper.core.vo.deepindex.PersonalDetails;
import com.github.dozermapper.core.vo.deepindex.Pet;
import com.github.dozermapper.core.vo.deepindex.customconverter.First;
import com.github.dozermapper.core.vo.deepindex.customconverter.Last;
import com.github.dozermapper.core.vo.deepindex.isaccessible.FlatPerson;
import com.github.dozermapper.core.vo.deepindex.isaccessible.Person;
import com.github.dozermapper.core.vo.deepindex.isaccessible.Phone;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeepMappingWithIndexTest extends AbstractFunctionalTest {

    @Override
    @Before
    public void setUp() throws Exception {
        mapper = getMapper("mappings/deepMappingWithIndexedFields.xml");
    }

    @Test
    public void testDeepMappingWithIndexOnSrcField() {
        SimpleObj simpleObj = newInstance(SimpleObj.class);
        simpleObj.setField1("985756");

        SrcNestedDeepObj2 srcNestedObj2 = newInstance(SrcNestedDeepObj2.class);
        srcNestedObj2.setSimpleObjects(new SimpleObj[] {simpleObj, newInstance(SimpleObj.class)});

        SrcNestedDeepObj srcNestedObj = newInstance(SrcNestedDeepObj.class);
        srcNestedObj.setSrcNestedObj2(srcNestedObj2);

        AnotherTestObject anotherTestObject = newInstance(AnotherTestObject.class);
        anotherTestObject.setField3("another test object field 3 value");
        anotherTestObject.setField4("6453");

        TestObject testObject1 = newInstance(TestObject.class);
        TestObject testObject2 = newInstance(TestObject.class);
        testObject2.setEqualNamedList(Arrays.asList(anotherTestObject));

        SrcDeepObj src = newInstance(SrcDeepObj.class);
        src.setSomeList(Arrays.asList(testObject1, testObject2));
        src.setSrcNestedObj(srcNestedObj);

        DestDeepObj dest = mapper.map(src, DestDeepObj.class);
        assertEquals(Integer.valueOf("985756"), dest.getDest2());
        assertEquals("another test object field 3 value", dest.getDest5());
        assertEquals(Integer.valueOf("6453"), ((TestObjectPrime)dest.getHintList().get(0)).getTwoPrime());
    }

    @Test
    public void testDeepMappingWithIndexOnDestField() {
        DestDeepObj src = newInstance(DestDeepObj.class);
        src.setDest2(new Integer(857557));
        src.setDest5("789777");

        SrcDeepObj dest = mapper.map(src, SrcDeepObj.class);
        assertEquals("857557", dest.getSrcNestedObj().getSrcNestedObj2().getSimpleObjects()[0].getField1());
        TestObject destTestObj = (TestObject)dest.getSomeList().get(1);
        assertEquals("789777", ((AnotherTestObject)destTestObj.getEqualNamedList().get(0)).getField3());
    }

    @Test
    public void testDeepMapIndexed() {
        Pet[] myPets = new Pet[2];
        Family source = new Family("john", "jane", "doe", new Integer(22000), new Integer(20000));
        Pet firstPet = new Pet("molly", 2, null);
        myPets[0] = firstPet;

        Pet[] offSprings = new Pet[4];
        offSprings[0] = new Pet("Rocky1", 1, null);
        offSprings[1] = new Pet("Rocky2", 1, null);
        offSprings[2] = new Pet("Rocky3", 1, null);
        offSprings[3] = new Pet("Rocky4", 1, null);

        Pet secondPet = new Pet("Rocky", 3, offSprings);
        myPets[1] = secondPet;

        // Save the pet details into the source object
        source.setPets(myPets);

        HeadOfHouseHold dest = mapper.map(source, HeadOfHouseHold.class);
        assertEquals(((PersonalDetails)source.getFamilyMembers().get(0)).getFirstName(), dest.getFirstName());
        assertEquals(((PersonalDetails)source.getFamilyMembers().get(0)).getLastName(), dest.getLastName());
        assertEquals(((PersonalDetails)source.getFamilyMembers().get(0)).getSalary(), dest.getSalary());
        assertEquals(source.getPets()[1].getPetName(), dest.getPetName());
        assertEquals(String.valueOf(source.getPets()[1].getPetAge()), dest.getPetAge());
        assertEquals(source.getPets()[1].getOffSpring()[2].getPetName(), dest.getOffSpringName());
    }

    @Test
    public void testDeepMapInvIndexed() {
        HeadOfHouseHold source = newInstance(HeadOfHouseHold.class);
        source.setFirstName("Tom");
        source.setLastName("Roy");
        source.setPetName("Ronny");
        source.setSalary(new Integer(15000));
        source.setPetAge("2");
        source.setOffSpringName("Ronny2");

        Family dest = newInstance(Family.class);
        mapper.map(source, dest);

        assertEquals(((PersonalDetails)dest.getFamilyMembers().get(0)).getFirstName(), source.getFirstName());
        assertEquals(((PersonalDetails)dest.getFamilyMembers().get(0)).getLastName(), source.getLastName());
        assertEquals(((PersonalDetails)dest.getFamilyMembers().get(0)).getSalary(), source.getSalary());
        assertEquals(dest.getPets()[1].getPetName(), source.getPetName());
        assertEquals(String.valueOf(dest.getPets()[1].getPetAge()), source.getPetAge());
        assertEquals(dest.getPets()[1].getOffSpring()[2].getPetName(), source.getOffSpringName());
    }

    @Test
    public void testDeepMappingWithIndexOnTheEnd() {
        HeadOfHouseHold sourceHouseHold = newInstance(HeadOfHouseHold.class);
        sourceHouseHold.setFirstName("Tom");
        sourceHouseHold.setLastName("Roy");
        sourceHouseHold.setPetName("Ronny");
        sourceHouseHold.setSalary(new Integer(15000));
        sourceHouseHold.setPetAge("2");
        sourceHouseHold.setOffSpringName("Ronny2");

        HeadOfHouseHolds sourceHouseHolds = new HeadOfHouseHolds();
        sourceHouseHolds.setHeadOfHouseHoldsList(Collections.singletonList(sourceHouseHold));

        HeadOfHouseHoldsContainer source = new HeadOfHouseHoldsContainer();
        source.setHeadOfHouseHolds(sourceHouseHolds);

        Family dest = newInstance(Family.class);
        mapper.map(source, dest);

        assertEquals(((PersonalDetails)dest.getFamilyMembers().get(0)).getFirstName(), sourceHouseHold.getFirstName());
        assertEquals(((PersonalDetails)dest.getFamilyMembers().get(0)).getLastName(), sourceHouseHold.getLastName());
        assertEquals(((PersonalDetails)dest.getFamilyMembers().get(0)).getSalary(), sourceHouseHold.getSalary());
    }

    @Test
    public void testDeepMapIndexedIsAccessible() {
        mapper = getMapper("mappings/deepMappingWithIndexAndIsAccessible.xml");

        Person source = newInstance(Person.class);
        Vector<Phone> phonesList = new Vector<>();
        Phone phone = new Phone();
        phone.setNumber("911");
        phonesList.add(phone);
        source.phones = phonesList;
        FlatPerson dest = newInstance(FlatPerson.class);

        mapper.map(source, dest);
        assertEquals(dest.getPhoneNumber(), source.phones.get(0).getNumber());
    }

    @Test
    public void testDeepMapIndexedIsAccessibleInversed() {
        mapper = getMapper("mappings/deepMappingWithIndexAndIsAccessible.xml");

        FlatPerson source = newInstance(FlatPerson.class);
        source.setPhoneNumber("911");
        Person dest = newInstance(Person.class);

        mapper.map(source, dest);
        assertEquals(dest.phones.get(0).getNumber(), source.getPhoneNumber());
    }

    /**
     * TestCase to reproduce the bug#1845706.
     * <p>
     * Originally, error happens when users define a multi-level indexed mapping like
     * "secondArray[0].thirdArray[7]".  When parsing, XMLParser will replace all indexes,
     * thus this custom mapping is translated into "secondArray.thirdArray".  However, this will cause
     * com.github.dozermapper.core.util.mapping.propertydescriptor.GetterSetterPropertyDescriptor(line105~110)
     * incorrectly invoke the getter method, getThirdArray(), on the array object instead of array's
     * specific element, thus causing Reflection throws
     * "java.lang.IllegalArgumentException: object is not an instance of declaring class".
     * <p>
     * To fix this problem, let com.github.dozermapper.core.XMLParser only replace the last
     * indexes in getFieldNameOfIndexedField.
     */
    @Test
    public void testDeepIndexMappingWithCustomConverter() {
        mapper = getMapper("mappings/deepMappingWithIndexedFieldsByCustomConverter.xml");
        First first = new First();
        Last last = mapper.map(first, Last.class);
        assertNotNull("nested third object should not be null", last.getThird());
        assertNotNull("name should not be null", last.getThird().getName());
        assertEquals(first.getSecondArray()[0].getThirdArray()[7].getName(), last.getThird().getName());
    }

    // bug #1803172
    @Test
    public void testDeepIndexMapping_CollectionNeedsResizing() {
        mapper = getMapper("mappings/deepMappingWithIndexedFields.xml");
        A src = new A();
        src.setId1(new Integer(10));
        src.setId2(new Integer(20));

        B dest = mapper.map(src, B.class);
        assertEquals("wrong value for id1", src.getId1().intValue(), dest.getFoo()[0].getId());
        assertEquals("wrong value for id2", src.getId2().intValue(), dest.getFoo()[1].getId());
    }

}
