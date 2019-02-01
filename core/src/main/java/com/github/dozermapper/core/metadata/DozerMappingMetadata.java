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
package com.github.dozermapper.core.metadata;

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.ClassMappings;

/**
 * Internal use only.
 */
public final class DozerMappingMetadata implements MappingMetadata {

    private final ClassMappings classMappings;

    public DozerMappingMetadata(ClassMappings classMappings) {
        this.classMappings = classMappings;
    }

    public List<ClassMappingMetadata> getClassMappings() {
        List<ClassMappingMetadata> classMapMetadata = new ArrayList<>();

        for (ClassMap classMap : classMappings.getAll().values()) {
            classMapMetadata.add(new DozerClassMappingMetadata(classMap));
        }
        return classMapMetadata;
    }

    public List<ClassMappingMetadata> getClassMappingsBySourceName(String sourceClassName) {
        if (sourceClassName == null) {
            throw new IllegalArgumentException("The source class name cannot be null.");
        }

        return buildMappingListBySourceName(sourceClassName);
    }

    public List<ClassMappingMetadata> getClassMappingsByDestinationName(String destinationClassName) {
        if (destinationClassName == null) {
            throw new IllegalArgumentException("The destination class name cannot be null.");
        }

        return buildMappingListByDestinationName(destinationClassName);
    }

    public ClassMappingMetadata getClassMappingByName(String sourceClassName, String destinationClassName) {
        if (sourceClassName == null || destinationClassName == null) {
            throw new IllegalArgumentException("The source and destination class names need to be specified.");
        }

        return findMappingByName(sourceClassName, destinationClassName);
    }

    public List<ClassMappingMetadata> getClassMappingsBySource(Class<?> sourceClass) {
        if (sourceClass == null) {
            throw new IllegalArgumentException("The source class cannt be null.");
        }

        return buildMappingListBySourceName(sourceClass.getName());
    }

    public List<ClassMappingMetadata> getClassMappingsByDestination(Class<?> destinationClass) {
        if (destinationClass == null) {
            throw new IllegalArgumentException("The destination class cannot be null.");
        }

        return buildMappingListByDestinationName(destinationClass.getName());
    }

    public ClassMappingMetadata getClassMapping(Class<?> sourceClass, Class<?> destinationClass) {
        if (sourceClass == null || destinationClass == null) {
            throw new IllegalArgumentException("The source and destination classes need to be specified.");
        }

        return findMappingByName(sourceClass.getName(), destinationClass.getName());
    }

    private List<ClassMappingMetadata> buildMappingListBySourceName(String sourceClassName) {
        List<ClassMappingMetadata> classMapMetadata = new ArrayList<>();
        for (ClassMap classMap : classMappings.getAll().values()) {
            if (classMap.getSrcClassName().equals(sourceClassName)) {
                classMapMetadata.add(new DozerClassMappingMetadata(classMap));
            }
        }
        return classMapMetadata;
    }

    private List<ClassMappingMetadata> buildMappingListByDestinationName(String destinationClassName) {
        List<ClassMappingMetadata> classMapMetadata = new ArrayList<>();
        for (ClassMap classMap : classMappings.getAll().values()) {
            if (classMap.getDestClassName().equals(destinationClassName)) {
                classMapMetadata.add(new DozerClassMappingMetadata(classMap));
            }
        }
        return classMapMetadata;
    }

    private ClassMappingMetadata findMappingByName(String sourceClassName, String destinationClassName) {
        for (ClassMap classMap : classMappings.getAll().values()) {
            if (classMap.getSrcClassName().equals(sourceClassName)
                && classMap.getDestClassName().equals(destinationClassName)) {
                return new DozerClassMappingMetadata(classMap);
            }
        }
        throw new MetadataLookupException("No mapping definition found for: " + sourceClassName
                                          + " -> " + destinationClassName + ".");
    }

}
