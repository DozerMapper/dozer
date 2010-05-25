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

import java.util.HashSet;
import java.util.Set;

import org.dozer.AbstractDozerTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class StatisticManagerTest extends AbstractDozerTest {
  private StatisticsManagerImpl statMgr;

  @Override
  @Before
  public void setUp() throws Exception {
    statMgr = new StatisticsManagerImpl();
    statMgr.setStatisticsEnabled(true);
  }

  @Test
  public void testAddAndGetStatistic() {
    StatisticType type = StatisticType.FIELD_MAPPING_FAILURE_COUNT;
    Statistic stat = statMgr.increment(type, 1);

    assertEquals("invalid stat size", 1, statMgr.getStatistics().size());
    assertEquals("invalid stat found", stat, statMgr.getStatistic(type));
    assertTrue("invalid stat ref found", stat == statMgr.getStatistic(type));
  }

  @Test
  public void testSetStatisticsEnabled() {
    boolean value = true;
    if (statMgr.isStatisticsEnabled()) {
      value = false;
    }
    statMgr.setStatisticsEnabled(value);
    assertEquals("invalid stats enabled value", value, statMgr.isStatisticsEnabled());
  }

  @Test
  public void testClearAll() {
    statMgr.increment(StatisticType.MAPPING_FAILURE_TYPE_COUNT, 1);
    statMgr.increment(StatisticType.CACHE_HIT_COUNT, 1);
    assertEquals("invalid initial stat size", 2, statMgr.getStatistics().size());
    statMgr.clearAll();
    assertEquals("invalid stat size", 0, statMgr.getStatistics().size());
  }

  @Test
  public void testGetStatisticTypes() {
    StatisticType type = StatisticType.CUSTOM_CONVERTER_TIME;
    StatisticType type2 = StatisticType.CACHE_HIT_COUNT;
    statMgr.increment(type, 1);
    statMgr.increment(type2, 1);

    Set<StatisticType> expected = new HashSet<StatisticType>();
    expected.add(type);
    expected.add(type2);

    assertEquals("invalid stat types found", expected, statMgr.getStatisticTypes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncrementMissingParams() {
    statMgr.increment(null, "test", 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncrementMissingParams2() {
    statMgr.increment(StatisticType.CACHE_HIT_COUNT, null, 1);
  }

  @Test
  public void testGetStatisticValue() {
    StatisticType type = StatisticType.MAPPER_INSTANCES_COUNT;
    statMgr.increment(type, 100);

    assertEquals("invalid entry value", 100, statMgr.getStatisticValue(type));
  }

  @Test
  public void testGetStatisticValueByEntry() {
    final Object key1 = new Object();
    final Object key2 = new Object();

    statMgr.increment(StatisticType.CACHE_HIT_COUNT, key1, 100);
    statMgr.increment(StatisticType.CACHE_HIT_COUNT, key2, 1);

    long result = statMgr.getStatisticValue(StatisticType.CACHE_HIT_COUNT, key1);
    assertEquals("invalid entry value", 100, result);

    result = statMgr.getStatisticValue(StatisticType.CACHE_HIT_COUNT, key2);
    assertEquals("invalid entry value", 1, result);

    assertEquals("invalid entry value", 0, statMgr.getStatisticValue(StatisticType.CACHE_HIT_COUNT, new Object()));
  }

  @Test
  public void testAddDuplicateStatistic() {
    StatisticType type = StatisticType.CUSTOM_CONVERTER_SUCCESS_COUNT;
    Statistic first = statMgr.increment(type);
    Statistic statistic = statMgr.increment(type);
    assertSame(first, statistic);
  }

  @Test
  public void testIncrementUnknownTypeAndKey() {
    StatisticType type = StatisticType.MAPPING_FAILURE_EX_TYPE_COUNT;
    String entryKey = getRandomString();
    long incrementValue = 130;
    statMgr.increment(type, entryKey, incrementValue);

    assertEquals("invalid stat entry value", 0, statMgr.getStatisticValue(type));
    assertEquals("invalid stat entry value", incrementValue, statMgr.getStatisticValue(type, entryKey));
  }

  final static int NTHREADS = 200;

  @Test
  public void testMultiThread() throws InterruptedException {
    final Thread[] tasks = new Thread[NTHREADS];
    for (int i = 0; i < tasks.length; ++i) {
      Thread task = new Thread("Round " + i) {
        @Override
        public void run() {
          for (int i = 0; i < NTHREADS; ++i) {
            statMgr.increment(StatisticType.MAPPING_FAILURE_EX_TYPE_COUNT, tasks[i]);
          }
        }
      };
      tasks[i] = task;
    }
    for (Thread task : tasks) {
      task.start();
    }
    for (Thread task : tasks) {
      task.join(10000);
      assertFalse("thread timeout", task.isAlive());
    }
    for (Thread task : tasks) {
      assertEquals("invalid threading result " + task.getName(), NTHREADS, statMgr.getStatisticValue(StatisticType.MAPPING_FAILURE_EX_TYPE_COUNT, task));
    }
  }
}
