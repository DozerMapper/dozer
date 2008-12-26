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
package net.sf.dozer.factory;

import java.lang.reflect.Method;

import net.sf.dozer.BeanFactoryIF;
import net.sf.dozer.util.MappingUtils;
import net.sf.dozer.util.ReflectionUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Public custom bean factory that can be used by applition code when mapping JAXB data objects
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
      log.debug("createBean(Object, Class, String) - start [" + beanId + "]");
    }

    int indexOf = beanId.indexOf('$');
    if (indexOf > 0) {
      beanId = beanId.substring(0, indexOf) + beanId.substring(indexOf + 1);
      if (log.isDebugEnabled()) {
        log.debug("createBean(Object, Class, String) - HAS BEEN CHANGED TO  [" + beanId + "]");
      }
    }
    Object result = null;

    Class objectFactory = MappingUtils.loadClass(beanId.substring(0, beanId.lastIndexOf(".")) + ".ObjectFactory");
    Object factory = ReflectionUtils.newInstance(objectFactory);
    Method method = null;
    try {
      method = ReflectionUtils.getMethod(objectFactory, "create" + beanId.substring(beanId.lastIndexOf(".") + 1), new Class[] {});
    } catch (NoSuchMethodException e) {
      MappingUtils.throwMappingException(e);
    }
    Object returnObject = ReflectionUtils.invoke(method, factory, new Object[] {});
    if (log.isDebugEnabled()) {
      log.debug("createBean(Object, Class, String) - end [" + returnObject.getClass().getName() + "]");
    }
    result = returnObject;

    return result;
  }
}