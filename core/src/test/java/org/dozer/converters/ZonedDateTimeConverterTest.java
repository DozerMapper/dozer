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
package org.dozer.converters;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class ZonedDateTimeConverterTest {

    private final ZonedDateTimeConverter converter = new ZonedDateTimeConverter(DateTimeFormatter.ISO_ZONED_DATE_TIME);

    @Test
    @Parameters(method = "testData")
    public void testConverter(Object sourceObject, Object expected) {
        Object result = converter.convert(ZonedDateTime.class, sourceObject);
        assertEquals(expected, result);
        assertTrue(result instanceof ZonedDateTime);
    }

    private Object testData() {
        return new Object[][]{
                {
                        ZonedDateTime.of(
                                LocalDateTime.of(2017, 11, 2, 10, 0),
                                ZoneId.systemDefault()
                        ),
                        ZonedDateTime.of(
                                LocalDateTime.of(2017, 11, 2, 10, 0),
                                ZoneId.systemDefault()
                        ),
                },

                {
                        "2017-11-02T10:20:30+08:00[Europe/Madrid]",
                        ZonedDateTime.of(
                                LocalDateTime.of(2017, 11, 2, 10, 20, 30),
                                ZoneId.of("Europe/Madrid")
                        )
                },

                {
                        Instant.parse("2017-11-02T10:20:30.00Z"),
                        ZonedDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
                },

                {
                        Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli(),
                        ZonedDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())


                },

                {
                        new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli()),
                        ZonedDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
                }
        };
    }
}
