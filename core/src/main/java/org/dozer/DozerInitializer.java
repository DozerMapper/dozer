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

import org.dozer.config.BeanContainer;
import org.dozer.config.GlobalSettings;
import org.dozer.jmx.DozerAdminController;
import org.dozer.jmx.DozerStatisticsController;
import org.dozer.jmx.JMXPlatform;
import org.dozer.jmx.JMXPlatformImpl;
import org.dozer.util.DefaultClassLoader;
import org.dozer.util.DozerClassLoader;
import org.dozer.util.DozerConstants;
import org.dozer.util.DozerProxyResolver;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;
import org.dozer.loader.xml.ExpressionElementReader;
import org.dozer.loader.xml.ELEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import java.util.ServiceLoader;

/**
 * Internal class that performs one time Dozer initializations. Only intended for internal use.
 * Registers internal JMX MBeans if those are enabled in the configuration.
 *
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public final class DozerInitializer {

  private static final Logger log = LoggerFactory.getLogger(DozerInitializer.class);

  private static final String DOZER_STATISTICS_CONTROLLER = "org.dozer.jmx:type=DozerStatisticsController";
  private static final String DOZER_ADMIN_CONTROLLER = "org.dozer.jmx:type=DozerAdminController";

  private static final DozerInitializer instance = new DozerInitializer();

  private volatile boolean isInitialized = false;

  private DozerInitializer() {
  }

  public void init() {
    init(getClass().getClassLoader());
  }

  public void init(ClassLoader classLoader) {
    // Multiple threads may try to initialize simultaniously
    synchronized (this) {
      if (isInitialized) {
        log.debug("Tried to perform initialization when Dozer was already started.");
        return;
      }

      log.info("Initializing Dozer. Version: {}, Thread Name: {}",
              DozerConstants.CURRENT_VERSION, Thread.currentThread().getName());

      GlobalSettings globalSettings = GlobalSettings.getInstance();
      initialize(globalSettings, classLoader);

      isInitialized = true;
    }
  }

  void initialize(GlobalSettings globalSettings, ClassLoader classLoader) {
    if (globalSettings.isAutoregisterJMXBeans()) {
      // Register JMX MBeans. If an error occurs, don't propagate exception
      try {
        registerJMXBeans(new JMXPlatformImpl());
      } catch (Throwable t) {
        log.warn("Unable to register Dozer JMX MBeans with the PlatformMBeanServer.  Dozer will still function "
            + "normally, but management via JMX may not be available", t);
      }
    }

    String classLoaderName = globalSettings.getClassLoaderName();
    String proxyResolverName = globalSettings.getProxyResolverName();

    DefaultClassLoader defaultClassLoader = new DefaultClassLoader(classLoader);
    BeanContainer beanContainer = BeanContainer.getInstance();

    Class<? extends DozerClassLoader> classLoaderType = loadBeanType(classLoaderName, defaultClassLoader, DozerClassLoader.class);
    Class<? extends DozerProxyResolver> proxyResolverType = loadBeanType(proxyResolverName, defaultClassLoader, DozerProxyResolver.class);

    // TODO Chicken-egg problem - investigate
//    DozerClassLoader classLoaderBean = ReflectionUtils.newInstance(classLoaderType);
    DozerClassLoader classLoaderBean = defaultClassLoader;
    DozerProxyResolver proxyResolverBean = ReflectionUtils.newInstance(proxyResolverType);

    beanContainer.setClassLoader(classLoaderBean);
    beanContainer.setProxyResolver(proxyResolverBean);

    if (globalSettings.isElEnabled()) {
      ELEngine engine = new ELEngine();
      engine.init();
      beanContainer.setElEngine(engine);
      beanContainer.setElementReader(new ExpressionElementReader(engine));
    }

    for (DozerModule module : ServiceLoader.load(DozerModule.class)) {
      module.init();
    }
  }

  private <T> Class<? extends T> loadBeanType(String classLoaderName, DozerClassLoader classLoader, Class<T> iface) {
    Class<?> beanType = classLoader.loadClass(classLoaderName);
    if (beanType != null && !iface.isAssignableFrom(beanType)) {
      MappingUtils.throwMappingException("Incompatible types: " + iface.getName() + " and " + classLoaderName);
    }
    return (Class<? extends T>) beanType;
  }

  /**
   * Performs framework shutdown sequence. Includes de-registering existing Dozer JMX MBeans.
   */
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

  private void registerJMXBeans(JMXPlatform platform) throws MalformedObjectNameException, InstanceNotFoundException,
      MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
    if (platform.isAvailable()) {
      platform.registerMBean(DOZER_STATISTICS_CONTROLLER, new DozerStatisticsController());
      platform.registerMBean(DOZER_ADMIN_CONTROLLER, new DozerAdminController());
    } else {
      log.warn("jdk1.5 jmx management classes unavailable. Dozer JMX MBeans will not be auto registered.");
    }
  }

  private void unregisterJMXBeans(JMXPlatform platform) throws MBeanRegistrationException, MalformedObjectNameException {
    if (platform.isAvailable()) {
      platform.unregisterMBean(DOZER_ADMIN_CONTROLLER);
      platform.unregisterMBean(DOZER_STATISTICS_CONTROLLER);
    } else {
      log.warn("jdk1.5 jmx management classes unavailable.");
    }
  }

  public static DozerInitializer getInstance() {
    return instance;
  }

}
