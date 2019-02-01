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

import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.classmap.MappingDirection;
import com.github.dozermapper.core.classmap.RelationshipType;
import com.github.dozermapper.core.loader.DozerBuilder;

import org.apache.commons.lang3.StringUtils;

/**
 * Mapping options applied on Field level.
 */
public final class FieldsMappingOptions {

    private FieldsMappingOptions() {

    }

    public static FieldsMappingOption copyByReference() {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.copyByReference(true);
            }
        };
    }

    public static FieldsMappingOption customConverter(final Class<? extends CustomConverter> type) {
        return customConverter(type, null);
    }

    public static FieldsMappingOption customConverter(final Class<? extends CustomConverter> type, final String parameter) {
        return customConverter(type.getName(), parameter);
    }

    public static FieldsMappingOption customConverter(final String type) {
        return customConverter(type, null);
    }

    public static FieldsMappingOption customConverter(final String type, final String parameter) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.customConverter(type);
                fieldMappingBuilder.customConverterParam(parameter);
            }
        };
    }

    public static FieldsMappingOption customConverterId(final String id) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.customConverterId(id);
            }
        };
    }

    public static FieldsMappingOption customConverterId(final String id, final String parameter) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.customConverterId(id);
                fieldMappingBuilder.customConverterParam(parameter);
            }
        };
    }

    public static FieldsMappingOption useMapId(final String mapId) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.mapId(mapId);
            }
        };
    }

    public static FieldsMappingOption oneWay() {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.type(MappingDirection.ONE_WAY);
            }
        };
    }

    public static FieldsMappingOption hintA(final Class<?>... type) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                String declaration = mergeTypeNames(type);
                fieldMappingBuilder.srcHintContainer(declaration);
            }
        };
    }

    public static FieldsMappingOption hintB(final Class<?>... type) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                String declaration = mergeTypeNames(type);
                fieldMappingBuilder.destHintContainer(declaration);
            }
        };
    }

    public static FieldsMappingOption deepHintA(final Class<?>... type) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                String declaration = mergeTypeNames(type);
                fieldMappingBuilder.srcDeepIndexHintContainer(declaration);
            }
        };
    }

    public static FieldsMappingOption deepHintB(final Class<?>... type) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                String declaration = mergeTypeNames(type);
                fieldMappingBuilder.destDeepIndexHintContainer(declaration);
            }
        };
    }

    private static String mergeTypeNames(Class<?>[] type) {
        String[] typeNames = new String[type.length];
        for (int i = 0; i < type.length; i++) {
            Class<?> t = type[i];
            typeNames[i] = t.getName();
        }
        return StringUtils.join(typeNames, ",");
    }

    public static FieldsMappingOption removeOrphans() {
        return removeOrphans(true);
    }

    public static FieldsMappingOption removeOrphans(final boolean removeOrphans) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.removeOrphans(removeOrphans);
            }
        };
    }

    public static FieldsMappingOption relationshipType(final RelationshipType relationshipType) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.relationshipType(relationshipType);
            }
        };
    }

    public static FieldsMappingOption collectionStrategy(final boolean removeOrphans, final RelationshipType relationshipType) {
        return new FieldsMappingOption() {
            public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                fieldMappingBuilder.removeOrphans(removeOrphans);
                fieldMappingBuilder.relationshipType(relationshipType);
            }
        };
    }
}
