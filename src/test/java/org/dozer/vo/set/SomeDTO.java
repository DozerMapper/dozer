package org.dozer.vo.set;

import org.dozer.vo.BaseTestObject;

public class SomeDTO extends BaseTestObject {

  private Integer field1;

  private SomeOtherDTO[] field2;

  public Integer getField1() {
    return field1;
  }

  public void setField1(Integer field1) {
    this.field1 = field1;
  }

  public SomeOtherDTO[] getField2() {
    return field2;
  }

  public void setField2(SomeOtherDTO[] field2) {
    this.field2 = field2;
  }
}