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
package org.dozer.functional_tests;

import java.util.Iterator;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.vo.interfacerecursion.User;
import org.dozer.vo.interfacerecursion.UserGroup;
import org.dozer.vo.interfacerecursion.UserGroupImpl;
import org.dozer.vo.interfacerecursion.UserGroupPrime;
import org.dozer.vo.interfacerecursion.UserImpl;
import org.dozer.vo.interfacerecursion.UserPrime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Christoph Goldner
 */
public class RecursiveInterfaceMappingTest extends AbstractFunctionalTest {

  @Test
  public void testRecursiveInterfaceMapping() throws Exception {

    // prepare 2 Users in 1 UserGroup
    User user1 = newInstance(UserImpl.class);
    user1.setFirstName("first name 1");
    user1.setLastName("last name 1");

    User user2 = newInstance(UserImpl.class);
    user2.setFirstName("first name 2");
    user2.setLastName("last name 2");

    UserGroup userGroup = newInstance(UserGroupImpl.class);
    userGroup.setName("usergroup name");
    userGroup.addUser(user1);
    userGroup.addUser(user2);

    // assert recursion
    assertEquals("Two users in usergroup expected.", 2, userGroup.getUsers().size());
    Iterator<?> iterator = userGroup.getUsers().iterator();
    while (iterator.hasNext()) {
      User user = (User) iterator.next();
      assertNotNull(user);
      assertNotNull(user.getUserGroup());
      assertTrue(user.getUserGroup() == userGroup); // same reference
    }

    // get mapper
    Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles("mappings/interface-recursion-mappings.xml")
            .build();

    // do mapping
    UserGroupPrime userGroupPrime = null;
    try {
      userGroupPrime = mapper.map(userGroup, UserGroupPrime.class);
    } catch (StackOverflowError e) {
      fail("Recursive mapping caused a stack overflow.");
    }

    // check mapped group
    assertNotNull(userGroupPrime);
    assertEquals(userGroup.getName(), userGroupPrime.getName());

    // check mapped users and recursion
    assertEquals("Two users in mapped usergroup expected.", 2, userGroupPrime.getUsers().size());
    iterator = userGroupPrime.getUsers().iterator();
    while (iterator.hasNext()) {
      UserPrime userPrime = (UserPrime) iterator.next();
      assertNotNull(userPrime);
      assertNotNull(userPrime.getUserGroup());
      assertTrue(userPrime.getUserGroup() == userGroupPrime); // same reference
    }
  }

}
