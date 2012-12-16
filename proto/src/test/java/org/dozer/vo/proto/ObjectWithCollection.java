package org.dozer.vo.proto;

import org.dozer.vo.proto.TestObject;

import java.util.List;

/**
 * @author Dmitry Spikhalskiy
 */
public class ObjectWithCollection {
  private List<TestObject> objects;

  public List<TestObject> getObjects() {
    return objects;
  }

  public void setObjects(List<TestObject> objects) {
    this.objects = objects;
  }
}
