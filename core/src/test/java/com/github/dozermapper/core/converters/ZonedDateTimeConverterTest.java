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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZonedDateTimeConverterTest {

    private final ZonedDateTimeConverter converter = new ZonedDateTimeConverter(DateTimeFormatter.ISO_ZONED_DATE_TIME);

    @Test
    public void canConvertFromToZonedDateTime() {
        ZonedDateTime sourceObject = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 0), ZoneId.systemDefault());
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 0), ZoneId.systemDefault());

        Object result = converter.convert(ZonedDateTime.class, sourceObject);

        assertTrue(result instanceof ZonedDateTime);
        assertEquals(expected, result);
    }

    @Test
    public void canConvertFromStringToZonedDateTime() {
        String sourceObject = "2017-11-02T10:20:30+01:00[Europe/Madrid]";
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 20, 30), ZoneId.of("Europe/Madrid"));

        Object result = converter.convert(ZonedDateTime.class, sourceObject);

        assertTrue(result instanceof ZonedDateTime);
        assertEquals(expected, result);
    }

    @Test
    public void canConvertFromInstantToZonedDateTime() {
        Instant sourceObject = Instant.parse("2017-11-02T10:20:30.00Z");
        ZonedDateTime expected = ZonedDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        Object result = converter.convert(ZonedDateTime.class, sourceObject);

        assertTrue(result instanceof ZonedDateTime);
        assertEquals(expected, result);
    }

    @Test
    public void canConvertFromLongToZonedDateTime() {
        Long sourceObject = Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli();
        ZonedDateTime expected = ZonedDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        Object result = converter.convert(ZonedDateTime.class, sourceObject);

        assertTrue(result instanceof ZonedDateTime);
        assertEquals(expected, result);
    }

    @Test
    public void canConvertFromDateToZonedDateTime() {
        Date sourceObject = new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli());
        ZonedDateTime expected = ZonedDateTime.ofInstant(Instant.parse("2017-11-02T10:20:30.00Z"), ZoneId.systemDefault());

        Object result = converter.convert(ZonedDateTime.class, sourceObject);

        assertTrue(result instanceof ZonedDateTime);
        assertEquals(expected, result);
    }
}
