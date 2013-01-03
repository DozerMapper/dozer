package org.dozer.propertydescriptor.deep;

import org.dozer.propertydescriptor.DozerPropertyDescriptor;

/**
 * @author Dmitry Spikhalskiy
 * @since 04.01.13
 */
public class DozerDeepHierarchyElement {
  private DozerPropertyDescriptor propDescriptor;
  private int index;

  public DozerDeepHierarchyElement(DozerPropertyDescriptor propDescriptor, int index) {
    this.propDescriptor = propDescriptor;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public DozerPropertyDescriptor getPropDescriptor() {
    return propDescriptor;
  }
}
