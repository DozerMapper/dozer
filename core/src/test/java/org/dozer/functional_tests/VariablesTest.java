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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.dozer.functional_tests.runner.ProxyDataObjectInstantiator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author dmitry.buzdin
 */
public class VariablesTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapperWithEL("mappings/variables.xml");
  }

  @Test
  public void testTest() {
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
