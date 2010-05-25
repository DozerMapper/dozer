package org.dozer.classmap;

import org.dozer.AbstractDozerTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author Dmitry Buzdin
 */
public class CopyByReferenceContainerTest extends AbstractDozerTest{

  private CopyByReferenceContainer container;

  @Before
  public void setUp() throws Exception {
    container = new CopyByReferenceContainer();
  }

  @Test
  public void testContains() throws Exception {
    container.add(new CopyByReference("java.util.*"));
    
    assertFalse(container.contains(String.class.getName()));
    assertTrue(container.contains(List.class.getName()));
  }

}
