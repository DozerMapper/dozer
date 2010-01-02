package org.dozer.functional_tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.dozer.MappingException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author dmitry.buzdin
 */
public class InvalidMappingTest extends AbstractFunctionalTest {

  @Test
  public void testWrongClassName() {
    mapper = getMapper("invalidmapping1.xml");
    try {
      mapper.map("1", Integer.class);
      fail();
    } catch (MappingException e) {
      assertEquals(ClassNotFoundException.class, e.getCause().getClass());
    }
  }

  @Test
  public void testNoFieldB() {
    mapper = getMapper("invalidmapping2.xml");
    try {
      mapper.map("1", Integer.class);
      fail();
    } catch (MappingException e) {
      assertEquals(SAXException.class, e.getCause().getClass());
    }
  }

  @Test
  public void testEmptyFieldB() {
    mapper = getMapper("invalidmapping3.xml");
    try {
      mapper.map("1", Integer.class);
      fail();
    } catch (NullPointerException e) {
      //assertEquals(SAXException.class, e.getCause().getClass()); // TODO Fix NPE
    }
  }

//  @Test
//  public void testFieldDoesNotExist() {
//    mapper = getMapper("invalidmapping4.xml");
//    try {
//      mapper.map("1", Integer.class);
//      fail();
//    } catch (MappingException e) {
//    }
//  }

  @Test
  public void testNoClassA() {
    mapper = getMapper("invalidmapping5.xml");
    try {
      mapper.map("1", Integer.class);
      fail();
    } catch (MappingException e) {
      assertEquals(SAXException.class, e.getCause().getClass());
    }
  }

}
