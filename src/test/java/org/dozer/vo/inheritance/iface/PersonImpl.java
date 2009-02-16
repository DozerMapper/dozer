package org.dozer.vo.inheritance.iface;

import java.io.Serializable;

public class PersonImpl implements Person, Serializable {
  private Long id;
  private String name;
  private String address;

  protected PersonImpl() {
  }

  public PersonImpl(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

}
