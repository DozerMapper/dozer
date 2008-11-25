/*
 * Copyright 2005-2008 the original author or authors.
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
package net.sf.dozer.functional_tests;

import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.MapperIF;
import net.sf.dozer.util.mapping.vo.runtimesubclass.*;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Dmitry Buzdin
 */
public class RuntimeSubclassMappingTest extends AbstractMapperTest {

  protected void setUp() throws Exception {
    mapper = getMapper("runtimeSubclass.xml");
  }

  public void testSpecialMapping() throws Exception {
    // SpecialUserGroup(!)
    SpecialUserGroup userGroup = new SpecialUserGroup();
    userGroup.setName("special user group");

    // User in SpecialUserGroup
    User user = new User();
    user.setUserGroup(userGroup);

    // do mapping to UserPrime
    UserPrime userPrime = (UserPrime) mapper.map(user, UserPrime.class);

    // check class type of mapped group, should be SpecialUserGroupPrime!
    assertNotNull(userPrime.getUserGroup());
    assertEquals("special user group", userPrime.getUserGroup().getName());
    assertEquals(SpecialUserGroupPrime.class, userPrime.getUserGroup().getClass());
  }

  public void testNormalMapping() throws Exception {
    // normal UserGroup
    UserGroup userGroup = new UserGroup();
    userGroup.setName("user group");

    // User in normal UserGroup
    User user = new User();
    user.setUserGroup(userGroup);

    // do mapping to UserPrime
    UserPrime userPrime = (UserPrime) mapper.map(user, UserPrime.class);

    // check class type of mapped group, should NOT be SpecialUserGroupPrime!
    assertNotNull(userPrime.getUserGroup());
    assertFalse(userPrime.getUserGroup() instanceof SpecialUserGroupPrime);
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
