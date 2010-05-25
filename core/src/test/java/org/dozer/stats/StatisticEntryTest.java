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
public class StatisticEntryTest extends AbstractDozerTest {

  @Test
  public void testConstructor() throws Exception {
    String key = "testkey";
    StatisticEntry entry = new StatisticEntry(key);

    assertEquals("invalid key", key, entry.getKey());
    assertEquals("invalid initial value", 0, entry.getValue());
  }

  @Test
  public void testEquals() throws Exception {
    String key = "testkey";
    StatisticEntry entry = new StatisticEntry(key);
    StatisticEntry entry2 = new StatisticEntry(key);

    assertEquals("objects should be equal", entry, entry2);
    assertEquals("objects hashcode should be equal", entry.hashCode(), entry2.hashCode());
  }

  @Test
  public void testIncrement() throws Exception {
    String key = "testkey";
    StatisticEntry entry = new StatisticEntry(key);

    entry.increment(100);
    assertEquals("invalid value after 2nd increment", 100, entry.getValue());
  }

}
