package org.dozer.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dozer.AbstractDozerTest;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class IteratorUtilsTest extends AbstractDozerTest {

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
