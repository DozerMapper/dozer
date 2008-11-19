package net.sf.dozer.util.mapping;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dmitry.buzdin@ctco.lv">Dmitry Buzdin</a>
 */
public class MappingProcessorTest extends TestCase {

  private ArrayList sourceList;
  private ArrayList destinationList;

  protected void setUp() throws Exception {
    sourceList = new ArrayList();
    destinationList = new ArrayList();
  }

  public void testPrepareDetinationList_OK() {
    List result = MappingProcessor.prepareDestinationList(sourceList, destinationList);
    assertEquals(destinationList, result);
    
    destinationList.add("");
    result = MappingProcessor.prepareDestinationList(sourceList, destinationList);
    assertEquals(destinationList, result);
  }

  public void testPrepareDetinationList_Null() {
    List result = MappingProcessor.prepareDestinationList(sourceList, null);
    assertNotNull(result);
    assertEquals(new ArrayList(), result);
  }

  public void testPrepareDetinationList_Array() {
    List result = MappingProcessor.prepareDestinationList(sourceList, new Object [] {"A"});
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("A", result.iterator().next());
  }

  public void testPrepareDetinationList_StrangeCase() {
    List result = MappingProcessor.prepareDestinationList(sourceList, "Hullo");
    assertNotNull(result);
    assertEquals(new ArrayList(), result);
  }

  public void testRemoveOrphans_OK() {
    destinationList.add("A");
    MappingProcessor.removeOrphans(sourceList, destinationList);
    assertTrue(destinationList.isEmpty());
  }

  public void testRemoveOrphans_Many() {
    destinationList.add("A");
    destinationList.add("B");
    destinationList.add("C");

    sourceList.add("B");
    sourceList.add("D");

    MappingProcessor.removeOrphans(sourceList, destinationList);
    assertEquals(2, destinationList.size());
    assertEquals("B", destinationList.get(0));
    assertEquals("D", destinationList.get(1));
  }

  public void testRemoveOrphans_Ordering() {
    destinationList.add(new Ordered(1));
    destinationList.add(new Ordered(2));
    destinationList.add(new Ordered(3));

    sourceList.add(new Ordered(0));
    sourceList.add(new Ordered(3));
    sourceList.add(new Ordered(2));
    sourceList.add(new Ordered(1));

    MappingProcessor.removeOrphans(sourceList, destinationList);
    assertEquals(4, destinationList.size());
    assertEquals(new Ordered(1), destinationList.get(0));
    assertEquals(new Ordered(2), destinationList.get(1));
    assertEquals(new Ordered(3), destinationList.get(2));
    assertEquals(new Ordered(0), destinationList.get(3));
  }

  private static class Ordered {
    private int id;

    private Ordered(int id) {
      this.id = id;
    }

    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Ordered ordered = (Ordered) o;
      if (id != ordered.id) return false;
      return true;
    }

    public int hashCode() {
      return id;
    }
  }

}
