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
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OffsetDateTimeConverterTest {

    private final OffsetDateTimeConverter offsetDateTimeConverterDate = new OffsetDateTimeConverter(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    private final OffsetDateTimeConverter offsetDateTimeConverterTime = new OffsetDateTimeConverter(DateTimeFormatter.ISO_OFFSET_TIME);

    @Test
    public void canConvertFromOffsetDateTimeToOffsetDateTimeViaDate() {
        OffsetDateTime sourceObject = OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.UTC);
        OffsetDateTime expectedResult = OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.UTC);

        OffsetDateTime result = (OffsetDateTime)offsetDateTimeConverterDate.convert(OffsetDateTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromOffsetDateTimeStringToOffsetDateTime() {
        OffsetDateTime sourceObject = OffsetDateTime.parse("2017-11-02T10:20:30+02:00");
        OffsetDateTime expectedResult = OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.ofHours(2));

        OffsetDateTime result = (OffsetDateTime)offsetDateTimeConverterDate.convert(OffsetDateTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromZonedDateTimeToOffsetDateTimeViaDate() {
        ZonedDateTime sourceObject = ZonedDateTime.parse("2017-11-02T10:20:30+01:00[Europe/Paris]");
        OffsetDateTime expectedResult = OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.ofHours(1));

        OffsetDateTime result = (OffsetDateTime)offsetDateTimeConverterDate.convert(OffsetDateTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromStringToOffsetDateTimeViaDate() {
        String sourceObject = "2017-11-02T10:20:30+02:00";
        OffsetDateTime expectedResult = OffsetDateTime.of(2017, 11, 2, 10, 20, 30, 0, ZoneOffset.ofHours(2));

        OffsetDateTime result = (OffsetDateTime)offsetDateTimeConverterDate.convert(OffsetDateTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromInstantToOffsetDateTimeViaDate() {
        Instant sourceObject = Instant.parse("2017-11-02T10:20:30.00Z");
        OffsetDateTime expectedResult = OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        OffsetDateTime result = (OffsetDateTime)offsetDateTimeConverterDate.convert(OffsetDateTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromLongToOffsetDateTimeViaDate() {
        Long sourceObject = Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli();
        OffsetDateTime expectedResult = OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        OffsetDateTime result = (OffsetDateTime)offsetDateTimeConverterDate.convert(OffsetDateTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromDateToOffsetDateTimeViaDate() {
        Date sourceObject = new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli());
        OffsetDateTime expectedResult = OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        OffsetDateTime result = (OffsetDateTime)offsetDateTimeConverterDate.convert(OffsetDateTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromOffsetTimeToOffsetDateTimeViaTime() {
        OffsetTime sourceObject = OffsetTime.of(LocalTime.of(10, 20, 30), ZoneOffset.ofHours(2));
        OffsetTime expectedResult = OffsetTime.of(LocalTime.of(10, 20, 30), ZoneOffset.ofHours(2));

        OffsetTime result = (OffsetTime)offsetDateTimeConverterTime.convert(OffsetTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromStringToOffsetDateTimeViaTime() {
        String sourceObject = "10:20:30+04:00";
        OffsetTime expectedResult = OffsetTime.of(LocalTime.of(10, 20, 30), ZoneOffset.ofHours(4));

        OffsetTime result = (OffsetTime)offsetDateTimeConverterTime.convert(OffsetTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromLongToOffsetDateTimeViaTime() {
        Long sourceObject = Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli();
        OffsetTime expectedResult = OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault()).toOffsetTime();

        OffsetTime result = (OffsetTime)offsetDateTimeConverterTime.convert(OffsetTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }

    @Test
    public void canConvertFromDateToOffsetDateTimeViaTime() {
        Date sourceObject = new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli());
        OffsetTime expectedResult = OffsetDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault()).toOffsetTime();

        OffsetTime result = (OffsetTime)offsetDateTimeConverterTime.convert(OffsetTime.class, sourceObject);

        assertTrue(expectedResult.isEqual(result));
    }
}
