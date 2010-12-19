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
package org.dozer.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.MappingException;
import org.dozer.fieldmap.HintContainer;
import org.dozer.propertydescriptor.DeepHierarchyElement;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Internal class that provides a various reflection utilities(specific to Dozer requirements) used throughout the code
 * base. Not intended for direct use by application code.
 *
 * @author tierney.matt
 * @author garsombke.franz
 */
public final class ReflectionUtils {

  private static final String IAE_MESSAGE = "argument type mismatch";

  private ReflectionUtils() {}

  public static PropertyDescriptor findPropertyDescriptor(Class<?> objectClass, String fieldName, HintContainer deepIndexHintContainer) {
    PropertyDescriptor result = null;
    if (MappingUtils.isDeepMapping(fieldName)) {
      DeepHierarchyElement[] hierarchy = getDeepFieldHierarchy(objectClass, fieldName, deepIndexHintContainer);
      result = hierarchy[hierarchy.length - 1].getPropDescriptor();
    } else {
      PropertyDescriptor[] descriptors = getPropertyDescriptors(objectClass);

      if (descriptors != null) {
        int size = descriptors.length;
        for (int i = 0; i < size; i++) {

          /*
            Bugfix #2826468.
            if object class has methods, f.e, getValue() and getValue(int index) in this case
            could happen that this field couldn't be mapped, because getValue(int index) becomes first
            and PropertyDescriptor.getReadMethod() returns null. We need to exclude IndexedPropertyDescriptor from
            search. At this time dozer dosen't support mappings from indexed fields from POJO.

            See KnownFailures.testIndexedGetFailure()
          */
          // TODO Disables for now as it breaks indexed array mapping
//          if (descriptors[i] instanceof IndexedPropertyDescriptor) {
//            continue;
//          }

          String propertyName = descriptors[i].getName();
          if (fieldName.equals(propertyName)) {
            return descriptors[i];
          }

          if (fieldName.equalsIgnoreCase(propertyName)) {
            result = descriptors[i];
          }
        }
      }
    }

    return result;
  }

  public static DeepHierarchyElement[] getDeepFieldHierarchy(Class<?> parentClass, String field, HintContainer deepIndexHintContainer) {
    if (!MappingUtils.isDeepMapping(field)) {
      MappingUtils.throwMappingException("Field does not contain deep field delimitor");
    }

    StringTokenizer toks = new StringTokenizer(field, DozerConstants.DEEP_FIELD_DELIMITER);
    Class<?> latestClass = parentClass;
    DeepHierarchyElement[] hierarchy = new DeepHierarchyElement[toks.countTokens()];
    int index = 0;
    int hintIndex = 0;
    while (toks.hasMoreTokens()) {
      String aFieldName = toks.nextToken();
      String theFieldName = aFieldName;
      int collectionIndex = -1;

      if (aFieldName.contains("[")) {
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
          Class<?> genericType = determineGenericsType(propDescriptor);

          if (genericType == null && deepIndexHintContainer == null) {
            MappingUtils
                .throwMappingException("Hint(s) or Generics not specified.  Hint(s) or Generics must be specified for deep mapping with indexed field(s). Exception occurred determining deep field hierarchy for Class --> "
                    + parentClass.getName()
                    + ", Field --> "
                    + field
                    + ".  Unable to determine property descriptor for Class --> "
                    + latestClass.getName() + ", Field Name: " + aFieldName);
          }
          if (genericType != null) {
            latestClass = genericType;
          } else {
            latestClass = deepIndexHintContainer.getHint(hintIndex);
            hintIndex += 1;
          }
        }
      }
      hierarchy[index++] = r;
    }

