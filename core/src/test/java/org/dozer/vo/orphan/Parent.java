package org.dozer.vo.orphan;

import java.util.List;
import java.util.Set;
import java.util.Map;

public class Parent {

  private Long id;
  private String name;
  private Set childrenSet;
  private List childrenList;
  private Map childrenMap;

  private Parent() {
  }

  public Parent(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List getChildrenList() {
    return childrenList;
  }

  public void setChildrenList(List childrenList) {
    this.childrenList = childrenList;
  }

  public Set getChildrenSet() {
    return childrenSet;
  }

  public void setChildrenSet(Set childrenSet) {
    this.childrenSet = childrenSet;
  }

  public Map getChildrenMap() {
    return childrenMap;
  }

  public void setChildrenMap(Map childrenMap) {
    this.childrenMap = childrenMap;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Parent) {
      Parent castObj = (Parent) o;
      return id.equals(castObj.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

}