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

public class DataObjectInstantiator {

  public static final DataObjectInstantiator NO_PROXY_INSTANTIATOR = new DataObjectInstantiator(0);
  public static final DataObjectInstantiator PROXY_INSTANTIATOR = new DataObjectInstantiator(1);

  private int proxyMode = -1;

  private DataObjectInstantiator(int proxyMode) {
    this.proxyMode = proxyMode;
  }

  public Object newInstance(Class classToInstantiate) {
    if (proxyMode == 0) {
      try {
        return classToInstantiate.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (proxyMode == 1) {
      return proxyNewInstance(classToInstantiate);
    } else {
      throw new RuntimeException("invalid proxy mode specified: " + proxyMode);
    }
  }

  private Object proxyNewInstance(Class clazz) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(clazz);
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
