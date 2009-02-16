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

import java.lang.reflect.Field;

import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.HintContainer;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;


/**
 * Internal class that directly accesses the field via reflection. The getter/setter methods for the field are bypassed
 * and will NOT be invoked. Private fields are accessible by Dozer. Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author tierney.matt
 * @author dmitry.buzdin
 * 
 */
public class FieldPropertyDescriptor extends AbstractPropertyDescriptor implements DozerPropertyDescriptor {

  private Field field;

  public FieldPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
      HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);

    try {
      field = ReflectionUtils.getFieldFromBean(clazz, fieldName);
    } catch (NoSuchFieldException e) {
      MappingUtils.throwMappingException("No such field found " + clazz + "." + fieldName, e);
    }
    // Allow access to private instance var's that dont have public setter.
    field.setAccessible(true);
  }

  @Override
  public Class<?> getPropertyType() {
    return field.getType();
  }

  public Object getPropertyValue(Object bean) {
    Object result = null;
    try {
      result = field.get(bean);
    } catch (IllegalArgumentException e) {
      MappingUtils.throwMappingException(e);
    } catch (IllegalAccessException e) {
      MappingUtils.throwMappingException(e);
    }
    if (isIndexed) {
      result = MappingUtils.getIndexedValue(result, index);
    }

    return result;
  }

  public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
    if (getPropertyType().isPrimitive() && value == null) {
      // do nothing
      return;
    }

    // Check if dest value is already set and is equal to src value. If true, no need to rewrite the dest value
    if (getPropertyValue(bean) == value) {
      return;
    }

    try {
      if (isIndexed) {
        Object existingValue = field.get(bean);
        field.set(bean, prepareIndexedCollection(existingValue, value));
      } else {
        field.set(bean, value);
      }
    } catch (IllegalAccessException e) {
      MappingUtils.throwMappingException(e);
    }
  }

}
