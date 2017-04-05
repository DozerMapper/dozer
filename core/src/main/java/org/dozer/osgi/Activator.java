/**
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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Buzdin
 */
public final class Activator implements BundleActivator {

  private final Logger log = LoggerFactory.getLogger(Activator.class);

  public void start(BundleContext bundleContext) throws Exception {
    log.info("Starting Dozer OSGi bundle");
    OSGiClassLoader classLoader = new OSGiClassLoader(bundleContext);
    BeanContainer.getInstance().setClassLoader(classLoader);
    DozerInitializer.getInstance().init(Activator.class.getClassLoader());
  }

  public void stop(BundleContext bundleContext) throws Exception {
    log.info("Dozer OSGi bundle stopped");
  }

}
