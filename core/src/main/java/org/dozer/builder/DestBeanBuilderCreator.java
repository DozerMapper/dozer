/*
 * Copyright 2005-2012 the original author or authors.
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
package org.dozer.builder;

import org.dozer.BeanBuilder;
import org.dozer.factory.BeanCreationDirective;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Spikhalskiy
 */
public class DestBeanBuilderCreator {

  // order in this collection determines resolving priority
  static final List<BeanBuilderCreationStrategy> pluggedStrategies = new ArrayList<BeanBuilderCreationStrategy>();

  private DestBeanBuilderCreator() {
  }

  public static BeanBuilder create(BeanCreationDirective directive) {
    //TODO Spikhalskiy make copy on write for thread safe
    for (BeanBuilderCreationStrategy strategy : pluggedStrategies) {
      if (strategy.isApplicable(directive)) {
        return strategy.create(directive);
      }
    }

    return null;
  }

  public static void addPluggedStrategy(BeanBuilderCreationStrategy beanBuilderCreationStrategy) {
    pluggedStrategies.add(beanBuilderCreationStrategy);
  }
}
