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
package org.dozer.jmx;

import java.util.Set;
import org.dozer.AbstractDozerTest;
import org.dozer.config.GlobalSettings;
import org.dozer.stats.StatisticType;
import org.dozer.stats.StatisticsManager;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.Mockito.verify;

/**
 * @author tierney.matt
 */
public class DozerStatisticsControllerTest extends AbstractDozerTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private GlobalSettings globalSettingsMock;

  @Mock
  private StatisticsManager statisticsManager;

  @InjectMocks
  private DozerStatisticsController controller;

  @Test
  public void testIsStatisticsEnabled() throws Exception {
    boolean isStatisticsEnabled = controller.isStatisticsEnabled();
    controller.setStatisticsEnabled(!isStatisticsEnabled);
    verify(globalSettingsMock).setStatisticsEnabled(!isStatisticsEnabled);
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
