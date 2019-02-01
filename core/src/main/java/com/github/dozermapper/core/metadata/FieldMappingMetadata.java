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

import com.github.dozermapper.core.classmap.MappingDirection;

/**
 * This interface can be used to obtain information about the mapping between two fields.
 */
public interface FieldMappingMetadata {

    /**
     * Gets the source field name
     *
     * @return The name of the source field.
     */
    String getSourceName();

    /**
     * Gets the destination field name
     *
     * @return The name of the destination field.
     */
    String getDestinationName();

    /**
     * Gets the source field get method
     *
     * @return The name of the getter of the source field.
     */
    String getSourceFieldGetMethod();

    /**
     * Gets the source field set method
     *
     * @return The name of the setter of the source field.
     */
    String getSourceFieldSetMethod();

    /**
     * Gets the destination field get method
     *
     * @return The name of the getter of the destination field.
     */
    String getDestinationFieldGetMethod();

    /**
     * Gets the destination field set method
     *
     * @return The name of the setter of the destination field.
     */
    String getDestinationFieldSetMethod();

    /**
     * Gets whether mapping should copy by reference
     *
     * @return true if nested objects are copied by reference. false if a deep copy
     * is performed.
     */
    boolean isCopyByReference();

    boolean isSourceFieldAccessible();

    boolean isDestinationFieldAccessible();

    /**
     * Can be used to check whether a mapping in unidirectional or bidirectional.
     *
     * @return An instance of {@link MappingDirection}.
     */
    MappingDirection getMappingDirection();

    /**
     * Gets date format used when mapping
     *
     * @return The date format used for conversions as a string.
     */
    String getDateFormat();

    /**
     * The name of the custom converter class, or an empty string if no custom converter is used.
     *
     * @return The name of the custom converter class as a string.
     */
    String getCustomConverter();

    /**
     * Returns the map id of this mapping definition used for contextual mapping selection.
     *
     * @return The identifier as a string.
     */
    String getMapId();

}
