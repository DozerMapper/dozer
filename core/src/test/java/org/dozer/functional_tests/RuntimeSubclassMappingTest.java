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

import org.dozer.vo.runtimesubclass.SpecialUserGroup;
import org.dozer.vo.runtimesubclass.SpecialUserGroupPrime;
import org.dozer.vo.runtimesubclass.User;
import org.dozer.vo.runtimesubclass.UserGroup;
import org.dozer.vo.runtimesubclass.UserPrime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitry Buzdin
 */
public class RuntimeSubclassMappingTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("mappings/runtimeSubclass.xml");
  }

  @Test
  public void testSpecialMapping() throws Exception {
    // SpecialUserGroup(!)
    SpecialUserGroup userGroup = newInstance(SpecialUserGroup.class);
    userGroup.setName("special user group");

    // User in SpecialUserGroup
    User user = newInstance(User.class);
    user.setUserGroup(userGroup);

    // do mapping to UserPrime
    UserPrime userPrime = mapper.map(user, UserPrime.class);

    // check class type of mapped group, should be SpecialUserGroupPrime!
    assertNotNull(userPrime.getUserGroup());
    assertEquals("special user group", userPrime.getUserGroup().getName());
    assertEquals(SpecialUserGroupPrime.class, userPrime.getUserGroup().getClass());
  }

  @Test
  public void testNormalMapping() throws Exception {
    // normal UserGroup
    UserGroup userGroup = newInstance(UserGroup.class);
    userGroup.setName("user group");

    // User in normal UserGroup
    User user = newInstance(User.class);
    user.setUserGroup(userGroup);

    // do mapping to UserPrime
    UserPrime userPrime = mapper.map(user, UserPrime.class);

    // check class type of mapped group, should NOT be SpecialUserGroupPrime!
    assertNotNull(userPrime.getUserGroup());
    assertFalse(userPrime.getUserGroup() instanceof SpecialUserGroupPrime);
  }

}
