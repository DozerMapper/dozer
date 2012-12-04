package org.dozer.vo.proto;

import java.util.List;

/**
 * @author Dmitry Spikhalskiy
 */
public class ObjectWithEnumCollection {
  private List<SimpleEnum> enums;

  public ObjectWithEnumCollection() {
  }

  public ObjectWithEnumCollection(List<SimpleEnum> enums) {
    this.enums = enums;
  }

  public List<SimpleEnum> getEnums() {
    return enums;
  }

  public void setEnums(List<SimpleEnum> enums) {
    this.enums = enums;
  }
}
