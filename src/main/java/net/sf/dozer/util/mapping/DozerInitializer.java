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

public class DozerInitializer {

  private static final Log log = LogFactory.getLog(DozerInitializer.class);

  private static boolean isInitialized = false;

  public static void init() {
    if (!isInitialized) {
      InitLogger.log(log, "Initializing Dozer.  Version: "
          + MapperConstants.CURRENT_VERSION + ", Thread Name:" + Thread.currentThread().getName()
          + ", Is this JDK 1.5.x?:" + GlobalSettings.getInstance().isJava5());
      
      /*
       * Auto Register Dozer JMX MBeans 
       */
      if (GlobalSettings.getInstance().isAutoregisterJMXBeans()) {
        // Check to see that 1.5 JMX mgmt classes are available prior to attempting to regiser JMX MBeans
        if (!areJMXMgmtClassesAvailable()) {
          InitLogger.log(log, "jdk1.5 management classes unavailable.  Dozer JMX MBeans will not be auto registered.");
        } else {
          //Register JMX MBeans.  If an error occurs, don't propogate exception
          try {
            registerJMXBeans();  
          } catch (Throwable t) {
            log.warn("Unable to register Dozer JMX MBeans with the PlatformMBeanServer", t);
          }
        }
      }
      
      isInitialized = true;
    }
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

  /*
   * Auto register Dozer JMX mbeans for jdk 1.5 users. Need to use reflection so that the code base is backwards
   * compatible for older jdk's
   */
  private static void registerJMXBeans() throws ClassNotFoundException, InvocationTargetException,
      NoSuchMethodException, IllegalAccessException, InstantiationException {
    Class mgmtFactoryClass = Class.forName("java.lang.management.ManagementFactory");
    Class objectNameClass = Class.forName("javax.management.ObjectName");
    Class mbsClass = Class.forName("javax.management.MBeanServer");
    Object mbs = mgmtFactoryClass.getMethod("getPlatformMBeanServer", null).invoke(null, null);
    Method registerMBeanMethod = mbsClass.getMethod("registerMBean", new Class[] { Object.class, objectNameClass });
    Constructor objectNameConstructor = objectNameClass.getConstructor(new Class[] { String.class });
    String mbeanName = "net.sf.dozer.util.mapping.jmx:type=DozerStatisticsController";
    String mbean2Name = "net.sf.dozer.util.mapping.jmx:type=DozerAdminController";
    DozerStatisticsController mbean = new DozerStatisticsController();
    DozerAdminController mbean2 = new DozerAdminController();

    registerMBeanMethod.invoke(mbs,
        new Object[] { mbean, objectNameConstructor.newInstance(new Object[] { mbeanName }) });
    InitLogger.log(log, "Dozer JMX MBean [" + mbeanName + "] auto registered with the Platform MBean Server");

    registerMBeanMethod.invoke(mbs, new Object[] { mbean2,
        objectNameConstructor.newInstance(new Object[] { mbean2Name }) });
    InitLogger.log(log, "Dozer JMX MBean [" + mbean2Name + "] auto registered with the Platform MBean Server");
  }

}
