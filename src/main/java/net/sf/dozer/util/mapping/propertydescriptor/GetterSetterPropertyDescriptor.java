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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Hint;
import net.sf.dozer.util.mapping.util.DestBeanCreator;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

/**
 * @author garsombke.franz
 * 
 * This class is used to read and write values for fields that follow the java bean spec and have getter/setter methods
 * for the field. If the field does not have the necessary getter/setter, an exception will be thrown. This class
 * encapsulates underlying dozer specific logic such as index mapping and deep mapping for reading and writing field
 * values.
 * 
 */
public class GetterSetterPropertyDescriptor extends AbstractPropertyDescriptor implements DozerPropertyDescriptorIF {

  private final String customSetMethod;
  private final String customGetMethod;
  private final Class propertyType;

  private final ReflectionUtils reflectionUtils = new ReflectionUtils();
  private final MappingUtils mappingUtils = new MappingUtils();

  public GetterSetterPropertyDescriptor(Class clazz, String fieldName, boolean isIndexed, int index,
      String customSetMethod, String customGetMethod) {
    super(clazz, fieldName, isIndexed, index);
    this.customSetMethod = customSetMethod;
    this.customGetMethod = customGetMethod;

    try {
      this.propertyType = determinePropertyType();
    } catch (Exception e) {
      throw new MappingException("Unable to determine property type. Field: " + fieldName + " Class: " + clazz, e);
    }
  }

  public Class getPropertyType() {
    return this.propertyType;
  }

