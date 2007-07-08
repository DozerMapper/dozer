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
package net.sf.dozer.util.mapping.propertydescriptor;

import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

/**
 * 
 * Internal class used to read and write values for Map backed objects that use key/value pairs. The specified "key" is
 * used when invoking getter/setter. Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author tierney.matt
 */
public class MapPropertyDescriptor extends GetterSetterPropertyDescriptor {
  private final String setMethod;
  private final String getMethod;
  private final String key;

  public MapPropertyDescriptor(Class clazz, String fieldName, boolean isIndexed, int index, String setMethod, String getMethod,
      String key) {
    super(clazz, fieldName, isIndexed, index);
    this.setMethod = setMethod;
    this.getMethod = getMethod;
    this.key = key;
  }

  public Method getWriteMethod() throws NoSuchMethodException {
    return ReflectionUtils.getMethod(clazz, setMethod);
  }

  protected void invokeWriteMethod(Object target, Object value) throws NoSuchMethodException {
    if (key == null) {
      throw new MappingException("key must be specified");
    }
    ReflectionUtils.invoke(getWriteMethod(), target, new Object[] { key, value });
  }

  protected Method getReadMethod() throws NoSuchMethodException {
    return ReflectionUtils.getMethod(clazz, getMethod);
  }

  protected Object invokeReadMethod(Object target) throws NoSuchMethodException {
    if (key == null) {
      throw new MappingException("key must be specified");
    }
    return ReflectionUtils.invoke(getReadMethod(), target, new Object[] { key });
  }

  protected String getSetMethodName() throws NoSuchMethodException {
    return setMethod;
  }
}