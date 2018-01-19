/*
 * Copyright 2005-2017 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.functional_tests;

import java.util.HashSet;

import org.dozer.vo.direction.ContentItemGroup;
import org.dozer.vo.direction.ContentItemGroupDTO;
import org.dozer.vo.direction.ContentItemGroupDefault;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dmitry.buzdin
 */
public class InheritanceDirectionTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("mappings/inheritanceDirection.xml");
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
