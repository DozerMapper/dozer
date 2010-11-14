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
package org.dozer.config;

import org.dozer.util.DozerClassLoader;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Internal singleton class that holds the global settings used by Dozer. Most of these settings are configurable via an
 * optional Dozer properties file. By default, Dozer will look for a file named dozer.properties to load these
 * configuration properties. If a properties file is not found or specified, default values will be used.
 * <p/>
 * <p/>
 * An alternative Dozer properties file can be specified via the dozer.configuration system property.
 * <p/>
 * <p/>
 * ex) -Ddozer.configuration=someDozerConfigurationFile.properties
 *
 * @author tierney.matt
 */
public class GlobalSettings {

  private static final Logger log = LoggerFactory.getLogger(GlobalSettings.class);

  private static final GlobalSettings instance = new GlobalSettings();

  private String loadedByFileName;
  private boolean statisticsEnabled = DozerConstants.DEFAULT_STATISTICS_ENABLED;
  private int converterByDestTypeCacheMaxSize = DozerConstants.DEFAULT_CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE;
  private int superTypesCacheMaxSize = DozerConstants.DEFAULT_SUPER_TYPE_CHECK_CACHE_MAX_SIZE;
  private boolean autoregisterJMXBeans = DozerConstants.DEFAULT_AUTOREGISTER_JMX_BEANS;
  private boolean elEnabled = DozerConstants.DEFAULT_EL_ENABLED;

  private String classLoaderBeanName = DozerConstants.DEFAULT_CLASS_LOADER_BEAN;
  private String proxyResolverBeanName = DozerConstants.DEFAULT_PROXY_RESOLVER_BEAN;

  public static GlobalSettings getInstance() {
    return instance;
  }

  static GlobalSettings createNew() {
    return new GlobalSettings();
  }

  private GlobalSettings() {
    loadGlobalSettings();
  }

  protected String getLoadedByFileName() {
    return loadedByFileName;
  }

  public boolean isAutoregisterJMXBeans() {
    return autoregisterJMXBeans;
  }

  public int getConverterByDestTypeCacheMaxSize() {
    return converterByDestTypeCacheMaxSize;
  }

  public boolean isStatisticsEnabled() {
    return statisticsEnabled;
  }

  public void setStatisticsEnabled(boolean statisticsEnabled) {
    this.statisticsEnabled = statisticsEnabled;
  }

  public int getSuperTypesCacheMaxSize() {
    return superTypesCacheMaxSize;
  }

  public String getClassLoaderName() {
    return classLoaderBeanName;
  }

  public String getProxyResolverName() {
    return proxyResolverBeanName;
  }

  public boolean isElEnabled() {
    return elEnabled;
  }  

  private synchronized void loadGlobalSettings() {
    // Determine prop file name
    String propFileName = System.getProperty(DozerConstants.CONFIG_FILE_SYS_PROP);
    if (MappingUtils.isBlankOrNull(propFileName)) {
      propFileName = DozerConstants.DEFAULT_CONFIG_FILE;
    }

    log.info("Trying to find Dozer configuration file: {}", propFileName);
    // Load prop file. Prop file is optional, so if it's not found just use defaults
    DozerClassLoader classLoader = BeanContainer.getInstance().getClassLoader();
    URL url = classLoader.loadResource(propFileName);
    if (url == null) {
      log.warn("Dozer configuration file not found: {}.  Using defaults for all Dozer global properties.", propFileName);
      return;
    } else {
      log.info("Using URL [{}] for Dozer global property configuration", url);
    }

    Properties props = new Properties();
    InputStream inputStream = null;
    try {
      log.info("Reading Dozer properties from URL [{}]", url);
      inputStream = url.openStream();
      props.load(inputStream);
    } catch (IOException e) {
      MappingUtils.throwMappingException("Problem loading Dozer properties from URL [" + propFileName + "]", e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
        }
      }
    }

    populateSettings(props);

    loadedByFileName = propFileName;
    log.debug("Finished configuring Dozer global properties");
  }

  private void populateSettings(Properties props) {
    String propValue = props.getProperty(PropertyConstants.STATISTICS_ENABLED);
    if (propValue != null) {
      statisticsEnabled = Boolean.valueOf(propValue); // TODO Parsing errors?
    }
    propValue = props.getProperty(PropertyConstants.CONVERTER_CACHE_MAX_SIZE);
    if (propValue != null) {
      converterByDestTypeCacheMaxSize = Integer.parseInt(propValue);
    }
    propValue = props.getProperty(PropertyConstants.SUPERTYPE_CACHE_MAX_SIZE);
    if (propValue != null) {
      superTypesCacheMaxSize = Integer.parseInt(propValue);
    }
    propValue = props.getProperty(PropertyConstants.AUTOREGISTER_JMX_BEANS);
    if (propValue != null) {
      autoregisterJMXBeans = Boolean.valueOf(propValue);
    }
    propValue = props.getProperty(PropertyConstants.CLASS_LOADER_BEAN);
    if (propValue != null) {
      classLoaderBeanName = propValue;
    }
    propValue = props.getProperty(PropertyConstants.PROXY_RESOLVER_BEAN);
    if (propValue != null) {
      proxyResolverBeanName = propValue;
    }
    propValue = props.getProperty(PropertyConstants.EL_ENABLED);
    if (propValue != null) {
      elEnabled = Boolean.valueOf(propValue);
    }
  }

}
