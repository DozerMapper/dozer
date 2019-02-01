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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Internal class for converting supported types to {@link java.time.ZonedDateTime}.
 * Only intended for internal use.
 */
public class ZonedDateTimeConverter extends AbstractJava8DateTimeConverter {

    public ZonedDateTimeConverter(DateTimeFormatter formatter) {
        super(formatter);
    }

    @Override
    public Object convert(Class destClass, Object srcObject) {
        Class<?> srcObjectClass = srcObject.getClass();
        if (Date.class.isAssignableFrom(srcObjectClass)) {
            Instant instant = ((Date)srcObject).toInstant();
            return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        } else if (Instant.class.isAssignableFrom(srcObjectClass)) {
            return ZonedDateTime.ofInstant((Instant)srcObject, ZoneId.systemDefault());
        } else if (Long.class.isAssignableFrom(srcObjectClass)) {
            Instant instant = Instant.ofEpochMilli((Long)srcObject);
            return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        } else {
            return super.convert(destClass, srcObject);
        }
    }
}
