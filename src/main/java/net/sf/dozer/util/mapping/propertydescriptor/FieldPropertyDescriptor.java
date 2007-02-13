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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Hint;

/**
 * @author garsombke.franz
 */
public class FieldPropertyDescriptor implements DozerPropertyDescriptorIF {

  private final Field field;

  public FieldPropertyDescriptor(Class bean, String fieldName, boolean isAccessible) throws NoSuchFieldException {
    field = getFieldFromBean(bean, fieldName);
    //Allow access to private instance var's that dont have public setter.  "is-accessible=true" must be explicitly specified in 
    //mapping file to allow this access.  setAccessible indicates intent to bypass field protections.
    field.setAccessible(isAccessible);
  }

  public Class getPropertyType(Class clazz) {
    return field.getType();
  }

  public void setPropertyValue(Object bean, Object value, Hint hint, ClassMap classMap) {
    try {
      field.set(bean, value);
    } catch (Exception e) {
      throw new MappingException(e);
    }
  }

  public Object getPropertyValue(Object bean) {
    Object o = null;
    try {
      o = field.get(bean);
    } catch (Exception e) {
      throw new MappingException(e);
    }
    return o;
  }

  public String getReadMethodName(Class clazz) {
   return "get" + field.getName();
  }

  public String getWriteMethodName(Class clazz) {
    return "set" + field.getName();
  }

  public Method getReadMethod(Class clazz) {
    throw new UnsupportedOperationException();
  }

  public Method getWriteMethod(Class clazz) {
    throw new UnsupportedOperationException();
  }
  
  private Field getFieldFromBean(Class clazz, String fieldName) throws NoSuchFieldException {
    try {
      return clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      if (clazz.getSuperclass() != null) {
        return getFieldFromBean(clazz.getSuperclass(), fieldName);
      }
      throw e;
    }
  }  
  
}
