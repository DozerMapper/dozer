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

import java.util.Collections;
import java.util.List;

/**
 * This interface can be used to query mapping metadata from the dozer internal data structures. It provides
 * read only access to all important aspects of the mapping information on class and field level.
 */
public interface MappingMetadata {

    MappingMetadata EMPTY = new MappingMetadata() {
        @Override
        public List<ClassMappingMetadata> getClassMappings() {
            return Collections.emptyList();
        }

        @Override
        public List<ClassMappingMetadata> getClassMappingsBySourceName(String sourceClassName) {
            return Collections.emptyList();
        }

        @Override
        public List<ClassMappingMetadata> getClassMappingsByDestinationName(String destinationClassName) {
            return Collections.emptyList();
        }

        @Override
        public ClassMappingMetadata getClassMappingByName(String sourceClassName, String destinationClassName) {
            return null;
        }

        @Override
        public List<ClassMappingMetadata> getClassMappingsBySource(Class<?> sourceClass) {
            return Collections.emptyList();
        }

        @Override
        public List<ClassMappingMetadata> getClassMappingsByDestination(Class<?> destinationClass) {
            return Collections.emptyList();
        }

        @Override
        public ClassMappingMetadata getClassMapping(Class<?> sourceClass, Class<?> destinationClass) {
            return null;
        }
    };

    /**
     * Obtains a list of all available mapping definitions.
     *
     * @return A list of {@code ClassMappingMetadata}
     */
    List<ClassMappingMetadata> getClassMappings();

    /**
     * This method retrieves class mapping metadata based on the source class name.
     *
     * @param sourceClassName The fully qualified class name of the source class.
     * @return A list of mapping metadata which defines how to map a class with the name
     * {@code sourceClassName} to other classes.
     */
    List<ClassMappingMetadata> getClassMappingsBySourceName(String sourceClassName);

    /**
     * This method retrieves class mapping metadata based on the destination class name.
     *
     * @param destinationClassName The fully qualified class name of the destination class.
     * @return A list of mapping metadata which defines how to map to a class with the name
     * {@code destinationClassName}.
     */
    List<ClassMappingMetadata> getClassMappingsByDestinationName(String destinationClassName);

    /**
     * This method retrieves class mapping metadata based on the class names.
     *
     * @param sourceClassName      The fully qualified class name of the source class.
     * @param destinationClassName The fully qualified class name of the destination class.
     * @return A list of mapping metadata which defines how to map a class with the name
     * {@code sourceClassName} to a class with the name {@code destinationClassName}.
     */
    ClassMappingMetadata getClassMappingByName(String sourceClassName, String destinationClassName);

    /**
     * This method retrieves class mapping metadata based on the source class.
     *
     * @param sourceClass The Class object which references the source class.
     * @return A list of mapping metadata which defines how to map the class {@code sourceClass}
     * to other classes.
     */
    List<ClassMappingMetadata> getClassMappingsBySource(Class<?> sourceClass);

    /**
     * This method retrieves class mapping metadata based on the destination class.
     *
     * @param destinationClass The Class object which references the destination class.
     * @return A list of mapping metadata which defines how to map to the class
     * {@code destinationClass}.
     */
    List<ClassMappingMetadata> getClassMappingsByDestination(Class<?> destinationClass);

    /**
     * This method retrieves class mapping metadata based on two Class objects.
     *
     * @param sourceClass      The Class object that references the source class.
     * @param destinationClass The Class object that references the destination class.
     * @return The mapping metadata object which defines how to map the class {@code sourceClass}
     * to the class {@code destinationClass}.
     * @throws MetadataLookupException If no class map could be found.
     */
    ClassMappingMetadata getClassMapping(Class<?> sourceClass, Class<?> destinationClass);
}
