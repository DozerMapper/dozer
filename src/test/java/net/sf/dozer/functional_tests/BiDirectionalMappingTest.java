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

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.DozerBeanMapperSingletonWrapper;
import net.sf.dozer.Mapper;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.C;
import net.sf.dozer.vo.LoopObjectChild;
import net.sf.dozer.vo.LoopObjectParent;
import net.sf.dozer.vo.LoopObjectParentPrime;
import net.sf.dozer.vo.bidirectional.A;
import net.sf.dozer.vo.bidirectional.B;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class BiDirectionalMappingTest extends AbstractMapperTest {

  public void testSimpleBidirectional() {
    // A contains B and B contains A.
    A src = (A) newInstance(A.class);
    B field1 = (B) newInstance(B.class);
    field1.setField1(src);
    src.setField1(field1);

    B dest = mapper.map(src, B.class);
    assertNotNull("field1 should have been mapped", dest.getField1());
  }

  public void testBidirectionalWithCustomMapping() throws Exception {
    Mapper mapper = getMapper(new String[] { "infiniteLoopMapping.xml" });
    LoopObjectParent loopObjectParent = (LoopObjectParent) newInstance(LoopObjectParent.class);
    LoopObjectChild loopObjectChild = (LoopObjectChild) newInstance(LoopObjectChild.class);
    loopObjectChild.setParent(loopObjectParent);
    loopObjectParent.setChild(loopObjectChild);

    LoopObjectParentPrime loopObjectParentPrime = mapper.map(loopObjectParent, LoopObjectParentPrime.class);
    assertNotNull(loopObjectParentPrime);
    assertNotNull(loopObjectParentPrime.getChildPrime());
  }

  /*
   * This test checks the fix of bug #1715496 - Lost and Duplicated Objects
   * 
   * @author korittky.joachim
   */
  public void testManyObjects() {
    net.sf.dozer.vo.B a = (net.sf.dozer.vo.B) newInstance(net.sf.dozer.vo.B.class);
    net.sf.dozer.vo.B b = new net.sf.dozer.vo.B();
    a.setCs(new net.sf.dozer.vo.C[20000]);
    // Fill the list with C objects numbered from 0 to SIZE-1
    for (int i = 0; i < a.getCs().length; i++) {
      net.sf.dozer.vo.C c = (C) newInstance(net.sf.dozer.vo.C.class);
      c.setValue(Integer.toString(i));
      a.getCs()[i] = c;
    }

    Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
    mapper.map(a, b);

    // Check if C object nr i holds value i after the mapping
    for (int i = 0; i < b.getCs().length; i++) {
      assertEquals("Wrong value ", b.getCs()[i].getValue(), Integer.toString(i));
    }
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}