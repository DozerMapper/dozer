package org.dozer.vo.inheritance;

public class MainDto {

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
