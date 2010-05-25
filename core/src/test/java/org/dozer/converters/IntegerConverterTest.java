package org.dozer.converters;

import org.dozer.AbstractDozerTest;
import org.dozer.converters.IntegerConverter;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry.Buzdin
 */
public class IntegerConverterTest extends AbstractDozerTest{

  private IntegerConverter converter;

  @Before
  public void setUp() throws Exception {
    converter = new IntegerConverter();
  }

  @Test
  public void testConvert() {
    assertEquals(new Integer(1), converter.convert(Integer.class, Boolean.TRUE));
    assertEquals(new Integer(0), converter.convert(Integer.class, Boolean.FALSE));
    assertEquals(new Integer(100), converter.convert(Integer.class, "100"));
  }

}
