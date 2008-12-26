package net.sf.dozer.functional_tests.vo.deepindex.isaccessible;

import net.sf.dozer.functional_tests.vo.BaseTestObject;

public class Phone extends BaseTestObject {
  private String phoneNumber;

  public String getNumber() {
    return phoneNumber;
  }

  public void setNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
