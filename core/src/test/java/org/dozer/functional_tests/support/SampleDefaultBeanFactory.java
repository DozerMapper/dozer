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
package org.dozer.functional_tests.support;

import org.dozer.config.BeanContainer;

/**
 * @author tierney.matt
 */
public class SampleDefaultBeanFactory extends BaseSampleBeanFactory {

  public Object createBean(Object srcObj, Class<?> srcObjClass, String id, BeanContainer beanContainer) {
    try {
      Class<?> destClass = Class.forName(id);
      Object rvalue = destClass.newInstance();
      // just for unit testing. need something to indicate that it was created by the factory method
      setCreatedByFactoryName(rvalue, SampleDefaultBeanFactory.class.getName());
      return rvalue;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
