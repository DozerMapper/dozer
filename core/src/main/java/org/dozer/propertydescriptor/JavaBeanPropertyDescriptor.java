/**
 * Copyright 2005-2013 Dozer Project
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

import org.apache.commons.beanutils.MethodUtils;
import org.dozer.fieldmap.HintContainer;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Internal class used to read and write values for fields that follow the java bean spec and have corresponding
 * getter/setter methods for the field that are name accordingly. If the field does not have the necessary
 * getter/setter, an exception will be thrown. Only intended for internal use.
 *
 * @author garsombke.franz
 * @author tierney.matt
 */
public class JavaBeanPropertyDescriptor extends GetterSetterPropertyDescriptor {
  private PropertyDescriptor pd;
  private String writeMethodName;

  public JavaBeanPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
                                    HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
  }

  @Override
  public Method getWriteMethod() throws NoSuchMethodException {
    Method writeMethod = MethodUtils.getAccessibleMethod(clazz,
        getWriteMethod(clazz, getPropertyDescriptor(srcDeepIndexHintContainer)));
    writeMethod = writeMethod == null ? ReflectionUtils.getNonVoidSetter(clazz, fieldName) : writeMethod;
    if (writeMethod == null) {
      throw new NoSuchMethodException("Unable to determine write method for Field: '" + fieldName + "' in Class: " + clazz);
    }
    return writeMethod;
  }

  @Override
  protected String getSetMethodName() throws NoSuchMethodException {
    if (writeMethodName == null) {
      writeMethodName = getWriteMethod().getName();
    }
    return writeMethodName;
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

  /**
   * See {@link org.apache.commons.beanutils.PropertyUtilsBean#getWriteMethod(Class, java.beans.PropertyDescriptor)}
   *
   * @param beanCls the bean class
   * @param desc    the property descriptor
   * @return the write method
   */
  private Method getWriteMethod(Class<?> beanCls, PropertyDescriptor desc) {
    Method method = desc.getWriteMethod();
    if (method == null) {
      if (writeMethodName != null) {
        method = MethodUtils.getAccessibleMethod(beanCls, writeMethodName,
            desc.getPropertyType());
        if (method != null) {
          try {
            desc.setWriteMethod(method);
          } catch (IntrospectionException e) {
            // ignore, in this case the method is not cached
          }
        }
      }
    } else {
      writeMethodName = method.getName();
    }

    return method;
  }

}