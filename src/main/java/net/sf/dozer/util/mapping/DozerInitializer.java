package net.sf.dozer.util.mapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.exception.DozerRuntimeException;
import net.sf.dozer.util.mapping.jmx.DozerAdminController;
import net.sf.dozer.util.mapping.jmx.DozerStatisticsController;
import net.sf.dozer.util.mapping.util.InitLogger;
import net.sf.dozer.util.mapping.util.MapperConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DozerInitializer {

  private static final Log log = LogFactory.getLog(DozerInitializer.class);
  
  private static boolean isInitialized = false;

  public static void init() {
    if (!isInitialized) {
      InitLogger.log(log, "Initializing Dozer.  Version: "
          + MapperConstants.CURRENT_VERSION + ", Thread Name:" + Thread.currentThread().getName()
          + ", Is this JDK 1.5.x?:" + GlobalSettings.getInstance().isJava5());
      
      if (GlobalSettings.getInstance().isAutoregisterJMXBeans()) {
        registerJMXBeans();
      }
      
      isInitialized = true;
    }
  }
 
  /*
   * Auto register Dozer JMX mbeans for jdk 1.5 users. Need to use reflection so that the code base
   * is backwards compatible for older jdk's
   */
  private static void registerJMXBeans() {
    Class mgmtFactoryClass = null;
    Class objectNameClass = null;
    Class mbsClass = null;

    try {
      
      mgmtFactoryClass = Class.forName("java.lang.management.ManagementFactory");
      objectNameClass = Class.forName("javax.management.ObjectName");
      mbsClass = Class.forName("javax.management.MBeanServer");
      
    } catch (Throwable e) {
      InitLogger.log(log, "jdk1.5 management classes unavailable.  Dozer JMX MBeans will not be auto registered.");
    }
    
    try {
      
      Object mbs = mgmtFactoryClass.getMethod("getPlatformMBeanServer", null).invoke(null, null);
      Method registerMBeanMethod = mbsClass.getMethod("registerMBean", new Class[] {Object.class, objectNameClass});
      Constructor objectNameConstructor = objectNameClass.getConstructor(new Class[]{String.class});
      String mbeanName = "net.sf.dozer.util.mapping.jmx:type=DozerStatisticsController";
      String mbean2Name = "net.sf.dozer.util.mapping.jmx:type=DozerAdminController";
      DozerStatisticsController mbean = new DozerStatisticsController();
      DozerAdminController mbean2 = new DozerAdminController();
      
      registerMBeanMethod.invoke(mbs, new Object[] {mbean, objectNameConstructor.newInstance(new Object[] {mbeanName})});
      InitLogger.log(log, "Dozer JMX MBean [" + mbeanName + "] auto registered with the Platform MBean Server");
      
      registerMBeanMethod.invoke(mbs, new Object[] {mbean2, objectNameConstructor.newInstance(new Object[] {mbean2Name})});
      InitLogger.log(log, "Dozer JMX MBean [" + mbean2Name + "] auto registered with the Platform MBean Server");
      
    } catch (Exception e) {
      throw new DozerRuntimeException(e);
    }
  }

}
