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

import org.dozer.classmap.MappingDirection;

/**
 * This interface can be used to obtain information about the mapping between two fields.
 * @author  Florian Kunz
 */
public interface FieldMappingMetadata {

    /**
     * @return The name of the source field.
     */
    String getSourceName();
    
    /**
     * @return The name of the destination field.
     */
    String getDestinationName();
    
    /** 
     * @return The name of the getter of the source field.
     */
    String getSourceFieldGetMethod();
    
    /** 
     * @return The name of the setter of the source field.
     */
    String getSourceFieldSetMethod();
    
    /** 
     * @return The name of the getter of the destination field.
     */
    String getDestinationFieldGetMethod();
    
    /** 
     * @return The name of the setter of the destination field.
     */
    String getDestinationFieldSetMethod();
    
    /**
     * @return true if nested objects are copied by reference. false if a deep copy 
     * is performed.
     */
    boolean isCopyByReference();
    
    boolean isSourceFieldAccessible();
    
    boolean isDestinationFieldAccessible();
    
    /**
     * Can be used to check whether a mapping in unidirectional or bidirectional.
     *  
     * @return An instance of {@link org.dozer.classmap.MappingDirection}.
     */
    MappingDirection getMappingDirection();
    
    /**
     * @return The date format used for conversions as a string.
     */
    String getDateFormat();
    
    /** 
     * The name of the custom converter class, or an empty string if no custom converter is used.
     *
     * @return The name of the custom converter class as a string.
     */
    String getCustomConverter();
    
}
