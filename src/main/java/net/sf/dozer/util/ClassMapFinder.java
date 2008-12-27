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
package net.sf.dozer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.dozer.classmap.ClassMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal class that determines the appropriate class mapping to be used for the source and destination object being
 * mapped. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class ClassMapFinder {

  private static final Log log = LogFactory.getLog(ClassMapFinder.class);

  public static ClassMap findClassMap(Map<String, ClassMap> customMappings, Class<?> srcClass, Class<?> destClass, String mapId) {
    Class<?> srcLookupClass = MappingUtils.getRealClass(srcClass);
    Class<?> destLookupClass = MappingUtils.getRealClass(destClass);

    ClassMap mapping = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(srcLookupClass, destLookupClass, mapId));

    if (mapping == null) {
      mapping = findInterfaceMapping(customMappings, destClass, srcClass, mapId);
    }

    // one more try...
    // if the mapId is not null looking up a map is easy
    if (mapId != null && mapping == null) {
      // probably a more efficient way to do this...
      Iterator iter = customMappings.entrySet().iterator();
      ClassMap classMap = null;
      while (iter.hasNext()) {
        Map.Entry entry = (Entry) iter.next();
        classMap = (ClassMap) entry.getValue();
        if (StringUtils.equals(classMap.getMapId(), mapId)) {
          return classMap;
        }
      }
      log.info("No ClassMap found for mapId:" + mapId);
    }

    return mapping;
  }

  public static List<ClassMap> findInterfaceMappings(Map<String, ClassMap> customMappings, Class<?> srcClass, Class<?> destClass) {
    // If no existing cache entry is found, determine super type mapping and store in cache
    // Get interfaces
    Class[] srcInterfaces = srcClass.getInterfaces();
    Class[] destInterfaces = destClass.getInterfaces();
    List<ClassMap> interfaceMaps = new ArrayList<ClassMap>();
    int size = destInterfaces.length;
    for (int i = 0; i < size; i++) {
      // see if the source class is mapped to the dest class
      ClassMap interfaceClassMap = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(srcClass, destInterfaces[i]));
      if (interfaceClassMap != null) {
        interfaceMaps.add(interfaceClassMap);
      }
    }

    for (int i = 0; i < srcInterfaces.length; i++) {
      // see if the source class is mapped to the dest class
      ClassMap interfaceClassMap = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(srcInterfaces[i], destClass));
      if (interfaceClassMap != null) {
        interfaceMaps.add(interfaceClassMap);
      }
    }

    // multiple levels of custom mapping processed in wrong order - need to reverse
    Collections.reverse(interfaceMaps);

    return interfaceMaps;
  }

  // Look for an interface mapping
  private static ClassMap findInterfaceMapping(Map<String, ClassMap> customMappings, Class<?> destClass, Class<?> srcClass, String mapId) {
    // Use object array for keys to avoid any rare thread synchronization issues while iterating over the custom mappings. 
    // See bug #1550275.
    Object[] keys = customMappings.keySet().toArray();
    for (int i = 0; i < keys.length; i++) {
      ClassMap map = (ClassMap) customMappings.get(keys[i]);
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