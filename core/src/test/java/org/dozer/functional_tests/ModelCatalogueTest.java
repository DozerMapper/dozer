/*
 * Copyright 2011 the original author or authors.
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

import java.util.Arrays;

import org.dozer.DozerBeanMapper;
import org.dozer.metadata.ClassMappingMetadata;
import org.dozer.metadata.FieldMappingMetadata;
import org.dozer.metadata.MappingMetadata;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


/**
 * Tests for the new mapping catalogue interfaces.
 * @author  florian.kunz
 */
public class ModelCatalogueTest extends AbstractFunctionalTest {
	
	private static final String MAPPING_FILE = "modelCatalogueTest.xml";
	private static final String CLASS_A = "org.dozer.vo.model.catalogue.ClassA";
	private static final String CLASS_B = "org.dozer.vo.model.catalogue.ClassB";
	private static final String AUTOFIELD = "autoField";
	private static final String CUSTOM_FIELD_A = "customFieldA";
	private static final String CUSTOM_FIELD_B = "customFieldB";
	
	private MappingMetadata mapCat;
	
	@Before
	public void setup() {
		DozerBeanMapper beanMapper = (DozerBeanMapper) getMapper(MAPPING_FILE);
		mapCat = beanMapper.getMappingMetadata();
	}

	@Test
	public void testMapCatNotNull() {
		ClassMappingMetadata classCat = mapCat.getClassMapping(CLASS_A, CLASS_B);
		assertNotNull(classCat);
	}
	
	@Test
	public void testAutoFieldMap() {
		ClassMappingMetadata classCat = mapCat.getClassMapping(CLASS_A, CLASS_B);
		FieldMappingMetadata fieldMap = classCat.getFieldMappingBySource(AUTOFIELD);
		assertEquals(AUTOFIELD, fieldMap.getDestinationName());
	}
	
	@Test
	public void testCustomFieldMap() {
		ClassMappingMetadata classCat = mapCat.getClassMapping(CLASS_A, CLASS_B);
		FieldMappingMetadata fieldMap = classCat.getFieldMappingBySource(CUSTOM_FIELD_A);
		assertEquals(CUSTOM_FIELD_B, fieldMap.getDestinationName());
	}
	
	@Test
	public void testCustomFieldMapBack() {
		ClassMappingMetadata classCat = mapCat.getClassMapping(CLASS_A, CLASS_B);
		FieldMappingMetadata fieldMap = classCat.getFieldMappingByDestination(CUSTOM_FIELD_B);
		assertEquals(CUSTOM_FIELD_A, fieldMap.getSourceName());
	}

}
