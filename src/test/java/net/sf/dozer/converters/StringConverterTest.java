package net.sf.dozer.converters;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import net.sf.dozer.util.DateFormatContainer;

public class StringConverterTest extends TestCase {

  public void testDateToString_NoDateFormatSpecified() {
    Date src = new Date();
    StringConverter converter = new StringConverter(new DateFormatContainer(null));
    String result = (String) converter.convert(String.class, src);
    assertEquals("incorrect value returned from converter", src.toString(), result);
  }

  public void testCalendarToString_NoDateFormatSpecified() {
    Calendar src = Calendar.getInstance();
    StringConverter converter = new StringConverter(new DateFormatContainer(null));
    String result = (String) converter.convert(String.class, src);
    assertEquals("incorrect value returned from converter", src.toString(), result);
  }

}
