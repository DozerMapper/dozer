/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.stats;

import java.util.HashSet;
import java.util.Set;

import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.MappingException;

/**
 * @author tierney.matt
 */
public class StatisticManagerTest extends AbstractDozerTest {
  private StatisticsManager statMgr;

  protected void setUp() throws Exception {
    super.setUp();
    statMgr = new StatisticsManager();
    statMgr.setStatisticsEnabled(true);
  }

  public void testAddAndGetStatistic() {
    String type = getRandomString();
    Statistic stat = new Statistic(type);
    statMgr.addStatistic(stat);

    assertEquals("invalid stat size", 1, statMgr.getStatistics().size());
    assertEquals("invalid stat found", stat, statMgr.getStatistic(type));
    assertTrue("invalid stat ref found", stat == statMgr.getStatistic(type));
  }

  public void testSetStatisticsEnabled() {
    boolean value = true;
    if (statMgr.isStatisticsEnabled()) {
      value = false;
    }
    statMgr.setStatisticsEnabled(value);
    assertEquals("invalid stats enabled value", value, statMgr.isStatisticsEnabled());
  }

  public void testClearAll() {
    String type = getRandomString();
    statMgr.addStatistic(new Statistic(type));
    statMgr.addStatistic(new Statistic(type + "2"));
    assertEquals("invalid initial stat size", 2, statMgr.getStatistics().size());
    statMgr.clearAll();
    assertEquals("invalid stat size", 0, statMgr.getStatistics().size());
  }

  public void testGetStatiticNotFound() {
    try {
      statMgr.getStatistic(getRandomString());
      fail("should have thrown not found exception");
    } catch (MappingException e) {
    }
  }

  public void testGetStatisticTypes() {
    String type = getRandomString();
    String type2 = type + "-2";
    statMgr.addStatistic(new Statistic(type));
    statMgr.addStatistic(new Statistic(type2));

    Set expected = new HashSet();
    expected.add(type);
    expected.add(type2);

    assertEquals("invalid stat types found", expected, statMgr.getStatisticTypes());
  }

  public void testIncrementMissingParams() {
    try {
      statMgr.increment(null, "test", 1);
      fail("missing type should have thrown exception");
    } catch (IllegalArgumentException e) {
    }

    try {
      statMgr.increment("test2", null, 1);
      fail("missing entry key should have thrown exception");
    } catch (IllegalArgumentException e) {
    }
  }

  public void testGetStatisticValue() {
    String type = getRandomString();
    Statistic stat = new Statistic(type);
    String entryKey = getRandomString();
    stat.addEntry(new StatisticEntry(entryKey));

    statMgr.addStatistic(stat);
    statMgr.increment(type, entryKey, 100);

    assertEquals("invalid entry value", 100, statMgr.getStatisticValue(type));
  }

  public void testGetStatisticValueException() {
    String type = getRandomString();
    Statistic stat = new Statistic(type);

    stat.addEntry(new StatisticEntry(getRandomString()));
    stat.addEntry(new StatisticEntry(getRandomString()));
    statMgr.addStatistic(stat);

    try {
      statMgr.getStatisticValue(type);
      fail("trying to get stat value for type that has more than 1 entry should throw exception");
    } catch (IllegalArgumentException e) {
    }
  }

  public void testAddDuplicateStatistic() {
    String type = getRandomString();
    statMgr.addStatistic(new Statistic(type));

    try {
      statMgr.addStatistic(new Statistic(type));
      fail("add duplicate stat should have thrown exception");
    } catch (IllegalArgumentException e) {
    }
  }

  public void testIncrementUnknownTypeAndKey() {
    String type = getRandomString();
    String entryKey = getRandomString();
    long incrementValue = 130;
    statMgr.increment(type, entryKey, incrementValue);

    assertEquals("invalid stat entry value", incrementValue, statMgr.getStatisticValue(type));
  }

}
