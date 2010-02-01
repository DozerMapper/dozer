package org.dozer.vo.set;

import java.util.Date;

import org.dozer.vo.BaseTestObject;


public class SomeOtherDTO extends BaseTestObject {
  private Integer otherField1;
  private SomeDTO otherField2;
  private String otherField3;
  private Date otherField4;
  public Integer getOtherField1() {
    return otherField1;
  }
  public void setOtherField1(Integer otherField1) {
    this.otherField1 = otherField1;
  }
  public SomeDTO getOtherField2() {
    return otherField2;
  }
  public void setOtherField2(SomeDTO otherField2) {
    this.otherField2 = otherField2;
  }
  public String getOtherField3() {
    return otherField3;
  }
  public void setOtherField3(String otherField3) {
    this.otherField3 = otherField3;
  }
  public Date getOtherField4() {
    return otherField4;
  }
  public void setOtherField4(Date otherField4) {
    this.otherField4 = otherField4;
  }
}
