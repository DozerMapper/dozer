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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitry Buzdin
 */
public class IsAccessibleTest extends AbstractFunctionalTest {

  @Before
  public void setUp() {
    mapper = getMapper("mappings/nestedAccessible.xml");
  }

  @Test
  public void shouldWorkWithNestedFields() {
    Node child = new Node(null);
    Node root = new Node(new Node(child));

    mapper.map("a", root);

    assertEquals("a", root.child.child.value);
  }

  @Test
  public void shouldPreinstantiateChainElements() {
    Node node = new Node(null);

    mapper.map("a", node);

    assertEquals("a", node.child.child.value);
  }

  @Test
  public void shouldApplyIsAccessibleOnClass() {
    Node node = new Node(null);

    mapper.map("true", node, "class-level");

    assertEquals("true", node.value);
    assertEquals("true", node.publicValue);
    assertTrue(node.setterInvoked);
  }

  @Test
  public void shouldApplyIsAccessibleOnClass_Backwards() {
    Node node = new Node(null);
    node.publicValue = "true";

    Node result = mapper.map(node, Node.class, "third");

    assertEquals("true", result.value);
    assertTrue(node.getterInvoked);
  }

  public static class Node {
    Node child;
    String value;
    String publicValue;
    boolean setterInvoked;
    boolean getterInvoked;

    public Node() {
    }

    public Node(Node child) {
      this.child = child;
    }

    public String getPublicValue() {
      this.getterInvoked = true;
      return publicValue;
    }

    public void setPublicValue(String publicValue) {
      this.setterInvoked = true;
      this.publicValue = publicValue;
    }

    public boolean isSetterInvoked() {
      return setterInvoked;
    }

    public boolean isGetterInvoked() {
      return getterInvoked;
    }
  }

}
