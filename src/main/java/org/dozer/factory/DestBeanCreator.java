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
package org.dozer.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.BeanFactory;
import org.dozer.MappingException;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

/**
 * Internal class that contains the logic used to create a new instance of the destination object being mapped. Performs
 * various checks to determine how the destination object instance is created. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public final class DestBeanCreator {
  
  // only making public temporarily while refactoring. This static data should be relocated.
  // The stored factories don't belong in MappingUtils and need to be relocated
  public static final Map<String, BeanFactory> storedFactories = new ConcurrentHashMap<String, BeanFactory>();


  private DestBeanCreator() {
  }

  private static final Log log = LogFactory.getLog(DestBeanCreator.class);

  public static <T> T create(Class<T> targetClass) {
    return (T) create(targetClass, null);
  }

  public static Object create(Class<?> targetClass, Class<?> alternateClass) {
    return create(null, null, targetClass, alternateClass, null, null, null);
  }

  public static Object create(Object srcObject, Class<?> srcClass, Class<?> targetClass, Class<?> alternateClass, String factoryName,
      String factoryId, String createMethod) {
    Class<?> classToCreate = targetClass != null ? targetClass : alternateClass;

    // If custom create method was specified, see if there are any static createMethods
    if (!MappingUtils.isBlankOrNull(createMethod)) {
      Method method = null;
      try {
        method = ReflectionUtils.getMethod(classToCreate, createMethod, null);
      } catch (NoSuchMethodException e) {
        MappingUtils.throwMappingException(e);
      }
      return ReflectionUtils.invoke(method, null, null);
    }

    // If factory name was specified, create using factory.  Otherwise just create a new instance
    if (!MappingUtils.isBlankOrNull(factoryName)) {
      Object factoryCreatedObj = createFromFactory(srcObject, srcClass, classToCreate, factoryName, factoryId);
      // verify factory returned expected dest object type
      if (!classToCreate.isAssignableFrom(factoryCreatedObj.getClass())) {
        MappingUtils
            .throwMappingException("Custom bean factory did not return correct type of destination data object.  Expected: "
                + classToCreate + ", Actual: " + factoryCreatedObj.getClass());
      }
      return factoryCreatedObj;
    }

    //Typical oject creation
    Object rvalue = null;
    if (MappingUtils.isSupportedMap(classToCreate)) {
      rvalue = new HashMap<Object, Object>();
    } else {
      try {
        rvalue = newInstance(classToCreate);
      } catch (Exception e) {
        if (alternateClass != null) {
          try {
            rvalue = newInstance(alternateClass);
          } catch (MappingException me) {
            if (me.getCause() instanceof NoSuchMethodException) {
              // let's see if it is an XMLBean
              try {
                rvalue = createFromFactory(srcObject, srcClass, classToCreate, "org.dozer.factory.XMLBeanFactory",
                    factoryId);
              } catch (MappingException e1) {
                // well this was just a stab in the dark. log and rethrow the original exception
                log.error("Error trying to use XMLBeanFactory.", e1);
                throw me;
              }
            } else {
              throw me;
            }
          }
        } else {
          MappingUtils.throwMappingException(e);
        }
      }
    }

    return rvalue;
  }

  private static Object createFromFactory(Object srcObject, Class<?> srcObjectClass, Class<?> destClass, String factoryName,
      String factoryBeanId) {

    // By default, use dest object class name for factory bean id
    String beanId = !MappingUtils.isBlankOrNull(factoryBeanId) ? factoryBeanId : destClass.getName();

    BeanFactory factory = storedFactories.get(factoryName);

    if (factory == null) {
      Class<?> factoryClass = MappingUtils.loadClass(factoryName);
      if (!BeanFactory.class.isAssignableFrom(factoryClass)) {
        MappingUtils.throwMappingException("Custom bean factory must implement the BeanFactoryIF interface.");
      }
      factory = (BeanFactory) newInstance(factoryClass);
      // put the created factory in our factory map
      storedFactories.put(factoryName, factory);
    }
    Object rvalue = factory.createBean(srcObject, srcObjectClass, beanId);

    if (log.isDebugEnabled()) {
      log.debug("Bean instance created with custom factory -->" + "\n  Bean Type: " + rvalue.getClass().getName()
              + "\n  Factory Name: " + factoryName);
    }
    return rvalue;
  }

  private static <T> T newInstance(Class<T> clazz) {
    //Create using public or private no-arg constructor
    Constructor<T> constructor = null;
    try {
      constructor = clazz.getDeclaredConstructor(null);
    } catch (SecurityException e) {
      MappingUtils.throwMappingException(e);
    } catch (NoSuchMethodException e) {
      MappingUtils.throwMappingException(e);
    }

    if (constructor == null) {
      MappingUtils.throwMappingException("Could not create a new instance of the dest object: " + clazz
          + ".  Could not find a no-arg constructor for this class.");
    }

    // If private, make it accessible
    if (!constructor.isAccessible()) {
      constructor.setAccessible(true);
    }

    T result = null;
    try {
      result = constructor.newInstance(null);
    } catch (IllegalArgumentException e) {
      MappingUtils.throwMappingException(e);
    } catch (InstantiationException e) {
      MappingUtils.throwMappingException(e);
    } catch (IllegalAccessException e) {
      MappingUtils.throwMappingException(e);
    } catch (InvocationTargetException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

}