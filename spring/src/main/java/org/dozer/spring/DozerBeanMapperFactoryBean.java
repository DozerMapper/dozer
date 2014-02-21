/**
 * Copyright 2005-2013 Dozer Project
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
package org.dozer.spring;

import org.dozer.*;
import org.dozer.loader.api.BeanMappingBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Public Spring FactoryBean that can be used by application code.
 * Uses Spring InitializingBean and DisposableBean contracts to properly start-up and
 * release global Dozer resources.
 *
 * @author S'ren Chittka
 * @author dmitry.buzdin
 */
public class DozerBeanMapperFactoryBean implements FactoryBean<Mapper>,
    InitializingBean, DisposableBean, ApplicationContextAware {

  DozerBeanMapper beanMapper;
  private Resource[] mappingFiles;
  private List<BeanMappingBuilder> mappingBuilders;
  private CustomFieldMapper customFieldMapper;
  private List<CustomConverter> customConverters;
  private Map<String, CustomConverter> customConvertersWithId;
  private List<DozerEventListener> eventListeners;
  private Map<String, BeanFactory> factories;
  private ApplicationContext applicationContext;

  /**
   * Spring resources definition for providing mapping file location.
   * Could be used for loading all mapping files by wildcard definition for example
   * {@code
   *   <bean class="org.dozer.spring.DozerBeanMapperFactoryBean">
   *       <property name="mappingFiles" value="classpath*:/*.dozer.xml"/>
   *   </bean>
   * }
   *
   * @param mappingFiles Spring resource definition
   */
  public final void setMappingFiles(final Resource[] mappingFiles) {
    this.mappingFiles = mappingFiles;
  }

  public final void setMappingBuilders(final List<BeanMappingBuilder> mappingBuilders) {
      this.mappingBuilders = mappingBuilders;
  }

  public void setCustomFieldMapper(CustomFieldMapper customFieldMapper) {
      this.customFieldMapper = customFieldMapper;
  }
  
  public final void setCustomConverters(final List<CustomConverter> customConverters) {
    this.customConverters = customConverters;
  }

  public void setCustomConvertersWithId(Map<String, CustomConverter> customConvertersWithId) {
    this.customConvertersWithId = customConvertersWithId;
  }

  public final void setEventListeners(final List<DozerEventListener> eventListeners) {
    this.eventListeners = eventListeners;
  }

  public final void setFactories(final Map<String, BeanFactory> factories) {
    this.factories = factories;
  }

  // ==================================================================================================================================
  // interface 'FactoryBean'
  // ==================================================================================================================================
  public final Mapper getObject() throws Exception {
    return this.beanMapper;
  }

  public final Class<Mapper> getObjectType() {
    return Mapper.class;
  }

  public final boolean isSingleton() {
    return true;
  }

  // ==================================================================================================================================
  // interface 'InitializingBean'
  // ==================================================================================================================================
  public final void afterPropertiesSet() throws Exception {
    this.beanMapper = new DozerBeanMapper();

    loadMappingFiles();

    List<CustomConverter> allConverters = new ArrayList<CustomConverter>();
    Map<String, CustomConverter> allIdConverters = new HashMap<String, CustomConverter>();
    Map<String, BeanFactory> allFactories = new HashMap<String, BeanFactory>();
    List<DozerEventListener> allListeners = new ArrayList<DozerEventListener>();
    List<BeanMappingBuilder> allMappingBuilders = new ArrayList<BeanMappingBuilder>();

    Map<String, CustomConverter> contextConverters = applicationContext.getBeansOfType(CustomConverter.class);
    Map<String, BeanFactory> contextBeanFactories = applicationContext.getBeansOfType(BeanFactory.class);
    Map<String, DozerEventListener> contextEventListeners = applicationContext.getBeansOfType(DozerEventListener.class);
    Map<String, BeanMappingBuilder> contextMappingBuilders = applicationContext.getBeansOfType(BeanMappingBuilder.class);

    allConverters.addAll(contextConverters.values());
    allIdConverters.putAll(contextConverters);
    allFactories.putAll(contextBeanFactories);
    allListeners.addAll(contextEventListeners.values());
    allMappingBuilders.addAll(contextMappingBuilders.values());

    if(customFieldMapper != null){
        this.beanMapper.setCustomFieldMapper(customFieldMapper);
    }
    if (this.customConverters != null) {
      allConverters.addAll(this.customConverters);
    }
    if (this.customConvertersWithId != null) {
      allIdConverters.putAll(this.customConvertersWithId);
    }
    if (this.eventListeners != null) {
      allListeners.addAll(this.eventListeners);
    }
    if (this.factories != null) {
      allFactories.putAll(this.factories);
    }
    if (this.mappingBuilders != null) {
      allMappingBuilders.addAll(this.mappingBuilders);
    }

    if (!allConverters.isEmpty()) {
      this.beanMapper.setCustomConverters(allConverters);
    }
    if (!allIdConverters.isEmpty()) {
      this.beanMapper.setCustomConvertersWithId(allIdConverters);
    }
    if (!allFactories.isEmpty()) {
      this.beanMapper.setFactories(allFactories);
    }
    if (!allListeners.isEmpty()) {
      this.beanMapper.setEventListeners(allListeners);
    }
    if (!allMappingBuilders.isEmpty()) {
      for (BeanMappingBuilder mappingBuilder : allMappingBuilders) {
        this.beanMapper.addMapping(mappingBuilder);
      }
    }
  }

  private void loadMappingFiles() throws IOException {
    if (this.mappingFiles != null) {
      final List<String> mappings = new ArrayList<String>(this.mappingFiles.length);
      for (Resource mappingFile : this.mappingFiles) {
        URL url = mappingFile.getURL();
        mappings.add(url.toString());
      }
      this.beanMapper.setMappingFiles(mappings);
    }
  }

  /**
   * Spring DisposableBean method implementation. Triggered when application context is stopped.
   * Used to release global Dozer resources for hot redeployment without stopping the JVM.
   *
   * @throws Exception
   */
  public void destroy() throws Exception {
    if (this.beanMapper != null) {
      this.beanMapper.destroy();
    }
  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

}
