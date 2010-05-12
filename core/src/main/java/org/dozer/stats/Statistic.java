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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Internal class that provides an interface to a single statistic. Holds all of the statistic entries for the
 * statistic. Only intended for internal use.
 *
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public class Statistic {

  private final StatisticType type;
  private final ConcurrentMap<Object, StatisticEntry> entriesMap = new ConcurrentHashMap<Object, StatisticEntry>();

  public Statistic(StatisticType type) {
    this.type = type;
  }

  public StatisticType getType() {
    return type;
  }

  public StatisticEntry increment(Object statisticEntryKey, long value) {
    if (statisticEntryKey == null) {
      throw new IllegalArgumentException("statistic entry key must be specified");
    }

    // Get the StatisticEntry object which contains the actual value.
    // If it doesn't already exist, create it so that it can be incremented
    StatisticEntry statisticEntry = entriesMap.get(statisticEntryKey);
    if (statisticEntry == null) {
      StatisticEntry newStatisticEntry = new StatisticEntry(statisticEntryKey);
      statisticEntry = entriesMap.putIfAbsent(statisticEntryKey, newStatisticEntry);
      if (statisticEntry == null) {
        statisticEntry = newStatisticEntry;
      }
    }
    // Increment the actual value
    statisticEntry.increment(value);
    return statisticEntry;
  }

  public void clear() {
    entriesMap.clear();
  }

  public Set<StatisticEntry> getEntries() {
    return new HashSet<StatisticEntry>(entriesMap.values());
  }

  public StatisticEntry getEntry(Object entryKey) {
    return entriesMap.get(entryKey);
  }

  public long getStatisticValue(Object entryKey) {
    StatisticEntry statisticEntry = entriesMap.get(entryKey);
    return statisticEntry != null ? statisticEntry.getValue() : 0;
  }

  @Override
  public boolean equals(Object object) {
    if ((this == object)) {
      return true;
    }
    if (!(object instanceof Statistic)) {
      return false;
    }
    Statistic entry = (Statistic) object;
    return new EqualsBuilder().append(this.getType(), entry.getType()).isEquals();
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
