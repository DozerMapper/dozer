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

import net.sf.dozer.util.mapping.fieldmap.HintContainer;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

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

  public CustomGetSetPropertyDescriptor(Class clazz, String fieldName, boolean isIndexed, int index, String customSetMethod,
      String customGetMethod, HintContainer srcDeepIndexHint, HintContainer destDeepIndexHint) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHint, destDeepIndexHint);
    this.customSetMethod = customSetMethod;
    this.customGetMethod = customGetMethod;
  }

  public Method getWriteMethod() throws NoSuchMethodException {
    if (customSetMethod != null && !(fieldName.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) > 0)) {
      return ReflectionUtils.findAMethod(clazz, customSetMethod);
    } else {
      return super.getWriteMethod();
    }
  }

  protected Method getReadMethod() throws NoSuchMethodException {
    return customGetMethod != null ? ReflectionUtils.findAMethod(clazz, customGetMethod) : super.getReadMethod();
  }

  protected String getSetMethodName() throws NoSuchMethodException {
    return customSetMethod != null ? customSetMethod : super.getSetMethodName();
  }
}