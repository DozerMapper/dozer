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
package org.dozer.stats;

import org.dozer.AbstractDozerTest;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class GlobalStatisticsTest extends AbstractDozerTest {

  @Test
  public void testGetInstance() throws Exception {
    GlobalStatistics mgr = GlobalStatistics.getInstance();

    assertEquals("stat mgrs should be equal", mgr, GlobalStatistics.getInstance());
    assertSame("stat mgrs should be the same instance", mgr, GlobalStatistics.getInstance());
  }
}
