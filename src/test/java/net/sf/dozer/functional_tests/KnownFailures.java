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
package net.sf.dozer.functional_tests;

import java.util.ArrayList;
import java.util.List;

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.MessageHeaderDTO;
import net.sf.dozer.vo.MessageHeaderVO;
import net.sf.dozer.vo.MessageIdVO;
import net.sf.dozer.vo.inheritance.Inner;
import net.sf.dozer.vo.inheritance.Outer;
import net.sf.dozer.vo.inheritance.Target;
import net.sf.dozer.vo.inheritance.cc.C;
import net.sf.dozer.vo.inheritance.cc.Z;

/**
 * This is a holding grounds for test cases that reproduce known bugs, features, or gaps discovered during development.
 * As the use cases are resolved, these tests should be moved to the live unit test classes.
 * 
 * @author tierney.matt
 */
public class KnownFailures extends AbstractMapperTest {

  protected void setUp() throws Exception {
    super.setUp();
    mapper = getMapper(new String[] { "knownFailures.xml" });
  }

  /*
   * Feature Request #1731158. Need a way to explicitly specify a mapping between a custom data object and String. Not
   * sure the best way to do this. Copy by reference doesnt seem like a good fit.
   */
  public void testListOfCustomObjectsToStringArray() {
    MessageHeaderVO vo = new MessageHeaderVO();
    List ids = new ArrayList();
    ids.add(new MessageIdVO("1"));
    ids.add(new MessageIdVO("2"));
    vo.setMsgIds(ids);
    MessageHeaderDTO result = mapper.map(vo, MessageHeaderDTO.class);
    assertEquals("1", result.getIdList().getMsgIdsArray()[0]);
    assertEquals("2", result.getIdList().getMsgIdsArray()[1]);
  }

  public void testObjectField() throws Exception {
    Outer o = new Outer();
    Target t = mapper.map(o, Target.class);

    assertEquals(((Inner) o.getInner()).getString(), t.getString());
  }

  /*
   * Bug #1953410
   */
  public void testInheritanceBug() {
    Z z = new Z();
    z.setTest("testString");

    mapper = getMapper(new String[] { "inheritanceBug.xml" });

    C c = mapper.map(z, C.class);
    assertEquals("wrong value", "customConverter", c.getTest());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }
}