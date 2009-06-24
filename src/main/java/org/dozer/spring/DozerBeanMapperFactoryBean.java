/*
 * Copyright 2005-2010 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.dozer.BeanFactory;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerEventListener;
import org.dozer.Mapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;

/**
 * Public Spring FactoryBean that can be used by application code.
 * Uses Spring InitializingBean and DisposableBean contracts to properly start-up and
 * release global Dozer resources.
 *
 * @author Sören Chittka
 */
public class DozerBeanMapperFactoryBean implements FactoryBean, InitializingBean, DisposableBean {

  private DozerBeanMapper beanMapper;
  private Resource[] mappingFiles;
  private List<CustomConverter> customConverters;
  private List<DozerEventListener> eventListeners;
  private Map<String, BeanFactory> factories;

  public final void setMappingFiles(final Resource[] mappingFiles) {
    this.mappingFiles = mappingFiles;
  }

  public final void setCustomConverters(final List<CustomConverter> customConverters) {
    this.customConverters = customConverters;
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
  public final Object getObject() throws Exception {
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

    if (this.mappingFiles != null) {
      final List<String> mappings = new ArrayList<String>(this.mappingFiles.length);
      for (Resource mappingFile : this.mappingFiles) {
        mappings.add(mappingFile.getURL().toString());
      }
      this.beanMapper.setMappingFiles(mappings);
    }
    if (this.customConverters != null) {
      this.beanMapper.setCustomConverters(this.customConverters);
    }
    if (this.eventListeners != null) {
      this.beanMapper.setEventListeners(this.eventListeners);
    }
    if (this.factories != null) {
      this.beanMapper.setFactories(this.factories);
    }
  }

  /**
   * Spring DisposableBean method implemention. Triggered when application context is stopped.
   * Used to release global Dozer resources for hot redeployment without stopping the JVM.
   *
   * @throws Exception
   */
  public void destroy() throws Exception {
    if (this.beanMapper != null) {
      this.beanMapper.destroy();
    }
  }

}
