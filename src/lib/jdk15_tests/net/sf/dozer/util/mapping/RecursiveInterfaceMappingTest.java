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
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.MapperIF;
import net.sf.dozer.util.mapping.vo.interfacerecursion.User;
import net.sf.dozer.util.mapping.vo.interfacerecursion.UserGroup;
import net.sf.dozer.util.mapping.vo.interfacerecursion.UserGroupImpl;
import net.sf.dozer.util.mapping.vo.interfacerecursion.UserGroupPrime;
import net.sf.dozer.util.mapping.vo.interfacerecursion.UserImpl;
import net.sf.dozer.util.mapping.vo.interfacerecursion.UserPrime;

/**
 * @author Christoph Goldner
 */
public class RecursiveInterfaceMappingTest extends TestCase {

  public void testRecursiveInterfaceMapping() throws Exception {

    // prepare 2 Users in 1 UserGroup
    User user1 = new UserImpl();
    user1.setFirstName("first name 1");
    user1.setLastName("last name 1");

    User user2 = new UserImpl();
    user2.setFirstName("first name 2");
    user2.setLastName("last name 2");

    UserGroup userGroup = new UserGroupImpl();
    userGroup.setName("usergroup name");
    userGroup.addUser(user1);
    userGroup.addUser(user2);

    // assert recursion
    assertEquals("Two users in usergroup expected.", 2, userGroup.getUsers().size());
    Iterator iterator = userGroup.getUsers().iterator();
    while (iterator.hasNext()) {
      User user = (User) iterator.next();
      assertNotNull(user);
      assertNotNull(user.getUserGroup());
      assertTrue(user.getUserGroup() == userGroup); // same reference
    }

    // get mapper
    List mappingFiles = new ArrayList();
    mappingFiles.add("interface-recursion-mappings.xml");
    MapperIF mapper = new DozerBeanMapper(mappingFiles);

    // do mapping
    UserGroupPrime userGroupPrime = null;
    try {
      userGroupPrime = (UserGroupPrime) mapper.map(userGroup, UserGroupPrime.class);
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
