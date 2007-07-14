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
package net.sf.dozer.util.mapping.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.dozer.util.mapping.fieldmap.HintContainer;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Internal class that provides a various reflection utilities(specific to Dozer requirements) used throughout the code
 * base. Not intended for direct use by application code.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class ReflectionUtils {

  public static PropertyDescriptor findPropertyDescriptor(Class objectClass, String fieldName, HintContainer deepIndexHintContainer) {
    PropertyDescriptor result = null;

    if (fieldName.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) >= 0) {
      DeepHierarchyElement[] hierarchy = getDeepFieldHierarchy(objectClass, fieldName, deepIndexHintContainer);
      result = hierarchy[hierarchy.length - 1].getPropDescriptor();
    } else {
      PropertyDescriptor[] descriptors = getPropertyDescriptors(objectClass);

      if (descriptors != null) {
        int size = descriptors.length;
        for (int i = 0; i < size; i++) {
          if (fieldName.equalsIgnoreCase(descriptors[i].getName())) {
            result = descriptors[i];
            break;
          }
        }
      }
    }

    return result;
  }

  public static DeepHierarchyElement[] getDeepFieldHierarchy(Class parentClass, String field, HintContainer deepIndexHintContainer) {
    if (field.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) < 0) {
      MappingUtils.throwMappingException("Field does not contain deep field delimitor");
    }

    StringTokenizer toks = new StringTokenizer(field, MapperConstants.DEEP_FIELD_DELIMITOR);
    Class latestClass = parentClass;
    DeepHierarchyElement[] hierarchy = new DeepHierarchyElement[toks.countTokens()];
    int index = 0;
    int hintIndex = 0;    
    while (toks.hasMoreTokens()) {
      String aFieldName = toks.nextToken();
      String theFieldName = aFieldName;
      int collectionIndex = -1;

      if (aFieldName.indexOf("[") > 0) {
        theFieldName = aFieldName.substring(0, aFieldName.indexOf("["));
        collectionIndex = Integer.parseInt(aFieldName.substring(aFieldName.indexOf("[") + 1, aFieldName.indexOf("]")));
      }

      PropertyDescriptor propDescriptor = findPropertyDescriptor(latestClass, theFieldName, deepIndexHintContainer);
      DeepHierarchyElement r = new DeepHierarchyElement(propDescriptor, collectionIndex);

      if (propDescriptor == null) {
        MappingUtils.throwMappingException("Exception occurred determining deep field hierarchy for Class --> "
            + parentClass.getName() + ", Field --> " + field + ".  Unable to determine property descriptor for Class --> "
            + latestClass.getName() + ", Field Name: " + aFieldName);
      }

      latestClass = propDescriptor.getPropertyType();
      if (toks.hasMoreTokens()) {
        if (latestClass.isArray()) {
          latestClass = latestClass.getComponentType();
        } else if (Collection.class.isAssignableFrom(latestClass)) {
          if (deepIndexHintContainer == null) {
            MappingUtils
                .throwMappingException("Hint(s) not specified.  Hint(s) must be specified for deep mapping with indexed field(s)");
          }
          latestClass = (Class) deepIndexHintContainer.getHint(hintIndex);
          hintIndex += 1;
        }
      }
      hierarchy[index++] = r;
    }

    return hierarchy;
  }

  public static Method getMethod(Object obj, String methodName) {
    return getMethod(obj.getClass(), methodName);
  }

  public static Method getMethod(Class clazz, String methodName) {
    Method result = findMethod(clazz, methodName);
    if (result == null) {
      MappingUtils.throwMappingException("No method found for class:" + clazz + " and method name:" + methodName);
    }
    return result;
  }

  private static Method findMethod(Class clazz, String methodName) {
    Method[] methods = clazz.getMethods();
    Method resultMethod = null;
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      if (method.getName().equals(methodName)) {
        resultMethod = method;
      }
    }
    return resultMethod;
  }

  public static Method findAMethod(Class parentDestClass, String methodName) throws NoSuchMethodException {
    // TODO USE HELPER from bean utils to find method w/ params
    StringTokenizer tokenizer = new StringTokenizer(methodName, "(");
    String m = tokenizer.nextToken();
    // If tokenizer has more elements, it mean that parameters may have been specified
    if (tokenizer.hasMoreElements()) {
      StringTokenizer tokens = new StringTokenizer(tokenizer.nextToken(), ")");
      String params = (tokens.hasMoreTokens() ? tokens.nextToken() : null);
      return findMethodWithParam(parentDestClass, m, params);
    }
    return findMethod(parentDestClass, methodName);
  }

  private static Method findMethodWithParam(Class parentDestClass, String methodName, String params) throws NoSuchMethodException {
    // TODO USE HELPER from bean utils to find method w/ params
    List list = new ArrayList();
    if (params != null) {
      StringTokenizer tokenizer = new StringTokenizer(params, ",");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        list.add(MappingUtils.loadClass(token));
      }
    }
    return getMethod(parentDestClass, methodName, (Class[]) list.toArray(new Class[list.size()]));
  }

  protected static PropertyDescriptor[] getPropertyDescriptors(Class objectClass) {
    // If the class is an interface, use custom method to get all prop descriptors in the inheritance hierarchy.
    // PropertyUtils.getPropertyDescriptors() does not work correctly for interface inheritance. It finds props in the
    // actual interface ok, but does not find props in the inheritance hierarchy.
    if (objectClass.isInterface()) {
      return getInterfacePropertyDescriptors(objectClass);
    } else {
      return PropertyUtils.getPropertyDescriptors(objectClass);
    }
  }

  private static PropertyDescriptor[] getInterfacePropertyDescriptors(Class interfaceClass) {
    List propDescriptors = new ArrayList();
    // Add prop descriptors for interface passed in
    propDescriptors.addAll(Arrays.asList(PropertyUtils.getPropertyDescriptors(interfaceClass)));

    // Look for interface inheritance. If super interfaces are found, recurse up the hierarchy tree and add prop
    // descriptors for each interface found.
    // PropertyUtils.getPropertyDescriptors() does not correctly walk the inheritance hierarchy for interfaces.
    Class[] interfaces = interfaceClass.getInterfaces();
    if (interfaces != null) {
      for (int i = 0; i < interfaces.length; i++) {
        Class superInterfaceClass = interfaces[i];
        propDescriptors.addAll(Arrays.asList(getInterfacePropertyDescriptors(superInterfaceClass)));
      }
    }
    return (PropertyDescriptor[]) propDescriptors.toArray(new PropertyDescriptor[propDescriptors.size()]);
  }

  public static Field getFieldFromBean(Class clazz, String fieldName) throws NoSuchFieldException {
    try {
      return clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      if (clazz.getSuperclass() != null) {
        return getFieldFromBean(clazz.getSuperclass(), fieldName);
      }
      throw e;
    }
  }

  public static Object invoke(Method method, Object obj, Object[] args) {
    Object result = null;
    try {
      result = method.invoke(obj, args);
    } catch (IllegalArgumentException e) {
      MappingUtils.throwMappingException(e);
    } catch (IllegalAccessException e) {
      MappingUtils.throwMappingException(e);
    } catch (InvocationTargetException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }
  
  public static Method getMethod(Class clazz, String name, Class[] parameterTypes) throws NoSuchMethodException {
    return clazz.getMethod(name, parameterTypes);
  }

  public static Object newInstance(Class clazz) {
    Object result = null;
    try {
      result = clazz.newInstance();
    } catch (InstantiationException e) {
      MappingUtils.throwMappingException(e);
    } catch (IllegalAccessException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

}