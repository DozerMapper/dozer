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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.vo.generics.Status;
import com.github.dozermapper.core.vo.generics.User;
import com.github.dozermapper.core.vo.generics.UserGroup;
import com.github.dozermapper.core.vo.generics.UserGroupPrime;
import com.github.dozermapper.core.vo.generics.UserPrime;
import com.github.dozermapper.core.vo.generics.deepindex.AnotherTestObject;
import com.github.dozermapper.core.vo.generics.deepindex.DestDeepObj;
import com.github.dozermapper.core.vo.generics.deepindex.Family;
import com.github.dozermapper.core.vo.generics.deepindex.HeadOfHouseHold;
import com.github.dozermapper.core.vo.generics.deepindex.Pet;
import com.github.dozermapper.core.vo.generics.deepindex.SrcDeepObj;
import com.github.dozermapper.core.vo.generics.deepindex.TestObject;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Ignore("Failing with cglib")
public class GenericCollectionMappingTest extends AbstractFunctionalTest {

    @Test
    public void testGenericCollectionMapping() throws Exception {
        Mapper mapper = getMapper("mappings/genericCollectionMapping.xml");

        // prepare beans
        User user1 = newInstance(User.class);
        user1.setFirstName("first name 1");
        user1.setLastName("last name 1");

        User user2 = newInstance(User.class);
        user2.setFirstName("first name 2");
        user2.setLastName("last name 2");

        Set<User> users = newInstance(HashSet.class);
        users.add(user1);
        users.add(user2);

        UserGroup userGroup = newInstance(UserGroup.class);
        userGroup.setName("usergroup name");
        userGroup.setUsers(users);
        userGroup.setStatus(Status.SUCCESS);

        // do mapping
        UserGroupPrime userGroupPrime = mapper.map(userGroup, UserGroupPrime.class);

        // prove access to generic type information
        Method setter = UserGroupPrime.class.getMethod("setUsers", List.class);
        Type[] parameterTypes = setter.getGenericParameterTypes();
        assertEquals(1, parameterTypes.length);
        ParameterizedType parameterType = (ParameterizedType)parameterTypes[0];
        assertEquals(List.class, parameterType.getRawType());
        assertEquals(UserPrime.class, parameterType.getActualTypeArguments()[0]);

        // check group
        assertNotNull(userGroupPrime);
        assertEquals(userGroup.getName(), userGroupPrime.getName());

        // check resulting collection
        List<?> usersPrime = userGroupPrime.getUsers();
        assertNotNull(usersPrime);
        assertEquals(2, usersPrime.size());
        assertTrue("Expecting instance of UserPrime.", usersPrime.get(0) instanceof UserPrime);
        assertTrue("Expecting instance of UserPrime.", usersPrime.get(1) instanceof UserPrime);
        assertEquals("SUCCESS", userGroupPrime.getStatusPrime().name());

        // Map the other way
        UserGroup userGroupMapBack = mapper.map(userGroupPrime, UserGroup.class);
        Set<?> usersGroupPrime = userGroupMapBack.getUsers();
        assertNotNull(usersGroupPrime);
        assertEquals(2, usersGroupPrime.size());
        assertTrue("Expecting instance of UserPrime.", usersGroupPrime.iterator().next() instanceof User);
    }

    @Test
    public void testDeepMappingWithIndexOnSrcField() {
        Mapper mapper = getMapper("mappings/genericCollectionMapping.xml");

        AnotherTestObject anotherTestObject = newInstance(AnotherTestObject.class);
        anotherTestObject.setField3("another test object field 3 value");
        anotherTestObject.setField4("6453");

        TestObject testObject1 = newInstance(TestObject.class);
        TestObject testObject2 = newInstance(TestObject.class);
        testObject2.setEqualNamedList(Arrays.asList(anotherTestObject));

        SrcDeepObj src = newInstance(SrcDeepObj.class);
        src.setSomeList(Arrays.asList(testObject1, testObject2));

        DestDeepObj dest = mapper.map(src, DestDeepObj.class);
        assertEquals("another test object field 3 value", dest.getDest5());
        assertEquals(Integer.valueOf("6453"), (dest.getHintList().get(0)).getTwoPrime());
    }

    @Test
    public void testDeepMappingWithIndexOnDestField() {
        Mapper mapper = getMapper("mappings/genericCollectionMapping.xml");
        DestDeepObj src = newInstance(DestDeepObj.class);
        src.setDest5("some string value for field");

        SrcDeepObj dest = mapper.map(src, SrcDeepObj.class);
        assertEquals("some string value for field", dest.getSomeList().get(1).getEqualNamedList().get(0).getField3());
    }

    @Test
    public void testDeepMapIndexed() {
        Mapper mapper = getMapper("mappings/genericCollectionMapping.xml");
        Pet[] myPets = new Pet[2];
        Family source = newInstance(Family.class, new Object[] {"john", "jane", "doe", new Integer(22000), new Integer(20000)});
        Pet firstPet = newInstance(Pet.class, new Object[] {"molly", 2});
        myPets[0] = firstPet;

        Pet[] offSprings = new Pet[4];
        offSprings[0] = newInstance(Pet.class, new Object[] {"Rocky1", 1});
        offSprings[1] = newInstance(Pet.class, new Object[] {"Rocky2", 1});
        offSprings[2] = newInstance(Pet.class, new Object[] {"Rocky3", 1});
        offSprings[3] = newInstance(Pet.class, new Object[] {"Rocky4", 1});

        Pet secondPet = newInstance(Pet.class, new Object[] {"Rocky", 3, offSprings});
        myPets[1] = secondPet;

        // Save the pet details into the source object
        source.setPets(myPets);

        HeadOfHouseHold dest = mapper.map(source, HeadOfHouseHold.class);
        assertEquals((source.getFamilyMembers().get(0)).getFirstName(), dest.getFirstName());
        assertEquals((source.getFamilyMembers().get(0)).getLastName(), dest.getLastName());
        assertEquals((source.getFamilyMembers().get(0)).getSalary(), dest.getSalary());
        assertEquals(source.getPets()[1].getPetName(), dest.getPetName());
        assertEquals(String.valueOf(source.getPets()[1].getPetAge()), dest.getPetAge());
        assertEquals(source.getPets()[1].getOffSpring()[2].getPetName(), dest.getOffSpringName());
    }

    @Test
    public void testDeepMapInvIndexed() {
        Mapper mapper = getMapper("mappings/genericCollectionMapping.xml");
        HeadOfHouseHold source = newInstance(HeadOfHouseHold.class);
        source.setFirstName("Tom");
        source.setLastName("Roy");
        source.setPetName("Ronny");
        source.setSalary(new Integer(15000));
        source.setPetAge("2");
        source.setOffSpringName("Ronny2");

        Family dest = newInstance(Family.class);
        mapper.map(source, dest);

        assertEquals((dest.getFamilyMembers().get(0)).getFirstName(), source.getFirstName());
        assertEquals((dest.getFamilyMembers().get(0)).getLastName(), source.getLastName());
        assertEquals((dest.getFamilyMembers().get(0)).getSalary(), source.getSalary());
        assertEquals(dest.getPets()[1].getPetName(), source.getPetName());
        assertEquals(String.valueOf(dest.getPets()[1].getPetAge()), source.getPetAge());
        assertEquals(dest.getPets()[1].getOffSpring()[2].getPetName(), source.getOffSpringName());
    }

}
