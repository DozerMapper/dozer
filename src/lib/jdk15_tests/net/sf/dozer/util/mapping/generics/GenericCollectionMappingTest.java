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
package net.sf.dozer.util.mapping.generics;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.MapperIF;

/**
 * @author garsombke.franz
 */
public class GenericCollectionMappingTest extends AbstractDozerTest {

  public void testGenericCollectionMapping() throws Exception {
    MapperIF mapper = getNewMapper(new String[] { "genericCollectionMapping.xml" });

    // prepare beans
    User user1 = new User();
    user1.setFirstName("first name 1");
    user1.setLastName("last name 1");

    User user2 = new User();
    user2.setFirstName("first name 2");
    user2.setLastName("last name 2");

    Set<User> users = new HashSet<User>();
    users.add(user1);
    users.add(user2);

    UserGroup userGroup = new UserGroup();
    userGroup.setName("usergroup name");
    userGroup.setUsers(users);
    userGroup.setStatus(Status.SUCCESS);

    // do mapping
    UserGroupPrime userGroupPrime = (UserGroupPrime) mapper.map(userGroup, UserGroupPrime.class);

    // prove access to generic type information
    Method setter = UserGroupPrime.class.getMethod("setUsers", List.class);
    Type[] parameterTypes = setter.getGenericParameterTypes();
    assertEquals(1, parameterTypes.length);
    ParameterizedType parameterType = (ParameterizedType) parameterTypes[0];
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
    UserGroup userGroupMapBack = (UserGroup) mapper.map(userGroupPrime, UserGroup.class);
    Set<?> usersGroupPrime = userGroupMapBack.getUsers();
    assertNotNull(usersGroupPrime);
    assertEquals(2, usersGroupPrime.size());
    assertTrue("Expecting instance of UserPrime.", usersGroupPrime.iterator().next() instanceof User);
    
  }
}
