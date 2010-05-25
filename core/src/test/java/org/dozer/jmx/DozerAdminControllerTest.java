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
package org.dozer.jmx;

import org.dozer.AbstractDozerTest;
import org.dozer.util.DozerConstants;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class DozerAdminControllerTest extends AbstractDozerTest {

  private DozerAdminController controller;

  @Override
  @Before
  public void setUp() throws Exception {
    controller = new DozerAdminController();
  }

  @Test
  public void testSetStatisticsEnabled() throws Exception {
    boolean isStatisticsEnabled = controller.isStatisticsEnabled();
    controller.setStatisticsEnabled(!isStatisticsEnabled);
    assertEquals("statistics enabled value was not updated", !isStatisticsEnabled, controller.isStatisticsEnabled());
  }

  @Test
  public void testGetCurrentVersion() throws Exception {
    assertEquals("incorrect current version", DozerConstants.CURRENT_VERSION, controller.getCurrentVersion());
  }

}
