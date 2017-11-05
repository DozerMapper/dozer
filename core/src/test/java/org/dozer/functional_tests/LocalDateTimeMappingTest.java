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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
public class LocalDateTimeMappingTest
        extends AbstractFunctionalTest {

    private final LocalDate sampleLocalDate = LocalDate.of(2017, 11, 2);
    private final LocalDateTime sample = LocalDateTime.of(2017, 11, 2, 10, 20, 30);
    private final ZoneOffset sampleOffset = ZoneId.systemDefault().getRules().getOffset(sample);
    private Mapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = getMapper("mappings/jsr330Mapping.xml");
    }

    @Test
    @Parameters(method = "localDateTimeTestData")
    public void testLocalDateTimeMapping(Object source) {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        LocalDateTimeVO dest = mapper.map(sourceMap, LocalDateTimeVO.class);
        assertNotNull(dest);
        LocalDateTime result = dest.getResult();
        assertTrue(
                String.format("Values are not equals. Expected: %s. Actual: %s", sample, result),
                sample.isEqual(result)
        );
    }

    @Test
    @Parameters(method = "localDateTestData")
    public void testLocalDateMapping(Object source) {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key", source);
        LocalDateVO dest = mapper.map(sourceMap, LocalDateVO.class);
        assertNotNull(dest);
        LocalDate result = dest.getResult();
        assertTrue(
                String.format("Values are not equals. Expected: %s. Actual: %s", sampleLocalDate, result),
                sampleLocalDate.isEqual(result)
        );
    }

    private Object localDateTestData() {
        return new Object[][]{
                {DateTimeFormatter.ISO_LOCAL_DATE.format(sampleLocalDate)}
        };
    }

    private Object localDateTimeTestData() {
        return new Object[][]{
                {DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(sample)},
                {sample.toInstant(sampleOffset)},
                {sample.toInstant(sampleOffset).toEpochMilli()},
                {new Date(sample.toInstant(sampleOffset).toEpochMilli())}
        };
    }

    public static final class LocalDateTimeVO {

        private LocalDateTime result;

        public LocalDateTime getResult() {
            return result;
        }

        public LocalDateTimeVO setResult(LocalDateTime result) {
            this.result = result;
            return this;
        }
    }

    public static final class LocalDateVO {

        private LocalDate result;

        public LocalDate getResult() {
            return result;
        }

        public LocalDateVO setResult(LocalDate result) {
            this.result = result;
            return this;
        }
    }

}
