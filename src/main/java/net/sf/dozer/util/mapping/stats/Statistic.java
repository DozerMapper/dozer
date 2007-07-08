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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Internal class that provides an interface to a single statistic. Holds all of the statistic entries for the
 * statistic. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public class Statistic {
  private final String type;
  private final Map entriesMap = new HashMap();

  public Statistic(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void clear() {
    entriesMap.clear();
  }

  public Set getEntries() {
    return new HashSet(entriesMap.values());
  }

  public void addEntry(StatisticEntry statEntry) {
    if (statEntry == null) {
      throw new IllegalArgumentException("Statistic Entry cannot be null");
    }
    entriesMap.put(statEntry.getKey(), statEntry);
  }

  public StatisticEntry getEntry() {
    return getEntry(type);
  }

  public StatisticEntry getEntry(Object entryKey) {
    return (StatisticEntry) entriesMap.get(entryKey);
  }

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

  public int hashCode() {
    return new HashCodeBuilder().append(getType()).toHashCode();
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
