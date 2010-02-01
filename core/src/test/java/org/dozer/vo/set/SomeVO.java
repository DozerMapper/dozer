package org.dozer.vo.set;

import java.util.Set;

import org.dozer.vo.BaseTestObject;


public class SomeVO extends BaseTestObject {

  private Integer field1;
  private Set field2;

  public Integer getField1() {
    return field1;
  }
  public void setField1(Integer field1) {
    this.field1 = field1;
  }

  public Set getField2() {
    return field2;
  }
  public void setField2(Set field2) {
    this.field2 = field2;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SomeVO other = (SomeVO) obj;
    if (field2 == null) {
      if (other.field2 != null) {
        return false;
      }
    } else if (!field2.equals(other.field2)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = super.hashCode();
    result = PRIME * result + ((field2 == null) ? 0 : field2.hashCode());
    return result;
  }
}
