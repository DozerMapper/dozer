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

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.metadata.ClassMappingMetadata;
import org.dozer.metadata.MappingMetadata;
import org.dozer.metadata.MetadataLookupException;
import org.dozer.vo.metadata.ClassA;
import org.dozer.vo.metadata.ClassB;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Tests for the new metadata interfaces.
 * @author  florian.kunz
 */
public class MetadataTest extends AbstractFunctionalTest {
    
    private static final String MAPPING_FILE = "mappings/metadataTest.xml";
    private static final String CLASS_A = "org.dozer.vo.metadata.ClassA";
    private static final String CLASS_B = "org.dozer.vo.metadata.ClassB";
    private static final String CLASS_NONEXISTENT = "org.dozer.vo.metadata.ClassNonExistent";
    
    private MappingMetadata mapMetadata;
    
    @Before
    public void setup() {
        Mapper beanMapper = getMapper(MAPPING_FILE);
        mapMetadata = beanMapper.getMappingMetadata();
    }
    
    @Test
    public void testGetClassMappings() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappings();
        assertTrue(metadata.size() == 4);
    }
    
    @Test
    public void testGetClassMappingBySource() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsBySource(ClassA.class);
        assertTrue(metadata.size() == 1);
    }
    
    @Test
    public void testGetClassMappingByDestination() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsByDestination(ClassB.class);
        assertTrue(metadata.size() == 1);
    }

    @Test
    public void testGetClassMapping() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMapping(ClassA.class, ClassB.class);
        assertNotNull(classMetadata);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingBySourceNull() {
        mapMetadata.getClassMappingsBySource(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingByDestinationNull() {
        mapMetadata.getClassMappingsByDestination(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingByNull1() {
        mapMetadata.getClassMapping(ClassA.class, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingByNull2() {
        mapMetadata.getClassMapping(null, ClassB.class);
    }
    
    @Test(expected=MetadataLookupException.class)
    public void testGetClassMappingByNonExistent() {
        mapMetadata.getClassMapping(DozerBeanMapper.class, ClassB.class);
    }
    
    
    @Test
    public void testGetClassMappingBySourceName() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsBySourceName(CLASS_A);
        assertTrue(metadata.size() == 1);
    }
    
    @Test
    public void testGetClassMappingByDestinationName() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsByDestinationName(CLASS_B);
        assertTrue(metadata.size() == 1);
    }

    @Test
    public void testGetClassMappingByName() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(CLASS_A, CLASS_B);
        assertNotNull(classMetadata);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingBySourceNameNull() {
        mapMetadata.getClassMappingsBySourceName(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingByDestinationNameNull() {
        mapMetadata.getClassMappingsByDestinationName(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingByNameNull1() {
        mapMetadata.getClassMappingByName(CLASS_A, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetClassMappingByNameNull2() {
        mapMetadata.getClassMappingByName(null, CLASS_B);
    }
    
    @Test(expected=MetadataLookupException.class)
    public void testGetClassMappingByNameNonExistent() {
        mapMetadata.getClassMappingByName(CLASS_NONEXISTENT, CLASS_B);
    }
    
}
