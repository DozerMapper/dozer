package org.dozer.spring.functional_tests.support;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dmitry Buzdin
 */
public class ReferencingBean {

  @Autowired
  Mapper mapper;

  public Mapper getMapper() {
    return mapper;
  }

}
