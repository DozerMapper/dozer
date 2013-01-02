package org.dozer.spring.functional_tests.support;

import org.dozer.BeanFactory;

/**
 * @author Dmitry Buzdin
 */
public class SampleCustomBeanFactory implements BeanFactory {

  public Object createBean(Object srcObj, Class<?> srcObjClass, String id) {
    try {
      return Class.forName(id).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
