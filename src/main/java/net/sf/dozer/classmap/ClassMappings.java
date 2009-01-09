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
package net.sf.dozer.classmap;

import net.sf.dozer.cache.Cache;
import net.sf.dozer.cache.CacheEntry;
import net.sf.dozer.cache.CacheKeyFactory;
import net.sf.dozer.cache.DozerCache;
import net.sf.dozer.cache.DozerCacheManager;
import net.sf.dozer.cache.DozerCacheType;
import net.sf.dozer.util.MappingUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Internal class that determines the appropriate class mapping to be used for the source and destination object being
 * mapped. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class ClassMappings {

  private static final Log log = LogFactory.getLog(ClassMappings.class);

  private final DozerCacheManager cacheManager = new DozerCacheManager();
  private Cache <Object, ClassMap> cache;

  public ClassMappings() {
    cacheManager.addCache(DozerCacheType.CLASS_MAPPINGS.name(), 2000); // TODO Parameterize this
    cache = cacheManager.getCache(DozerCacheType.CLASS_MAPPINGS.name());
  }
  
  public void add(Class<?> srcClass, Class<?> destClass, ClassMap classMap) {
    cache.put(CacheKeyFactory.createKey(srcClass, destClass), classMap);
  }
  
  public void add(Class<?> srcClass, Class<?> destClass, String mapId, ClassMap classMap) {
    cache.put(CacheKeyFactory.createKey(srcClass, destClass, mapId), classMap);
  }
  
  public void addAll(ClassMappings classMappings) {
    ((DozerCache) cache).addEntries(classMappings.getAll());
  }
  
  public Collection<CacheEntry> getAll() {
    return ((DozerCache) cache).getEntries();
  }
  
  public long size() {
    return cache.getSize();
  }

  public ClassMap find(Class<?> srcClass, Class<?> destClass) {
    return cache.get(CacheKeyFactory.createKey(srcClass, destClass));
  }
  
  public boolean contains(Class<?> srcClass, Class<?> destClass, String mapId) {
    Object key = CacheKeyFactory.createKey(srcClass, destClass, mapId);
    return cache.containsKey(key);
  }
  
  public ClassMap find(Class<?> srcClass, Class<?> destClass, String mapId) {
    Class<?> srcLookupClass = MappingUtils.getRealClass(srcClass);
    Class<?> destLookupClass = MappingUtils.getRealClass(destClass);

    ClassMap mapping = cache.get(CacheKeyFactory.createKey(srcLookupClass, destLookupClass, mapId));

    if (mapping == null) {
      mapping = findInterfaceMapping(destClass, srcClass, mapId);
    }

    // one more try...
    // if the mapId is not null looking up a map is easy
    if (mapId != null && mapping == null) {
      // probably a more efficient way to do this...
      for (CacheEntry entry : getAll()) {
        ClassMap classMap = (ClassMap) entry.getValue();
        if (StringUtils.equals(classMap.getMapId(), mapId)) {
          return classMap;
        }
      }
      log.info("No ClassMap found for mapId:" + mapId);
    }

    return mapping;
  }

  public List<ClassMap> findInterfaceMappings(Class<?> srcClass, Class<?> destClass) {
    // If no existing cache entry is found, determine super type mapping and store in cache
    // Get interfaces
    Class<?>[] srcInterfaces = srcClass.getInterfaces();
    Class<?>[] destInterfaces = destClass.getInterfaces();
    List<ClassMap> interfaceMaps = new ArrayList<ClassMap>();
    int size = destInterfaces.length;
    for (int i = 0; i < size; i++) {
      // see if the source class is mapped to the dest class
      ClassMap interfaceClassMap = cache.get(CacheKeyFactory.createKey(srcClass, destInterfaces[i]));
      if (interfaceClassMap != null) {
        interfaceMaps.add(interfaceClassMap);
      }
    }

    for (Class<?> srcInterface : srcInterfaces) {
      // see if the source class is mapped to the dest class
      ClassMap interfaceClassMap = cache.get(CacheKeyFactory.createKey(srcInterface, destClass));
      if (interfaceClassMap != null) {
        interfaceMaps.add(interfaceClassMap);
      }
    }

    // multiple levels of custom mapping processed in wrong order - need to reverse
    Collections.reverse(interfaceMaps);

    return interfaceMaps;
  }

  // Look for an interface mapping
  private ClassMap findInterfaceMapping(Class<?> destClass, Class<?> srcClass, String mapId) {
    // Use object array for keys to avoid any rare thread synchronization issues while iterating over the custom mappings. 
    // See bug #1550275.
    Object[] keys = ((DozerCache) cache).keySet().toArray();
    for (Object key : keys) {
      ClassMap map = cache.get(key);
      Class<?> mappingDestClass = map.getDestClassToMap();
      Class<?> mappingSrcClass = map.getSrcClassToMap();

      if ((mapId == null && map.getMapId() != null) || (mapId != null && !mapId.equals(map.getMapId()))) {
        continue;
      }

      if (mappingSrcClass.isInterface() && mappingSrcClass.isAssignableFrom(srcClass)) {
        if (mappingDestClass.isInterface() && mappingDestClass.isAssignableFrom(destClass)) {
          return map;
        } else if (destClass.equals(mappingDestClass)) {
          return map;
        }
      }

      if (mappingDestClass.isInterface() && mappingDestClass.isAssignableFrom(destClass)) {
        if (srcClass.equals(mappingSrcClass)) {
          return map;
        }
      }

    }
    return null;
  }

}