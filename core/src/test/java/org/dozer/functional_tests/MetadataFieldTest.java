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
package org.dozer.functional_tests;

import java.util.List;

import org.dozer.Mapper;
import org.dozer.metadata.ClassMappingMetadata;
import org.dozer.metadata.FieldMappingMetadata;
import org.dozer.metadata.MappingMetadata;
import org.dozer.metadata.MetadataLookupException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the new mapping catalogue interfaces.
 * @author  florian.kunz
 */
public class MetadataFieldTest extends AbstractFunctionalTest {
    
    private static final String MAPPING_FILE = "mappings/metadataTest.xml";
    private static final String CLASS_A = "org.dozer.vo.metadata.ClassA";
    private static final String CLASS_B = "org.dozer.vo.metadata.ClassB";
    private static final String NONEXISTENTFIELD = "noField";
    private static final String AUTOFIELD = "autoField";
    private static final String CUSTOM_FIELD_A = "customFieldA";
    private static final String CUSTOM_FIELD_B = "customFieldB";
    
    private MappingMetadata mapMetadata;
    
    @Before
    public void setup() {
        Mapper beanMapper = getMapper(MAPPING_FILE);
        mapMetadata = beanMapper.getMappingMetadata();
    }
    
    @Test
    public void testFieldMaps() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        List<FieldMappingMetadata> fieldMetadata = classMetadata.getFieldMappings();
        assertTrue(AUTOFIELD, fieldMetadata.size() == 3);
    }
    
    @Test
    public void testAutoFieldMap() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        FieldMappingMetadata fieldMetadata = classMetadata.getFieldMappingBySource(AUTOFIELD);
        assertEquals(AUTOFIELD, fieldMetadata.getDestinationName());
    }
    
    @Test
    public void testCustomFieldMap() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        FieldMappingMetadata fieldMetadata = classMetadata.getFieldMappingBySource(CUSTOM_FIELD_A);
        assertEquals(CUSTOM_FIELD_B, fieldMetadata.getDestinationName());
    }
    
    @Test
    public void testFieldMapByDestination() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        classMetadata.getFieldMappingByDestination(CUSTOM_FIELD_B);
    }
    
    @Test
    public void testCustomFieldMapBack() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        FieldMappingMetadata fieldMetadata = classMetadata.getFieldMappingByDestination(CUSTOM_FIELD_B);
        assertEquals(CUSTOM_FIELD_A, fieldMetadata.getSourceName());
    }
    
    @Test(expected=MetadataLookupException.class)
    public void testFieldMapNonExistantSource() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        classMetadata.getFieldMappingBySource(NONEXISTENTFIELD);
    }
    
    @Test(expected=MetadataLookupException.class)
    public void testFieldMapNonExistantDestination() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        classMetadata.getFieldMappingByDestination(NONEXISTENTFIELD);
    }

}
