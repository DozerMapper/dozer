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
package com.github.dozermapper.core.events;

import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.fieldmap.FieldMap;

/**
 * An Event triggered at a certain stage in the mapping process
 */
public interface Event {

    /**
     * Returns the type of event
     *
     * @return type of event
     */
    EventTypes getType();

    /**
     * Returns the classmap being used by mapper
     *
     * @return classmap being used by mapper
     */
    ClassMap getClassMap();

    /**
     * Returns the fieldmap being used by mapper
     *
     * @return fieldmap being used by mapper
     */
    FieldMap getFieldMap();

    /**
     * Returns the source being mapped
     *
     * @return source being mapped
     */
    Object getSourceObject();

    /**
     * Returns destination being mapped
     *
     * @return destination being mapped
     */
    Object getDestinationObject();

    /**
     * Returns destination value being mapped
     *
     * @return destination value being mapped
     */
    Object getDestinationValue();
}
