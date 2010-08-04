package org.dozer.functional_tests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="dmitry.buzdin@ctco.lv">Dmitry Buzdin</a>
 */
public class IsAccessibleTest extends AbstractFunctionalTest {

  @Test
  public void shouldWorkWithNestedFields() {
    mapper = getMapper("nestedAccessible.xml");

    Node child = new Node(null);
    Node root = new Node(new Node(child));

    mapper.map("a", root);

    assertEquals("a", root.child.child.value);
  }

  @Test
  public void shouldPreinstantiateChainElements() {
    mapper = getMapper("nestedAccessible.xml");
    Node node = new Node(null);

    mapper.map("a", node);

    assertEquals("a", node.child.child.value);
  }

  public static class Node {
    Node child;
    String value;

    public Node() {
    }

    public Node(Node child) {
      this.child = child;
    }

  }

}
