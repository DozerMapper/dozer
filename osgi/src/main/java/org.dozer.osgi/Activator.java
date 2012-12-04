package org.dozer.osgi;

import org.dozer.DozerInitializer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Buzdin
 */
public class Activator implements BundleActivator {

  private static final Logger log = LoggerFactory.getLogger(Activator.class);

  public void start(BundleContext bundleContext) throws Exception {
    log.info("Activating Dozer OSGi bundle");
    DozerInitializer.getInstance().init(Activator.class.getClassLoader());
  }

  public void stop(BundleContext bundleContext) throws Exception {

  }

}