  public Object getPropertyValue(Object bean) {
    Object o = null;
    try {
      if (fieldName.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) < 0) {
        o = getReadMethod().invoke(bean, null);
        if (isIndexed) {
          return mappingUtils.getIndexedValue(o, index);
        } else {
          return o;
        }
      } else {
        return getDeepSrcFieldValue(bean);
      }
    } catch (Exception e) {
      throw new MappingException(e);
    }
  }

  public void setPropertyValue(Object bean, Object value, Hint hint, ClassMap classMap) {
    try {
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
            getWriteMethod().invoke(bean, new Object[] { value });
          } else {
            writeIndexedValue(null, bean, value);
          }
        }
      } else {
        writeDeepDestinationValue(bean, value, hint, classMap);
      }
    } catch (Exception e) {
      throw new MappingException(e);
    }
  }

  protected Object getDeepSrcFieldValue(Object srcObj) throws InvocationTargetException, IllegalAccessException {
    // follow deep field hierarchy. If any values are null along the way, then return null
    Object parentObj = srcObj;
    Object hierarchyValue = null;
    PropertyDescriptor[] hierarchy = getHierarchy(srcObj);
    int size = hierarchy.length;
    for (int i = 0; i < size; i++) {
      PropertyDescriptor pd = hierarchy[i];
      hierarchyValue = pd.getReadMethod().invoke(parentObj, null);
      parentObj = hierarchyValue;
      if (hierarchyValue == null) {
        break;
      }
    }

    // If field is indexed, get actual value within the collection at the specified index
    if (isIndexed) {
      hierarchyValue = mappingUtils.getIndexedValue(hierarchyValue, index);
    }

    return hierarchyValue;
  }

  protected void writeDeepDestinationValue(Object destObj, Object destFieldValue, Hint destHint, ClassMap classMap)
      throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException,
      NoSuchMethodException, NoSuchFieldException {
    // follow deep field hierarchy. If any values are null along the way, then create a new instance
    PropertyDescriptor[] hierarchy = getHierarchy(destObj);
    // first, iteratate through hierarchy and instantiate any objects that are null
    Object parentObj = destObj;
    int hierarchyLength = hierarchy.length - 1;
    for (int i = 0; i < hierarchyLength; i++) {
      PropertyDescriptor pd = hierarchy[i];
      Object value = pd.getReadMethod().invoke(parentObj, null);
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
          o = clazz.newInstance();
          pd.getWriteMethod().invoke(parentObj, new Object[] { o });
        } catch (InstantiationException e) {
          // lets see if they have a factory we can try. If not...throw the exception:
          if (classMap.getDestClass().getBeanFactory() != null) {
            DestBeanCreator destBeanCreator = new DestBeanCreator(MappingUtils.storedFactories);
            o = destBeanCreator.createFromFactory(null, classMap.getSourceClass().getClassToMap(), classMap
                .getDestClass().getBeanFactory(), classMap.getDestClass().getFactoryBeanId(), clazz);
            pd.getWriteMethod().invoke(parentObj, new Object[] { o });
          } else {
            throw e;
          }
        }
        value = pd.getReadMethod().invoke(parentObj, null);
      }
      parentObj = value;
    }
    // second, set deep field value
    PropertyDescriptor pd = hierarchy[hierarchy.length - 1];
    if (pd.getReadMethod().getReturnType().isPrimitive() && destFieldValue == null) {
    } else {
      if (!isIndexed) {
        Method method = pd.getWriteMethod();
        if (method == null && customSetMethod != null) {
          // lets see if we can find a custom method
          method = reflectionUtils.findAMethod(parentObj.getClass(), customSetMethod);
        }
        method.invoke(parentObj, new Object[] { destFieldValue });
      } else {
        writeIndexedValue(pd, parentObj, destFieldValue);
      }
    }
  }

  protected PropertyDescriptor[] getHierarchy(Object obj) {
    return reflectionUtils.getDeepFieldHierarchy(obj.getClass(), fieldName);
  }

  protected Method getReadMethod() throws NoSuchMethodException {
    Method result = null;
    if (customGetMethod == null) {
      PropertyDescriptor pd = reflectionUtils.findPropertyDescriptor(clazz, fieldName);
      if ((pd == null || pd.getReadMethod() == null)) {
        throw new NoSuchMethodException("Unable to determine read method for field: " + fieldName + " class: " + clazz);
      }
      result = pd.getReadMethod();
    } else {
      try {
        result = reflectionUtils.findAMethod(clazz, customGetMethod);
      } catch (Exception e) {
        throw new NoSuchMethodException("Unable to find method: " + " in class: " + clazz);

      }
    }

    return result;
  }

  public Method getWriteMethod() throws NoSuchMethodException {
    Method writeMethod = null;
    if (writeMethod == null) {
      if (customSetMethod == null || fieldName.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) > 0) {
        PropertyDescriptor pd = reflectionUtils.findPropertyDescriptor(clazz, fieldName);
        if ((pd == null || pd.getWriteMethod() == null)) {
          throw new NoSuchMethodException("Unable to determine write method for field: " + fieldName + " class: "
              + clazz);
        }
        writeMethod = pd.getWriteMethod();
      } else {
        try {
          writeMethod = reflectionUtils.findAMethod(clazz, customSetMethod);
        } catch (Exception e) {
          throw new MappingException(e);
        }
      }
    }
    return writeMethod;
  }

  protected void writeIndexedValue(PropertyDescriptor pd, Object destObj, Object destFieldValue)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
      NoSuchFieldException {
    Object existingValue = null;
    if (pd != null) {
      existingValue = pd.getReadMethod().invoke(destObj, null);
    } else {
      existingValue = getReadMethod().invoke(destObj, null);
    }

    Object indexedValue = getIndexedValue(existingValue, destFieldValue);

    if (pd != null) {
      pd.getWriteMethod().invoke(destObj, new Object[] { indexedValue });
    } else {
      getWriteMethod().invoke(destObj, new Object[] { indexedValue });
    }

  }

  private Class determinePropertyType() throws NoSuchMethodException {
    Class returnType;
    try {
      returnType = getReadMethod().getReturnType();
    } catch (Exception e) {
      // let us try the set method - the field might not have a 'get' method
      returnType = getWriteMethod().getParameterTypes()[0];
    }
    return returnType;
  }

}