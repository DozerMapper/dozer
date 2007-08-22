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

import net.sf.dozer.util.mapping.vo.Fruit;
import net.sf.dozer.util.mapping.vo.MessageHeaderDTO;
import net.sf.dozer.util.mapping.vo.MessageHeaderVO;
import net.sf.dozer.util.mapping.vo.MessageIdVO;
import net.sf.dozer.util.mapping.vo.inheritance.Inner;
import net.sf.dozer.util.mapping.vo.inheritance.Outer;
import net.sf.dozer.util.mapping.vo.inheritance.Target;
import net.sf.dozer.util.mapping.vo.perf.MyClassA;
import net.sf.dozer.util.mapping.vo.perf.MyClassB;

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
    MessageHeaderDTO result = null;

    result = (MessageHeaderDTO) mapper.map(vo, MessageHeaderDTO.class);
    assertEquals("1", result.getIdList().getMsgIdsArray()[0]);
    assertEquals("2", result.getIdList().getMsgIdsArray()[1]);
  }

  public void testIt() throws Exception {

    Outer o = new Outer();
    Target t = (Target) mapper.map(o, Target.class);

    assertEquals(((Inner) o.getInner()).getString(), t.getString());
  }

  public void testRemoveOrphans() {
    mapper = getMapper(new String[] { "removeOrphansMapping.xml" });

    MyClassA myClassA = new MyClassA();
    MyClassB myClassB = new MyClassB();

    Fruit apple = new Fruit();
    apple.setName("Apple");
    Fruit banana = new Fruit();
    banana.setName("Banana");
    Fruit cumcwat = new Fruit();
    cumcwat.setName("Cumcwat");
    Fruit orange = new Fruit();
    orange.setName("Orange");
    Fruit kiwiFruit = new Fruit();
    kiwiFruit.setName("Kiwi Fruit");

    List srcFruits = new ArrayList();
    srcFruits.add(apple);
    srcFruits.add(banana);
    srcFruits.add(kiwiFruit);

    List destFruits = new ArrayList();
    destFruits.add(cumcwat); // not in src
    destFruits.add(banana); // shared with src fruits
    destFruits.add(orange); // not in src

    myClassA.setAStringList(srcFruits);
    myClassB.setAStringList(destFruits);

    mapper.map(myClassA, myClassB, "testRemoveOrphansOnList");

    assertEquals(3, myClassB.getAStringList().size());
    assertTrue(myClassB.getAStringList().contains(apple));
    assertTrue(myClassB.getAStringList().contains(banana));
    assertTrue(myClassB.getAStringList().contains(kiwiFruit));
    assertFalse(myClassB.getAStringList().contains(cumcwat));
    assertFalse(myClassB.getAStringList().contains(orange));

  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }
}