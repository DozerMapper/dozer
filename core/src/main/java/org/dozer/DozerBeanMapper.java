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
package org.dozer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.builder.DestBeanBuilderCreator;
import org.dozer.cache.CacheManager;
import org.dozer.cache.DozerCacheManager;
import org.dozer.cache.DozerCacheType;
import org.dozer.classmap.ClassMappings;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingFileData;
import org.dozer.classmap.generator.BeanMappingGenerator;
import org.dozer.config.BeanContainer;
import org.dozer.config.Settings;
import org.dozer.event.DozerEventManager;
import org.dozer.factory.DestBeanCreator;
import org.dozer.metadata.DozerMappingMetadata;
import org.dozer.metadata.MappingMetadata;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;

/**
 * Public Dozer Mapper implementation. This should be used/defined as a singleton within your application. This class
 * performs several one-time initializations and loads the custom xml mappings, so you will not want to create many
 * instances of it for performance reasons. Typically a system will only have one DozerBeanMapper instance per VM. If
 * you are using an IOC framework (i.e Spring), define the Mapper as singleton="true". If you are not using an IOC
 * framework, a DozerBeanMapperSingletonWrapper convenience class has been provided in the Dozer jar.
 * <p>
 * It is technically possible to have multiple DozerBeanMapper instances initialized, but it will hinder internal
 * performance optimizations such as caching.
 *
 * @author tierney.matt
 * @author garsombke.franz
 * @author dmitry.buzdin
 * @author suwarnaratana.arm
 */
public class DozerBeanMapper implements Mapper {

  private final Settings settings;
  private final DozerInitializer dozerInitializer;
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
  private final List<MappingFileData> mappingsFileData;
  private final List<DozerEventListener> eventListeners;
  private final Map<String, CustomConverter> customConvertersWithId;
  private CustomFieldMapper customFieldMapper;

  /*
   * Not accessible for injection
   */

  // There are no global caches. Caches are per bean mapper instance
  private final CacheManager cacheManager;
  private DozerEventManager eventManager;

  DozerBeanMapper(List<String> mappingFiles,
                  Settings settings,
                  DozerInitializer dozerInitializer,
                  BeanContainer beanContainer,
                  DestBeanCreator destBeanCreator,
                  DestBeanBuilderCreator destBeanBuilderCreator,
                  BeanMappingGenerator beanMappingGenerator,
                  PropertyDescriptorFactory propertyDescriptorFactory,
                  List<CustomConverter> customConverters,
                  List<MappingFileData> mappingsFileData,
                  List<DozerEventListener> eventListeners,
                  CustomFieldMapper customFieldMapper,
                  Map<String, CustomConverter> customConvertersWithId,
                  ClassMappings customMappings,
                  Configuration globalConfiguration) {
    this.settings = settings;
    this.cacheManager = new DozerCacheManager();
    this.dozerInitializer = dozerInitializer;
    this.beanContainer = beanContainer;
    this.destBeanCreator = destBeanCreator;
    this.destBeanBuilderCreator = destBeanBuilderCreator;
    this.beanMappingGenerator = beanMappingGenerator;
    this.propertyDescriptorFactory = propertyDescriptorFactory;
    this.customConverters = new ArrayList<>(customConverters);
    this.mappingsFileData = new ArrayList<>(mappingsFileData);
    this.eventListeners = new ArrayList<>(eventListeners);
    this.mappingFiles = new ArrayList<>(mappingFiles);
    this.customFieldMapper = customFieldMapper;
    this.customConvertersWithId = new HashMap<>(customConvertersWithId);
    this.eventManager = new DozerEventManager(eventListeners);
    this.customMappings = customMappings;
    this.globalConfiguration = globalConfiguration;

    init();
  }

  /**
   * {@inheritDoc}
   */
  public void map(Object source, Object destination, String mapId) throws MappingException {
    getMappingProcessor().map(source, destination, mapId);
  }

  /**
   * {@inheritDoc}
   */
  public <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException {
    return getMappingProcessor().map(source, destinationClass, mapId);
  }

  /**
   * {@inheritDoc}
   */
  public <T> T map(Object source, Class<T> destinationClass) throws MappingException {
    return getMappingProcessor().map(source, destinationClass);
  }

  /**
   * {@inheritDoc}
   */
  public void map(Object source, Object destination) throws MappingException {
    getMappingProcessor().map(source, destination);
  }

  private void init() {
    // initialize any bean mapper caches. These caches are only visible to the bean mapper instance and
    // are not shared across the VM.
    cacheManager.addCache(DozerCacheType.CONVERTER_BY_DEST_TYPE.name(), settings.getConverterByDestTypeCacheMaxSize());
    cacheManager.addCache(DozerCacheType.SUPER_TYPE_CHECK.name(), settings.getSuperTypesCacheMaxSize());
  }

  public void destroy() {
    dozerInitializer.destroy(settings);
  }

  protected Mapper getMappingProcessor() {
    Mapper processor = new MappingProcessor(customMappings, globalConfiguration, cacheManager, customConverters,
            eventManager, customFieldMapper, customConvertersWithId, beanContainer, destBeanCreator, destBeanBuilderCreator,
            beanMappingGenerator, propertyDescriptorFactory);

    return processor;
  }

  /**
   * Returns list of provided mapping file URLs
   *
   * @return unmodifiable list of mapping files
   * @deprecated will be removed in 6.2. Please do not use {@link DozerBeanMapper} directly, only via {@link Mapper} interface.
   */
  @Deprecated
  public List<String> getMappingFiles() {
    return Collections.unmodifiableList(mappingFiles);
  }

  /**
   * @deprecated will be removed in 6.2. Please do not use {@link DozerBeanMapper} directly, only via {@link Mapper} interface.
   */
  @Deprecated
  public List<CustomConverter> getCustomConverters() {
    return Collections.unmodifiableList(customConverters);
  }

  /**
   * @deprecated will be removed in 6.2. Please do not use {@link DozerBeanMapper} directly, only via {@link Mapper} interface.
   */
  @Deprecated
  public Map<String, CustomConverter> getCustomConvertersWithId() {
    return Collections.unmodifiableMap(customConvertersWithId);
  }

  /**
   * @deprecated will be removed in 6.2. Please do not use {@link DozerBeanMapper} directly, only via {@link Mapper} interface.
   */
  @Deprecated
  public List<? extends DozerEventListener> getEventListeners() {
    return Collections.unmodifiableList(eventListeners);
  }

  /**
   * @deprecated will be removed in 6.2. Please do not use {@link DozerBeanMapper} directly, only via {@link Mapper} interface.
   */
  @Deprecated
  public CustomFieldMapper getCustomFieldMapper() {
    return customFieldMapper;
  }

  @Override
  public MappingMetadata getMappingMetadata() {
    return new DozerMappingMetadata(customMappings);
  }
}
