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
package org.dozer.metadata;

import java.util.List;

import org.dozer.classmap.MappingDirection;

/**
 * This interface provides read-only access to all important aspects of the mapping metadata that specifies
 * how two classes are mapped to one another. The interface also holds methods to query the individual field
 * mappings that are part of the class mapping definition. This covers custom field mappings as well as
 * implicit field mappings automatically introduced by the dozer framework.
 * 
 * @author Florian Kunz
 */
public interface ClassMappingMetadata {

    /**
     * 
     * Obtains the name of the source class in the mapping definition.
     * 
     * @return The name of the source class.
     */
    String getSourceClassName();

    /**
     * 
     * Obtains the name of the destination class in the mapping definition.
     * 
     * @return The name of the destination class.
     */
    String getDestinationClassName();

    /**
     * 
     * Obtains the Class object that represents the source class in the mapping definition.
     * 
     * @return The Class object of the source class.
     */
    Class<?> getSourceClass();

    /**
     * 
     * Obtains the Class object that represents the destination class in the mapping definition.
     * 
     * @return The Class object of the destination class.
     */
    Class<?> getDestinationClass();

    /**
     * @return true if the mapping will be stopped upon errors. 
     */
    boolean isStopOnErrors();

    /** 
     * @return true if strings are trimmed during mapping.
     */
    boolean isTrimStrings();

    /** 
     * @return The wildcard policy. True means that fields with the same name are automatically mapped.
     */
    boolean isWildcard();

    /** 
     * @return true if null values are mapped from the source class.
     */
    boolean isSourceMapNull();

    /** 
     * @return true if null values are mapped from the destination class.
     */
    boolean isDestinationMapNull();

    /** 
     * @return true if empty are mapped from the source class.
     */
    boolean isSourceMapEmptyString();

    /** 
     * @return true if empty are mapped from the destination class.
     */
    boolean isDestinationMapEmptyString();

    /**
     * 
     * Obtains the date format that is used during date conversions.
     * 
     * @return The date format as a string.
     */
    String getDateFormat();

    /**
     * Used to check if a mapping is bi- or unidirectional.
     * 
     * @return The {@link org.dozer.classmap.MappingDirection} object that specifies the 
     * direction of the map.
     */
    MappingDirection getMappingDirection();

    /**
     * Returns the map id of this mapping definition used for contextual mapping selection.
     * 
     * @return The identifier as a string.
     */
    String getMapId();

    /**
     * Gets a list of all field mapping definitions that are used for the mapping of the classes.
     * 
     * @return The list of {@link FieldMappingMetadata} objects.
     * 
     */
    List<FieldMappingMetadata> getFieldMappings();

    /**
     * 
     * Gets a single field mapping definition by looking up the name of the source field.
     * 
     * @param sourceFieldName The name of the source field.
     * 
     * @return A {@link FieldMappingMetadata} object.
     * @throws MetadataLookupException
     * If no field map could be found.
     */
    FieldMappingMetadata getFieldMappingBySource(String sourceFieldName);

    /**
     * 
     * Gets a single field mapping definition by looking up the name of the destination field.
     * 
     * @param destinationFieldName The name of the destination field.
     * 
     * @return A {@link FieldMappingMetadata} object.
     * @throws MetadataLookupException If no field map could be found.
     */
    FieldMappingMetadata getFieldMappingByDestination(String destinationFieldName);
}
