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

import org.dozer.AbstractDozerTest;
import org.dozer.config.GlobalSettings;
import org.dozer.util.DozerConstants;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class GlobalSettingsTest extends AbstractDozerTest {

  @Test
  public void testLoadDefaultPropFile_Default() {
    GlobalSettings globalSettings = GlobalSettings.createNew();
    assertNotNull("loaded by name should not be null", globalSettings.getLoadedByFileName());
    assertEquals("invalid loaded by file name", DozerConstants.DEFAULT_CONFIG_FILE, globalSettings.getLoadedByFileName());
  }

  @Test
  public void testLoadDefaultPropFile_NotFound() {
    String propFileName = String.valueOf(System.currentTimeMillis());
    System.setProperty(DozerConstants.CONFIG_FILE_SYS_PROP, propFileName);
    GlobalSettings globalSettings = GlobalSettings.createNew();

    // assert all global settings equal the default values
    assertNull("loaded by file name should be null", globalSettings.getLoadedByFileName());
    assertEquals("invalid stats enabled value", DozerConstants.DEFAULT_STATISTICS_ENABLED, globalSettings.isStatisticsEnabled());
    assertEquals("invalid converter cache max size value", DozerConstants.DEFAULT_CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE,
        globalSettings.getConverterByDestTypeCacheMaxSize());
    assertEquals("invalid super type cache max size value", DozerConstants.DEFAULT_SUPER_TYPE_CHECK_CACHE_MAX_SIZE, globalSettings
        .getSuperTypesCacheMaxSize());
    assertEquals("invalid autoregister jmx beans", DozerConstants.DEFAULT_AUTOREGISTER_JMX_BEANS, globalSettings
        .isAutoregisterJMXBeans());
    assertEquals(DozerConstants.DEFAULT_PROXY_RESOLVER_BEAN, globalSettings.getProxyResolverName());
    assertEquals(DozerConstants.DEFAULT_CLASS_LOADER_BEAN, globalSettings.getClassLoaderName());
    assertEquals(DozerConstants.DEFAULT_EL_ENABLED, globalSettings.isElEnabled());
  }

  @Test
  public void testLoadPropFile_SpecifiedViaSysProp() {
    String propFileName = "samplecustomdozer.properties";
    System.setProperty(DozerConstants.CONFIG_FILE_SYS_PROP, propFileName);

    GlobalSettings globalSettings = GlobalSettings.createNew();

    assertNotNull("loaded by name should not be null", globalSettings.getLoadedByFileName());
    assertEquals("invalid load by file name", propFileName, globalSettings.getLoadedByFileName());
    assertEquals("invalid stats enabled value", true, globalSettings.isStatisticsEnabled());
    assertEquals("invalid converter cache max size value", 25000, globalSettings.getConverterByDestTypeCacheMaxSize());
    assertEquals("invalid super type cache max size value", 10000, globalSettings.getSuperTypesCacheMaxSize());
    assertEquals("invalid autoregister jmx beans", false, globalSettings.isAutoregisterJMXBeans());
    assertEquals("org.dozer.CustomLoader", globalSettings.getClassLoaderName());
    assertEquals("org.dozer.CustomResolver", globalSettings.getProxyResolverName());
    assertEquals(true, globalSettings.isElEnabled());
  }
  
}
