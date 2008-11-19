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
package net.sf.dozer.util.mapping.converters;

import java.util.ArrayList;
import java.util.List;

import net.sf.dozer.util.mapping.cache.Cache;
import net.sf.dozer.util.mapping.cache.CacheEntry;
import net.sf.dozer.util.mapping.cache.CacheKeyFactory;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Internal class for holding custom converter definitions. Only intended for internal use.
 * 
 * @author sullins.ben
 */
public class CustomConverterContainer {

  private List converters = new ArrayList();

  public List getConverters() {
    return converters;
  }

  public void setConverters(List converters) {
    this.converters = converters;
  }

  public void addConverter(CustomConverterDescription converter) {
    getConverters().add(converter);
  }

  public Class getCustomConverter(Class srcClass, Class destClass, Cache converterByDestTypeCache) {
    // If no converters have been specified, no point in continuing. Just return.
    if (converters == null || converters.size() < 1) {
      return null;
    }

    // Let's see if the incoming class is a primitive:
    Class src = srcClass;
    if (srcClass.isPrimitive()) {
      Class c = getWrapper(srcClass);
      if (c != null) {
        src = c;
      }
    }

    Class dest = destClass;
    if (dest.isPrimitive()) {
      Class c = getWrapper(destClass);
      if (c != null) {
        dest = c;
      }
    }

    // Check cache first
    Object cacheKey = CacheKeyFactory.createKey(dest, src);
    CacheEntry cacheEntry = converterByDestTypeCache.get(cacheKey);
    if (cacheEntry != null) {
      return (Class) cacheEntry.getValue();
    }

    // Otherwise, loop through custom converters and look for a match. Also, store the result in the cache
    Class result = null;
    long size = converters.size();
    for (int i = 0; i < size; i++) {
      CustomConverterDescription customConverter = (CustomConverterDescription) converters.get(i);
      Class classA = customConverter.getClassA();
      Class classB = customConverter.getClassB();

      // we check to see if the destination class is the same as classA defined in the converter mapping xml.
      // we next check if the source class is the same as classA defined in the converter mapping xml.
      // we also to check to see if it is assignable to either. We then perform these checks in the other direction for classB
      if ((classA.isAssignableFrom(dest) && classB.isAssignableFrom(src))
          || (classA.isAssignableFrom(src) && classB.isAssignableFrom(dest))) {
        result = customConverter.getType();
      }
    }
    cacheEntry = new CacheEntry(cacheKey, result);
    converterByDestTypeCache.put(cacheEntry);
    return result;
  }

  private Class getWrapper(Class c) {
    return PrimitiveOrWrapperConverter.wrapPrimitive(c);
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}