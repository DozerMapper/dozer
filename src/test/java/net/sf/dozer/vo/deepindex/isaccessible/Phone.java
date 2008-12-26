package net.sf.dozer.vo.deepindex.isaccessible;

import net.sf.dozer.vo.BaseTestObject;

public class Phone extends BaseTestObject {
  private String phoneNumber;

  public String getNumber() {
    return phoneNumber;
  }

  public void setNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
