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
package net.sf.dozer.util.mapping.factory;

import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.BeanFactoryIF;
import net.sf.dozer.util.mapping.MappingException;

/**
 * Public custom bean factory that can be used by applition code when mapping XMLBean data objects
 *  
 * @author garsombke.franz
 */
public class XMLBeanFactory implements BeanFactoryIF {
  private static Class[] emptyArglist = new Class[0];
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
  public Object createBean(Object srcObj, Class srcObjClass, String beanId) {
    Object result = null;
    try {
      Class destClass;
      destClass = Class.forName(beanId);
      Class[] innerClasses = destClass.getClasses();
      Class factory = null;
      for (int i = 0; i < innerClasses.length; i++) {
        if (innerClasses[i].getName().endsWith("Factory")) {
          factory = innerClasses[i];
        }
      }
      if (factory == null) {
        throw new MappingException("Factory class of Bean of type " + beanId + " not found.");
      }
      Method newInstance = factory.getMethod("newInstance", emptyArglist);
      result = newInstance.invoke(null, emptyArglist);
    } catch (ClassNotFoundException e) {
      throw new MappingException("Bean of type " + beanId + " not found.", e);
    } catch (Exception e) {
      throw new MappingException(e);
    }
    return result;
  }
}