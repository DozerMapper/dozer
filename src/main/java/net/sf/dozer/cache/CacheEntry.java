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
package net.sf.dozer.cache;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Internal class that represents one entry in the cache. Holds the cache value, unique key for lookup, and creation
 * time. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public class CacheEntry {

  private final Object key;
  private final Object value;

  public CacheEntry(Object key, Object value) {
    this.key = key;
    this.value = value;
  }

  public Object getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

  public int hashCode() {
    return key.hashCode();
  }

  public boolean equals(Object object) {
    if ((this == object)) {
      return true;
    }
    if (!(object instanceof CacheEntry)) {
      return false;
    }
    CacheEntry entry = (CacheEntry) object;
    return this.getKey().equals(entry.getKey());
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}
