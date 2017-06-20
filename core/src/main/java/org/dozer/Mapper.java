/*
 * Copyright 2005-2017 Dozer Project
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

import org.dozer.metadata.MappingMetadata;

/**
 * Public root interface for performing Dozer mappings from application code.
 *
 * @author tierney.matt
 * @author garsombke.franz
 */
public interface Mapper {

    /**
     * Constructs new instance of destinationClass and performs mapping between from source
     *
     * @param source object to convert from
     * @param destinationClass type to convert to
     * @param <T> type to convert to
     * @return mapped object
     * @throws MappingException mapping failure
     */
    <T> T map(Object source, Class<T> destinationClass) throws MappingException;

    /**
     * Performs mapping between source and destination objects
     *
     * @param source object to convert from
     * @param destination object to convert to
     * @throws MappingException mapping failure
     */
    void map(Object source, Object destination) throws MappingException;

    /**
     * Constructs new instance of destinationClass and performs mapping between from source
     *
     * @param source object to convert from
     * @param destinationClass type to convert to
     * @param mapId id in configuration for mapping
     * @param <T> type to convert to
     * @return mapped object
     * @throws MappingException mapping failure
     */
    <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException;

    /**
     * Performs mapping between source and destination objects
     *
     * @param source object to convert from
     * @param destination object to convert to
     * @param mapId id in configuration for mapping
     * @throws MappingException mapping failure
     */
    void map(Object source, Object destination, String mapId) throws MappingException;

    /**
     * The {@link org.dozer.metadata.MappingMetadata} interface can be used to query information about the current
     * mapping definitions. It provides read only access to all important classes and field
     * mapping properties. When first called, initializes all mappings if map() has not yet been called.
     *
     * @return An instance of {@link org.dozer.metadata.MappingMetadata} which serves starting point
     * for querying mapping information.
     */
    default MappingMetadata getMappingMetadata() {
        return MappingMetadata.EMPTY;
    };
}
