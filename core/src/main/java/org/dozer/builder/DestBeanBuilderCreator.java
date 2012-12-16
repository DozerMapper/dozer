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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Dmitry Spikhalskiy
 */
public class DestBeanBuilderCreator {

  //elements of this collections should have very specific isApplicable method to avoid application to class,
  //which should be processed by another builder
  static final List<BeanBuilderCreationStrategy> pluggedStrategies = new ArrayList<BeanBuilderCreationStrategy>();

  private DestBeanBuilderCreator() {
  }

  public static BeanBuilder create(BeanCreationDirective directive) {
    for (BeanBuilderCreationStrategy strategy : new CopyOnWriteArrayList<BeanBuilderCreationStrategy>(pluggedStrategies)) {
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
