/*
 * Copyright 2005-2017 Dozer Project
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.BeanFactory;
import org.dozer.CustomConverter;
import org.dozer.CustomFieldMapper;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.DozerEventListener;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * Public Spring FactoryBean that can be used by application code.
 * Uses Spring InitializingBean contracts to properly start-up
 *
 * @author S'ren Chittka
 * @author dmitry.buzdin
 */
public class DozerBeanMapperFactoryBean implements ApplicationContextAware, InitializingBean, FactoryBean<Mapper> {

    private ApplicationContext applicationContext;

    private CustomFieldMapper customFieldMapper;
    private List<String> mappingFileUrls = new ArrayList<>(1);
    private List<CustomConverter> customConverters = new ArrayList<>(0);
    private List<BeanMappingBuilder> mappingBuilders = new ArrayList<>(0);
    private List<DozerEventListener> eventListeners = new ArrayList<>(0);
    private Map<String, BeanFactory> beanFactories = new HashMap<>(0);
    private Map<String, CustomConverter> customConvertersWithId = new HashMap<>(0);

    private Mapper mapper;

    public void setCustomFieldMapper(CustomFieldMapper customFieldMapper) {
        this.customFieldMapper = customFieldMapper;
    }

    /**
     * Spring resources definition for providing mapping file location.
     * Could be used for loading all mapping files by wildcard definition for example
     *
     * {@code
     * <bean class="org.dozer.spring.DozerBeanMapperFactoryBean">
     *   <property name="mappingFiles" value="classpath*:/*.dozer.xml"/>
     * </bean>
     * }
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

    public void setCustomConverters(List<CustomConverter> customConverters) {
        this.customConverters.addAll(customConverters);
    }

    public void setMappingBuilders(List<BeanMappingBuilder> mappingBuilders) {
        this.mappingBuilders.addAll(mappingBuilders);
    }

    public void setEventListeners(List<DozerEventListener> eventListeners) {
        this.eventListeners.addAll(eventListeners);
    }

    public void setFactories(Map<String, BeanFactory> beanFactories) {
        this.beanFactories.putAll(beanFactories);
    }

    public void setCustomConvertersWithId(Map<String, CustomConverter> customConvertersWithId) {
        this.customConvertersWithId.putAll(customConvertersWithId);
    }

    // ===
    // Methods for: ApplicationContextAware
    // ===

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // ===
    // Methods for: InitializingBean
    // ===

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, CustomConverter> contextCustomConvertersWithId = applicationContext.getBeansOfType(CustomConverter.class);
        Map<String, BeanMappingBuilder> contextBeanMappingBuilders = applicationContext.getBeansOfType(BeanMappingBuilder.class);
        Map<String, DozerEventListener> contextEventListeners = applicationContext.getBeansOfType(DozerEventListener.class);
        Map<String, BeanFactory> contextBeanFactorys = applicationContext.getBeansOfType(BeanFactory.class);

        customConverters.addAll(contextCustomConvertersWithId.values());
        mappingBuilders.addAll(contextBeanMappingBuilders.values());
        beanFactories.putAll(contextBeanFactorys);
        eventListeners.addAll(contextEventListeners.values());
        customConvertersWithId.putAll(contextCustomConvertersWithId);

        DozerBeanMapperBuilder builder = DozerBeanMapperBuilder.create();

        for (String url : mappingFileUrls) {
            builder.withMappingFiles(url);
        }

        builder.withCustomFieldMapper(customFieldMapper);

        for (CustomConverter converter : customConverters) {
            builder.withCustomConverter(converter);
        }

        for (BeanMappingBuilder mappingBuilder : mappingBuilders) {
            builder.withMappingBuilder(mappingBuilder);
        }

        for (DozerEventListener listener : eventListeners) {
            builder.withEventListener(listener);
        }

        for (Map.Entry<String, BeanFactory> beanFactoryEntry : beanFactories.entrySet()) {
            builder.withBeanFactory(beanFactoryEntry.getKey(), beanFactoryEntry.getValue());
        }

        for (Map.Entry<String, CustomConverter> customConverterEntry : customConvertersWithId.entrySet()) {
            builder.withCustomConverterWithId(customConverterEntry.getKey(), customConverterEntry.getValue());
        }

        this.mapper = builder.build();
    }

    // ===
    // Methods for: FactoryBean<Mapper>
    // ===

    @Override
    public Mapper getObject() throws Exception {
        return this.mapper;
    }

    @Override
    public Class<Mapper> getObjectType() {
        return Mapper.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
