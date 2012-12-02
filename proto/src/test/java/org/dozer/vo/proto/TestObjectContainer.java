package org.dozer.vo.proto;

import org.dozer.vo.proto.TestObject;

/**
 * @author Dmitry Spikhalskiy
 */
public class TestObjectContainer {
  private TestObject nested;
  private String one;

  public TestObjectContainer() {
  }

  public TestObjectContainer(TestObject nested, String one) {
    this.nested = nested;
    this.one = one;
  }

  public String getOne() {
    return one;
  }

  public void setOne(String one) {
    this.one = one;
  }

  public TestObject getNested() {
    return nested;
  }

  public void setNested(TestObject nested) {
    this.nested = nested;
  }
}
