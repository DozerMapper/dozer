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

import org.dozer.AbstractDozerTest;
import org.dozer.config.GlobalSettings;
import org.dozer.util.DozerConstants;

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
public class DozerAdminControllerTest extends AbstractDozerTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private GlobalSettings globalSettingsMock;

  @InjectMocks
  private DozerAdminController controller;

  @Test
  public void testSetStatisticsEnabled() throws Exception {
    boolean isStatisticsEnabled = controller.isStatisticsEnabled();
    controller.setStatisticsEnabled(!isStatisticsEnabled);
    verify(globalSettingsMock).setStatisticsEnabled(!isStatisticsEnabled);
  }

  @Test
  public void testGetCurrentVersion() throws Exception {
    assertEquals("incorrect current version", DozerConstants.CURRENT_VERSION, controller.getCurrentVersion());
  }

}
