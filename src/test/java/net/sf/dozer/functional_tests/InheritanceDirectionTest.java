package net.sf.dozer.functional_tests;

import net.sf.dozer.util.mapping.vo.direction.ContentItemGroupDefault;
import net.sf.dozer.util.mapping.vo.direction.ContentItemGroupDTO;

import java.util.HashSet;

/**
 * @author dmitry.buzdin
 */
public class InheritanceDirectionTest extends AbstractMapperTest {

  protected void setUp() throws Exception {
    mapper = getMapper("inheritanceDirection.xml");
  }

  public void testInheritanceDirection() {
    ContentItemGroupDefault parentItem = new ContentItemGroupDefault();
    parentItem.id = "A";

    ContentItemGroupDefault childItem = new ContentItemGroupDefault();
    childItem.setParentGroup(parentItem);
    childItem.id = "B";

    parentItem.addChildGroup(childItem);

    ContentItemGroupDTO result = (ContentItemGroupDTO) mapper.map(parentItem, ContentItemGroupDTO.class);
    
    assertNotNull(result);
    assertEquals("A", result.getId());
    assertEquals(1, result.getChildGroups().size());

    ContentItemGroupDTO childDTO = (ContentItemGroupDTO) result.getChildGroups().iterator().next();
    assertTrue(result.getChildGroups().contains(childDTO));

    assertEquals("B", childDTO.getId());
    assertEquals(result, childDTO.getParentGroup());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
