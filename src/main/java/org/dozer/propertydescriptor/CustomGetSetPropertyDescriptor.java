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

import java.lang.reflect.Method;

import org.dozer.fieldmap.HintContainer;
import org.dozer.util.ReflectionUtils;


/**
 * 
 * Internal class used to read and write values for fields that have an explicitly specified getter or setter method.
 * Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author tierney.matt
 */
public class CustomGetSetPropertyDescriptor extends JavaBeanPropertyDescriptor {
  private final String customSetMethod;
  private final String customGetMethod;
  private Method writeMethod;
  private Method readMethod;

  public CustomGetSetPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index, String customSetMethod,
      String customGetMethod, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
    this.customSetMethod = customSetMethod;
    this.customGetMethod = customGetMethod;
  }

  @Override
  public Method getWriteMethod() throws NoSuchMethodException {
    if (writeMethod == null) {
      if (customSetMethod != null && !isDeepField()) {
        writeMethod = ReflectionUtils.findAMethod(clazz, customSetMethod);
      } else {
        writeMethod = super.getWriteMethod();
      }
    }
    return writeMethod;
  }

  @Override
  protected Method getReadMethod() throws NoSuchMethodException {
    if (readMethod == null) {
      readMethod = customGetMethod != null ? ReflectionUtils.findAMethod(clazz, customGetMethod) : super.getReadMethod();
    }
    return readMethod;
  }

  @Override
  protected String getSetMethodName() throws NoSuchMethodException {
    return customSetMethod != null ? customSetMethod : super.getSetMethodName();
  }
  
  @Override
  protected boolean isCustomSetMethod() {
    return customSetMethod != null;
  }

}