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

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.dozer.AbstractDozerTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class JMXPlatformImplTest extends AbstractDozerTest {

  private static final String DOZER_ADMIN_CONTROLLER = "org.dozer.jmx:type=DozerAdminController";

  private MBeanServer mbs;
  private JMXPlatformImpl platform;

  @Before
  public void setUp() throws Exception {
    mbs = ManagementFactory.getPlatformMBeanServer();
    platform = new JMXPlatformImpl();
  }

  @Test
  public void testAvailable() {
    assertTrue(platform.isAvailable());
  }

  @Test
  public void testRegister() throws Exception {
    assertFalse(mbs.isRegistered(new ObjectName(DOZER_ADMIN_CONTROLLER)));

    platform.registerMBean(DOZER_ADMIN_CONTROLLER, new DozerAdminController());
    assertTrue(mbs.isRegistered(new ObjectName(DOZER_ADMIN_CONTROLLER)));

    platform.unregisterMBean(DOZER_ADMIN_CONTROLLER);
    assertFalse(mbs.isRegistered(new ObjectName(DOZER_ADMIN_CONTROLLER)));
  }

  @Test
  public void testDoubleRegister() throws Exception {
    platform.registerMBean(DOZER_ADMIN_CONTROLLER, new DozerAdminController());
    platform.registerMBean(DOZER_ADMIN_CONTROLLER, new DozerAdminController());
  }

  @Test
  public void testUselessUnregister() throws Exception {
    platform.unregisterMBean(DOZER_ADMIN_CONTROLLER);
  }

  @After
  public void tearDown() throws Exception {
    try {
      mbs.unregisterMBean(new ObjectName(DOZER_ADMIN_CONTROLLER));
    } catch (Exception e) {
    }
  }

}
