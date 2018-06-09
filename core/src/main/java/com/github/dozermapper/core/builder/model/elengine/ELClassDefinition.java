/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.builder.model.elengine;

import com.github.dozermapper.core.builder.model.jaxb.ClassDefinition;
import com.github.dozermapper.core.builder.model.jaxb.ConverterTypeDefinition;
import com.github.dozermapper.core.builder.model.jaxb.MappingDefinition;
import com.github.dozermapper.core.classmap.DozerClass;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.ELEngine;

import org.apache.commons.lang3.StringUtils;

/**
 * {@inheritDoc}
 */
public class ELClassDefinition extends ClassDefinition {

    private final ELEngine elEngine;

    public ELClassDefinition(ELEngine elEngine, ClassDefinition copy) {
        this(elEngine, null, null);

        if (copy != null) {
            this.clazz = copy.getClazz();
            this.beanFactory = copy.getBeanFactory();
            this.factoryBeanId = copy.getFactoryBeanId();
            this.mapSetMethod = copy.getMapSetMethod();
            this.mapGetMethod = copy.getMapGetMethod();
            this.createMethod = copy.getCreateMethod();
            this.mapNull = copy.getMapNull();
            this.mapEmptyString = copy.getMapEmptyString();
            this.isAccessible = copy.getIsAccessible();
            this.skipConstructor = copy.getSkipConstructor();
        }
    }

    public ELClassDefinition(ELEngine elEngine, MappingDefinition parentMappingDefinition, ConverterTypeDefinition parentConverterTypeDefinition) {
        super(parentMappingDefinition, parentConverterTypeDefinition);

        this.elEngine = elEngine;
    }

    @Override
    public DozerClass build(BeanContainer beanContainer) {
        if (!StringUtils.isBlank(clazz)) {
            setClazz(elEngine.resolve(clazz));
        }

        if (!StringUtils.isBlank(beanFactory)) {
            setBeanFactory(elEngine.resolve(beanFactory));
        }

        return super.build(beanContainer);
    }
}

