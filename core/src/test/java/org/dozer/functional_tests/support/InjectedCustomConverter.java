package org.dozer.functional_tests.support;

import org.dozer.CustomConverter;
import org.dozer.vo.Vehicle;


/**
 * @author garsombke.franz
 *
 */
public class InjectedCustomConverter implements CustomConverter {

  private String injectedName;

  public Object convert(Object destination, Object source, Class<?> destClass, Class<?> sourceClass) {
    Vehicle rvalue = null;
    try {
      rvalue = (Vehicle) destClass.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    rvalue.setName(getInjectedName() != null ? getInjectedName() : "defaultValueSetByCustomConverter");
    return rvalue;
  }

  public String getInjectedName() {
    return injectedName;
  }

  public void setInjectedName(String injectedName) {
    this.injectedName = injectedName;
  }

}