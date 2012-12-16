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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Internal class that contains the logic used to create a new instance of the destination object being mapped. Performs
 * various checks to determine how the destination object instance is created. Only intended for internal use.
 *
 * @author tierney.matt
 * @author garsombke.franz
 * @author dmitry.buzdin
 */
public final class DestBeanCreator {

  static final List<BeanCreationStrategy> pluggedStrategies = new ArrayList<BeanCreationStrategy>();

  // order in this collection determines resolving priority
  static final BeanCreationStrategy[] availableStrategies = new BeanCreationStrategy[]{
          ConstructionStrategies.byCreateMethod(),
          ConstructionStrategies.byGetInstance(),
          ConstructionStrategies.xmlGregorianCalendar(),
          ConstructionStrategies.byInterface(),
          ConstructionStrategies.xmlBeansBased(),
          ConstructionStrategies.byFactory(),
          ConstructionStrategies.byConstructor()
  };

  private DestBeanCreator() {
  }

  public static <T> T create(Class<T> targetClass) {
    return (T) create(targetClass, null);
  }

  public static Object create(Class<?> targetClass, Class<?> alternateClass) {
    return create(new BeanCreationDirective(null, null, targetClass, alternateClass, null, null, null));
  }

  public static Object create(BeanCreationDirective directive) {
    Object result = applyStrategies(directive, pluggedStrategies);
    if (result == null) result = applyStrategies(directive, Arrays.asList(availableStrategies));
    return result;
  }

  private static Object applyStrategies(BeanCreationDirective directive, List<BeanCreationStrategy> strategies) {
    // TODO create method lookup by annotation/convention
    // TODO Cache ConstructionStrategy (reuse caching infrastructure)
    // TODO Resolve JAXB by XmlType Annotation
    // TODO Check resulting type in each method
    // TODO Directive toString()
    // TODO review and document

    for (BeanCreationStrategy strategy : new CopyOnWriteArrayList<BeanCreationStrategy>(strategies)) {
      if (strategy.isApplicable(directive)) {
        return strategy.create(directive);
      }
    }
    return null;
  }

  public static void setStoredFactories(Map<String, BeanFactory> factories) {
    ConstructionStrategies.byFactory().setStoredFactories(factories);
  }

  public static void addPluggedStrategy(BeanCreationStrategy strategy) {
    pluggedStrategies.add(strategy);
  }

}