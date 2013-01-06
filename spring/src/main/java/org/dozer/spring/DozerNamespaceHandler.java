package org.dozer.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Dmitry Buzdin
 */
public class DozerNamespaceHandler extends NamespaceHandlerSupport {

  @Override
  public void init() {
    registerBeanDefinitionParser("mapper", new MapperDefinitionParser());
  }

}
