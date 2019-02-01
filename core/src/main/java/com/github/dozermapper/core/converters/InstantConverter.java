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
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.apache.commons.beanutils.Converter;

/**
 * Internal converter to {@link Instant} type. Only intended for internal use.
 */
public final class InstantConverter implements Converter {

    @Override
    public Object convert(Class destClass, Object srcObject) {
        Class<?> srcObjectClass = srcObject.getClass();

        if (Date.class.isAssignableFrom(srcObjectClass)) {
            Instant instant = ((Date) srcObject).toInstant();
            return instant;
        } else if (Instant.class.isAssignableFrom(srcObjectClass)) {
            return Instant.from((Instant) srcObject);
        } else if (TemporalAccessor.class.isAssignableFrom(srcObjectClass)) {
            return Instant.from((TemporalAccessor) srcObject);
        } else if (String.class.isAssignableFrom(srcObjectClass)) {
            return Instant.parse((String) srcObject);
        } else {
            throw new ConversionException(String.format("Unsupported source object type: %s", srcObjectClass), null);
        }
    }
}
