package net.sf.dozer.util.mapping.util;

import java.util.Map;

import net.sf.dozer.util.mapping.fieldmap.Configuration;


public class LoadMappingsResult {
  
  private Map customMappings;
  private Configuration globalConfiguration;

  public LoadMappingsResult(Map customMappings, Configuration globalConfiguration) {
    this.customMappings = customMappings;
    this.globalConfiguration = globalConfiguration;
  }

  public Map getCustomMappings() {
    return customMappings;
  }
  
  public Configuration getGlobalConfiguration() {
    return globalConfiguration;
  }

}
