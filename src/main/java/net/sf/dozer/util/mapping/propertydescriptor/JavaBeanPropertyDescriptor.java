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
import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

/**
 * 
 * Internal class used to read and write values for fields that follow the java bean spec and have corresponding getter/setter methods
 * for the field that are name accordingly. If the field does not have the necessary getter/setter, an exception will be thrown. Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author tierney.matt
 */
public class JavaBeanPropertyDescriptor extends GetterSetterPropertyDescriptor {

  public JavaBeanPropertyDescriptor(Class clazz, String fieldName, boolean isIndexed, int index) {
    super(clazz, fieldName, isIndexed, index);
  }

  public Method getWriteMethod() throws NoSuchMethodException {
    Method writeMethod = getPropertyDescriptor().getWriteMethod();
      if (writeMethod == null) {
        throw new NoSuchMethodException("Unable to determine write method for Field: " + fieldName + " in Class: "
            + clazz);
      }
    
    return writeMethod;
  }
  
  protected String getSetMethodName() throws NoSuchMethodException {
    return getWriteMethod().getName();
  }

  protected Method getReadMethod() throws NoSuchMethodException {
    Method result = getPropertyDescriptor().getReadMethod();
    if (result == null) {
      throw new NoSuchMethodException("Unable to determine read method for Field: " + fieldName + " in Class: " + clazz);
    }
    return result;
  }
  
  private PropertyDescriptor getPropertyDescriptor() {
    PropertyDescriptor pd = ReflectionUtils.findPropertyDescriptor(clazz, fieldName);
    if (pd == null) {
      MappingUtils.throwMappingException("Property: " + fieldName + " not found in Class: " + clazz);          
    }
    return pd;
  }

}