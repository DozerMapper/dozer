package net.sf.dozer.functional_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.inheritance.twolevel.A;
import net.sf.dozer.vo.inheritance.twolevel.B;
import net.sf.dozer.vo.inheritance.twolevel.C;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class InheritanceTwoLevelTest extends AbstractMapperTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("inheritanceTwoLevel.xml");
  }

  @Test
  public void testMapping_TwoLevels() {
    C source = new C();
    source.setA("A");
    source.setB("B");

    B destination = mapper.map(source, B.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
    assertNotNull("B", destination.getB());
  }

  @Test
  public void testMapping_TwoLevelsReverse() {
    B source = new B();
    source.setA("A");
    source.setB("B");

    C destination = mapper.map(source, C.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
    assertNotNull("B", destination.getB());
  }

  @Test
  public void testMapping_OneLevel() {
    C source = new C();
    source.setA("A");
    source.setB("B");

    A destination = mapper.map(source, A.class);
    assertNotNull(destination);
    assertEquals("A", destination.getA());
  }

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
