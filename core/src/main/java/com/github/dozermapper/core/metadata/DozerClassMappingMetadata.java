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
import com.github.dozermapper.core.classmap.MappingDirection;
import com.github.dozermapper.core.fieldmap.FieldMap;

/**
 * Internal use only.
 */
public final class DozerClassMappingMetadata implements ClassMappingMetadata {

    private final ClassMap classMap;

    public DozerClassMappingMetadata(ClassMap classMap) {
        this.classMap = classMap;
    }

    public String getSourceClassName() {
        return classMap.getSrcClassName();
    }

    public String getDestinationClassName() {
        return classMap.getDestClassName();
    }

    public Class<?> getSourceClass() {
        return classMap.getSrcClassToMap();
    }

    public Class<?> getDestinationClass() {
        return classMap.getDestClassToMap();
    }

    public boolean isStopOnErrors() {
        return classMap.isStopOnErrors();
    }

    public boolean isTrimStrings() {
        return classMap.isTrimStrings();
    }

    public boolean isWildcard() {
        return classMap.isWildcard();
    }

    public boolean isSourceMapNull() {
        return classMap.isSrcMapNull();
    }

    public boolean isDestinationMapNull() {
        return classMap.isDestMapNull();
    }

    public boolean isSourceMapEmptyString() {
        return classMap.isSrcMapEmptyString();
    }

    public boolean isDestinationMapEmptyString() {
        return classMap.isDestMapEmptyString();
    }

    public String getDateFormat() {
        return classMap.getDateFormat();
    }

    public MappingDirection getMappingDirection() {
        return classMap.getType();
    }

    public String getMapId() {
        return classMap.getMapId();
    }

    public List<FieldMappingMetadata> getFieldMappings() {
        List<FieldMappingMetadata> fieldMapCats = new ArrayList<>();
        for (FieldMap fieldMap : classMap.getFieldMaps()) {
            fieldMapCats.add(new DozerFieldMappingMetadata(fieldMap));
        }

        return fieldMapCats;
    }

    public FieldMappingMetadata getFieldMappingBySource(String sourceFieldName) {
        FieldMap fieldMap = classMap.getFieldMapUsingSrc(sourceFieldName);
        if (fieldMap == null) {
            throw new MetadataLookupException("Field mapping " + sourceFieldName + " not found for class " + classMap.getSrcClassName());
        }

        return new DozerFieldMappingMetadata(fieldMap);
    }

    public FieldMappingMetadata getFieldMappingByDestination(String destinationFieldName) {
        FieldMap fieldMap = classMap.getFieldMapUsingDest(destinationFieldName);
        if (fieldMap == null) {
            throw new MetadataLookupException("Field mapping " + destinationFieldName + " not found for class " + classMap.getDestClassName());
        }

        return new DozerFieldMappingMetadata(fieldMap);
    }

}
