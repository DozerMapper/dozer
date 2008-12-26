package net.sf.dozer.functional_tests;

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.inheritance.twolevel.A;
import net.sf.dozer.vo.inheritance.twolevel.B;
import net.sf.dozer.vo.inheritance.twolevel.C;

/**
 * @author Dmitry Buzdin
 */
public class InheritanceTwoLevelTest extends AbstractMapperTest {

  protected void setUp() throws Exception {
    mapper = getMapper("inheritanceTwoLevel.xml");
  }

  public void testMapping_TwoLevels() {
    C source = new C();
    source.setA("A");
    source.setB("B");

    B destination = (B) mapper.map(source, B.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
    assertNotNull("B", destination.getB());
  }

  public void testMapping_TwoLevelsReverse() {
    B source = new B();
    source.setA("A");
    source.setB("B");

    C destination = (C) mapper.map(source, C.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
    assertNotNull("B", destination.getB());
  }

  public void testMapping_OneLevel() {
    C source = new C();
    source.setA("A");
    source.setB("B");

    A destination = (A) mapper.map(source, A.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
