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
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.dozer.util.mapping.cache.CacheManagerIF;
import net.sf.dozer.util.mapping.cache.DozerCacheManager;
import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.config.Settings;
import net.sf.dozer.util.mapping.converters.CustomConverterContainer;
import net.sf.dozer.util.mapping.converters.CustomConverterDescription;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Configuration;
import net.sf.dozer.util.mapping.fieldmap.Mappings;
import net.sf.dozer.util.mapping.interceptor.StatisticsInterceptor;
import net.sf.dozer.util.mapping.stats.GlobalStatistics;
import net.sf.dozer.util.mapping.stats.StatisticTypeConstants;
import net.sf.dozer.util.mapping.stats.StatisticsManagerIF;
import net.sf.dozer.util.mapping.util.ClassMapBuilder;
import net.sf.dozer.util.mapping.util.InitLogger;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingFileReader;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.MappingValidator;
import net.sf.dozer.util.mapping.util.MappingsParser;

import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Mapper Implementation.  This should be used/defined as a singleton within
 * your application.  This class perfoms several one time initializations and loads
 * the custom xml mappings, so you will not want to create many instances of it for performance reasons.
 * Typically a system will only have one DozerBeanMapper instance per VM.
 * If you are using an IOC framework(i.e Spring), define the MapperIF as singleton="true". 
 * If you are not using an IOC framework, a DozerBeanMapperSingletonWrapper convenience class 
 * has been provided in the Dozer jar. 
 *
 * 
 * @author tierney.matt
 */
public class DozerBeanMapper implements MapperIF {

  //private static final Logge log = Logger.getLogger(DozerBeanMapper.class);
  private static final Log log = LogFactory.getLog(DozerBeanMapper.class);

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
  private final CacheManagerIF cacheManager = DozerCacheManager.createNew();
  private final MappingUtils mappingUtils = new MappingUtils();
  private final MappingValidator mappingValidator = new MappingValidator();
  private final ClassMapBuilder classMapBuilder = new ClassMapBuilder();
  
  /*
   * Not accessible for injection.  Global
   */
  private static final StatisticsManagerIF statsMgr = GlobalStatistics.getInstance().getStatsMgr();
  private static final Settings settings = GlobalSettings.getInstance().getSettings();

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
    mappingUtils.addFactories(factories);
  }

  public void setCustomConverters(List customConverters) {
    this.customConverters = customConverters;
  }

  public List getCustomConverters() {
    return customConverters;
  }

  private void init() {
    // initialize any bean mapper caches. These caches are only visible to the bean mapper instance and
    // are not shared across the VM.
    cacheManager.addCache(MapperConstants.CONVERTER_BY_DEST_TYPE_CACHE, settings.getConverterByDestTypeCacheMaxSize());
    cacheManager.addCache(MapperConstants.SUPER_TYPE_CHECK_CACHE, settings.getSuperTypesCacheMaxSize());

    // stats
    statsMgr.increment(StatisticTypeConstants.MAPPER_INSTANCES_COUNT);
  }

  protected MapperIF getMappingProcessor() {
    MapperIF processor = new MappingProcessor(getCustomMappings(), globalConfiguration, cacheManager,
        statsMgr, customConverters, getEventListeners(), getCustomFieldMapper());

    // If statistics are enabled, then Proxy the processor with a statistics interceptor
    if (statsMgr.isStatisticsEnabled()) {
      processor = (MapperIF) Proxy.newProxyInstance(processor.getClass().getClassLoader(), processor.getClass()
          .getInterfaces(), new StatisticsInterceptor(processor, statsMgr));
    }

    return processor;
  }
  
  private synchronized Map getCustomMappings() {
    //loadCustomMappings() has to be called outside of init() method because the custom converters are injected.
    if (this.customMappings == null) {
      this.customMappings = Collections.synchronizedMap(loadCustomMappings()); 
    }
    return this.customMappings;
  }

  private synchronized Map loadCustomMappings() {
    Map customMappings = new HashMap();

    synchronized (customMappings) {
      ListOrderedSet customConverterDescriptions = new ListOrderedSet();
      InitLogger.log(log,"Initializing a new instance of the dozer bean mapper.  Version: " + MapperConstants.CURRENT_VERSION
          + ", Thread Name:" + Thread.currentThread().getName());

      if (mappingFiles != null && mappingFiles.size() > 0) {
        InitLogger.log(log, "Using the following xml files to load custom mappings for the bean mapper instance: " + mappingFiles);
        Iterator iter = mappingFiles.iterator();
        while (iter.hasNext()) {
          String mappingFileName = (String) iter.next();
          InitLogger.log(log,"Trying to find xml mapping file: " + mappingFileName);
          URL url = mappingValidator.validateURL(MapperConstants.DEFAULT_PATH_ROOT + mappingFileName);
          InitLogger.log(log, "Using URL [" + url + "] to load custom xml mappings");
          MappingFileReader mappingFileReader = new MappingFileReader(url);
          Mappings mappings = mappingFileReader.read();
          InitLogger.log(log,"Successfully loaded custom xml mappings from URL: [" + url + "]");

          // the last configuration is the 'global' configuration
          globalConfiguration = mappings.getConfiguration();
          // build up the custom converters to make them global
          if (mappings.getConfiguration() != null && mappings.getConfiguration().getCustomConverters() != null
              && mappings.getConfiguration().getCustomConverters().getConverters() != null) {
            Iterator iterator = mappings.getConfiguration().getCustomConverters().getConverters().iterator();
            while (iterator.hasNext()) {
              CustomConverterDescription cc = (CustomConverterDescription) iterator.next();
              customConverterDescriptions.add(cc);
            }
          }
          MappingsParser mappingsParser = new MappingsParser();
          customMappings.putAll(mappingsParser.parseMappings(mappings));
        }
      }

      // Add default mappings using matching property names if wildcard policy
      // is true. The addDefaultFieldMappings will check the wildcard policy of each classmap
      if (customMappings != null) {
        classMapBuilder.addDefaultFieldMappings(customMappings);
      }
      // iterate through the classmaps and set all of the customconverters on them
      Iterator keyIter = customMappings.keySet().iterator();
      while (keyIter.hasNext()) {
        String key = (String) keyIter.next();
        ClassMap classMap = (ClassMap) customMappings.get(key);
        if (classMap.getConfiguration() == null) {
          classMap.setConfiguration(new Configuration());
        }
        if (classMap.getConfiguration().getCustomConverters() != null) {
          classMap.getConfiguration().getCustomConverters().setConverters(customConverterDescriptions.asList());
        } else {
          classMap.getConfiguration().setCustomConverters(new CustomConverterContainer());
          classMap.getConfiguration().getCustomConverters().setConverters(customConverterDescriptions.asList());
        }
      }
    }
    return customMappings;
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