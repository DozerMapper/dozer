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

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.vo.generics.parameterized.A;
import org.dozer.vo.generics.parameterized.AA;
import org.dozer.vo.generics.parameterized.B;
import org.dozer.vo.generics.parameterized.C;
import org.dozer.vo.generics.parameterized.GenericTestType;
import org.junit.Assert;
import org.junit.Test;

public class GenericsTest extends AbstractFunctionalTest {

  /**
   * test mapping of a generic field
   */
  @Test
  public void testSimpleGenerics() {

    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    A a = new A();
    a.setId(15L);

    B b = mapper.map(a, B.class);
    Assert.assertEquals((Integer) a.getId().intValue(), b.getId());

    A a2 = mapper.map(a, A.class);
    Assert.assertEquals(a.getId(), a2.getId());

  }

  /**
   * test mapping of a generic field with inheritance
   */
  @Test
  public void testSimpleGenericsInheritance() {
    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    B b = new B();
    b.setId(15);

    AA aa = mapper.map(b, AA.class);
    Assert.assertEquals((Long) b.getId().longValue(), aa.getId());

    AA aa2 = mapper.map(b, AA.class);
    Assert.assertEquals((Long) b.getId().longValue(), aa2.getId());

  }

  /**
   * Test mapping for deepn generic inheritance
   * @throws Exception
   */
  @Test
  public void testDeepGenericInheritanceTest() throws Exception {
    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    B b = new B();
    b.setId(12345);
    C c1 = mapper.map(b, C.class);
    Assert.assertEquals(c1.getId().longValue(), 12345L);
  }

  /**
   * test mapping of an object with three type parameters
   */
  @Test
  public void testGenericsWithSeveralTypes() {
    Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    GenericTestType testType = new GenericTestType();
    testType.setA(15L);
    testType.setB("test");
    testType.setC(7);

    GenericTestType testType2 = mapper.map(testType, GenericTestType.class);
    Assert.assertEquals(testType.getA(), testType2.getA());
    Assert.assertEquals(testType.getB(), testType2.getB());
    Assert.assertEquals(testType.getC(), testType2.getC());
  }

}
