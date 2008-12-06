package net.sf.dozer.util.mapping.vo.deepindex.isaccessible;

import net.sf.dozer.util.mapping.vo.BaseTestObject;

public class Phone extends BaseTestObject {
  private String phoneNumber;

  public String getNumber() {
    return phoneNumber;
  }

  public void setNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
