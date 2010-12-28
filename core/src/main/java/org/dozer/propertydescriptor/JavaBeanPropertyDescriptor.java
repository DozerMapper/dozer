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
package org.dozer.propertydescriptor;

import org.dozer.fieldmap.HintContainer;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;


/**
 * 
 * Internal class used to read and write values for fields that follow the java bean spec and have corresponding
 * getter/setter methods for the field that are name accordingly. If the field does not have the necessary
 * getter/setter, an exception will be thrown. Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author tierney.matt
 */
public class JavaBeanPropertyDescriptor extends GetterSetterPropertyDescriptor {
  private PropertyDescriptor pd;

  public JavaBeanPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
      HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
  }

  @Override
  public Method getWriteMethod() throws NoSuchMethodException {
    Method writeMethod = getPropertyDescriptor(destDeepIndexHintContainer).getWriteMethod();
    if (writeMethod == null) {
      throw new NoSuchMethodException("Unable to determine write method for Field: '" + fieldName + "' in Class: " + clazz);
    }

    return writeMethod;
  }

  @Override
  protected String getSetMethodName() throws NoSuchMethodException {
    return getWriteMethod().getName();
  }

  @Override
  protected Method getReadMethod() throws NoSuchMethodException {
    Method result = getPropertyDescriptor(srcDeepIndexHintContainer).getReadMethod();
    if (result == null) {
      throw new NoSuchMethodException("Unable to determine read method for Field: '" + fieldName + "' in Class: " + clazz);
    }
    return result;
  }
  
  @Override
  protected boolean isCustomSetMethod() {
    return false;
  }

  private PropertyDescriptor getPropertyDescriptor(HintContainer deepIndexHintContainer) {
    if (pd == null) {
      pd = ReflectionUtils.findPropertyDescriptor(clazz, fieldName, deepIndexHintContainer);
      if (pd == null) {
        MappingUtils.throwMappingException("Property: '" + fieldName + "' not found in Class: " + clazz);
      }
    }
    return pd;
  }

}