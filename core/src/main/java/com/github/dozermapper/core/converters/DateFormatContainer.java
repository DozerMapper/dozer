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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Internal class used as a container to determine the date format to use for a particular field mapping. Only intended
 * for internal use.
 */
public class DateFormatContainer {

    private String dfStr;
    private DateFormat dateFormat;
    private DateTimeFormatter dateTimeFormatter;

    public DateFormatContainer(String dfStr) {
        this.dfStr = dfStr;
    }

    /**
     * Whether a date format is present.
     *
     * @return If present, return {@code true}
     * @since 6.5.0
     */
    public boolean isPresent() {
        return dfStr != null || dateFormat != null;
    }

    public DateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = determineDateFormat();
        }
        return dateFormat;
    }

    /**
     * Date and time formatter for Java 8 Date &amp; Time objects.
     *
     * @return formatter
     */
    public DateTimeFormatter getDateTimeFormatter() {
        if (dateTimeFormatter == null) {
            if (dfStr != null) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(dfStr);
            }
        }
        return dateTimeFormatter;
    }

    /**
     * TODO replace method call with reflection in tests.
     *
     * @param dateFormat dateFormat
     * @deprecated This method will break internal state of the instance by setting formatter which is not using
     * format provided using constructor. It will be removed in future releases.
     */
    @Deprecated
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    private DateFormat determineDateFormat() {
        return dfStr == null ? null : new SimpleDateFormat(dfStr, Locale.getDefault());
    }
}
