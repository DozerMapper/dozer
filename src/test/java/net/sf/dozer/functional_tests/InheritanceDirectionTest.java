package net.sf.dozer.functional_tests;

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.direction.ContentItemGroup;
import net.sf.dozer.vo.direction.ContentItemGroupDTO;
import net.sf.dozer.vo.direction.ContentItemGroupDefault;

import java.util.HashSet;

/**
 * @author dmitry.buzdin
 */
public class InheritanceDirectionTest extends AbstractMapperTest {

  protected void setUp() throws Exception {
    mapper = getMapper("inheritanceDirection.xml");
  }

  public void testInheritanceDirection_Child() {
    ContentItemGroupDefault parentItem = new ContentItemGroupDefault();
    parentItem.setId("A");

    ContentItemGroupDefault childItem = new ContentItemGroupDefault();
    childItem.setId("B");
    childItem.setParentGroup(parentItem);

    parentItem.addChildGroup(childItem);

    ContentItemGroupDTO resultChild = mapper.map(childItem, ContentItemGroupDTO.class);
    
    assertNotNull(resultChild);
    assertEquals("B", resultChild.getId());
    assertNull(resultChild.getChildGroups());

    ContentItemGroupDTO parentResult = resultChild.getParentGroup();
    assertEquals("A", parentResult.getId());
    assertTrue(parentResult.getChildGroups().contains(resultChild));
  }

  public void testInheritanceDirection_Parent() {
    ContentItemGroupDefault parentItem = new ContentItemGroupDefault();
    parentItem.setId("A");

    ContentItemGroupDefault childItem = new ContentItemGroupDefault();
    childItem.setId("B");
    childItem.setParentGroup(parentItem);

    parentItem.addChildGroup(childItem);

    ContentItemGroupDTO resultParent = mapper.map(parentItem, ContentItemGroupDTO.class);

    ContentItemGroupDTO resultChild = (ContentItemGroupDTO) resultParent.getChildGroups().iterator().next();

    assertNotNull(resultChild);
    assertEquals("B", resultChild.getId());
    assertNull(resultChild.getChildGroups());

    assertEquals("A", resultParent.getId());
    assertTrue(resultParent.getChildGroups().contains(resultChild));
  }

  public void testInheritanceDirection_Reverse() {
    ContentItemGroupDTO parent = new ContentItemGroupDTO();
    parent.setId("A");

    ContentItemGroupDTO child = new ContentItemGroupDTO();
    child.setId("B");
    child.setParentGroup(parent);
    HashSet childGroups = new HashSet();
    childGroups.add(child);
    parent.setChildGroups(childGroups);

    ContentItemGroup result = mapper.map(parent, ContentItemGroupDefault.class);
    assertNotNull(result);
    ContentItemGroup childResult = (ContentItemGroup) result.getChildGroups().iterator().next();
    assertEquals(result, childResult.getParentGroup());
    assertEquals("A", result.getId());
    assertEquals("B", childResult.getId());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
