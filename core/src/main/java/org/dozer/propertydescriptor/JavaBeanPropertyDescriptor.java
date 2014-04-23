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
  private PropertyDescriptorBean pd;
  private String writeMethodName;

  public JavaBeanPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
                                    HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
  }

  /**
   * See {@link org.apache.commons.beanutils.PropertyUtilsBean#getWriteMethod(Class, java.beans.PropertyDescriptor)}
   *
   * @return the write method
   */
  @Override
  public Method getWriteMethod() throws NoSuchMethodException {
    PropertyDescriptorBean descBean = getPropertyDescriptorBean(srcDeepIndexHintContainer);
    Method writeMethod = descBean.getPd().getWriteMethod();
    if (writeMethod == null && writeMethodName != null) {
      writeMethod = MethodUtils.getAccessibleMethod(descBean.getParentClass(), writeMethodName,
          descBean.getPd().getPropertyType());
      if (writeMethod != null) {
        try {
          descBean.getPd().setWriteMethod(writeMethod);
        } catch (IntrospectionException e) {
          // ignore, in this case the method is not cached
        }
      }
    }
    writeMethod = writeMethod == null ? ReflectionUtils.getNonVoidSetter(clazz, fieldName) : writeMethod;
    if (writeMethod == null) {
      throw new NoSuchMethodException("Unable to determine write method for Field: '" + fieldName + "' in Class: " + clazz);
    } else {
      writeMethodName = writeMethod.getName();
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
    Method result = getPropertyDescriptorBean(srcDeepIndexHintContainer).getPd().getReadMethod();
    if (result == null) {
      throw new NoSuchMethodException("Unable to determine read method for Field: '" + fieldName + "' in Class: " + clazz);
    }
    return result;
  }

  @Override
  protected boolean isCustomSetMethod() {
    return false;
  }

  private PropertyDescriptorBean getPropertyDescriptorBean(HintContainer deepIndexHintContainer) {
    if (pd == null) {
      pd = ReflectionUtils.findPropertyDescriptor(clazz, fieldName, deepIndexHintContainer);
      if (pd == null) {
        MappingUtils.throwMappingException("Property: '" + fieldName + "' not found in Class: " + clazz);
      }
    }
    return pd;
  }

}