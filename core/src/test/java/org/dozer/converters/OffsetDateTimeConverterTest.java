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
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class OffsetDateTimeConverterTest {

    @Test
    @Parameters(method = "offsetDateTimeTestData")
    public void testOffsetDateTimeConverter(
            Object sourceObject, OffsetDateTime expectedResult)
            throws Exception {
        OffsetDateTime result = (OffsetDateTime) new OffsetDateTimeConverter(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .convert(OffsetDateTime.class, sourceObject);
        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    @Parameters(method = "offsetTimeTestData")
    public void testOffsetTimeConverter(
            Object sourceObject, OffsetTime expectedResult)
            throws Exception {
        OffsetTime result = (OffsetTime) new OffsetDateTimeConverter(DateTimeFormatter.ISO_OFFSET_TIME)
                .convert(OffsetTime.class, sourceObject);
        assertTrue(expectedResult.isEqual(result));
    }

    /**
     * Test data for
     * {@link OffsetDateTimeConverterTest#testOffsetDateTimeConverter(Object, OffsetDateTime)} method.
     * @return test data
     */
    private Object offsetDateTimeTestData() {
        return new Object[][]{
                {
                        OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.UTC)
                },
                {
                        OffsetDateTime.parse("2017-11-02T10:20:30+02:00"),
                        OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.ofHours(2))
                },
                {
                        ZonedDateTime.parse("2017-11-02T10:20:30+01:00[Europe/Paris]"),
                        OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.ofHours(1))
                },
                {
                        "2017-11-02T10:20:30+02:00",
                        OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.ofHours(2))
                },
                {
                        Instant.parse("2017-11-02T10:20:30.00Z"),
                        OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
                },
                {
                        Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli(),
                        OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
                },
                {
                        new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli()),
                        OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
                }
        };
    }

    /**
     * Test data for
     * {@link OffsetDateTimeConverterTest#testOffsetTimeConverter(Object, OffsetTime)} method.
     * @return test data
     */
    private Object offsetTimeTestData() {
        return new Object[][] {
                {
                        OffsetTime.of(LocalTime.of(10, 20, 30), ZoneOffset.ofHours(2)),
                        OffsetTime.of(LocalTime.of(10, 20, 30), ZoneOffset.ofHours(2))
                },
                {
                        "10:20:30+04:00",
                        OffsetTime.of(LocalTime.of(10, 20, 30), ZoneOffset.ofHours(4))
                },
                {
                        Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli(),
                        OffsetDateTime
                                .ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
                                .toOffsetTime()
                },
                {
                        new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli()),
                        OffsetDateTime
                                .ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
                                .toOffsetTime()
                }
        };
    }
}
