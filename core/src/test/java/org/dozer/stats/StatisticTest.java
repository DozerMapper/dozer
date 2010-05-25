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
public class StatisticTest extends AbstractDozerTest {

  @Test
  public void testConstructor() throws Exception {
    StatisticType type = StatisticType.CACHE_HIT_COUNT;
    Statistic stat = new Statistic(type);

    assertEquals("invalid type", type, stat.getType());
  }

  @Test
  public void testEquals() throws Exception {
    StatisticType type = StatisticType.MAPPING_FAILURE_TYPE_COUNT;
    Statistic stat = new Statistic(type);
    Statistic stat2 = new Statistic(type);

    assertEquals("objects should be equal", stat, stat2);
    assertEquals("objects hashcode should be equal", stat.hashCode(), stat2.hashCode());
  }

  @Test
  public void testAddGetEntries() throws Exception {
    Statistic stat = new Statistic(StatisticType.CUSTOM_CONVERTER_TIME);
    int numEntriesToAdd = 5;
    for (int i = 0; i < numEntriesToAdd; i++) {
      String key = "testkey" + String.valueOf(i);
      StatisticEntry entry= stat.increment(key, 1);

      assertEquals("invalid entry size", i + 1, stat.getEntries().size());

      StatisticEntry entry2 = stat.getEntry(key);
      assertNotNull("stat entry should not be null", entry2);
      assertEquals("stat entries should be equal", entry, entry2);
      assertSame("stat entries should be same instance", entry, entry2);
    }
    assertEquals("invlid stat size", numEntriesToAdd, stat.getEntries().size());
  }

  @Test
  public void testClear() throws Exception {
    Statistic stat = new Statistic(StatisticType.CUSTOM_CONVERTER_TIME);
    stat.increment(getRandomString(), 1);

    assertEquals("stat should contain entry", 1, stat.getEntries().size());
    stat.clear();
    assertEquals("stat entries should have been cleared", 0, stat.getEntries().size());
  }

  @Test
  public void testGetEntryWithDefaultKey() throws Exception {
    StatisticType type = StatisticType.FIELD_MAPPING_SUCCESS_COUNT;
    Statistic stat = new Statistic(type);
    StatisticEntry entry = new StatisticEntry(type);
    stat.increment(type, 1);

    assertEquals("invalid entry found", entry, stat.getEntry(type));
  }

  @Test
  public void testGetEntryWithDefaultKeyNotFound() throws Exception {
    Statistic stat = new Statistic(StatisticType.MAPPING_FAILURE_TYPE_COUNT);
    StatisticEntry entry = new StatisticEntry(getRandomString());
    stat.increment(entry, 0);

    assertNull("entry should not have been found", stat.getEntry(StatisticType.MAPPING_FAILURE_TYPE_COUNT));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNull() {
    Statistic stat = new Statistic(StatisticType.CACHE_MISS_COUNT);
    stat.increment(null, 0);
  }
}
