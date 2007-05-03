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
package net.sf.dozer.util.mapping.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.dozer.util.mapping.fieldmap.ClassMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal class that determines the appropriate class mapping to be used for the source and destination object being mapped.  Only intended for internal use. 
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class ClassMapFinder {

  private static final Log log = LogFactory.getLog(ClassMapFinder.class);

  public ClassMap findClassMap(Map customMappings, Object sourceObj, Class destClass, String mapId, boolean isInstance) {
    ClassMap mapping = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(sourceObj.getClass(), destClass,
        mapId));

    // determine if it is an Interface or Abstract Class
    // iterate through the class maps and see if this class has any sub classes
    // that are mapped to the source class OR
    // any of the source classes super classes
    // if we find a mapping don't even bother walking the tree
    if (mapping == null) {
      mapping = findInterfaceOrAbstractMapping(customMappings, destClass, sourceObj, mapId);
    }

    if (mapping != null) {
      return mapping;
    }

    // one more try...
    // if the mapId is not null looking up a map is easy
    if (mapId != null && mapping == null) {
      // probably a more efficient way to do this...
      Iterator iter = customMappings.keySet().iterator();
      ClassMap classMap = null;
      while (iter.hasNext()) {
        String key = (String) iter.next();
        classMap = (ClassMap) customMappings.get(key);
        if (StringUtils.equals(classMap.getMapId(), mapId)) {
          return classMap;
        }
      }
      log.info("No ClassMap found for mapId:" + mapId);
    }

    return mapping;
  }

  public List findInterfaceMappings(Map customMappings, Class sourceClass, Class destClass) {
    // If no existing cache entry is found, determine super type mapping and
    // store in cache
    // Get interfaces
    Class[] sourceInterfaces = sourceClass.getInterfaces();
    Class[] destInterfaces = destClass.getInterfaces();
    List interfaceMaps = new ArrayList();
    int size = destInterfaces.length;
    for (int i = 0; i < size; i++) {
      // see if the source class is mapped to the dest class
      ClassMap interfaceClassMap = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(sourceClass,
          destInterfaces[i]));
      if (interfaceClassMap != null) {
        interfaceMaps.add(interfaceClassMap);
      }
    }

    for (int i = 0; i < sourceInterfaces.length; i++) {
      // see if the source class is mapped to the dest class
      ClassMap interfaceClassMap = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(sourceInterfaces[i],
          destClass));
      if (interfaceClassMap != null) {
        interfaceMaps.add(interfaceClassMap);
      }
    }

    // multiple levels of custom mapping processed in wrong order - need to
    // reverse
    Collections.reverse(interfaceMaps);

    return interfaceMaps;
  }

  // TODO: what is logic difference between this method and
  // checkForInterfaceMappingsInCustomMappings. By the names
  // these methods seem to be trying to accomplish the same thing with a
  // different implementation. Also, one takes a map-id
  // and the other doesnt ?? somewhat confusing
  public ClassMap findInterfaceOrAbstractMapping(Map customMappings, Class destClass, Object sourceObj, String mapId) {
    ClassMap newClassMap = null;
    Class newClass = null;
    // Use object array for keys to avoid any rare thread synchronization
    // issues while iterating
    // over the custom mappings. See bug #1550275.
    Object[] keys = customMappings.keySet().toArray();
    for (int i = 0; i < keys.length; i++) {
      ClassMap map = (ClassMap) customMappings.get(keys[i]);
      Class dest = map.getDestClass().getClassToMap();

      // is Assignable?
      // now that we have the a sub class for the abstract class or
      // interface we need to
      // verify that the source class in the map IS a super class of the
      // source object...or the source object itself. .
      if (destClass.isAssignableFrom(dest)
          && (map.getSourceClass().getClassToMap().isAssignableFrom(sourceObj.getClass()) || map.getSourceClass()
              .getClassToMap().isInstance(sourceObj))
          // look for most specific mapping
          && (newClass == null || newClass.isAssignableFrom(dest))
          && (mapId == null || ((String) keys[i]).endsWith(mapId))) {
        newClassMap = map;
        newClass = dest;
      }
    }
    return newClassMap;
  }

}