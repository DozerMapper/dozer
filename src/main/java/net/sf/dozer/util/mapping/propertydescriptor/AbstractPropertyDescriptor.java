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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.dozer.util.mapping.util.CollectionUtils;

/**
 * Internal abstract property descriptor containing common property descriptor logic.  Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class AbstractPropertyDescriptor implements DozerPropertyDescriptorIF {
  
  protected final Class clazz;
  protected final String fieldName;
  protected boolean isIndexed = false;
  protected int index;
  
  public AbstractPropertyDescriptor(final Class clazz, final String fieldName, boolean isIndexed, int index) {
    this.clazz = clazz;
    this.fieldName = fieldName;
    this.isIndexed = isIndexed;
    this.index = index;
  }

  public abstract Class getPropertyType();
  
  protected Object getIndexedValue(Object existingFieldValue, Object newFieldValue) {
    Object result = null;
    if (existingFieldValue == null) {
      Class returnType = getPropertyType();

      if (returnType.isArray()) {
        existingFieldValue = Array.newInstance(returnType.getComponentType(), 0);
      } else if (CollectionUtils.isSet(returnType)) {
        existingFieldValue = new HashSet();
      } else { // default
        existingFieldValue = new ArrayList();
      }
    }
    if (existingFieldValue instanceof Collection) {
      Collection newCollection;
      if (existingFieldValue instanceof Set) {
        newCollection = new HashSet();
      } else {
        newCollection = new ArrayList();
      }

      Collection c = (Collection) existingFieldValue;
      Iterator i = c.iterator();
      int x = 0;
      while (i.hasNext()) {
        if (x != index) {
          newCollection.add(i.next());
        } else {
          newCollection.add(newFieldValue);
        }
        x++;
      }
      if (newCollection.size() <= index) {
        while (newCollection.size() < index) {
          newCollection.add(null);
        }
        newCollection.add(newFieldValue);
      }
      result = newCollection;
    } else if (existingFieldValue.getClass().isArray()) {
      Object[] objs = (Object[]) existingFieldValue;
      Object[] x = (Object[]) Array.newInstance(objs.getClass().getComponentType(),
          objs.length > index ? objs.length + 1 : index + 1);
      for (int i = 0; i < objs.length; i++) {
        x[i] = objs[i];
      }
      x[index] = newFieldValue;
      result = x;
    }
    return result;
  }
  

}
