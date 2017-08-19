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
import org.dozer.functional_tests.model.MyEntity;
import org.dozer.functional_tests.model.MyMinimalDto;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OneWayClassMappingTest {

    private Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles("mappings/oneway-minimal.xml")
            .build();

    @Test
    public void testEntity2Dto() {
        MyEntity entity = new MyEntity();
        entity.setName("name");
        entity.setOneWay("oneway");
        entity.setId("id");

        MyMinimalDto dto = mapper.map(entity, MyMinimalDto.class);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getOneWay(), entity.getOneWay());
    }

    @Test
    public void testDto2Entity() {
        MyMinimalDto dto = new MyMinimalDto();
        dto.setId("id");
        dto.setOneWay("thatshouldnotbemapped");

        MyEntity entity = mapper.map(dto, MyEntity.class);

        assertNull(entity.getId());
        assertNull(entity.getOneWay());
    }
}
