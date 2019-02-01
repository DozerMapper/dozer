/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.dozermapper.core.BeanBuilder;
import com.github.dozermapper.core.factory.BeanCreationDirective;

public final class DestBeanBuilderCreator {

    /**
     * Elements of this collections should have very specific isApplicable method to avoid application to class,
     * which should be processed by another builder
     */
    private final List<BeanBuilderCreationStrategy> pluggedStrategies = new ArrayList<>();

    public DestBeanBuilderCreator() {

    }

    public BeanBuilder create(BeanCreationDirective directive) {
        for (BeanBuilderCreationStrategy strategy : new CopyOnWriteArrayList<>(pluggedStrategies)) {
            if (strategy.isApplicable(directive)) {
                return strategy.create(directive);
            }
        }

        return null;
    }

    public void addPluggedStrategies(Collection<BeanBuilderCreationStrategy> beanBuilderCreationStrategies) {
        pluggedStrategies.addAll(beanBuilderCreationStrategies);
    }
}
