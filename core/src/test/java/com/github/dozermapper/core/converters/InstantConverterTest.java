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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InstantConverterTest {

    private final InstantConverter converter = new InstantConverter();

    @Test
    public void canConvertFromInstantToInstant() {
        Instant sourceObject = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 0), ZoneOffset.UTC).toInstant();
        Instant expected = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 0), ZoneOffset.UTC).toInstant();

        Object result = converter.convert(Instant.class, sourceObject);

        assertTrue(result instanceof Instant);
        assertEquals(expected, result);
    }

    @Test
    public void canConvertFromStringToInstant() {
        String sourceObject = "2017-11-02T10:20:30.00Z";
        Instant expected = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 20, 30), ZoneOffset.UTC).toInstant();

        Object result = converter.convert(Instant.class, sourceObject);

        assertTrue(result instanceof Instant);
        assertEquals(expected, result);
    }

    @Test
    public void canConvertFromZonedDateTimeToInstant() {
        ZonedDateTime sourceObject = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 20, 30), ZoneOffset.UTC);
        Instant expected = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 20, 30), ZoneOffset.UTC).toInstant();

        Object result = converter.convert(Instant.class, sourceObject);

        assertTrue(result instanceof Instant);
        assertEquals(expected, result);
    }

    @Test
    public void canConvertFromDateToInstant() {
        Date sourceObject = new Date(Instant.parse("2017-11-02T10:20:30.00Z").toEpochMilli());
        Instant expected = ZonedDateTime.of(LocalDateTime.of(2017, 11, 2, 10, 20, 30), ZoneOffset.UTC).toInstant();

        Object result = converter.convert(Instant.class, sourceObject);

        assertTrue(result instanceof Instant);
        assertEquals(expected, result);
    }
}
