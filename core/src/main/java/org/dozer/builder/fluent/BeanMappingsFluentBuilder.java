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
package org.dozer.builder.fluent;

import java.util.Arrays;
import java.util.List;

import org.dozer.builder.BeanMappingsBuilder;
import org.dozer.builder.model.elengine.ELMappingsDefinition;
import org.dozer.builder.model.jaxb.ConfigurationDefinition;
import org.dozer.builder.model.jaxb.MappingDefinition;
import org.dozer.builder.model.jaxb.MappingsDefinition;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingFileData;
import org.dozer.config.BeanContainer;
import org.dozer.el.ELEngine;
import org.dozer.factory.DestBeanCreator;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;

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
