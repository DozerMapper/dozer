/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dozer.functional_tests.proxied;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import javassist.util.proxy.ProxyFactory;
import org.dozer.MappingException;
import org.dozer.functional_tests.DataObjectInstantiator;

public class JavassistDataObjectInstantiator implements DataObjectInstantiator {

  public static final DataObjectInstantiator INSTANCE = new JavassistDataObjectInstantiator();

  private JavassistDataObjectInstantiator() {
  }

  public <T> T newInstance(Class<T> classToInstantiate) {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(classToInstantiate);
    Class newClass = proxyFactory.createClass();
    Object instance;
    try {
      instance = newClass.newInstance();
    } catch (Exception e) {
      throw new MappingException(e);
    }
    return (T) instance;
  }

  public <T> T newInstance(Class<T> classToInstantiate, Object[] args) {
    return null;
  }

  public Object newInstance(Class<?>[] interfacesToProxy, Object target) {
    return null;
  }

}
