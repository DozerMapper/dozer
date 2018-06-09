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
import com.github.dozermapper.core.builder.model.jaxb.CustomConvertersDefinition;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.converters.CustomConverterDescription;
import com.github.dozermapper.core.el.ELEngine;

import org.apache.commons.lang3.StringUtils;

/**
 * {@inheritDoc}
 */
public class ELConverterTypeDefinition extends ConverterTypeDefinition {

    private final ELEngine elEngine;

    public ELConverterTypeDefinition(ELEngine elEngine, ConverterTypeDefinition copy) {
        this(elEngine, (CustomConvertersDefinition)null);

        if (copy != null) {
            this.classA = new ELClassDefinition(elEngine, copy.getClassA());
            this.classB = new ELClassDefinition(elEngine, copy.getClassB());
            this.type = copy.getType();
        }
    }

    public ELConverterTypeDefinition(ELEngine elEngine, CustomConvertersDefinition parent) {
        super(parent);

        this.elEngine = elEngine;
    }

    @Override
    public ClassDefinition withClassA() {
        ELClassDefinition classA = new ELClassDefinition(elEngine, null, this);
        setClassA(classA);

        return classA;
    }

    @Override
    public ClassDefinition withClassB() {
        ELClassDefinition classB = new ELClassDefinition(elEngine, null, this);
        setClassB(classB);

        return classB;
    }

    @Override
    public CustomConverterDescription build(BeanContainer beanContainer) {
        if (!StringUtils.isBlank(type)) {
            setType(elEngine.resolve(type));
        }

        return super.build(beanContainer);
    }
}

