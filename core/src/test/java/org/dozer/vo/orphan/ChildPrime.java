package org.dozer.vo.orphan;

public class ChildPrime {
  private Long id;
  private String name;

  private ChildPrime() {
  }

  public ChildPrime(Long id, String name) {
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

  @Override
  public boolean equals(Object o) {
    if (o instanceof ChildPrime) {
      ChildPrime castObj = (ChildPrime) o;
      return id.equals(castObj.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}