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

  @Test(expected=MappingException.class)
  public void testWrongClassName() {
    mapper = getMapper("invalidmapping1.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=MappingException.class)
  public void testNoFieldB() {
    mapper = getMapper("invalidmapping2.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=NullPointerException.class)
  public void testEmptyFieldB() {
    mapper = getMapper("invalidmapping3.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=MappingException.class)
  public void testFieldDoesNotExist() {
    mapper = getMapper("invalidmapping4.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=MappingException.class)
  public void testNoClassA() {
    mapper = getMapper("invalidmapping5.xml");
    mapper.map("1", Integer.class);
    fail();
  }

}
