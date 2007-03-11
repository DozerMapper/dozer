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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Field;
import net.sf.dozer.util.mapping.fieldmap.Hint;
import net.sf.dozer.util.mapping.util.CollectionUtils;
import net.sf.dozer.util.mapping.util.DestBeanCreator;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author garsombke.franz
 */
public class BruteForcePropertyDescriptor implements DozerPropertyDescriptorIF {

  private static final Log log = LogFactory.getLog(DozerBeanMapper.class);
  

  private final Field field;
  private final ReflectionUtils reflectionUtils = new ReflectionUtils();
  private final CollectionUtils collectionUtils = new CollectionUtils();
  private final MappingUtils mappingUtils = new MappingUtils();
  private final DestBeanCreator destBeanCreator = new DestBeanCreator(MappingUtils.storedFactories);//only temp use the public static factories.  The factories data needs to be relocated to a better place
  

  public BruteForcePropertyDescriptor(Field field) {
    this.field = field;
  }

  public Class getPropertyType(Class clazz) {
    Class returnType;
    try {
      returnType = getReadMethod(clazz).getReturnType();
    } catch (Exception e) {
      log.debug("Read method exception:" + e);
      // let us try the set method - the field might not have a 'get' method
      returnType = getWriteMethod(clazz).getParameterTypes()[0];
    }
    return returnType;
  }

  public void setPropertyValue(Object bean, Object value, Hint hint, ClassMap classMap) {
    try {
      if (field.getName().indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) < 0) {
        if (getPropertyType(bean.getClass()).isPrimitive() && value == null) {
          // do nothing
        } else {
          //Check if dest value is already set and is equal to src value.  If true, no need to rewrite the dest value
          try {
            if (getPropertyValue(bean) == value) {
              return;
            }
          } catch (Exception e) {
            //if we failed to read the value, assume we must write, and continue...
          }  
          if (!field.isIndexed()) {
            getWriteMethod(bean.getClass()).invoke(bean, new Object[] { value });
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

  public Object getPropertyValue(Object bean) {
    Object o = null;
    try {
      if (field.getName().indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) < 0) {
        o = getReadMethod(bean.getClass()).invoke(bean, null);
        if (field.isIndexed()) {
          return mappingUtils.getIndexedValue(o, field.getIndex());
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
    
    //If field is indexed, get actual value within the collection at the specified index
    if (field.isIndexed()) {
      hierarchyValue = mappingUtils.getIndexedValue(hierarchyValue, field.getIndex()); 
    }
    
    return hierarchyValue;
  }

  protected Method getReadMethod(Class objectClass, String fieldName) {
    PropertyDescriptor pd = reflectionUtils.getPropertyDescriptor(objectClass, fieldName);
    if ((pd == null || pd.getReadMethod() == null)) {
      throw new MappingException("Unable to determine read method for field: " + fieldName + " class: " + objectClass);
    }
    return pd.getReadMethod();
  }

  protected Method getWriteMethod(Class objectClass, String fieldName) {
    PropertyDescriptor pd = reflectionUtils.getPropertyDescriptor(objectClass, fieldName);
    if ((pd == null || pd.getWriteMethod() == null)) {
      throw new MappingException("Unable to determine write method for field: " + fieldName + " class: " + objectClass);
    }
    return pd.getWriteMethod();
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
      if (!field.isIndexed()) {
        Method method = pd.getWriteMethod();
        if(method == null && field.getTheSetMethod() != null) {
          // lets see if we can find a custom method
          method = reflectionUtils.findAMethod(parentObj.getClass(), field.getTheSetMethod());
        }
        method.invoke(parentObj, new Object[] { destFieldValue });
      } else {
        writeIndexedValue(pd, parentObj, destFieldValue);
      }
    }
  }

  protected PropertyDescriptor[] getHierarchy(Object obj) {
    return reflectionUtils.getDeepFieldHierarchy(obj.getClass(), field.getName());
  }

  public String getReadMethodName(Class clazz) {
    Method readMethod;
    try {
      readMethod = getReadMethod(clazz);
    } catch (MappingException e) {
      log.debug("Can't find read method for class:" + clazz + " and field:" + field.getName());
      return "NoReadMethodFound";
    }
    return readMethod.getName();
  }

  public String getWriteMethodName(Class clazz) {
    return getWriteMethod(clazz).getName();
  }

  public Method getReadMethod(Class bean) {
    Method readMethod;
    if (field.getTheGetMethod() == null) {
      readMethod = getReadMethod(bean, field.getName());
    } else {
      try {
        readMethod = reflectionUtils.findAMethod(bean, field.getTheGetMethod());
      } catch (Exception e) {
        throw new MappingException(e);
      }
    }
    return readMethod;
  }

  public Method getWriteMethod(Class clazz) {
    Method writeMethod;
    if (field.getTheSetMethod() == null || field.getName().indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) > 0) {
      writeMethod = getWriteMethod(clazz, field.getName());
    } else {
      try {
        writeMethod = reflectionUtils.findAMethod(clazz, field.getTheSetMethod());
      } catch (Exception e) {
        throw new MappingException(e);
      }
    }
    return writeMethod;
  }

  protected void writeIndexedValue(PropertyDescriptor pd, Object destObj, Object destFieldValue)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
      NoSuchFieldException {
    int index = field.getIndex();
    Object o = null;
    if (pd != null) {
      o = pd.getReadMethod().invoke(destObj, null);
    } else {
      o = getReadMethod(destObj.getClass()).invoke(destObj, null);
    }
    if (o == null) {
      Class returnType = null;
      if (pd != null) {
        returnType = pd.getReadMethod().getReturnType(); 
      } else {
        returnType = getPropertyType(destObj.getClass());
      }
      if (returnType.isArray()) {
        o = Array.newInstance(returnType.getComponentType(), 0);
      } else if (collectionUtils.isSet(returnType)) {
        o = new HashSet();
      } else { // default
        o = new ArrayList();
      }
    }
    if (o instanceof Collection) {
      Collection newCollection;
      if (o instanceof Set) {
        newCollection = new HashSet();
      } else {
        newCollection = new ArrayList();
      }

      Collection c = (Collection) o;
      Iterator i = c.iterator();
      int x = 0;
      while (i.hasNext()) {
        if (x != index) {
          newCollection.add(i.next());
        } else {
          newCollection.add(destFieldValue);
        }
        x++;
      }
      if (newCollection.size() <= index) {
        while (newCollection.size() < index) {
          newCollection.add(null);
        }
        newCollection.add(destFieldValue);
      }
      if (pd != null) {
        pd.getWriteMethod().invoke(destObj, new Object[] { newCollection });
      } else {
        getWriteMethod(destObj.getClass()).invoke(destObj, new Object[] { newCollection });
      }
    } else if (o.getClass().isArray()) {
      Object[] objs = (Object[]) o;
      Object[] x = (Object[]) Array.newInstance(objs.getClass().getComponentType(),
          objs.length > index ? objs.length + 1 : index + 1);
      for (int i = 0; i < objs.length; i++) {
        x[i] = objs[i];
      }
      x[index] = destFieldValue;

      if (pd == null) {
        getWriteMethod(destObj.getClass()).invoke(destObj, new Object[] { x });
      } else {
        pd.getWriteMethod().invoke(destObj, new Object[] { x });
      }
    }
  }

}