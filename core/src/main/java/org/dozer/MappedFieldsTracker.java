/*
 * Copyright 2005-2009 the original author or authors.
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
package org.dozer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps track of mapped object during this mapping process execution.
 * Objects, which are referenced multiple types in object hierarchy will be fetched from here
 * to retain referential integrity of resulting object graph.
 *
 * @author dmitry.buzdin
 */
public class MappedFieldsTracker {

  // Hash Code is ignored as it can serve application specific needs 
  private final Map<Object, List<Object>> mappedFields = new IdentityHashMap<Object, List<Object>>();

  public void put(Object src, Object dest) {
    List<Object> mappedTo = mappedFields.get(src);
    if (mappedTo == null) {
      mappedTo = new ArrayList<Object>();
      mappedFields.put(src, mappedTo);
    }
    if (!mappedTo.contains(dest)) {
      mappedTo.add(dest);
    }
  }

  public Object getMappedValue(Object src, Class<?> destType) {
    List<Object> alreadyMappedValues = mappedFields.get(src);
    if (alreadyMappedValues != null) {
      for (Object alreadyMappedValue : alreadyMappedValues) {
        if (alreadyMappedValue != null) {
          // 1664984 - bi-directionnal mapping with sets & subclasses
          if (destType.isAssignableFrom(alreadyMappedValue.getClass())) {
            // Source value has already been mapped to the required destFieldType.
            return alreadyMappedValue;
          }
        }
      }
    }
    return null;
  }
}
