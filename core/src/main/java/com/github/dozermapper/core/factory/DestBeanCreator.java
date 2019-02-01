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
package com.github.dozermapper.core.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.config.BeanContainer;

/**
 * Internal class that contains the logic used to create a new instance of the destination object being mapped. Performs
 * various checks to determine how the destination object instance is created. Only intended for internal use.
 */
public final class DestBeanCreator {

    static final List<BeanCreationStrategy> pluggedStrategies = new ArrayList<>();

    // order in this collection determines resolving priority
    private final BeanCreationStrategy[] availableStrategies;
    private final ConstructionStrategies constructionStrategies;
    private final BeanContainer beanContainer;

    public DestBeanCreator(BeanContainer beanContainer) {
        this.constructionStrategies = new ConstructionStrategies(beanContainer);
        this.beanContainer = beanContainer;
        this.availableStrategies = new BeanCreationStrategy[] {
                this.constructionStrategies.byNoArgObjectConstructor(),
                this.constructionStrategies.byCreateMethod(),
                this.constructionStrategies.byGetInstance(),
                this.constructionStrategies.xmlGregorianCalendar(),
                this.constructionStrategies.byInterface(),
                this.constructionStrategies.xmlBeansBased(),
                this.constructionStrategies.jaxbBeansBased(),
                this.constructionStrategies.byFactory(),
                this.constructionStrategies.byConstructor()
        };
    }

    public <T> T create(Class<T> targetClass) {
        return (T)create(targetClass, null);
    }

    public Object create(Class<?> targetClass, Class<?> alternateClass) {
        return create(new BeanCreationDirective(null, null, targetClass, alternateClass, null, null, null, null));
    }

    public Object create(BeanCreationDirective directive) {
        Object result = applyStrategies(directive, pluggedStrategies);
        if (result == null) {
            result = applyStrategies(directive, Arrays.asList(availableStrategies));
        }

        return result;
    }

    private Object applyStrategies(BeanCreationDirective directive, List<BeanCreationStrategy> strategies) {
        // TODO create method lookup by annotation/convention
        // TODO Cache ConstructionStrategy (reuse caching infrastructure)
        // TODO Check resulting type in each method
        // TODO Directive toString()
        // TODO review and document

        for (BeanCreationStrategy strategy : new CopyOnWriteArrayList<>(strategies)) {
            if (strategy.isApplicable(directive)) {
                return strategy.create(directive);
            }
        }
        return null;
    }

    public void setStoredFactories(Map<String, BeanFactory> factories) {
        constructionStrategies.byFactory().setStoredFactories(factories);
    }

    public void addPluggedStrategy(BeanCreationStrategy strategy) {
        pluggedStrategies.add(strategy);
    }

}