    return hierarchy;
  }

  public static Method getMethod(Object obj, String methodName) {
    return getMethod(obj.getClass(), methodName);
  }

  public static Method getMethod(Class<?> clazz, String methodName) {
    Method result = findMethod(clazz, methodName);
    if (result == null) {
      MappingUtils.throwMappingException("No method found for class:" + clazz + " and method name:" + methodName);
    }
    return result;
  }

  private static Method findMethod(Class<?> clazz, String methodName) {
    Method[] methods = clazz.getMethods();
    Method result = null;
    for (Method method : methods) {
      if (method.getName().equals(methodName)) {
        result = method;
      }
    }
    return result;
  }

  public static Method findAMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
    StringTokenizer tokenizer = new StringTokenizer(methodName, "(");
    String m = tokenizer.nextToken();
    Method result;
    // If tokenizer has more elements, it mean that parameters may have been specified
    if (tokenizer.hasMoreElements()) {
      StringTokenizer tokens = new StringTokenizer(tokenizer.nextToken(), ")");
      String params = (tokens.hasMoreTokens() ? tokens.nextToken() : null);
      result = findMethodWithParam(clazz, m, params);
    } else {
      result = findMethod(clazz, methodName);
    }
    if (result == null) {
      throw new NoSuchMethodException(clazz.getName() + "." + methodName);
    }
    return result;
  }

  private static Method findMethodWithParam(Class<?> parentDestClass, String methodName, String params) throws NoSuchMethodException {
    List<Class<?>> list = new ArrayList<Class<?>>();
    if (params != null) {
      StringTokenizer tokenizer = new StringTokenizer(params, ",");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        list.add(MappingUtils.loadClass(token));
      }
    }
    return getMethod(parentDestClass, methodName, list.toArray(new Class[list.size()]));
  }

  public static PropertyDescriptor[] getPropertyDescriptors(Class<?> objectClass) {
    // If the class is an interface, use custom method to get all prop descriptors in the inheritance hierarchy.
    // PropertyUtils.getPropertyDescriptors() does not work correctly for interface inheritance. It finds props in the
    // actual interface ok, but does not find props in the inheritance hierarchy.
    if (objectClass.isInterface()) {
      return getInterfacePropertyDescriptors(objectClass);
    } else {
      return PropertyUtils.getPropertyDescriptors(objectClass);
    }
  }

  static PropertyDescriptor[] getInterfacePropertyDescriptors(Class<?> interfaceClass) {
    List<PropertyDescriptor> propDescriptors = new ArrayList<PropertyDescriptor>();
    // Add prop descriptors for interface passed in
    propDescriptors.addAll(Arrays.asList(PropertyUtils.getPropertyDescriptors(interfaceClass)));

    // Look for interface inheritance. If super interfaces are found, recurse up the hierarchy tree and add prop
    // descriptors for each interface found.
    // PropertyUtils.getPropertyDescriptors() does not correctly walk the inheritance hierarchy for interfaces.
    Class<?>[] interfaces = interfaceClass.getInterfaces();
    if (interfaces != null) {
      for (Class<?> superInterfaceClass : interfaces) {
        List<PropertyDescriptor> superInterfacePropertyDescriptors = Arrays.asList(getInterfacePropertyDescriptors(superInterfaceClass));
        /*
         * #1814758 
         * Check for existing descriptor with the same name to prevent 2 property descriptors with the same name being added
         * to the result list.  This caused issues when getter and setter of an attribute on different interfaces in 
         * an inheritance hierarchy
         */
        for (PropertyDescriptor superPropDescriptor : superInterfacePropertyDescriptors) {
          PropertyDescriptor existingPropDescriptor = findPropDescriptorByName(propDescriptors, superPropDescriptor.getName());
          if (existingPropDescriptor == null) {
            propDescriptors.add(superPropDescriptor);
          } else {
            try {
              if (existingPropDescriptor.getReadMethod() == null) {
                existingPropDescriptor.setReadMethod(superPropDescriptor.getReadMethod());
              }
              if (existingPropDescriptor.getWriteMethod() == null) {
                existingPropDescriptor.setWriteMethod(superPropDescriptor.getWriteMethod());
              }
            } catch (IntrospectionException e) {
              throw new MappingException(e);
            }

          }
        }
      }
    }
    return propDescriptors.toArray(new PropertyDescriptor[propDescriptors.size()]);
  }

  private static PropertyDescriptor findPropDescriptorByName(List<PropertyDescriptor> propDescriptors, String name) {
    PropertyDescriptor result = null;
    for (PropertyDescriptor pd : propDescriptors) {
      if (pd.getName().equals(name)) {
        result = pd;
        break;
      }
    }
    return result;
  }

  public static Field getFieldFromBean(Class<?> clazz, String fieldName)  {
    return getFieldFromBean(clazz, fieldName, clazz);
  }

  private static Field getFieldFromBean(Class<?> clazz, String fieldName, final Class<?> originalClass)  {
    try {
      Field field = clazz.getDeclaredField(fieldName);
      // Allow access to private instance var's that dont have public setter.
      field.setAccessible(true);
      return field;
    } catch (NoSuchFieldException e) {
      if (clazz.getSuperclass() != null) {
        return getFieldFromBean(clazz.getSuperclass(), fieldName, originalClass);
      }
      throw new MappingException("No such field found " + originalClass.getName() + "." + fieldName, e);
    }
  }

  public static Object invoke(Method method, Object obj, Object[] args) {
    Object result = null;
    try {
      result = method.invoke(obj, args);
    } catch (IllegalArgumentException e) {

      if (e.getMessage().equals(IAE_MESSAGE)) {
        MappingUtils.throwMappingException(prepareExceptionMessage(method, args), e);
      }
      MappingUtils.throwMappingException(e);
    } catch (IllegalAccessException e) {
      MappingUtils.throwMappingException(e);
    } catch (InvocationTargetException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

  private static String prepareExceptionMessage(Method method, Object[] args) {
    StringBuffer message = new StringBuffer("Illegal object type for the method '" + method.getName() + "'. \n ");
    message.append("Expected types: \n");
    for (Class<?> type : method.getParameterTypes()) {
      message.append(type.getName());
    }
    message.append("\n Actual types: \n");
    for (Object param: args) {
      message.append(param.getClass().getName());
    }
    return message.toString();
  }

  public static Method getMethod(Class<?> clazz, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
    return clazz.getMethod(name, parameterTypes);
  }

  public static <T> T newInstance (Class<T> clazz) {
    T result = null;
    try {
      result = clazz.newInstance();
    } catch (InstantiationException e) {
      MappingUtils.throwMappingException(e);
    } catch (IllegalAccessException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

  public static Class<?> determineGenericsType(PropertyDescriptor propDescriptor) {
    Class<?> result = null;
    //Try getter and setter to determine the Generics type in case one does not exist
    if (propDescriptor.getWriteMethod() != null) {
      result = determineGenericsType(propDescriptor.getWriteMethod(), false);
    }

    if (result == null && propDescriptor.getReadMethod() != null) {
      result = determineGenericsType(propDescriptor.getReadMethod(), true);
    }

    return result;
  }

  public static Class<?> determineGenericsType(Method method, boolean isReadMethod) {
    Class<?> result = null;
    if (isReadMethod) {
      Type parameterType = method.getGenericReturnType();
      result = determineGenericsType(parameterType);
    } else {
      Type[] parameterTypes = method.getGenericParameterTypes();
      if (parameterTypes != null) {
        result = determineGenericsType(parameterTypes[0]);
      }
    }

    return result;
  }

  public static Class<?> determineGenericsType(Type type) {
    Class<?> result = null;
    if (type != null && ParameterizedType.class.isAssignableFrom(type.getClass())) {
      Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
      if (genericType != null) {
        result = (Class<?>) genericType;
      }
    }
    return result;
  }

}