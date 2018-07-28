/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.converters;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
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
public class LocalDateTimeConverterTest {

    private LocalDateTimeConverter localTimeConverter = new LocalDateTimeConverter(DateTimeFormatter.ISO_LOCAL_TIME);
    private LocalDateTimeConverter localDateTimeConverter =
            new LocalDateTimeConverter(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private LocalDateTimeConverter localDateConverter = new LocalDateTimeConverter(DateTimeFormatter.ISO_LOCAL_DATE);

    @Test
    @Parameters(method = "localDateTimeTestData")
    public void testLocalDateTimeConverter(Object sourceObject, LocalDateTime expectedResult) {
        LocalDateTime result = (LocalDateTime) localDateTimeConverter.convert(LocalDateTime.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    @Parameters(method = "localDateTestData")
    public void testLocalDateConverter(Object sourceObject, LocalDate expectedResult) {
        LocalDate result = (LocalDate) localDateConverter.convert(LocalDate.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    @Parameters(method = "localTimeTestData")
    public void testLocalTimeConverter(Object sourceObject, LocalTime expectedResult) {
        LocalTime result = (LocalTime) localTimeConverter.convert(LocalTime.class, sourceObject);

        assertEquals(expectedResult, result);
    }

    @Test(expected = ConversionException.class)
    public void testThrowExceptionWhenSourceNotSupported() {
        localTimeConverter.convert(LocalDateTime.class, 'a');
    }

    /**
     * Test data for
     * {@link LocalDateTimeConverterTest#testLocalDateTimeConverter(Object, LocalDateTime)} method.
     *
     * @return test data
     */
    private Object localTimeTestData() {
        return new Object[][]{
            {
                LocalTime.of(10, 20, 30),
                LocalTime.of(10, 20, 30)
            },
            {
                OffsetDateTime.parse("2017-11-02T10:20:30+02:00"),
                LocalTime.of(10, 20, 30)
            },
            {
                "10:20:30",
                LocalTime.of(10, 20, 30)
            },
            {
                Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli(),
                LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault()).toLocalTime()
            },
            {
                new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli()),
                LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault()).toLocalTime()
            }
        };
    }

    /**
     * Test data for {@link LocalDateTimeConverterTest#testLocalDateConverter(Object, LocalDate)} method.
     *
     * @return test data
     */
    private Object localDateTestData() {
        return new Object[][]{
            {
                LocalDate.of(2017, 11, 2),
                LocalDate.of(2017, 11, 2)
            },
            {
                OffsetDateTime.parse("2017-11-02T10:20:30+02:00"),
                LocalDate.of(2017, 11, 2)
            },
            {
                "2017-11-02",
                LocalDate.of(2017, 11, 2)
            },
            {
                Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli(),
                LocalDate.of(2017, 11, 2)
            },
            {
                new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli()),
                LocalDate.of(2017, 11, 2)
            }
        };
    }

    /**
     * Test data for {@link LocalDateTimeConverterTest#testLocalTimeConverter(Object, LocalTime)} method.
     *
     * @return test data
     */
    private Object localDateTimeTestData() {
        return new Object[][]{
            {
                LocalDateTime.of(2017, 11, 2, 10, 0),
                LocalDateTime.of(2017, 11, 2, 10, 0)
            },
            {
                OffsetDateTime.parse("2017-11-02T10:20:30+02:00"),
                OffsetDateTime.parse("2017-11-02T10:20:30+02:00").toLocalDateTime()
            },
            {
                ZonedDateTime.parse("2017-11-02T10:20:30+02:00[Europe/Paris]"),
                ZonedDateTime.parse("2017-11-02T10:20:30+02:00[Europe/Paris]").toLocalDateTime()
            },
            {
                "2017-11-02T10:20:30",
                LocalDateTime.parse("2017-11-02T10:20:30")
            },
            {
                Instant.parse("2017-11-02T10:20:30.00Z"),
                LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
            },
            {
                Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli(),
                LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault())
            }
        };
    }
}
