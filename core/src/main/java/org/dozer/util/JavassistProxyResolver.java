package org.dozer.util;

import javassist.util.proxy.ProxyFactory;

/**
 * @author Dmitry Buzdin
 */
public class JavassistProxyResolver extends DefaultProxyResolver {

  @Override
  public boolean isProxy(Class<?> clazz) {
    return ProxyFactory.isProxyClass(clazz);
  }

}
