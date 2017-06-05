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
package org.dozer.osgi;

import org.dozer.DozerInitializer;
import org.dozer.config.BeanContainer;
import org.dozer.config.GlobalSettings;
import org.dozer.stats.StatisticsManagerImpl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Buzdin
 */
public final class Activator implements BundleActivator {

  private final Logger log = LoggerFactory.getLogger(Activator.class);
  private static Bundle bundle;

  public void start(BundleContext bundleContext) throws Exception {
    log.info("Starting Dozer OSGi bundle");
    bundle = bundleContext.getBundle();
    // todo all this should be moved to DozerMapperBeanBuilder
    OSGiClassLoader classLoader = new OSGiClassLoader(bundleContext);
    BeanContainer.getInstance().setClassLoader(classLoader);
    GlobalSettings globalSettings = new GlobalSettings(BeanContainer.getInstance().getClassLoader());
    new DozerInitializer().init(
            globalSettings,
            Activator.class.getClassLoader(),
            new StatisticsManagerImpl(globalSettings));
  }

  public static Bundle getBundle() {
    return bundle;
  }

  public void stop(BundleContext bundleContext) throws Exception {
    log.info("Dozer OSGi bundle stopped");
  }

}
