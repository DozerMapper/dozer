package org.dozer.vo.inheritance;

import org.dozer.vo.BaseTestObject;

public class Main extends BaseTestObject {

  private String name;
  private SubMarker sub;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SubMarker getSub() {
    return sub;
  }

  public void setSub(SubMarker sub) {
    this.sub = sub;
  }

}
