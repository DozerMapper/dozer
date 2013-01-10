/*
 * Copyright 2005-2013 the original author or authors.
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
package org.dozer.classmap;

import org.apache.commons.lang3.StringUtils;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal class that determines the appropriate class mapping to be used for
 * the source and destination object being mapped. Only intended for internal
 * use.
 *
 * @author tierney.matt
 * @author garsombke.franz
 */
public class ClassMappings {

  // Cache key --> Mapping Structure
  private Map<String, ClassMap> classMappings = new ConcurrentHashMap<String, ClassMap>();
  private Map<String, ClassMap> defaultMappings = new ConcurrentHashMap<String, ClassMap>();
  private ClassMapKeyFactory keyFactory;

  public ClassMappings() {
    keyFactory = new ClassMapKeyFactory();
  }

  // Default mappings. May be overwritten due to multiple threads generating same mapping
  public void addDefault(Class<?> srcClass, Class<?> destClass, ClassMap classMap) {
    String key = keyFactory.createKey(srcClass, destClass);
    classMappings.put(key, classMap);
    defaultMappings.put(key, classMap);
  }

  public void add(Class<?> srcClass, Class<?> destClass, ClassMap classMap) {
    String key = keyFactory.createKey(srcClass, destClass);
    addDefined(key, classMap);
  }

  public void add(Class<?> srcClass, Class<?> destClass, String mapId, ClassMap classMap) {
    String key = keyFactory.createKey(srcClass, destClass, mapId);
    addDefined(key, classMap);
  }

  public void addAll(ClassMappings additionalClassMappings) {
    Map<String, ClassMap> newMappings = additionalClassMappings.getAll();
    for (Entry<String, ClassMap> entry : newMappings.entrySet()) {
      addDefined(entry.getKey(), entry.getValue());
    }
  }

  private void addDefined(String key, ClassMap classMap) {
    ClassMap result = classMappings.put(key, classMap);
    failOnDuplicate(result, classMap);
    defaultMappings.remove(key);
  }

  public void failOnDuplicate(Object result, ClassMap classMap) {
    if (result != null && !classMap.getSrcClassName().equals(classMap.getDestClassName())) {
      throw new IllegalArgumentException("Duplicate Class Mapping Found. Source: " + classMap.getSrcClassName()
              + " Destination: " + classMap.getDestClassName() + " map-id: " + classMap.getMapId());
    }
  }

  public Map<String, ClassMap> getAll() {
    return new HashMap<String, ClassMap>(classMappings);
  }

  public long size() {
    return classMappings.size();
  }

  public ClassMap find(Class<?> srcClass, Class<?> destClass) {
    return classMappings.get(keyFactory.createKey(srcClass, destClass));
  }

  public boolean contains(Class<?> srcClass, Class<?> destClass, String mapId) {
    String key = keyFactory.createKey(srcClass, destClass, mapId);
    return classMappings.containsKey(key);
  }

  private boolean isNonDefault(String key) {
    return classMappings.containsKey(key) && !defaultMappings.containsKey(key);
  }

  /**
   * This method should not return ClassMap for superclass of srcClass because it hierarchy is processed in the main code,
   * But could return for it's interface or concrete class
   * @return ClassMap
   */
  public ClassMap find(Class<?> srcClass, Class<?> destClass, String mapId, boolean canResultDestClassBeSubClass) {
    final String key = keyFactory.createKey(srcClass, destClass, mapId);
    ClassMap mapping = classMappings.get(key);

    if (mapping == null) {
      mapping = findInterfaceMapping(destClass, srcClass, mapId, canResultDestClassBeSubClass);
    }

    // one more try...
    // if the mapId is not null looking up a map is easy
    if (!MappingUtils.isBlankOrNull(mapId) && mapping == null) {
      // probably a more efficient way to do this...
      for (Entry<String, ClassMap> entry : classMappings.entrySet()) {
        ClassMap classMap = entry.getValue();
        if (StringUtils.equals(classMap.getMapId(), mapId)
                && classMap.getSrcClassToMap().isAssignableFrom(srcClass)
                && classMap.getDestClassToMap().isAssignableFrom(destClass)) {
          return classMap;
        } else if (StringUtils.equals(classMap.getMapId(), mapId) && srcClass.equals(destClass)) {
          return classMap;
        }
      }

      // If map-id was specified and mapping was not found, then fail
      MappingUtils.throwMappingException("Class mapping not found by map-id: " + key);
    }

    return mapping;
  }

  // Look for an interface mapping
  private ClassMap findInterfaceMapping(Class<?> destClass, Class<?> srcClass, String mapId, boolean canResultDestClassBeSubClass) {
    // Use object array for keys to avoid any rare thread synchronization issues
    // while iterating over the custom mappings.
    // See bug #1550275.
    ClassMap interfaceMappingClassMap = null;
    ClassMap bestSubclassMappingClassMap = null;

    //polymorphic search has more priority, so if bestSubclassMappingClassMap is found - interface search is not needed
    //if one result for interface search found - interface mapping is not needed anymore, only polymorphic
    boolean interfaceSearchActive = true;

    String[] keys = classMappings.keySet().toArray(new String[classMappings.keySet().size()]);
    for (String key : keys) {
      ClassMap map = classMappings.get(key);
      Class<?> mappingDestClass = map.getDestClassToMap();
      Class<?> mappingSrcClass = map.getSrcClassToMap();

      if ((mapId == null && map.getMapId() != null) || (mapId != null && !mapId.equals(map.getMapId()))) {
        continue;
      }

      if (interfaceSearchActive) {
        if (isInterfaceImplementation(srcClass, mappingSrcClass)) {
          if (isInterfaceImplementation(destClass, mappingDestClass) || destClass.equals(mappingDestClass)) {
            interfaceMappingClassMap = map;
            interfaceSearchActive = false;
          }
        }
      }

      if (MappingUtils.getRealClass(srcClass).equals(mappingSrcClass)) {
        if (interfaceSearchActive) {

          if (isInterfaceImplementation(destClass, mappingDestClass) ||
                  isAbstract(destClass) && destClass.isAssignableFrom(mappingDestClass)) {
            interfaceMappingClassMap = map;
            interfaceSearchActive = false;
          }

        }

        //if exists better polymorphic class map which defined by user - use it
        if (canResultDestClassBeSubClass && isNonDefault(key) && destClass.isAssignableFrom(mappingDestClass)) {
          if (bestSubclassMappingClassMap != null) {
            if (bestSubclassMappingClassMap.getDestClassToMap().isAssignableFrom(mappingDestClass)) {
              bestSubclassMappingClassMap = map; //find the reached element in hierarchy
            }
          } else {
            bestSubclassMappingClassMap = map;
            interfaceSearchActive = false;
          }
        }
      }
    }

    return bestSubclassMappingClassMap != null ? bestSubclassMappingClassMap : interfaceMappingClassMap;
  }

  private boolean isInterfaceImplementation(Class<?> type, Class<?> mappingType) {
    return mappingType.isInterface() && mappingType.isAssignableFrom(type);
  }

  private static boolean isAbstract(Class<?> destClass) {
    return Modifier.isAbstract(destClass.getModifiers());
  }

}
