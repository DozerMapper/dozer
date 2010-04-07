package org.dozer.functional_tests;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.dozer.functional_tests.proxied.ProxyDataObjectInstantiator;
import org.dozer.config.GlobalSettings;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dmitry.buzdin
 */
public class VariablesTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("variables.xml");
  }

  @Test
  public void testTest() {
    assertTrue(GlobalSettings.getInstance().isElEnabled());

    Container<Child> source = new Container<Child>();
    Container<ChildClone> destination = new Container<ChildClone>();

    Parent parent = newInstance(Parent.class);
    parent.setId("PARENT");

    HashSet<Child> children = new HashSet<Child>();

    Child child = newInstance(Child.class);
    child.setId("CHILD");
    child.setParent(parent);

    children.add(child);
    parent.setChildren(children);

    source.add(child);

    mapper.map(source, destination, "from");

    assertEquals(1, destination.size());
    ChildClone childClone = destination.get(0);
    assertEquals("CHILD", childClone.getId());
    assertNotNull(childClone.getParent());

    ParentClone parentClone = childClone.getParent();
    assertEquals("PARENT", parentClone.getId());
  }

  public static class Container<T> extends ArrayList<T> {
  }

  public static class Parent {

    String id;
    Set<Child> children;

    public Set<Child> getChildren() {
      throw new IllegalStateException("Not Allowed!");
    }

    public void setChildren(Set<Child> children) {
      this.children = children;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  public static class ParentClone {
    String id;
    Set<ChildClone> children;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Set<ChildClone> getChildren() {
      throw new IllegalStateException();
    }

    public void setChildren(Set<ChildClone> children) {
      this.children = children;
    }
  }

  public static class ChildClone {

    String id;
    ParentClone parent;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public ParentClone getParent() {
      return parent;
    }

    public void setParent(ParentClone parent) {
      this.parent = parent;
    }
  }

  public static class Child {
    String id;
    Parent parent;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Parent getParent() {
      return parent;
    }

    public void setParent(Parent parent) {
      this.parent = parent;
    }
  }

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return ProxyDataObjectInstantiator.INSTANCE;
  }

}
