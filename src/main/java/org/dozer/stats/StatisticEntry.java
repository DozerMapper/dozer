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
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Internal class that represents one entry in the statistic. Holds the statistic value and unique key for lookup. Only
 * intended for internal use.
 * 
 * @author tierney.matt
 */
public class StatisticEntry {
  private final Object key;
  private long value = 0;

  public StatisticEntry(Object key) {
    this.key = key;
  }

  public Object getKey() {
    return key;
  }

  public long getValue() {
    return value;
  }

  public void increment() {
    increment(1);
  }

  public synchronized void increment(long value) {
    this.value += value;
  }

  @Override
  public boolean equals(Object object) {
    if ((this == object)) {
      return true;
    }
    if (!(object instanceof StatisticEntry)) {
      return false;
    }
    StatisticEntry entry = (StatisticEntry) object;
    return new EqualsBuilder().append(this.getKey(), entry.getKey()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getKey()).toHashCode();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
