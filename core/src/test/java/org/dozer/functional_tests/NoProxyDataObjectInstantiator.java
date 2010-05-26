package org.dozer.functional_tests;

import org.apache.commons.beanutils.ConstructorUtils;

/*
 * Copyright 2005-2010 the original author or authors.
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

public class NoProxyDataObjectInstantiator implements DataObjectInstantiator {

  public static final NoProxyDataObjectInstantiator INSTANCE = new NoProxyDataObjectInstantiator();

  private NoProxyDataObjectInstantiator() {
  }

  public <T> T newInstance(Class<T> classToInstantiate) {
    try {
      return classToInstantiate.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public <T> T newInstance(Class<T> classToInstantiate, Object[] args) {
    try {
      return (T) ConstructorUtils.invokeConstructor(classToInstantiate, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Object newInstance(Class<?>[] interfacesToProxy, Object target) {
    return target;
  }

}
