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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A provided custom bean factory that can be used by applition code to handle JAXB data objects 
 * 
 * @author Vincent Jassogne
 */
public class JAXBBeanFactory implements BeanFactoryIF {
  private static final Log log = LogFactory.getLog(JAXBBeanFactory.class);

  /**
   * Creat a bean implementation of a jaxb interface.
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
    if (log.isDebugEnabled()) {
      log.debug("createBean(Object, Class, String) - start [" + beanId +"]");
    }

    int indexOf = beanId.indexOf('$');
    if (indexOf > 0) {
      beanId = beanId.substring(0, indexOf) + beanId.substring(indexOf + 1);
      if (log.isDebugEnabled()) {
        log.debug("createBean(Object, Class, String) - HAS BEEN CHANGED TO  ["+ beanId + "]");
      }
    }
    try {
      Class objectFactory = Class.forName(beanId.substring(0, beanId.lastIndexOf(".")) + ".ObjectFactory");
      Object factory = objectFactory.newInstance();
      Method method = objectFactory.getMethod("create" + beanId.substring(beanId.lastIndexOf(".") + 1), new Class[] {});
      Object returnObject = method.invoke(factory, new Object[] {});
      if (log.isDebugEnabled()) {
        log.debug("createBean(Object, Class, String) - end ["
            + returnObject.getClass().getName() + "]");
      }
      return returnObject;
    } catch (ClassNotFoundException e) {
      log.error("createBean(Object, Class, String)", e);
        throw new MappingException("Bean of type " + beanId + " not found.", e);
    } catch (Exception e) {
      log.error("createBean(Object, Class, String)", e);
      throw new MappingException(e);
    }
  }
}