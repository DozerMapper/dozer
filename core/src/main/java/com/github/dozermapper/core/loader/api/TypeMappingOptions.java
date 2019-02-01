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
package com.github.dozermapper.core.loader.api;

import com.github.dozermapper.core.classmap.MappingDirection;
import com.github.dozermapper.core.classmap.RelationshipType;
import com.github.dozermapper.core.loader.DozerBuilder;

/**
 * Mapping options applie on Type level.
 */
public final class TypeMappingOptions {

    private TypeMappingOptions() {

    }

    public static TypeMappingOption mapId(final String mapId) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.mapId(mapId);
            }
        };
    }

    public static TypeMappingOption beanFactory(final String value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.beanFactory(value);
            }
        };
    }

    public static TypeMappingOption dateFormat(final String value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.dateFormat(value);
            }
        };
    }

    public static TypeMappingOption mapEmptyString() {
        return mapEmptyString(true);
    }

    public static TypeMappingOption mapEmptyString(final boolean value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.mapEmptyString(value);
            }
        };
    }

    public static TypeMappingOption mapNull() {
        return mapNull(true);
    }

    public static TypeMappingOption mapNull(final boolean value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.mapNull(value);
            }
        };
    }

    public static TypeMappingOption relationshipType(final RelationshipType value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.relationshipType(value);
            }
        };
    }

    public static TypeMappingOption stopOnErrors() {
        return stopOnErrors(true);
    }

    public static TypeMappingOption stopOnErrors(final boolean value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.stopOnErrors(value);
            }
        };
    }

    public static TypeMappingOption trimStrings() {
        return trimStrings(true);
    }

    public static TypeMappingOption trimStrings(final boolean value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.trimStrings(value);
            }
        };
    }

    public static TypeMappingOption oneWay() {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.type(MappingDirection.ONE_WAY);
            }
        };
    }

    public static TypeMappingOption wildcard(final boolean value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.wildcard(value);
            }
        };
    }

    public static TypeMappingOption wildcardCaseInsensitive(final boolean value) {
        return new TypeMappingOption() {
            public void apply(DozerBuilder.MappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.wildcardCaseInsensitive(value);
            }
        };
    }
}
