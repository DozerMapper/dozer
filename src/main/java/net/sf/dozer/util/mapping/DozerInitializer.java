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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.jmx.DozerAdminController;
import net.sf.dozer.util.mapping.jmx.DozerStatisticsController;
import net.sf.dozer.util.mapping.util.InitLogger;
import net.sf.dozer.util.mapping.util.MapperConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Performs one time Dozer initializations
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
        + Thread.currentThread().getName() + ", Is this JDK 1.5.x?:" + GlobalSettings.getInstance().isJava5());

    /*
     * Auto Register Dozer JMX MBeans
     */
    if (GlobalSettings.getInstance().isAutoregisterJMXBeans()) {
      // Check to see that 1.5 JMX mgmt classes are available prior to attempting to regiser JMX MBeans
      if (!areJMXMgmtClassesAvailable()) {
        InitLogger.log(log, "jdk1.5 management classes unavailable.  Dozer JMX MBeans will not be auto registered.");
      } else {
        // Register JMX MBeans. If an error occurs, don't propogate exception
        try {
          registerJMXBeans();
        } catch (Throwable t) {
          log.warn("Unable to register Dozer JMX MBeans with the PlatformMBeanServer.  Dozer will still function " +
              "normally, but management via JMX may not be available", t);
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

  /*
   * Auto register Dozer JMX mbeans for jdk 1.5 users
   */
  private static void registerJMXBeans() throws ClassNotFoundException, InvocationTargetException,
      NoSuchMethodException, IllegalAccessException, InstantiationException {
    registerJMXBean("net.sf.dozer.util.mapping.jmx:type=DozerStatisticsController", new DozerStatisticsController());
    registerJMXBean("net.sf.dozer.util.mapping.jmx:type=DozerAdminController", new DozerAdminController());
  }

  /*
   * Auto register Dozer JMX mbean for jdk 1.5 users. Need to use reflection so that the code base is backwards
   * compatible with older jdk's
   */
  private static void registerJMXBean(String mbeanName, Object mbean) throws ClassNotFoundException, InvocationTargetException,
      NoSuchMethodException, IllegalAccessException, InstantiationException {
    Class mgmtFactoryClass = Class.forName("java.lang.management.ManagementFactory");
    Class objectNameClass = Class.forName("javax.management.ObjectName");
    Class mbsClass = Class.forName("javax.management.MBeanServer");

    Constructor objectNameConstructor = objectNameClass.getConstructor(new Class[] { String.class });
    Object mbeanObjectName = objectNameConstructor.newInstance(new Object[] { mbeanName });
    Object mbs = mgmtFactoryClass.getMethod("getPlatformMBeanServer", null).invoke(null, null);

    //Check if MBean is already registered.  If so, unregister it.  This is primarily to handle app redeployment use cases.
    Method isMBeanRegisteredMethod = mbsClass.getMethod("isRegistered", new Class[] { objectNameClass });
    Boolean isMBeanRegistered = (Boolean) isMBeanRegisteredMethod.invoke(mbs, new Object[] { mbeanObjectName });
    if (isMBeanRegistered.booleanValue()) {
      InitLogger.log(log, "Dozer JMX MBean [" + mbeanName + "] already registered.  Unregistering the existing MBean.");
      Method unregisterMBeanMethod = mbsClass.getMethod("unregisterMBean", new Class[] { objectNameClass });
      unregisterMBeanMethod.invoke(mbs, new Object[] { mbeanObjectName });
    }
    
    //Register mbean
    Method registerMBeanMethod = mbsClass.getMethod("registerMBean", new Class[] { Object.class, objectNameClass });
    registerMBeanMethod.invoke(mbs, new Object[] { mbean, mbeanObjectName });
    InitLogger.log(log, "Dozer JMX MBean [" + mbeanName + "] auto registered with the Platform MBean Server");
  }

}
