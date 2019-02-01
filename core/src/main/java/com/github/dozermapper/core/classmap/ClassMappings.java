/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.classmap;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Internal class that determines the appropriate class mapping to be used for
 * the source and destination object being mapped. Only intended for internal
 * use.
 */
public class ClassMappings {

    // Cache key --> Mapping Structure
    private ConcurrentMap<String, ClassMap> classMappings = new ConcurrentHashMap<>();
    private ClassMapKeyFactory keyFactory;
    private final BeanContainer beanContainer;

    public ClassMappings(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
        keyFactory = new ClassMapKeyFactory(beanContainer);
    }

    // Default mappings. May be ovewritten due to multiple threads generating same mapping
    public void addDefault(Class<?> srcClass, Class<?> destClass, ClassMap classMap) {
        classMappings.put(keyFactory.createKey(srcClass, destClass), classMap);
    }

    public void add(Class<?> srcClass, Class<?> destClass, ClassMap classMap) {
        ClassMap result = classMappings.put(keyFactory.createKey(srcClass, destClass), classMap);
        failOnDuplicate(result, classMap);
    }

    public void add(Class<?> srcClass, Class<?> destClass, String mapId, ClassMap classMap) {
        ClassMap result = classMappings.put(keyFactory.createKey(srcClass, destClass, mapId), classMap);
        failOnDuplicate(result, classMap);
    }

    public void addAll(ClassMappings additionalClassMappings) {
        Map<String, ClassMap> newMappings = additionalClassMappings.getAll();
        for (Entry<String, ClassMap> entry : newMappings.entrySet()) {
            ClassMap result = classMappings.put(entry.getKey(), entry.getValue());
            failOnDuplicate(result, entry.getValue());
        }
    }

    public void failOnDuplicate(Object result, ClassMap classMap) {
        if (result != null && !classMap.getSrcClassName().equals(classMap.getDestClassName())) {
            throw new IllegalArgumentException("Duplicate Class Mapping Found. Source: " + classMap.getSrcClassName()
                                               + " Destination: " + classMap.getDestClassName() + " map-id: " + classMap.getMapId());
        }
    }

    public Map<String, ClassMap> getAll() {
        return new HashMap<>(classMappings);
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

    public ClassMap find(Class<?> srcClass, Class<?> destClass, String mapId) {
        final String key = keyFactory.createKey(srcClass, destClass, mapId);
        ClassMap mapping = classMappings.get(key);

        if (mapping == null) {
            mapping = findInterfaceMapping(destClass, srcClass, mapId);
            if (mapping != null) {
                ClassMap previous = classMappings.putIfAbsent(keyFactory.createKey(srcClass, destClass, mapId), mapping);
                if (previous != null) {
                    mapping = previous;
                }
            }
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
    private ClassMap findInterfaceMapping(Class<?> destClass, Class<?> srcClass, String mapId) {
        // Use object array for keys to avoid any rare thread synchronization issues
        // while iterating over the custom mappings.
        // See bug #1550275.
        Object[] keys = classMappings.keySet().toArray();
        for (Object key : keys) {
            ClassMap map = classMappings.get(key);
            Class<?> mappingDestClass = map.getDestClassToMap();
            Class<?> mappingSrcClass = map.getSrcClassToMap();

            if ((mapId == null && map.getMapId() != null) || (mapId != null && !mapId.equals(map.getMapId()))) {
                continue;
            }

            if (isInterfaceImplementation(srcClass, mappingSrcClass)) {
                if (isInterfaceImplementation(destClass, mappingDestClass)) {
                    return map;
                } else if (destClass.equals(mappingDestClass)) {
                    return map;
                }
            }

            // Destination could be an abstract type. Picking up the best concrete type to use.
            if ((destClass.isAssignableFrom(mappingDestClass) && isAbstract(destClass))
                || (isInterfaceImplementation(destClass, mappingDestClass))) {
                if (MappingUtils.getRealClass(srcClass, beanContainer).equals(mappingSrcClass)) {
                    return map;
                }
            }

        }
        return null;
    }

    private boolean isInterfaceImplementation(Class<?> type, Class<?> mappingType) {
        return mappingType.isInterface() && mappingType.isAssignableFrom(type);
    }

    private static boolean isAbstract(Class<?> destClass) {
        return Modifier.isAbstract(destClass.getModifiers());
    }

}
