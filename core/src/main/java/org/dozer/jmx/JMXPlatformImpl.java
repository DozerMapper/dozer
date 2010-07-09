/*
 * Copyright 2005-2009 the original author or authors.
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
package org.dozer.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 *
 * Default JMX Platform implementation working with java.lang.management.ManagementFactory.
 *
 * @author dmitry.buzdin
 */
public class JMXPlatformImpl implements JMXPlatform {

  private static final Logger log = LoggerFactory.getLogger(JMXPlatformImpl.class);

  public boolean isAvailable() {
    try {
      Class.forName("java.lang.management.ManagementFactory");
      Class.forName("javax.management.ObjectName");
      Class.forName("javax.management.MBeanServer");
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

  public void registerMBean(String name, Object bean) throws MalformedObjectNameException, MBeanRegistrationException, NotCompliantMBeanException {
    ObjectName mbeanObjectName = new ObjectName(name);
    unregister(name, mbeanObjectName);
    register(name, bean, mbeanObjectName);
  }

  public void unregisterMBean(String name) throws MBeanRegistrationException, MalformedObjectNameException {
    ObjectName mbeanObjectName = new ObjectName(name);
    unregister(name, mbeanObjectName);
  }
  
  private void register(String name, Object bean, ObjectName mbeanObjectName) throws MBeanRegistrationException, NotCompliantMBeanException {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    try {
      mbs.registerMBean(bean, mbeanObjectName);
      log.info("Dozer JMX MBean [" + name + "] auto registered with the Platform MBean Server");
    } catch (InstanceAlreadyExistsException e) {
      log.info("JMX MBean instance exists, unable to overwrite [{}].", name);
    }
  }

  private void unregister(String name, ObjectName mbeanObjectName) throws MBeanRegistrationException {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    boolean isMBeanRegistered = mbs.isRegistered(mbeanObjectName);
    if (isMBeanRegistered) {
      log.info("Unregistering existing Dozer JMX MBean [{}].", name);
      try {
        mbs.unregisterMBean(mbeanObjectName);
      } catch (InstanceNotFoundException e) {
        log.info("JMX MBean not found to unregister [{}].", name);
      }
    }
  }

}
