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

import org.apache.commons.lang.ClassUtils;

/**
 * @author dmitry.buzdin
 */
public class DefaultProxyResolver implements DozerProxyResolver {

  public Class<?> loadClass(String name) {
    Class<?> result = null;
    try {
    	//Class caller = Reflection.getCallerClass(3); TODO OSGi fix - Move to specific implementation
    	result = ClassUtils.getClass(name);
    } catch (ClassNotFoundException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

  public boolean isProxy(Class<?> clazz) {
    if (clazz.isInterface()) {
      return false;
    }
    return clazz.getName().contains(DozerConstants.CGLIB_ID)
        || clazz.getName().contains(DozerConstants.JAVASSIST_ID);
  }

  public Class<?> getRealSuperclass(Class<?> clazz) {
    if (isProxy(clazz)) {
      return clazz.getSuperclass().getSuperclass();
    }
    return clazz.getSuperclass();
  }

  public Class<?> getRealClass(Class<?> clazz) {
    if (isProxy(clazz)) {
      return clazz.getSuperclass();
    }
    return clazz;
  }


}
