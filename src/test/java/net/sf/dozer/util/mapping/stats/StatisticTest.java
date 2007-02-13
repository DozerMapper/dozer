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


import net.sf.dozer.util.mapping.DozerTestBase;

/**
 * @author tierney.matt
 */
public class StatisticTest extends DozerTestBase {
  
  public void testConstructor() throws Exception {
    String type = getRandomString();
    Statistic stat = new Statistic(type);
    
    assertEquals("invalid type", type, stat.getType());
  }
  
  public void testEquals() throws Exception {
    String type = getRandomString();
    Statistic stat = new Statistic(type);
    Statistic stat2 = new Statistic(type);
    
    assertEquals("objects should be equal", stat, stat2);
    assertEquals("objects hashcode should be equal", stat.hashCode(), stat2.hashCode());
  }
  
  public void testAddGetEntries() throws Exception {
    Statistic stat = new Statistic(getRandomString());
    int numEntriesToAdd = 5;
    for (int i = 0; i < numEntriesToAdd; i++) {
      String key = "testkey" + String.valueOf(i);
      StatisticEntry entry = new StatisticEntry(key);
      stat.addEntry(entry);
      
      assertEquals("invalid entry size", i + 1, stat.getEntries().size());
      
      StatisticEntry entry2 = stat.getEntry(key);
      assertNotNull("stat entry should not be null", entry2);
      assertEquals("stat entries should be equal", entry, entry2);
      assertSame("stat entries should be same instance", entry, entry2);
    }
    assertEquals("invlid stat size", numEntriesToAdd, stat.getEntries().size());
  }
  
  public void testClear() throws Exception {
    Statistic stat = new Statistic(getRandomString());
    StatisticEntry entry = new StatisticEntry(getRandomString());
    stat.addEntry(entry);
    
    assertEquals("stat should contain entry", 1, stat.getEntries().size());
    stat.clear();
    assertEquals("stat entries should have been cleared", 0, stat.getEntries().size());
  }
  
  public void testGetEntryWithDefaultKey() throws Exception {
    String type = getRandomString();
    Statistic stat = new Statistic(type);
    StatisticEntry entry = new StatisticEntry(type);
    stat.addEntry(entry);
    
    assertEquals("invalid entry found", entry, stat.getEntry());
  }
  
  public void testGetEntryWithDefaultKeyNotFound() throws Exception {
    Statistic stat = new Statistic(getRandomString());
    StatisticEntry entry = new StatisticEntry(getRandomString());
    stat.addEntry(entry);
    
    assertNull("entry should not have been found", stat.getEntry());
  }
  
  public void testAddNull() {
    Statistic stat = new Statistic(getRandomString());
    try {
      stat.addEntry(null);
      fail("should have thrown exception");
    } catch (IllegalArgumentException e) {
    }
  }
}
