package org.dozer.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="dmitry.buzdin@ctco.lv">Dmitry Buzdin</a>
 */
public class IteratorUtilsTest extends TestCase {

  @Test
  public void testToList() {
    Set<String> set = new LinkedHashSet<String>();
    set.add("A");
    set.add("B");

    List<String> result = IteratorUtils.toList(set.iterator());
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("A", result.get(0));
  }

}
