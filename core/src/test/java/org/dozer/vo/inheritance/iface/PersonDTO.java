package org.dozer.vo.inheritance.iface;

import java.io.Serializable;

public class PersonDTO implements Serializable {
  private Long personId;
  private String name;

  public Long getPersonId() {
    return personId;
  }

  public void setPersonId(Long personId) {
    this.personId = personId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
