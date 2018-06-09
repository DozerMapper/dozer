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

import com.github.dozermapper.core.builder.model.jaxb.FieldDefinition;
import com.github.dozermapper.core.builder.model.jaxb.MappingDefinition;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.ELEngine;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

import org.apache.commons.lang3.StringUtils;

/**
 * {@inheritDoc}
 */
public class ELFieldDefinition extends FieldDefinition {

    private final ELEngine elEngine;

    public ELFieldDefinition(ELEngine elEngine, FieldDefinition copy) {
        this(elEngine, (MappingDefinition)null);

        if (copy != null) {
            this.a = copy.getA();
            this.b = copy.getB();
            this.aHint = copy.getAHint();
            this.bHint = copy.getBHint();
            this.aDeepIndexHint = copy.getADeepIndexHint();
            this.bDeepIndexHint = copy.getBDeepIndexHint();
            this.relationshipType = copy.getRelationshipType();
            this.removeOrphans = copy.getRemoveOrphans();
            this.type = copy.getType();
            this.mapId = copy.getMapId();
            this.copyByReference = copy.getCopyByReference();
            this.customConverter = copy.getCustomConverter();
            this.customConverterId = copy.getCustomConverterId();
            this.customConverterParam = copy.getCustomConverterParam();
        }
    }

    public ELFieldDefinition(ELEngine elEngine, MappingDefinition parent) {
        super(parent);

        this.elEngine = elEngine;
    }

    @Override
    public FieldMap build(ClassMap classMap, BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        if (!StringUtils.isBlank(customConverter)) {
            setCustomConverter(elEngine.resolve(customConverter));
        }

        if (!StringUtils.isBlank(aHint)) {
            setAHint(elEngine.resolve(aHint));
        }

        if (!StringUtils.isBlank(bHint)) {
            setBHint(elEngine.resolve(bHint));
        }

        if (!StringUtils.isBlank(aDeepIndexHint)) {
            setADeepIndexHint(elEngine.resolve(aDeepIndexHint));
        }

        if (!StringUtils.isBlank(bDeepIndexHint)) {
            setBDeepIndexHint(elEngine.resolve(bDeepIndexHint));
        }

        return super.build(classMap, beanContainer, destBeanCreator, propertyDescriptorFactory);
    }
}

