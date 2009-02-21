package org.dozer.vo.inheritance.cc;

import org.dozer.CustomConverter;

/**
 * This is the base class for creating all dummy-objects.
 * If you need a concrete implementation, create it and implement
 * the method createBo, where you must return the concrete dummy-object.
 * 
 * @author kammermannf
 */
public class InheritanceBugConverter implements CustomConverter {

  /**
   * The convert-method
   * 
   * @param destination
   * @param source
   * @param destClass
   * @param sourceClass
   * @return
   */
  public Object convert(Object destination, Object source, Class destClass, Class sourceClass) {
    return "customConverter";
  }
}
