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

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Hint;
import net.sf.dozer.util.mapping.util.DestBeanCreator;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

/**
 * 
 * Internal class used to read and write values for fields that have a getter and setter.  This class
 * encapsulates underlying dozer specific logic such as index mapping and deep mapping for reading and writing field
 * values. Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author tierney.matt
 * 
 */
public abstract class GetterSetterPropertyDescriptor extends AbstractPropertyDescriptor {
  private Class propertyType;

  public GetterSetterPropertyDescriptor(Class clazz, String fieldName, boolean isIndexed, int index) {
    super(clazz, fieldName, isIndexed, index);
  }
  
  public abstract Method getWriteMethod() throws NoSuchMethodException;
  protected abstract void invokeWriteMethod(Object target, Object value) throws NoSuchMethodException;

  protected abstract Method getReadMethod() throws NoSuchMethodException;
  protected abstract Object invokeReadMethod(Object target) throws NoSuchMethodException;
  
  protected abstract String getSetMethodName() throws NoSuchMethodException;

  public Class getPropertyType() {
    if (propertyType == null) {
      propertyType = determinePropertyType();
    }
    return propertyType;
  }

  public Object getPropertyValue(Object bean) {
    Object result = null;
    if (fieldName.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) < 0) {
      Object o = null;
      try {
        o = invokeReadMethod(bean);
      } catch (NoSuchMethodException e) {
        MappingUtils.throwMappingException(e);
      }
      if (isIndexed) {
        result = MappingUtils.getIndexedValue(o, index);
      } else {
        result = o;
      }
    } else {
      result = getDeepSrcFieldValue(bean);
    }
    return result;
  }

  public void setPropertyValue(Object bean, Object value, Hint hint, ClassMap classMap) {
    if (fieldName.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) < 0) {
      if (getPropertyType().isPrimitive() && value == null) {
        // do nothing
      } else {
        // Check if dest value is already set and is equal to src value. If true, no need to rewrite the dest value
        try {
          if (getPropertyValue(bean) == value) {
            return;
          }
        } catch (Exception e) {
          // if we failed to read the value, assume we must write, and continue...
        }
        if (!isIndexed) {
          try {
            invokeWriteMethod(bean, value);
          } catch (NoSuchMethodException e) {
            MappingUtils.throwMappingException(e);
          }
        } else {
          writeIndexedValue(null, bean, value);
        }
      }
    } else {
      writeDeepDestinationValue(bean, value, hint, classMap);
    }
  }  
  
  
  private Object getDeepSrcFieldValue(Object srcObj) {
    // follow deep field hierarchy. If any values are null along the way, then return null
    Object parentObj = srcObj;
    Object hierarchyValue = null;
    PropertyDescriptor[] hierarchy = getHierarchy(srcObj);
    int size = hierarchy.length;
    for (int i = 0; i < size; i++) {
      PropertyDescriptor pd = hierarchy[i];
      hierarchyValue = ReflectionUtils.invoke(pd.getReadMethod(), parentObj, null);
      parentObj = hierarchyValue;
      if (hierarchyValue == null) {
        break;
      }
    }

    // If field is indexed, get actual value within the collection at the specified index
    if (isIndexed) {
      hierarchyValue = MappingUtils.getIndexedValue(hierarchyValue, index);
    }

    return hierarchyValue;
  }

  private void writeDeepDestinationValue(Object destObj, Object destFieldValue, Hint destHint, ClassMap classMap) {
    // follow deep field hierarchy. If any values are null along the way, then create a new instance
    PropertyDescriptor[] hierarchy = getHierarchy(destObj);
    // first, iteratate through hierarchy and instantiate any objects that are null
    Object parentObj = destObj;
    int hierarchyLength = hierarchy.length - 1;
    for (int i = 0; i < hierarchyLength; i++) {
      PropertyDescriptor pd = hierarchy[i];
      Object value = ReflectionUtils.invoke(pd.getReadMethod(), parentObj, null);
      Class clazz = null;
      if (value == null) {
        clazz = pd.getPropertyType();
        if (clazz.isInterface()) {
          // before setting the property on the destination object we should check for a destination hint. need to know
          // that we are at the end of the line
          if ((i + 1) == hierarchyLength) {
            // determine the property type
            if (destHint != null) {
              clazz = destHint.getHint();
            }
          }
        }
        Object o;
        try {
          o = ReflectionUtils.newInstance(clazz);
          ReflectionUtils.invoke(pd.getWriteMethod(), parentObj, new Object[] { o });
        } catch (Exception e) {
          // lets see if they have a factory we can try. If not...throw the exception:
          if (classMap.getDestClass().getBeanFactory() != null) {
            DestBeanCreator destBeanCreator = new DestBeanCreator(MappingUtils.storedFactories);
            o = destBeanCreator.createFromFactory(null, classMap.getSourceClass().getClassToMap(), classMap
                .getDestClass().getBeanFactory(), classMap.getDestClass().getFactoryBeanId(), clazz);
            ReflectionUtils.invoke(pd.getWriteMethod(), parentObj, new Object[] { o });
          } else {
            MappingUtils.throwMappingException(e);
          }
        }
        value = ReflectionUtils.invoke(pd.getReadMethod(), parentObj, null);
      }
      parentObj = value;
    }
    // second, set deep field value
    PropertyDescriptor pd = hierarchy[hierarchy.length - 1];
    if (pd.getReadMethod().getReturnType().isPrimitive() && destFieldValue == null) {
    } else {
      if (!isIndexed) {
        Method method = pd.getWriteMethod();
        try {

        if (method == null && getSetMethodName() != null) {
          // lets see if we can find a custom method
            method = ReflectionUtils.findAMethod(parentObj.getClass(), getSetMethodName());
        }
        } catch (NoSuchMethodException e) {
          MappingUtils.throwMappingException(e);
        }
        
        ReflectionUtils.invoke(method, parentObj, new Object[] { destFieldValue });
      } else {
        writeIndexedValue(pd, parentObj, destFieldValue);
      }
    }
  }

  private PropertyDescriptor[] getHierarchy(Object obj) {
    return ReflectionUtils.getDeepFieldHierarchy(obj.getClass(), fieldName);
  }

  private void writeIndexedValue(PropertyDescriptor pd, Object destObj, Object destFieldValue) {
    Object existingValue = null;
    if (pd != null) {
      existingValue = ReflectionUtils.invoke(pd.getReadMethod(), destObj, null);
    } else {
      try {
        existingValue = ReflectionUtils.invoke(getReadMethod(), destObj, null);
      } catch (NoSuchMethodException e) {
        MappingUtils.throwMappingException(e);
      }
    }

    Object indexedValue = getIndexedValue(existingValue, destFieldValue);

    if (pd != null) {
      ReflectionUtils.invoke(pd.getWriteMethod(), destObj, new Object[] { indexedValue });
    } else {
      try {
        ReflectionUtils.invoke(getWriteMethod(), destObj, new Object[] { indexedValue });
      } catch (NoSuchMethodException e) {
        MappingUtils.throwMappingException(e);
      }
    }

  }

  private Class determinePropertyType() {
    Class returnType = null;
    try {
      returnType = getReadMethod().getReturnType();
    } catch (Exception e) {
      // let us try the set method - the field might not have a 'get' method
      try {
        returnType = getWriteMethod().getParameterTypes()[0];
      } catch (NoSuchMethodException e1) {
        MappingUtils.throwMappingException(e);      }
    }
    return returnType;
  }

}