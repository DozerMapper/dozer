/*
 * Copyright 2005-2017 Dozer Project
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

import java.lang.reflect.Method;

import org.dozer.BeanFactory;
import org.dozer.config.BeanContainer;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;


/**
 * Public custom bean factory that can be used by application code when mapping XMLBean data objects
 *
 * @author garsombke.franz
 */
public class XMLBeanFactory implements BeanFactory {
  private static final Class<?>[] emptyArgList = new Class[0];
  /**
   * Creat a bean implementation of a xml bean interface.
   *
   * @param srcObj
   *          The source object
   * @param srcObjClass
   *          The source object class
   * @param beanId
   *          the name of the destination interface class
   * @return A implementation of the destination interface
   */
  public Object createBean(Object srcObj, Class<?> srcObjClass, String beanId, BeanContainer beanContainer) {
    Object result;
    Class<?> destClass = MappingUtils.loadClass(beanId, beanContainer);
    Class<?>[] innerClasses = destClass.getClasses();
    Class<?> factory = null;
    for (Class<?> innerClass : innerClasses) {
      if (innerClass.getName().endsWith("Factory")) {
        factory = innerClass;
      }
    }
    if (factory == null) {
      MappingUtils.throwMappingException("Factory class of Bean of type " + beanId + " not found.");
    }
    Method newInstanceMethod = null;
    try {
      newInstanceMethod = ReflectionUtils.getMethod(factory, "newInstance", emptyArgList);
    } catch (NoSuchMethodException e) {
      MappingUtils.throwMappingException(e);
    }
    result = ReflectionUtils.invoke(newInstanceMethod, null, emptyArgList);
    return result;
  }
}
