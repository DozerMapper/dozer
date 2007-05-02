package net.sf.dozer.util.mapping.converters;

import java.util.Calendar;
import java.util.Date;

import net.sf.dozer.util.mapping.util.DateFormatContainer;
import junit.framework.TestCase;

public class StringConverterTest extends TestCase {

  public void testDateToString_NoDateFormatSpecified() {
    Date src = new Date();
    StringConverter converter = new StringConverter(new DateFormatContainer(null));
    String result = (String) converter.convert(String.class,src);
    assertEquals("incorrect value returned from converter",  src.toString(), result);
  }
  
  public void testCalendarToString_NoDateFormatSpecified() {
    Calendar src = Calendar.getInstance();
    StringConverter converter = new StringConverter(new DateFormatContainer(null));
    String result = (String) converter.convert(String.class,src);
    assertEquals("incorrect value returned from converter",  src.toString(), result);
  }

}
