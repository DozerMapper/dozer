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
package net.sf.dozer.util.mapping.factories;

import net.sf.dozer.util.mapping.BeanFactoryIF;

import java.lang.reflect.Method;

/**
 * @author tierney.matt
 */
public abstract class BaseSampleBeanFactory implements BeanFactoryIF {

  protected static void setCreatedByFactoryName(Object target, String name) {
    try {
      Method method = target.getClass().getMethod("setCreatedByFactoryName", new Class[] {String.class});
      method.invoke(target, new Object[]{name});
    } catch (Exception e) {
      //this object is only used for unit testing so do a catch all for ease of use
    }
  }
}
