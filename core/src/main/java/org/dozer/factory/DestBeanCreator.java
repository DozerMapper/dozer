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

import org.dozer.BeanFactory;

import java.util.Map;

/**
 * Internal class that contains the logic used to create a new instance of the destination object being mapped. Performs
 * various checks to determine how the destination object instance is created. Only intended for internal use.
 *
 * @author tierney.matt
 * @author garsombke.franz
 * @author dmitry.buzdin
 */
public final class DestBeanCreator {

  private static final BeanCreationStrategy byCreateMethod = new ConstructionStrategies.ByCreateMethod();
  private static final BeanCreationStrategy byGetInstance = new ConstructionStrategies.ByGetInstance();
  private static final BeanCreationStrategy byInterface = new ConstructionStrategies.ByInterface();
  private static final BeanCreationStrategy xmlBeansBased = new ConstructionStrategies.XMLBeansBased();
  private static final BeanCreationStrategy constructorBased = new ConstructionStrategies.ByConstructor();
  private static final ConstructionStrategies.ByFactory byFactory = new ConstructionStrategies.ByFactory();

  private DestBeanCreator() {
  }

  public static <T> T create(Class<T> targetClass) {
    return (T) create(targetClass, null);
  }

  public static Object create(Class<?> targetClass, Class<?> alternateClass) {
    return create(new BeanCreationDirective(null, null, targetClass, alternateClass, null, null, null));
  }

  public static Object create(BeanCreationDirective directive) {

    // TODO create method lookup by annotation/convention
    // TODO support of fully qualified static create methods
    // TODO Cache ConstructionStrategy (reuse caching infrastructure)
    // TODO Resolve JAXB by XmlType Annotation
    // TODO Check resulting type in each method
    // TODO Create prioritized chain
    // TODO Retries through the chain
    // TODO Directive toString()
    // TODO review and document
    // TODO Analyze getInstance usage in JDK
    // TODO Rename this class

    if (byCreateMethod.isApplicable(directive)) {
      return byCreateMethod.create(directive);
    }

    if (byFactory.isApplicable(directive)) {
      return byFactory.create(directive);
    }

    if (byGetInstance.isApplicable(directive)) {
      return byGetInstance.create(directive);
    }

    if (byInterface.isApplicable(directive)) {
      return byInterface.create(directive);
    }

    if (xmlBeansBased.isApplicable(directive)) {
      return xmlBeansBased.create(directive);
    }

    if (constructorBased.isApplicable(directive)) {
      return constructorBased.create(directive);
    }

    return null;
  }


  public static void setStoredFactories(Map<String, BeanFactory> factories) {
    byFactory.setStoredFactories(factories);
  }

}