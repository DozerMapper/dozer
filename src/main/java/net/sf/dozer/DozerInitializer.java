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
package net.sf.dozer;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import net.sf.dozer.config.GlobalSettings;
import net.sf.dozer.jmx.DozerAdminController;
import net.sf.dozer.jmx.DozerStatisticsController;
import net.sf.dozer.util.InitLogger;
import net.sf.dozer.util.MapperConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal class that performs one time Dozer initializations. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public class DozerInitializer {

  private static final Log log = LogFactory.getLog(DozerInitializer.class);

  private static boolean isInitialized = false;

  public static void init() {
    if (isInitialized) {
      return;
    }

    InitLogger.log(log, "Initializing Dozer.  Version: " + MapperConstants.CURRENT_VERSION + ", Thread Name:"
        + Thread.currentThread().getName());

    // Auto Register Dozer JMX MBeans
    if (GlobalSettings.getInstance().isAutoregisterJMXBeans()) {
      // Check to see that 1.5 JMX mgmt classes are available prior to attempting to regiser JMX MBeans
      if (!areJMXMgmtClassesAvailable()) {
        InitLogger.log(log, "jdk1.5 management classes unavailable.  Dozer JMX MBeans will not be auto registered.");
      } else {
        // Register JMX MBeans. If an error occurs, don't propogate exception
        try {
          registerJMXBeans();
        } catch (Throwable t) {
          log.warn("Unable to register Dozer JMX MBeans with the PlatformMBeanServer.  Dozer will still function "
              + "normally, but management via JMX may not be available", t);
        }
      }
    }

    isInitialized = true;
  }

  private static boolean areJMXMgmtClassesAvailable() {
    boolean result = false;

    try {
      Class.forName("java.lang.management.ManagementFactory");
      Class.forName("javax.management.ObjectName");
      Class.forName("javax.management.MBeanServer");
      result = true;
    } catch (Throwable t) {
      result = false;
    }

    return result;
  }

  protected static boolean isInitialized() {
    return isInitialized;
  }

  // Auto register Dozer JMX mbeans for jdk 1.5 users
  private static void registerJMXBeans() throws MalformedObjectNameException, InstanceNotFoundException,
      MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
    registerJMXBean("net.sf.dozer.util.mapping.jmx:type=DozerStatisticsController", new DozerStatisticsController());
    registerJMXBean("net.sf.dozer.util.mapping.jmx:type=DozerAdminController", new DozerAdminController());
  }

  /*
   * Auto register Dozer JMX mbean for jdk 1.5 users. Need to use reflection so that the code base is backwards
   * compatible with older jdk's
   */
  private static void registerJMXBean(String mbeanName, Object mbean) throws MalformedObjectNameException,
      InstanceNotFoundException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
    ObjectName mbeanObjectName = new ObjectName(mbeanName);
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    boolean isMBeanRegistered = mbs.isRegistered(mbeanObjectName);

    // Check if MBean is already registered. If so, unregister it. This is primarily to handle app redeployment use cases.    
    if (isMBeanRegistered) {
      InitLogger.log(log, "Dozer JMX MBean [" + mbeanName + "] already registered.  Unregistering the existing MBean.");
      mbs.unregisterMBean(mbeanObjectName);
    }

    // Register mbean
    mbs.registerMBean(mbean, mbeanObjectName);
    InitLogger.log(log, "Dozer JMX MBean [" + mbeanName + "] auto registered with the Platform MBean Server");
  }

}
