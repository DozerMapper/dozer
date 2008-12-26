package net.sf.dozer.converters;

import net.sf.dozer.converters.IntegerConverter;
import junit.framework.TestCase;

/**
 * @author Dmitry.Buzdin
 */
public class IntegerConverterTest extends TestCase {

  private IntegerConverter converter;

  protected void setUp() throws Exception {
    converter = new IntegerConverter();
  }

  public void testConvert() {
    assertEquals(new Integer(1), converter.convert(Integer.class, Boolean.TRUE));
    assertEquals(new Integer(0), converter.convert(Integer.class, Boolean.FALSE));
    assertEquals(new Integer(100), converter.convert(Integer.class, "100"));
  }

}
