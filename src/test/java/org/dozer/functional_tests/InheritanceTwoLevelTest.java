package org.dozer.functional_tests;

import org.dozer.vo.inheritance.twolevel.A;
import org.dozer.vo.inheritance.twolevel.B;
import org.dozer.vo.inheritance.twolevel.C;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class InheritanceTwoLevelTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("inheritanceTwoLevel.xml");
  }

  @Test
  public void testMapping_TwoLevels() {
    C source = newInstance(C.class);
    source.setA("A");
    source.setB("B");

    B destination = mapper.map(source, B.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
    assertNotNull("B", destination.getB());
  }

  @Test
  public void testMapping_TwoLevelsReverse() {
    B source = newInstance(B.class);
    source.setA("A");
    source.setB("B");

    C destination = mapper.map(source, C.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
    assertNotNull("B", destination.getB());
  }

  @Test
  public void testMapping_OneLevel() {
    C source = newInstance(C.class);
    source.setA("A");
    source.setB("B");

    A destination = mapper.map(source, A.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
  }

}
