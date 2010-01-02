package org.dozer.functional_tests;

import org.dozer.vo.direction.ContentItemGroup;
import org.dozer.vo.direction.ContentItemGroupDTO;
import org.dozer.vo.direction.ContentItemGroupDefault;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

/**
 * @author dmitry.buzdin
 */
public class InheritanceDirectionTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("inheritanceDirection.xml");
  }

  @Test
  public void testInheritanceDirection_Child() {
    ContentItemGroupDefault parentItem = newInstance(ContentItemGroupDefault.class);
    parentItem.setId("A");

    ContentItemGroupDefault childItem = newInstance(ContentItemGroupDefault.class);
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

  @Test
  public void testInheritanceDirection_Parent() {
    ContentItemGroupDefault parentItem = newInstance(ContentItemGroupDefault.class);
    parentItem.setId("A");

    ContentItemGroupDefault childItem = newInstance(ContentItemGroupDefault.class);
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

  @Test
  public void testInheritanceDirection_Reverse() {
    ContentItemGroupDTO parent = newInstance(ContentItemGroupDTO.class);
    parent.setId("A");

    ContentItemGroupDTO child = newInstance(ContentItemGroupDTO.class);
    child.setId("B");
    child.setParentGroup(parent);
    HashSet<ContentItemGroupDTO> childGroups = newInstance(HashSet.class);
    childGroups.add(child);
    parent.setChildGroups(childGroups);

    ContentItemGroup result = mapper.map(parent, ContentItemGroupDefault.class);
    assertNotNull(result);
    ContentItemGroup childResult = (ContentItemGroup) result.getChildGroups().iterator().next();
    assertEquals(result, childResult.getParentGroup());
    assertEquals("A", result.getId());
    assertEquals("B", childResult.getId());
  }

}
