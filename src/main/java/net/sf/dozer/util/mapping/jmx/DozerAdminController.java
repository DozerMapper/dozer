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
package net.sf.dozer.util.mapping.jmx;

import net.sf.dozer.util.mapping.cache.CacheManagerIF;
import net.sf.dozer.util.mapping.cache.DozerCacheManager;
import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.config.Settings;
import net.sf.dozer.util.mapping.util.MapperConstants;

/**
 * @author tierney.matt
 */
public class DozerAdminController implements DozerAdminControllerMBean {
  private final CacheManagerIF cacheMgr = DozerCacheManager.getInstance(); 
  private final Settings globalSettings = GlobalSettings.getInstance().getSettings();

  public String getCurrentVersion() {
    return MapperConstants.CURRENT_VERSION;
  }
  
  public void clearGlobalCaches() {
    cacheMgr.clearAllEntries();
  }
  
  public boolean isStatisticsEnabled() {
    return globalSettings.isStatisticsEnabled();
  }
  
  public void setStatisticsEnabled(boolean statisticsEnabled) {
    globalSettings.setStatisticsEnabled(statisticsEnabled);
  }
  
  public void logGlobalCaches() {
    cacheMgr.logCaches();
  }
}
