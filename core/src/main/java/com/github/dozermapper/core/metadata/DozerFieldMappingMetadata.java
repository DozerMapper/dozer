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
import com.github.dozermapper.core.fieldmap.FieldMap;

/**
 * Internal use only.
 */
public final class DozerFieldMappingMetadata implements FieldMappingMetadata {

    private final FieldMap fieldMap;

    public DozerFieldMappingMetadata(FieldMap fieldMap) {
        this.fieldMap = fieldMap;
    }

    public String getSourceName() {
        return fieldMap.getSrcFieldName();
    }

    public String getDestinationName() {
        return fieldMap.getDestFieldName();
    }

    public String getSourceFieldGetMethod() {
        return fieldMap.getSrcFieldMapGetMethod();
    }

    public String getSourceFieldSetMethod() {
        return fieldMap.getSrcFieldMapSetMethod();
    }

    public String getDestinationFieldGetMethod() {
        return fieldMap.getDestFieldMapGetMethod();
    }

    public String getDestinationFieldSetMethod() {
        return fieldMap.getDestFieldMapSetMethod();
    }

    public boolean isCopyByReference() {
        return fieldMap.isCopyByReference();
    }

    public boolean isSourceFieldAccessible() {
        return fieldMap.isSrcFieldAccessible();
    }

    public boolean isDestinationFieldAccessible() {
        return fieldMap.isDestFieldAccessible();
    }

    public MappingDirection getMappingDirection() {
        return fieldMap.getType();
    }

    public String getDateFormat() {
        return fieldMap.getDateFormat();
    }

    public String getCustomConverter() {
        return fieldMap.getCustomConverter();
    }

    @Override
    public String getMapId() {
        return fieldMap.getMapId();
    }
}
