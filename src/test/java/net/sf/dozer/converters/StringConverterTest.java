package net.sf.dozer.converters;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import net.sf.dozer.util.DateFormatContainer;
public class StringConverterTest  {

  @Test
  public void testDateToString_NoDateFormatSpecified() {
    Date src = new Date();
    StringConverter converter = new StringConverter(new DateFormatContainer(null));
    String result = (String) converter.convert(String.class, src);
    assertEquals("incorrect value returned from converter", src.toString(), result);
  }

  @Test
  public void testCalendarToString_NoDateFormatSpecified() {
    Calendar src = Calendar.getInstance();
    StringConverter converter = new StringConverter(new DateFormatContainer(null));
    String result = (String) converter.convert(String.class, src);
    assertEquals("incorrect value returned from converter", src.toString(), result);
  }

}
