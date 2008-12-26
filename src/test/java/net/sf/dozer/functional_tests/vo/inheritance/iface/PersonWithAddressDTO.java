package net.sf.dozer.functional_tests.vo.inheritance.iface;

import java.io.Serializable;

public class PersonWithAddressDTO extends PersonDTO implements Serializable {
  private String address;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
