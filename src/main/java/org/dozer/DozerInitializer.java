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
package org.dozer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.config.GlobalSettings;
import org.dozer.jmx.DozerAdminController;
import org.dozer.jmx.DozerStatisticsController;
import org.dozer.jmx.JMXPlatform;
import org.dozer.jmx.JMXPlatformImpl;
import org.dozer.util.DozerConstants;
import org.dozer.util.InitLogger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

/**
 * Internal class that performs one time Dozer initializations. Only intended for internal use.
 * Registers internal JMX MBeans if those are enabled in the configuration.
 *
 * @author tierney.matt
 */
public final class DozerInitializer {

  private static final Log log = LogFactory.getLog(DozerInitializer.class);

  private static final String DOZER_STATISTICS_CONTROLLER = "org.dozer.jmx:type=DozerStatisticsController";
  private static final String DOZER_ADMIN_CONTROLLER = "org.dozer.jmx:type=DozerAdminController";

  private static final DozerInitializer instance = new DozerInitializer();

  private volatile boolean isInitialized = false;

  private DozerInitializer() {
  }

  public void init() {
    // Multiple threads may try to initialize simultaniously
    synchronized (this) {
      if (isInitialized) {
        log.debug("Tried to perform initialization when Dozer already started.");
        return;
      }

      InitLogger.log(log, "Initializing Dozer.  Version: " + DozerConstants.CURRENT_VERSION + ", Thread Name:"
          + Thread.currentThread().getName());

      if (GlobalSettings.getInstance().isAutoregisterJMXBeans()) {
        // Register JMX MBeans. If an error occurs, don't propogate exception
        try {
          registerJMXBeans(new JMXPlatformImpl());
        } catch (Throwable t) {
          log.warn("Unable to register Dozer JMX MBeans with the PlatformMBeanServer.  Dozer will still function "
              + "normally, but management via JMX may not be available", t);
        }
      }

      isInitialized = true;
    }
  }

  public void destroy() {
    synchronized (this) {
      if (!isInitialized) {
        log.debug("Tried to destroy when no Dozer instance started.");
        return;
      }

      try {
        unregisterJMXBeans(new JMXPlatformImpl());
      } catch (Throwable e) {
        log.warn("Exception caught while disposing Dozer JMX MBeans.", e);
      }
      isInitialized = false;
    }
  }

  public boolean isInitialized() {
    return isInitialized;
  }

  // Auto register Dozer JMX mbeans for jdk 1.5 users
  private void registerJMXBeans(JMXPlatform platform) throws MalformedObjectNameException, InstanceNotFoundException,
      MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
    if (platform.isAvailable()) {
      platform.registerMBean(DOZER_STATISTICS_CONTROLLER, new DozerStatisticsController());
      platform.registerMBean(DOZER_ADMIN_CONTROLLER, new DozerAdminController());
    } else {
      InitLogger.log(log, "jdk1.5 management classes unavailable. Dozer JMX MBeans will not be auto registered.");
    }
  }

  private void unregisterJMXBeans(JMXPlatform platform) throws MBeanRegistrationException, MalformedObjectNameException {
    if (platform.isAvailable()) {
      platform.unregisterMBean(DOZER_ADMIN_CONTROLLER);
      platform.unregisterMBean(DOZER_STATISTICS_CONTROLLER);
    } else {
      log.warn("jdk1.5 management classes unavailable.");
    }
  }

  public static DozerInitializer getInstance() {
    return instance;
  }

}
