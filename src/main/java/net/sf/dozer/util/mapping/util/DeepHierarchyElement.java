package net.sf.dozer.util.mapping.util;

import java.beans.PropertyDescriptor;

public class DeepHierarchyElement {
  private PropertyDescriptor propDescriptor;
  private int index;

  public DeepHierarchyElement(PropertyDescriptor propDescriptor, int index) {
    super();
    this.propDescriptor = propDescriptor;
    this.index = index;
  }
  
  public int getIndex() {
    return index;
  }
  
  public void setIndex(int index) {
    this.index = index;
  }
  
  public PropertyDescriptor getPropDescriptor() {
    return propDescriptor;
  }
  
  public void setPropDescriptor(PropertyDescriptor propDescriptor) {
    this.propDescriptor = propDescriptor;
  }

}