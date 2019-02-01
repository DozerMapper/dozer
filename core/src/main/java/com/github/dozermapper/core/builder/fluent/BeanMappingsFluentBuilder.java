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
package com.github.dozermapper.core.builder.fluent;

import java.util.Arrays;
import java.util.List;

import com.github.dozermapper.core.builder.BeanMappingsBuilder;
import com.github.dozermapper.core.builder.model.elengine.ELMappingsDefinition;
import com.github.dozermapper.core.builder.model.jaxb.ConfigurationDefinition;
import com.github.dozermapper.core.builder.model.jaxb.MappingDefinition;
import com.github.dozermapper.core.builder.model.jaxb.MappingsDefinition;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.ELEngine;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

/**
 * Builds a mapping definition based on fluent directives
 */
public abstract class BeanMappingsFluentBuilder implements BeanMappingsBuilder {

    private final MappingsDefinition mappingsDefinition;

    public BeanMappingsFluentBuilder(ELEngine elEngine) {
        this.mappingsDefinition = new ELMappingsDefinition(elEngine);

        configure();
    }

    /**
     * Configure the {@link MappingsDefinition}
     */
    protected abstract void configure();

    protected ConfigurationDefinition configuration() {
        return mappingsDefinition.withConfiguration();
    }

    protected MappingDefinition mapping() {
        return mappingsDefinition.addMapping();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MappingFileData> build(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        Configuration configuration = null;
        if (mappingsDefinition.getConfiguration() != null) {
            configuration = mappingsDefinition.getConfiguration().build(beanContainer);
        }

        MappingFileData data = new MappingFileData();
        data.setConfiguration(configuration);
        data.getClassMaps().addAll(mappingsDefinition.build(configuration, beanContainer, destBeanCreator, propertyDescriptorFactory));

        return Arrays.asList(data);
    }
}
