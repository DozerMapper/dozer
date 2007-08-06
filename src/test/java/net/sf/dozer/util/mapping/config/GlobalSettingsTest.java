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
package net.sf.dozer.util.mapping.config;

import java.net.MalformedURLException;

import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.util.MapperConstants;

/**
 * @author tierney.matt
 */
public class GlobalSettingsTest extends AbstractDozerTest {

  public void testLoadDefaultPropFile_Default() {
    GlobalSettings globalSettings = GlobalSettings.createNew();
    assertNotNull("loaded by name should not be null", globalSettings.getLoadedByFileName());
    assertEquals("invalid loaded by file name", MapperConstants.DEFAULT_CONFIG_FILE, globalSettings
        .getLoadedByFileName());
  }

  public void testLoadDefaultPropFile_NotFound() {
    String propFileName = String.valueOf(System.currentTimeMillis());
    System.setProperty(MapperConstants.CONFIG_FILE_SYS_PROP, propFileName);
    try {
      GlobalSettings globalSettings = GlobalSettings.createNew();
      fail("should have thrown a dozer mapping exception");
    } catch (MappingException t) {
      assertTrue(t.getCause() instanceof MalformedURLException);
    }

  }

  public void testLoadPropFile_SpecifiedViaSysProp() {
    String propFileName = "samplecustomdozer.properties";
    System.setProperty(MapperConstants.CONFIG_FILE_SYS_PROP, propFileName);

    GlobalSettings globalSettings = GlobalSettings.createNew();

    assertNotNull("loaded by name should not be null", globalSettings.getLoadedByFileName());
    assertEquals("invalid load by file name", propFileName, globalSettings.getLoadedByFileName());
    assertEquals("invalid stats enabled value", true, globalSettings.isStatisticsEnabled());
    assertEquals("invalid converter cache max size value", 25000, globalSettings.getConverterByDestTypeCacheMaxSize());
    assertEquals("invalid super type cache max size value", 10000, globalSettings.getSuperTypesCacheMaxSize());
    assertEquals("invalid autoregister jmx beans", false, globalSettings.isAutoregisterJMXBeans());
  }
}
