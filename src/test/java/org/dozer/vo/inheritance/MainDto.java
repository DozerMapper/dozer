package org.dozer.vo.inheritance;

import org.dozer.vo.BaseTestObject;

public class MainDto extends BaseTestObject {

  private String name;
  private SubMarkerDto sub;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SubMarkerDto getSub() {
    return sub;
  }

  public void setSub(SubMarkerDto sub) {
    this.sub = sub;
  }

}
