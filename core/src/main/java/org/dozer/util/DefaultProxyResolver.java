/*
 * Copyright 2005-2009 the original author or authors.
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
package org.dozer.util;

/**
 *
 * Default implementation. Supports only simple proxy cases of Cglib and Javassist.
 * For more complicated scenarious consider using framework specific ProxyResolver.
 *
 * @author dmitry.buzdin
 */
public class DefaultProxyResolver implements DozerProxyResolver {

  public <T> T unenhanceObject(T object) {
    return object;
  }

  public Class<?> getRealClass(Class<?> clazz) {
    if (MappingUtils.isProxy(clazz)) {
      Class<?> superclass = clazz.getSuperclass();
      // Proxy could be created based on set of interfaces. In this case we will rely on inheritance mappings.
      if (DozerConstants.BASE_CLASS.equals(superclass.getName())) {
        return clazz;
      }
      return superclass;
    }
    return clazz;
  }

}
