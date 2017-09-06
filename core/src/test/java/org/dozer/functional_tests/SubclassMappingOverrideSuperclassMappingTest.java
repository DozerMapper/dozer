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

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.functional_tests.model.MySubDto;
import org.dozer.functional_tests.model.MySubEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SubclassMappingOverrideSuperclassMappingTest {

    private Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles("mappings/oneway-minimal.xml")
            .build();

    @Test
    public void testEntity2Dto() {
        MySubEntity subEntity = new MySubEntity();
        subEntity.setName("name");
        subEntity.setId("id");
        subEntity.setExcludeMe("exclude");

        MySubDto subDto = mapper.map(subEntity, MySubDto.class);

        assertEquals(subDto.getId(), subEntity.getId());
        assertEquals(subDto.getOtherName(), subEntity.getName());
        assertNull(subDto.getExcludeMe());
    }

    @Test
    public void testDto2Entity() {
        MySubDto subDto = new MySubDto();
        subDto.setId("id");
        subDto.setOtherName("name");
        subDto.setExcludeMe("exclude");

        MySubEntity subEntity = mapper.map(subDto, MySubEntity.class);

        assertNull(subEntity.getId());
        assertEquals(subEntity.getName(), subDto.getOtherName());
        assertNull(subEntity.getExcludeMe());
    }
}
