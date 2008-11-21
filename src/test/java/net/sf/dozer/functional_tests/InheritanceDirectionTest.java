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
    parentItem.setId("A");

    ContentItemGroupDefault childItem = new ContentItemGroupDefault();
    childItem.setParentGroup(parentItem);
    childItem.setId("B");

    parentItem.addChildGroup(childItem);

    ContentItemGroupDTO resultChild = (ContentItemGroupDTO) mapper.map(childItem, ContentItemGroupDTO.class);
    
    assertNotNull(resultChild);
    //assertEquals("B", resultChild.getId());
    assertNull(resultChild.getChildGroups());

    ContentItemGroupDTO parentResult = resultChild.getParentGroup();
    //assertEquals("A", parentResult.getId());
    assertTrue(parentResult.getChildGroups().contains(resultChild));
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
