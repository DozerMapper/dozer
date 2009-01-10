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
package net.sf.dozer.propertydescriptor;

import java.lang.reflect.Method;

import net.sf.dozer.MappingException;
import net.sf.dozer.fieldmap.FieldMap;
import net.sf.dozer.fieldmap.HintContainer;
import net.sf.dozer.util.MappingUtils;
import net.sf.dozer.util.ReflectionUtils;

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
  private Method writeMethod;
  private Method readMethod;

  public MapPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index, String setMethod, String getMethod,
      String key, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
    this.setMethod = setMethod;
    this.getMethod = getMethod;
    this.key = key;
  }

  @Override
  public Method getWriteMethod() throws NoSuchMethodException {
    if (MappingUtils.isBlankOrNull(setMethod)) {
      throw new MappingException("Custom Map set method not specified for field mapping to class: " + clazz
          + ".  Perhaps the map-set-method wasn't specified in the dozer mapping file?");
    }
    if (writeMethod == null) {
      writeMethod = ReflectionUtils.getMethod(clazz, setMethod);
    }
    return writeMethod;
  }

  @Override
  public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
    if (isDeepField()) {
      writeDeepDestinationValue(bean, value, fieldMap);
    } else {
      if (!getPropertyType().isPrimitive() || value != null) {
        // Check if dest value is already set and is equal to src value. If true, no need to rewrite the dest value
        try {
          if (getPropertyValue(bean) == value) {
            return;
          }
        } catch (Exception e) {
          // if we failed to read the value, assume we must write, and continue...
        }
        invokeWriteMethod(bean, value);
      }
    }
  }

  @Override
  protected Method getReadMethod() throws NoSuchMethodException {
    if (MappingUtils.isBlankOrNull(getMethod)) {
      throw new MappingException("Custom Map get method not specified for field mapping to class: " + clazz
          + ".  Perhaps the map-get-method wasn't specified in the dozer mapping file?");
    }
    if (readMethod == null) {
      readMethod = ReflectionUtils.getMethod(clazz, getMethod);
    }
    return readMethod;
  }

  @Override
  protected String getSetMethodName() throws NoSuchMethodException {
    return setMethod;
  }
  
  @Override
  protected boolean isCustomSetMethod() {
    return true;
  }

  @Override
  protected void invokeWriteMethod(Object target, Object value) {
    if (key == null) {
      throw new MappingException("key must be specified");
    }
    try {
      ReflectionUtils.invoke(getWriteMethod(), target, new Object[] { key, value });
    } catch (NoSuchMethodException e) {
      MappingUtils.throwMappingException(e);
    }
  }

  @Override
  protected Object invokeReadMethod(Object target) {
    if (key == null) {
      throw new MappingException("key must be specified");
    }
    Object result = null;
    try {
      result = ReflectionUtils.invoke(getReadMethod(), target, new Object[] { key });
    } catch (NoSuchMethodException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }
}