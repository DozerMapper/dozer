package org.dozer.functional_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.dozer.vo.inheritance.A;
import org.dozer.vo.mapid.AContainer;
import org.dozer.vo.mapid.BContainer;
import org.junit.Test;

public class MapIdTest extends AbstractFunctionalTest {

  @Test
  public void testMapIdWithSubClasses() {
    mapper = getMapper(new String[] { "inheritanceMapping4.xml" });

    AContainer src = new AContainer();
    src.setAProperty(getA());

    BContainer dest = (BContainer) mapper.map(src, BContainer.class);

    assertNotNull("B property of dest should be instantiated if mapping by map-id works", dest
        .getBProperty());

    // Remap to each other to test bi-directional mapping
    AContainer mappedSrc = (AContainer) mapper.map(dest, AContainer.class);
    BContainer mappedDest = (BContainer) mapper.map(mappedSrc, BContainer.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  private A getA() {
    A result = newInstance(A.class);
    result.setField1("field1value");
    result.setFieldA("fieldAValue");
    result.setSuperAField("superAFieldValue");
    result.setSuperField1("superField1Value");
    return result;

  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
