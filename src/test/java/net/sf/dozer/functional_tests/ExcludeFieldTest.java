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

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.excluded.*;

/**
 * @author Dmitry Buzdin
 */
public class ExcludeFieldTest extends AbstractMapperTest {

  protected void setUp() throws Exception {
    mapper = getMapper("excludedField.xml");
  }

  public void testExcludedField_SimpleLevel() {
    ZeroA zeroA;
    ZeroB zeroB = new ZeroB();

    zeroB.setId(Integer.valueOf("10"));
    zeroA = (ZeroA) mapper.map(zeroB, ZeroA.class);

    assertNull(zeroA.getId());
    assertEquals(Integer.valueOf("10"), zeroB.getId());
  }

  public void testExcludedField_SimpleReverse() {
    ZeroA zeroA = new ZeroA();
    ZeroB zeroB;

    zeroA.setId(Integer.valueOf("5"));
    System.out.println(zeroA);
    zeroB = (ZeroB) mapper.map(zeroA, ZeroB.class);

    assertEquals(Integer.valueOf("5"), zeroA.getId());
    assertNull(zeroB.getId());
  }
  
  public void testExcludedField_OneLevel() {
    OneA oneA;
    OneB oneB = new OneB();

    oneB.setId(Integer.valueOf("10"));
    oneA = (OneA) mapper.map(oneB, OneA.class);
    
    assertNull(oneA.getId());
    assertEquals(Integer.valueOf("10"), oneB.getId());
  }

  public void testExcludedField_OneLevelReverse() {
    OneA oneA = new OneA();
    OneB oneB;

    oneA.setId(Integer.valueOf("5"));
    oneB = (OneB) mapper.map(oneA, OneB.class);

    assertEquals(Integer.valueOf("5"), oneA.getId());
    assertNull(oneB.getId());
  }

  public void testExcludedField_TwoLevel() {
    TwoB twoB = new TwoB();
    TwoA twoA;

    twoB.setId(Integer.valueOf("10"));
    twoA = (TwoA) mapper.map(twoB, TwoA.class);
    assertNull(twoA.getId());
    assertEquals(Integer.valueOf("10"), twoB.getId());
  }

  public void testExcludedField_TwoLevelReverse() {
    TwoB twoB;
    TwoA twoA = new TwoA();

    twoA.setId(Integer.valueOf("5"));
    twoB = (TwoB) mapper.map(twoA, TwoB.class);
    assertEquals(Integer.valueOf("5"), twoA.getId());
    assertNull(twoB.getId());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
