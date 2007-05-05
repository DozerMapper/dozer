/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import net.sf.dozer.util.mapping.cache.CacheManagerIF;
import net.sf.dozer.util.mapping.cache.DozerCacheManager;
import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.fieldmap.Configuration;
import net.sf.dozer.util.mapping.interceptor.StatisticsInterceptor;
import net.sf.dozer.util.mapping.stats.GlobalStatistics;
import net.sf.dozer.util.mapping.stats.StatisticTypeConstants;
import net.sf.dozer.util.mapping.stats.StatisticsManagerIF;
import net.sf.dozer.util.mapping.util.CustomMappingsLoader;
import net.sf.dozer.util.mapping.util.InitLogger;
import net.sf.dozer.util.mapping.util.LoadMappingsResult;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Public Dozer MapperIF implementation.  This should be used/defined as a singleton within
 * your application.  This class perfoms several one time initializations and loads
 * the custom xml mappings, so you will not want to create many instances of it for performance reasons.
 * Typically a system will only have one DozerBeanMapper instance per VM.
 * If you are using an IOC framework(i.e Spring), define the MapperIF as singleton="true". 
 * If you are not using an IOC framework, a DozerBeanMapperSingletonWrapper convenience class 
 * has been provided in the Dozer jar. 
 *
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class DozerBeanMapper implements MapperIF {

  private static final Log log = LogFactory.getLog(DozerBeanMapper.class);
  private static final StatisticsManagerIF statsMgr = GlobalStatistics.getInstance().getStatsMgr();
  
  static {
    DozerInitializer.init();
  }

  /*
   * Accessible for custom injection
   */
  private List mappingFiles; // String file names
  private List customConverters;
  private List eventListeners;
  private CustomFieldMapperIF customFieldMapper;

  /*
   * Not accessible for injection
   */
  private Map customMappings;
  private Configuration globalConfiguration;
  //There are no global caches.  Caches are per bean mapper instance
  private final CacheManagerIF cacheManager = new DozerCacheManager();

  public DozerBeanMapper() {
    this(null);
  }

  public DozerBeanMapper(List mappingFiles) {
    this.mappingFiles = mappingFiles;
    init();
  }

  public void map(Object sourceObj, Object destObj, String mapId) throws MappingException {
    getMappingProcessor().map(sourceObj, destObj, mapId);
  }

  public Object map(Object sourceObj, Class destClass, String mapId) throws MappingException {
    return getMappingProcessor().map(sourceObj, destClass, mapId);
  }

  public Object map(Object sourceObj, Class destClass) throws MappingException {
    return getMappingProcessor().map(sourceObj, destClass);
  }

  public void map(Object sourceObj, Object destObj) throws MappingException {
    getMappingProcessor().map(sourceObj, destObj);
  }

  public List getMappingFiles() {
    return mappingFiles;
  }

  public void setMappingFiles(List mappingFiles) {
    this.mappingFiles = mappingFiles;
  }

  public void setFactories(Map factories) {
    MappingUtils.addFactories(factories);
  }

  public void setCustomConverters(List customConverters) {
    this.customConverters = customConverters;
  }

  private void init() {
    InitLogger.log(log, "Initializing a new instance of the dozer bean mapper.");
    
    // initialize any bean mapper caches. These caches are only visible to the bean mapper instance and
    // are not shared across the VM.
    cacheManager.addCache(MapperConstants.CONVERTER_BY_DEST_TYPE_CACHE, GlobalSettings.getInstance().getConverterByDestTypeCacheMaxSize());
    cacheManager.addCache(MapperConstants.SUPER_TYPE_CHECK_CACHE, GlobalSettings.getInstance().getSuperTypesCacheMaxSize());

    // stats
    statsMgr.increment(StatisticTypeConstants.MAPPER_INSTANCES_COUNT);
  }

  protected MapperIF getMappingProcessor() {
    if (customMappings == null) {
      loadCustomMappings();
    }
    MapperIF processor = new MappingProcessor(customMappings, globalConfiguration, 
        cacheManager, statsMgr, customConverters, getEventListeners(), getCustomFieldMapper());

    // If statistics are enabled, then Proxy the processor with a statistics interceptor
    if (statsMgr.isStatisticsEnabled()) {
      processor = (MapperIF) Proxy.newProxyInstance(processor.getClass().getClassLoader(), processor.getClass()
          .getInterfaces(), new StatisticsInterceptor(processor, statsMgr));
    }

    return processor;
  }
  
  private synchronized void loadCustomMappings() {
    //loadCustomMappings() has to be called outside of init() method because the custom converters are injected.
    if (this.customMappings == null) {
      CustomMappingsLoader customMappingsLoader = new CustomMappingsLoader();
      LoadMappingsResult loadMappingsResult = customMappingsLoader.load(mappingFiles);
      this.customMappings = loadMappingsResult.getCustomMappings();
      this.globalConfiguration = loadMappingsResult.getGlobalConfiguration();
    }
  }

  public List getEventListeners() {
    return eventListeners;
  }

  public void setEventListeners(List eventListeners) {
    this.eventListeners = eventListeners;
  }

  public CustomFieldMapperIF getCustomFieldMapper() {
    return customFieldMapper;
  }

  public void setCustomFieldMapper(CustomFieldMapperIF customFieldMapper) {
    this.customFieldMapper = customFieldMapper;
  }

}
