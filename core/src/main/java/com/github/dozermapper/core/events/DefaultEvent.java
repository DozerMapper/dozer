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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Event details
 */
public class DefaultEvent implements Event {

    private final EventTypes type;
    private final ClassMap classMap;
    private final FieldMap fieldMap;
    private final Object sourceObject;
    private final Object destinationObject;
    private final Object destinationValue;

    /**
     * Event details
     *
     * @param type              type of event
     * @param classMap          classmap being used by mapper
     * @param fieldMap          fieldMap being used by mapper
     * @param sourceObject      source being mapped
     * @param destinationObject destination being mapped
     * @param destinationValue  destination value being mapped
     */
    public DefaultEvent(EventTypes type, ClassMap classMap, FieldMap fieldMap, Object sourceObject, Object destinationObject,
                        Object destinationValue) {
        this.type = type;
        this.classMap = classMap;
        this.fieldMap = fieldMap;
        this.sourceObject = sourceObject;
        this.destinationObject = destinationObject;
        this.destinationValue = destinationValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventTypes getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassMap getClassMap() {
        return classMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMap getFieldMap() {
        return fieldMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSourceObject() {
        return sourceObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDestinationObject() {
        return destinationObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDestinationValue() {
        return destinationValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("classMap", classMap)
                .append("fieldMap", fieldMap)
                .append("sourceObject", sourceObject)
                .append("destinationObject", destinationObject)
                .append("destinationValue", destinationValue)
                .toString();
    }
}
