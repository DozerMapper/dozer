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
package com.github.dozermapper.core.functional_tests;

import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.metadata.ClassMappingMetadata;
import com.github.dozermapper.core.metadata.FieldMappingMetadata;
import com.github.dozermapper.core.metadata.MappingMetadata;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MetadataMapIdFieldTest extends AbstractFunctionalTest {

    private static final String CUSTOM_FIELD_MAPPING_ID = "field-map-id";

    private static final String MAPPING_FILE = "mappings/metadataMapIdTest.xml";
    private static final String SOURCE = "com.github.dozermapper.core.vo.metadata.ClassA";
    private static final String DEST = "com.github.dozermapper.core.vo.metadata.ClassB";
    private static final String SOURCE_FIELD_1 = "customFieldA";
    private static final String SOURCE_FIELD_2 = "classC";

    private MappingMetadata mapMetadata;

    @Before
    public void setup() {
        Mapper beanMapper = getMapper(MAPPING_FILE);
        mapMetadata = beanMapper.getMappingMetadata();
    }

    @Test
    public void testMapIdForFieldMap() {
        ClassMappingMetadata mapping = mapMetadata.getClassMappingByName(SOURCE, DEST);

        FieldMappingMetadata fieldMapping = mapping.getFieldMappingBySource(SOURCE_FIELD_1);
        assertEquals(fieldMapping.getMapId(), CUSTOM_FIELD_MAPPING_ID);

        fieldMapping = mapping.getFieldMappingBySource(SOURCE_FIELD_2);
        assertNull("MapId should be null", fieldMapping.getMapId());
    }
}
