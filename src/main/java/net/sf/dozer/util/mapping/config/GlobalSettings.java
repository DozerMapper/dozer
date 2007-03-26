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

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.util.InitLogger;
import net.sf.dozer.util.mapping.util.Loader;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tierney.matt
 */
public class GlobalSettings {
  
  private static final boolean isJdk5 = System.getProperty("java.vm.version", "1.4").startsWith("1.5");
  private static final Log log = LogFactory.getLog(GlobalSettings.class);
  private static GlobalSettings singleton;

  private final Settings settings;
  private String loadedByFileName;

  public static synchronized GlobalSettings getInstance() {
    if (singleton == null) {
      singleton = new GlobalSettings();
    }
    return singleton;
  }
  
  protected static GlobalSettings createNew() {
    return new GlobalSettings();
  }
  
  private GlobalSettings() {
    settings = loadSettings();
  }

  public Settings getSettings() {
    return settings;
  }
  
  protected String getLoadedByFileName() {
    return loadedByFileName;
  }

  public boolean isJava5() {
    return isJdk5; 
  }

  private synchronized Settings loadSettings() {
    Settings result = new Settings();
    MappingUtils utils = new MappingUtils();    

    //Determine prop file name
    String propFileName = System.getProperty(MapperConstants.CONFIG_FILE_SYS_PROP);
    if (utils.isBlankOrNull(propFileName)) {
      propFileName = MapperConstants.DEFAULT_CONFIG_FILE;
    }

    InitLogger.log(log,"Trying to find configuration file: " + propFileName);
    //Load prop file.  Prop file is optional, so if it's not found just use defaults
    Loader loader = new Loader();
    URL url = loader.getResource(propFileName);
    if (url == null) {
      InitLogger.log(log,"Configuration file not found: " + propFileName + ".  Using defaults for all global properties.");
      return result;
    } else {
      InitLogger.log(log,"Using URL [" + url + "] for global property configuration");
    }
    
    Properties props = new Properties();
    try {
      InitLogger.log(log,"Reading properties from URL [" + url + "]");
      props.load(url.openStream());
    } catch (IOException e) {
      throw new MappingException("Problem loading properties from URL [" + propFileName + "]", e);
    }
    
    //Populate settings from loaded properties
    SettingsHelper.populateSettingsFromProperties(result, props);
    loadedByFileName = propFileName;
    InitLogger.log(log,"Finished configuring global properties");    
    
    return result;
  }
  
}
