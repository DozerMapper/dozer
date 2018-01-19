/*
 * Copyright 2005-2017 Dozer Project
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

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.dozer.builder.DestBeanBuilderCreator;
import org.dozer.classmap.generator.BeanMappingGenerator;
import org.dozer.config.BeanContainer;
import org.dozer.config.Settings;
import org.dozer.config.SettingsDefaults;
import org.dozer.factory.DestBeanCreator;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.util.DefaultClassLoader;
import org.dozer.util.DozerClassLoader;
import org.dozer.util.DozerConstants;
import org.dozer.util.DozerProxyResolver;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class that performs one time Dozer initializations. Only intended for internal use.
 *
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public final class DozerInitializer {

  private final Logger log = LoggerFactory.getLogger(DozerInitializer.class);

  private volatile boolean isInitialized;

  public DozerInitializer() {
  }

  public void init(Settings settings, BeanContainer beanContainer,
                   DestBeanBuilderCreator destBeanBuilderCreator, BeanMappingGenerator beanMappingGenerator, PropertyDescriptorFactory propertyDescriptorFactory,
                   DestBeanCreator destBeanCreator) {
    init(settings, getClass().getClassLoader().getParent(), beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);
  }

  public void init(Settings settings, ClassLoader classLoader, BeanContainer beanContainer,
                   DestBeanBuilderCreator destBeanBuilderCreator, BeanMappingGenerator beanMappingGenerator, PropertyDescriptorFactory propertyDescriptorFactory,
                   DestBeanCreator destBeanCreator) {
    // Multiple threads may try to initialize simultaneously
    synchronized (this) {
      if (isInitialized) {
        log.debug("Tried to perform initialization when Dozer was already started.");
        return;
      }

      log.info("Initializing Dozer. Version: {}, Thread Name: {}",
              DozerConstants.CURRENT_VERSION, Thread.currentThread().getName());

      initialize(settings, classLoader, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);

      isInitialized = true;
    }
  }

  void initialize(Settings settings, ClassLoader classLoader, BeanContainer beanContainer,
                  DestBeanBuilderCreator destBeanBuilderCreator, BeanMappingGenerator beanMappingGenerator, PropertyDescriptorFactory propertyDescriptorFactory,
                  DestBeanCreator destBeanCreator) {
    registerClassLoader(settings, classLoader, beanContainer);
    registerProxyResolver(settings, beanContainer);

    try {
      ServiceLoader<DozerModule> services = ServiceLoader.load(DozerModule.class);
      for (DozerModule module : services) {
        module.init();
        module.init(beanContainer, destBeanCreator, propertyDescriptorFactory);

        destBeanBuilderCreator.addPluggedStrategies(module.getBeanBuilderCreationStrategies());
        beanMappingGenerator.addPluggedFieldDetectors(module.getBeanFieldsDetectors());
        propertyDescriptorFactory.addPluggedPropertyDescriptorCreationStrategies(module.getPropertyDescriptorCreationStrategies());
      }
    } catch (ServiceConfigurationError ex) {
      log.error("{}", ex.getMessage());
    }
  }

  private void registerClassLoader(Settings settings, ClassLoader classLoader, BeanContainer beanContainer) {
    String classLoaderName = settings.getClassLoaderBeanName();
    if (!SettingsDefaults.CLASS_LOADER_BEAN.equals(classLoaderName)) {
      DefaultClassLoader defaultClassLoader = new DefaultClassLoader(classLoader);
      Class<? extends DozerClassLoader> classLoaderType = loadBeanType(classLoaderName, defaultClassLoader, DozerClassLoader.class);
      DozerClassLoader classLoaderBean = ReflectionUtils.newInstance(classLoaderType);
      beanContainer.setClassLoader(classLoaderBean);
    }
  }

  private void registerProxyResolver(Settings settings, BeanContainer beanContainer) {
    String proxyResolverName = settings.getProxyResolverBeanName();
    if (!SettingsDefaults.PROXY_RESOLVER_BEAN.equals(proxyResolverName)) {
      DozerClassLoader initializedClassLoader = beanContainer.getClassLoader();
      Class<? extends DozerProxyResolver> proxyResolverType = loadBeanType(proxyResolverName, initializedClassLoader, DozerProxyResolver.class);
      DozerProxyResolver proxyResolverBean = ReflectionUtils.newInstance(proxyResolverType);
      beanContainer.setProxyResolver(proxyResolverBean);
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
   * Performs framework shutdown sequence.
   */
  public void destroy(Settings settings) {
    synchronized (this) {
      if (!isInitialized) {
        log.debug("Tried to destroy when no Dozer instance started.");
        return;
      }

      isInitialized = false;
    }
  }

  public boolean isInitialized() {
    return isInitialized;
  }
}
