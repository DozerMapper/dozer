package net.sf.dozer.util.mapping.vo.orphan;

public class Child {
  private Long id;
  private String name;

  public Child(Long id, String name) {
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

  public boolean equals(Object o) {
    if (o instanceof Child) {
      Child castObj = (Child) o;
      return id.equals(castObj.getId());
    }
    return false;
  }

  public int hashCode() {
    return id.hashCode();
  }
}
