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
import org.dozer.vo.InsideTestObject;
import org.dozer.vo.InsideTestObjectPrime;

/**
 * @author tierney.matt
 */
public class SampleCustomBeanFactory2 extends BaseSampleBeanFactory {

  public Object createBean(Object srcObj, Class<?> srcObjClass, String id, BeanContainer beanContainer) {
    // example of using all input objects. These params are passed in from the
    // dozer mapping processor.

    if (!id.equals("someBeanId")) {
      throw new IllegalArgumentException("Unsupported bean id: " + id);
    }

    if (srcObj == null || srcObjClass == null) {
      throw new IllegalArgumentException("Source Object and Source Object Class params"
          + " should have been provided by the Dozer mapping engine");
    }

    InsideTestObjectPrime result = new InsideTestObjectPrime();
    result.setLabelPrime(((InsideTestObject) srcObj).getLabel());
    // Setting the following field so that we have something
    // to assert on that indicates it was created by the factory
    setCreatedByFactoryName(result, SampleCustomBeanFactory2.class.getName());

    return result;
  }

}
