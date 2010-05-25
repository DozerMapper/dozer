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

import java.util.Set;

import org.dozer.AbstractDozerTest;
import org.dozer.stats.StatisticType;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class DozerStatisticsControllerTest extends AbstractDozerTest {

  private DozerStatisticsController controller;

  @Override
  @Before
  public void setUp() throws Exception {
    controller = new DozerStatisticsController();
  }

  @Test
  public void testIsStatisticsEnabled() throws Exception {
    boolean isStatisticsEnabled = controller.isStatisticsEnabled();
    controller.setStatisticsEnabled(!isStatisticsEnabled);
    assertEquals("statistics enabled value was not updated", !isStatisticsEnabled, controller.isStatisticsEnabled());
  }

  @Test
  public void testGetStatisticValues() throws Exception {
    // just verify these values are zero
    controller.clearAll();
    assertEquals(0, controller.getMappingSuccessCount());
    assertEquals(0, controller.getMappingFailureCount());
    assertEquals(0, controller.getMapperInstancesCount());
    assertEquals(0, controller.getMappingOverallTimeInMillis());
    assertEquals(0, controller.getFieldMappingSuccessCount());
    assertEquals(0, controller.getFieldMappingFailureCount());
    assertEquals(0, controller.getFieldMappingFailureIgnoredCount());
  }

  @Test
  public void testGetStatisticEntries() throws Exception {
    controller.clearAll();
    Set<?> entries = controller.getStatisticEntries(StatisticType.CACHE_HIT_COUNT);
    assertEquals("incorrect entries size", 0, entries.size());
  }

}
