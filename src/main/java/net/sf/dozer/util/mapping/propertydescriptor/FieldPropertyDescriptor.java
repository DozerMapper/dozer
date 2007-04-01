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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Hint;
import net.sf.dozer.util.mapping.util.CollectionUtils;
import net.sf.dozer.util.mapping.util.MappingUtils;

/**
 * @author garsombke.franz
 * 
 * This class is used to read and write values for fields accessed directly.  
 * The getter/setter methods for the field are bypassed and will NOT be invoked.  The field is accessed directly via Reflection.  
 * Private fields are accessible by Dozer.
 * 
 */
public class FieldPropertyDescriptor extends AbstractPropertyDescriptor implements DozerPropertyDescriptorIF {

  private final Field field;
  private MappingUtils mappingUtils = new MappingUtils();

  public FieldPropertyDescriptor(Class clazz, String fieldName, boolean isAccessible, boolean isIndexed, int index)
      throws NoSuchFieldException {
    super(clazz, fieldName, isIndexed, index);

    field = getFieldFromBean(clazz, fieldName);
    // Allow access to private instance var's that dont have public setter. "is-accessible=true" must be explicitly
    // specified in mapping file to allow this access. setAccessible indicates intent to bypass field protections.
    field.setAccessible(isAccessible);
  }

  public Class getPropertyType() {
    return field.getType();
  }

  public Object getPropertyValue(Object bean) {
    try {
      Object o = field.get(bean);
      if (isIndexed) {
        return mappingUtils.getIndexedValue(o, index);
      } else {
        return o;
      }
    } catch (Exception e) {
      throw new MappingException(e);
    }
  }

  public void setPropertyValue(Object bean, Object value, Hint hint, ClassMap classMap) {
    try {
      if (getPropertyType().isPrimitive() && value == null) {
        // do nothing
        return;
      }
      
      //Check if dest value is already set and is equal to src value.  If true, no need to rewrite the dest value
      try {
        if (getPropertyValue(bean) == value) {
          return;
        }
      } catch (Exception e) {
        //if we failed to read the value, assume we must write, and continue...
      }  

      if (isIndexed) {
        writeIndexedValue(bean, value);
      } else {
        field.set(bean, value);
      }
    } catch (IllegalAccessException e) {
      throw new MappingException(e);
    }
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

  protected void writeIndexedValue(Object destObj, Object destFieldValue) throws IllegalAccessException {
    Object existingValue = field.get(destObj);
    Object indexedValue = getIndexedValue(existingValue, destFieldValue);
    
    field.set(destObj, indexedValue);
  }

}
