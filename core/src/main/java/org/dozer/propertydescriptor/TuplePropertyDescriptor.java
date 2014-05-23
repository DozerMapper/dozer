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

import org.dozer.MappingException;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.HintContainer;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;


/**
 * Internal class used to read and write values for Map backed objects that use key/value pairs. The specified "key" is
 * used when invoking getter/setter. It is assumed that Map setter method has two parameters (for "key" and "value"),
 * but getter method one parameter (for "key").
 * <p/>
 * Overloaded methods are supported. Map class can have two set methods with different signatures, but class will
 * choose the one with appropriate number of parameters.
 * <p/>
 * <p/>
 * Only intended for internal use.
 *
 * @author ikozar
 */
public class TuplePropertyDescriptor extends MapPropertyDescriptor {

  public TuplePropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index,
                                 String setMethod, String getMethod, String key, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, false, 0, null, null, key, null, null);
  }

  public TuplePropertyDescriptor(Class<?> clazz, String fieldName, String key) {
    super(clazz, fieldName, false, 0, null, "get", key, null, null);
  }

  @Override
  public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) throws MappingException {
    // do nothing
  }

  @Override
  public Class<?> genericType() {
    return null;
  }

  @Override
  public Class<?> getPropertyType() {
    return super.getPropertyType();
  }

  @Override
  public Object getPropertyValue(Object bean) throws MappingException {
    int i = 0;
    for (Iterator<TupleElement<?>> it = ((Tuple) bean).getElements().iterator(); it.hasNext(); i++) {
      if (key.equals(it.next().getAlias())) {
        return ((Tuple) bean).get(i);
      }
    }
    return null;
  }

}