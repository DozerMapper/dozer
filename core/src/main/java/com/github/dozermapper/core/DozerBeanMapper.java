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
package com.github.dozermapper.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dozermapper.core.builder.DestBeanBuilderCreator;
import com.github.dozermapper.core.cache.CacheManager;
import com.github.dozermapper.core.classmap.ClassMappings;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.classmap.generator.BeanMappingGenerator;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.events.DefaultEventManager;
import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.events.EventManager;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.metadata.DozerMappingMetadata;
import com.github.dozermapper.core.metadata.MappingMetadata;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

/**
 * Public Dozer Mapper implementation. This should be used/defined as a singleton within your application. This class
 * performs several one-time initializations and loads the custom xml mappings, so you will not want to create many
 * instances of it for performance reasons. Typically a system will only have one DozerBeanMapper instance per VM. If
 * you are using an IOC framework (i.e Spring), define the Mapper as singleton="true". If you are not using an IOC
 * framework, a DozerBeanMapperSingletonWrapper convenience class has been provided in the Dozer jar.
 * <p>
 * It is technically possible to have multiple DozerBeanMapper instances initialized, but it will hinder internal
 * performance optimizations such as caching.
 */
public class DozerBeanMapper implements Mapper, MapperModelContext {

    private final BeanContainer beanContainer;

    private final DestBeanCreator destBeanCreator;
    private final DestBeanBuilderCreator destBeanBuilderCreator;
    private final BeanMappingGenerator beanMappingGenerator;
    private final PropertyDescriptorFactory propertyDescriptorFactory;

    private final ClassMappings customMappings;
    private final Configuration globalConfiguration;

    /*
     * Accessible for custom injection
     */
    private final List<String> mappingFiles;
    private final List<CustomConverter> customConverters;
    private final List<EventListener> eventListeners;
    private final Map<String, CustomConverter> customConvertersWithId;
    private CustomFieldMapper customFieldMapper;

    /*
     * Not accessible for injection
     */

    // There are no global caches. Caches are per bean mapper instance
    private final CacheManager cacheManager;
    private EventManager eventManager;

    DozerBeanMapper(List<String> mappingFiles,
                    BeanContainer beanContainer,
                    DestBeanCreator destBeanCreator,
                    DestBeanBuilderCreator destBeanBuilderCreator,
                    BeanMappingGenerator beanMappingGenerator,
                    PropertyDescriptorFactory propertyDescriptorFactory,
                    List<CustomConverter> customConverters,
                    List<MappingFileData> mappingsFileData,
                    List<EventListener> eventListeners,
                    CustomFieldMapper customFieldMapper,
                    Map<String, CustomConverter> customConvertersWithId,
                    ClassMappings customMappings,
                    Configuration globalConfiguration,
                    CacheManager cacheManager) {
        this.beanContainer = beanContainer;
        this.destBeanCreator = destBeanCreator;
        this.destBeanBuilderCreator = destBeanBuilderCreator;
        this.beanMappingGenerator = beanMappingGenerator;
        this.propertyDescriptorFactory = propertyDescriptorFactory;
        this.customConverters = new ArrayList<>(customConverters);
        this.eventListeners = new ArrayList<>(eventListeners);
        this.mappingFiles = new ArrayList<>(mappingFiles);
        this.customFieldMapper = customFieldMapper;
        this.customConvertersWithId = new HashMap<>(customConvertersWithId);
        this.eventManager = new DefaultEventManager(eventListeners);
        this.customMappings = customMappings;
        this.globalConfiguration = globalConfiguration;
        this.cacheManager = cacheManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void map(Object source, Object destination, String mapId) throws MappingException {
        getMappingProcessor().map(source, destination, mapId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException {
        return getMappingProcessor().map(source, destinationClass, mapId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T map(Object source, Class<T> destinationClass) throws MappingException {
        return getMappingProcessor().map(source, destinationClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void map(Object source, Object destination) throws MappingException {
        getMappingProcessor().map(source, destination);
    }

    protected Mapper getMappingProcessor() {
        Mapper processor = new MappingProcessor(customMappings, globalConfiguration, cacheManager, customConverters,
                                                eventManager, customFieldMapper, customConvertersWithId, beanContainer, destBeanCreator, destBeanBuilderCreator,
                                                beanMappingGenerator, propertyDescriptorFactory);

        return processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingMetadata getMappingMetadata() {
        return new DozerMappingMetadata(customMappings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapperModelContext getMapperModelContext() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMappingFiles() {
        return Collections.unmodifiableList(mappingFiles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomConverter> getCustomConverters() {
        return Collections.unmodifiableList(customConverters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, CustomConverter> getCustomConvertersWithId() {
        return Collections.unmodifiableMap(customConvertersWithId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends EventListener> getEventListeners() {
        return Collections.unmodifiableList(eventListeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomFieldMapper getCustomFieldMapper() {
        return customFieldMapper;
    }
}
