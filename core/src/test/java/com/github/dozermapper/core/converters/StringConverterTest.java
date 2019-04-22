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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringConverterTest extends AbstractDozerTest {

    @Test
    public void testDateToString_NoDateFormatSpecified() {
        Date src = new Date();
        StringConverter converter = new StringConverter(new DateFormatContainer(null));
        String result = (String)converter.convert(String.class, src);

        assertEquals("incorrect value returned from converter", src.toString(), result);
    }

    @Test
    public void testCalendarToString_NoDateFormatSpecified() {
        Calendar src = Calendar.getInstance();
        StringConverter converter = new StringConverter(new DateFormatContainer(null));
        String result = (String)converter.convert(String.class, src);

        assertEquals("incorrect value returned from converter", src.toString(), result);
    }

    @Test
    public void testLocalDateTimeToStringNoFormat() {
        LocalDateTime src = LocalDateTime.now();
        StringConverter converter = new StringConverter(new DateFormatContainer(null));
        String result = (String)converter.convert(String.class, src);

        assertEquals("incorrect value returned from converter", src.toString(), result);
    }

    @Test
    public void testLocalDateTimeToStringWithFormat() {
        LocalDateTime src = LocalDateTime.now();
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        String formattedSrc = DateTimeFormatter.ofPattern(pattern).format(src);

        StringConverter converter = new StringConverter(new DateFormatContainer(pattern));
        String result = (String)converter.convert(String.class, src);

        assertEquals("incorrect value returned from converter", formattedSrc, result);
    }

    // See https://github.com/DozerMapper/dozer/issues/747
    @Test
    public void testZonedDateTimeToStringWithFormat() {
        ZonedDateTime src = ZonedDateTime.now();
        String pattern = "uuuu/MM/dd HH:mm:ss.SSSZZZZZ'['VV']'";
        String formattedSrc = DateTimeFormatter.ofPattern(pattern).format(src);

        StringConverter converter = new StringConverter(new DateFormatContainer(pattern));
        String result = (String)converter.convert(String.class, src);

        assertEquals("incorrect value returned from converter", formattedSrc, result);
    }


}
