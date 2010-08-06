/**
 *
 */
package org.dozer.functional_tests;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.vo.generics.parameterized.*;
import org.junit.Assert;
import org.junit.Test;

public class GenericsTest extends AbstractFunctionalTest {

  /**
   * test mapping of a generic field
   */
  @Test
  public void testSimpleGenerics() {

    Mapper mapper = new DozerBeanMapper();

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
    Mapper mapper = new DozerBeanMapper();

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
    Mapper mapper = new DozerBeanMapper();

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
    Mapper mapper = new DozerBeanMapper();

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
