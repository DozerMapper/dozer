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
package com.github.dozermapper.spring;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.CustomFieldMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.io.Resource;

/**
 * {@link FactoryBean} that can be used to create an instance of {@link Mapper}
 */
public class DozerBeanMapperFactoryBean extends ApplicationObjectSupport implements InitializingBean, FactoryBean<Mapper> {

    private CustomFieldMapper customFieldMapper;
    private final List<String> mappingFileUrls = new ArrayList<>(1);
    private final List<CustomConverter> customConverters = new ArrayList<>(0);
    private final List<BeanMappingBuilder> mappingBuilders = new ArrayList<>(0);
    private final List<EventListener> eventListeners = new ArrayList<>(0);
    private final Map<String, BeanFactory> beanFactories = new HashMap<>(0);
    private final Map<String, CustomConverter> customConvertersWithId = new HashMap<>(0);

    private Mapper mapper;

    /**
     * Registers a {@link CustomFieldMapper} for the mapper.
     * <p>
     * By default, no custom field mapper is registered.
     *
     * @param customFieldMapper custom field mapper to be registered for the mapper.
     */
    public void setCustomFieldMapper(CustomFieldMapper customFieldMapper) {
        this.customFieldMapper = customFieldMapper;
    }

    /**
     * Spring resources definition for providing mapping file location.
     * Could be used for loading all mapping files by wildcard definition for example
     * <pre>
     * {@code
     *  <bean class="com.github.dozermapper.core.spring.DozerBeanMapperFactoryBean">
     *      <property name="mappingFiles" value="classpath*:/*.dozer.xml"/>
     *  <\/bean>
     * }
     * </pre>
     *
     * @param mappingFiles Spring resource definition
     * @throws IOException if URL fails to resolve
     */
    public void setMappingFiles(Resource[] mappingFiles) throws IOException {
        if (mappingFiles != null && mappingFiles.length > 0) {
            for (Resource mappingFile : mappingFiles) {
                URL url = mappingFile.getURL();
                mappingFileUrls.add(url.toString());
            }
        }
    }

    /**
     * Registers a collection of {@link CustomConverter} for the mapper.
     * <p>
     * By default, no custom converters are used by generated mapper.
     *
     * @param customConverters converters to be registered.
     */
    public void setCustomConverters(List<CustomConverter> customConverters) {
        this.customConverters.addAll(customConverters);
    }

    /**
     * Registers a {@link BeanMappingBuilder} for the mapper.
     * <p>
     * By default, no API builders are registered.
     *
     * @param mappingBuilders mapping builders to be registered for the mapper.
     */
    public void setMappingBuilders(List<BeanMappingBuilder> mappingBuilders) {
        this.mappingBuilders.addAll(mappingBuilders);
    }

    /**
     * Registers a {@link EventListener} for the mapper.
     * <p>
     * By default, no listeners are registered.
     *
     * @param eventListeners listeners to be registered for the mapper.
     */
    public void setEventListeners(List<EventListener> eventListeners) {
        this.eventListeners.addAll(eventListeners);
    }

    /**
     * Registers a {@link BeanFactory} for the mapper.
     * <p>
     * By default, no custom bean factories are registered.
     *
     * @param beanFactories factorys to be used by mapper.
     */
    public void setFactories(Map<String, BeanFactory> beanFactories) {
        this.beanFactories.putAll(beanFactories);
    }

    /**
     * Registers a {@link CustomConverter} which can be referenced in mapping by provided ID.
     * <p>
     * Converter instances provided this way are considered stateful and will not be initialized for each mapping.
     * <p>
     * By default, no converters with IDs are registered.
     *
     * @param customConvertersWithId converters to be used for provided ID.
     */
    public void setCustomConvertersWithId(Map<String, CustomConverter> customConvertersWithId) {
        this.customConvertersWithId.putAll(customConvertersWithId);
    }

    // ===
    // Methods for: InitializingBean
    // ===

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        Map<String, CustomConverter> contextCustomConvertersWithId = getApplicationContext().getBeansOfType(CustomConverter.class);
        Map<String, BeanMappingBuilder> contextBeanMappingBuilders = getApplicationContext().getBeansOfType(BeanMappingBuilder.class);
        Map<String, EventListener> contextEventListeners = getApplicationContext().getBeansOfType(EventListener.class);
        Map<String, BeanFactory> contextBeanFactories = getApplicationContext().getBeansOfType(BeanFactory.class);

        customConverters.addAll(contextCustomConvertersWithId.values());
        mappingBuilders.addAll(contextBeanMappingBuilders.values());
        beanFactories.putAll(contextBeanFactories);
        eventListeners.addAll(contextEventListeners.values());
        customConvertersWithId.putAll(contextCustomConvertersWithId);

        this.mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles(mappingFileUrls)
                .withCustomFieldMapper(customFieldMapper)
                .withCustomConverters(customConverters)
                .withMappingBuilders(mappingBuilders)
                .withEventListeners(eventListeners)
                .withBeanFactorys(beanFactories)
                .withCustomConvertersWithIds(customConvertersWithId)
                .build();
    }

    // ===
    // Methods for: FactoryBean<Mapper>
    // ===

    /**
     * {@inheritDoc}
     */
    @Override
    public Mapper getObject() {
        return this.mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Mapper> getObjectType() {
        return Mapper.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
