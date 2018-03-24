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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class ZonedDateTimeMappingTest
        extends AbstractFunctionalTest {

    private Mapper mapper;

    private ZonedDateTime sample = ZonedDateTime.of(
            LocalDateTime.of(2017, 11, 2, 10, 0),
            ZoneId.systemDefault()
    );


    @Before
    public void setUp() throws Exception {
        mapper = getMapper("mappings/jsr330Mapping.xml");
    }


    @Test
    @Parameters(method = "testData")
    public void testMapping(Object source) {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        ZonedDateTimeVO dest = mapper.map(sourceMap, ZonedDateTimeVO.class);
        assertNotNull(dest);
        ZonedDateTime result = dest.getResult();
        assertTrue(
                String.format("Values are not equals. Expected: %s. Actual: %s", sample, result),
                sample.isEqual(result)
        );
    }

    private Object testData() {
        return new Object[][]{
                {DateTimeFormatter.ISO_ZONED_DATE_TIME.format(sample)},
                {sample.toInstant()},
                {sample.toInstant().toEpochMilli()},
                {new Date(sample.toInstant().toEpochMilli())}
        };
    }

    public static class ZonedDateTimeVO {

        private ZonedDateTime result;

        public ZonedDateTime getResult() {
            return result;
        }

        public void setResult(ZonedDateTime result) {
            this.result = result;
        }
    }

}
