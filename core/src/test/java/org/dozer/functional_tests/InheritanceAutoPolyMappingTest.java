package org.dozer.functional_tests;

import org.dozer.Mapper;
import org.dozer.vo.inheritance.autopoly.AContainer;
import org.dozer.vo.inheritance.autopoly.C;
import org.dozer.vo.inheritance.autopoly.XContainer;
import org.dozer.vo.inheritance.autopoly.Z;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Test suite for automatic polymorphic resolving and conversion
 * @author Dmitry Spikhalskiy
 */
public class InheritanceAutoPolyMappingTest extends AbstractFunctionalTest {
  private Mapper mapper;

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper(new String[] {"autoPolyInheritanceMapping.xml"});
  }

  @Test
  public void testCustomMappingForAbstractClasses() throws Exception {
    AContainer aContainer = new AContainer();
    aContainer.setField(new C());
    XContainer result = mapper.map(aContainer, XContainer.class);

    assertTrue(result.getField() instanceof Z);
  }
}
