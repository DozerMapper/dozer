package org.dozer.functional_tests;

import org.dozer.vo.inheritance.A;
import org.dozer.vo.mapid.AContainer;
import org.dozer.vo.mapid.AListContainer;
import org.dozer.vo.mapid.BContainer;
import org.dozer.vo.mapid.BContainer2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

  @Test
  public void testMapIdWithHint() {
    mapper = getMapper(new String[] { "mapIdWithHint.xml" });

    AListContainer aListContainer = new AListContainer();
    aListContainer.getAList().add(getA());

    BContainer2 bContainer = new BContainer2();
    mapper.map(aListContainer, bContainer);

    assertNotNull(bContainer);
    assertNotNull(bContainer.getBField());
    assertEquals(((A) (aListContainer.getAList().get(0))).getSuperAField(), bContainer.getBField()
        .getSuperBField());

    AListContainer newAListContainer = (AListContainer) mapper
        .map(bContainer, AListContainer.class);
    assertEquals("failed reverse map", aListContainer, newAListContainer);
  }

  private A getA() {
    A result = newInstance(A.class);
    result.setField1("field1value");
    result.setFieldA("fieldAValue");
    result.setSuperAField("superAFieldValue");
    result.setSuperField1("superField1Value");
    return result;

  }

}
