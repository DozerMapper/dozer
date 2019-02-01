/*
 * Copyright 2005-2019 Dozer Project
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalDateTimeConverterTest {

    private LocalDateTimeConverter localTimeConverter = new LocalDateTimeConverter(DateTimeFormatter.ISO_LOCAL_TIME);
    private LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private LocalDateTimeConverter localDateConverter = new LocalDateTimeConverter(DateTimeFormatter.ISO_LOCAL_DATE);

    @Test
    public void canConvertFromLocalDateTimeToLocalDateTimeViaDateTime() {
        LocalDateTime sourceObject = LocalDateTime.of(2017, 11, 2, 10, 0);
        LocalDateTime expectedResult = LocalDateTime.of(2017, 11, 2, 10, 0);

        LocalDateTime result = (LocalDateTime)localDateTimeConverter.convert(LocalDateTime.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromOffsetDateTimeToLocalDateTimeViaDateTime() {
        OffsetDateTime sourceObject = OffsetDateTime.parse("2017-11-02T10:20:30+02:00");
        LocalDateTime expectedResult = OffsetDateTime.parse("2017-11-02T10:20:30+02:00").toLocalDateTime();

        LocalDateTime result = (LocalDateTime)localDateTimeConverter.convert(LocalDateTime.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromZonedDateTimeToLocalDateTimeViaDateTime() {
        ZonedDateTime sourceObject = ZonedDateTime.parse("2017-11-02T10:20:30+02:00[Europe/Paris]");
        LocalDateTime expectedResult = ZonedDateTime.parse("2017-11-02T10:20:30+02:00[Europe/Paris]").toLocalDateTime();

        LocalDateTime result = (LocalDateTime)localDateTimeConverter.convert(LocalDateTime.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromStringToLocalDateTimeViaDateTime() {
        String sourceObject = "2017-11-02T10:20:30";
        LocalDateTime expectedResult = LocalDateTime.parse("2017-11-02T10:20:30");

        LocalDateTime result = (LocalDateTime)localDateTimeConverter.convert(LocalDateTime.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromInstantToLocalDateTimeViaDateTime() {
        Instant sourceObject = Instant.parse("2017-11-02T10:20:30.00Z");
        LocalDateTime expectedResult = LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        LocalDateTime result = (LocalDateTime)localDateTimeConverter.convert(LocalDateTime.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromLongToLocalDateTimeViaDateTime() {
        Long sourceObject = Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli();
        LocalDateTime expectedResult = LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        LocalDateTime result = (LocalDateTime)localDateTimeConverter.convert(LocalDateTime.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromLocalDateToLocalDateTimeViaDate() {
        LocalDate sourceObject = LocalDate.of(2017, 11, 2);
        LocalDate expectedResult = LocalDate.of(2017, 11, 2);

        LocalDate result = (LocalDate)localDateConverter.convert(LocalDate.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromOffsetDateTimeToLocalDateTimeViaDate() {
        OffsetDateTime sourceObject = OffsetDateTime.parse("2017-11-02T10:20:30+02:00");
        LocalDate expectedResult = LocalDate.of(2017, 11, 2);

        LocalDate result = (LocalDate)localDateConverter.convert(LocalDate.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromStringToLocalDateTimeViaDate() {
        String sourceObject = "2017-11-02";
        LocalDate expectedResult = LocalDate.of(2017, 11, 2);

        LocalDate result = (LocalDate)localDateConverter.convert(LocalDate.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromLongToLocalDateTimeViaDate() {
        Long sourceObject = Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli();
        LocalDate expectedResult = LocalDate.of(2017, 11, 2);

        LocalDate result = (LocalDate)localDateConverter.convert(LocalDate.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromDateToLocalDateTimeViaDate() {
        Date sourceObject = new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli());
        LocalDate expectedResult = LocalDate.of(2017, 11, 2);

        LocalDate result = (LocalDate)localDateConverter.convert(LocalDate.class, sourceObject);

        assertTrue(String.format("%s is not equal to %s", result, expectedResult), expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromLocalTimeToLocalTimeViaTime() {
        LocalTime sourceObject = LocalTime.of(10, 20, 30);
        LocalTime expectedResult = LocalTime.of(10, 20, 30);

        LocalTime result = (LocalTime)localTimeConverter.convert(LocalTime.class, sourceObject);

        assertEquals(expectedResult, result);
    }

    @Test
    public void canConvertFromOffsetDateTimeToLocalTimeViaTime() {
        OffsetDateTime sourceObject = OffsetDateTime.parse("2017-11-02T10:20:30+02:00");
        LocalTime expectedResult = LocalTime.of(10, 20, 30);

        LocalTime result = (LocalTime)localTimeConverter.convert(LocalTime.class, sourceObject);

        assertEquals(expectedResult, result);
    }

    @Test
    public void canConvertFromStringToLocalTimeViaTime() {
        String sourceObject = "10:20:30";
        LocalTime expectedResult = LocalTime.of(10, 20, 30);

        LocalTime result = (LocalTime)localTimeConverter.convert(LocalTime.class, sourceObject);

        assertEquals(expectedResult, result);
    }

    @Test
    public void canConvertFromLongToLocalTimeViaTime() {
        Long sourceObject = Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli();
        LocalTime expectedResult = LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault()).toLocalTime();

        LocalTime result = (LocalTime)localTimeConverter.convert(LocalTime.class, sourceObject);

        assertEquals(expectedResult, result);
    }

    @Test
    public void canConvertFromDateToLocalTimeViaTime() {
        Date sourceObject = new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli());
        LocalTime expectedResult = LocalDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault()).toLocalTime();

        LocalTime result = (LocalTime)localTimeConverter.convert(LocalTime.class, sourceObject);

        assertEquals(expectedResult, result);
    }

    @Test(expected = ConversionException.class)
    public void testThrowExceptionWhenSourceNotSupported() {
        localTimeConverter.convert(LocalDateTime.class, 'a');
    }
}
