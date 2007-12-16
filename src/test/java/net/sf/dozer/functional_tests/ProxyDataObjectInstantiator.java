package net.sf.dozer.functional_tests;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Quick and dirty class used to create test data objects that are wrapped with a proxy.  Right now use CGLIB to proxy
 * the data objects since this is probably the most typical usage of proxied data objects(Hibernate lazy loading).
 */
public class ProxyDataObjectInstantiator implements DataObjectInstantiator {

  public static final ProxyDataObjectInstantiator INSTANCE = new ProxyDataObjectInstantiator();

  private ProxyDataObjectInstantiator() {
  }

  public Object newInstance(Class classToInstantiate) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(classToInstantiate);
    enhancer.setCallback(NoOpInterceptor.INSTANCE);
    return enhancer.create();
  }

  private static class NoOpInterceptor implements MethodInterceptor, Serializable {
    private static final NoOpInterceptor INSTANCE = new NoOpInterceptor();

    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      return methodProxy.invokeSuper(object, args);
    }
  }

}
