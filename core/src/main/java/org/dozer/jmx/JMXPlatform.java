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

import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

/**
 *
 * Representation of hosting JMX Platform.
 *
 * @author dmitry.buzdin
 */
public interface JMXPlatform {

  /**
   * Checks whether JMX management platform is available on the JVM
   * @return true if KMX platform is available
   */
  boolean isAvailable();

  void registerMBean(String name, Object bean) throws MalformedObjectNameException, MBeanRegistrationException, NotCompliantMBeanException;

  void unregisterMBean(String name) throws MBeanRegistrationException, MalformedObjectNameException;

}
