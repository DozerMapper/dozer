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

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegerConverterTest extends AbstractDozerTest {

    private IntegerConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new IntegerConverter();
    }

    @Test
    public void testConvert() {
        assertEquals(new Integer(1), converter.convert(Integer.class, Boolean.TRUE));
        assertEquals(new Integer(0), converter.convert(Integer.class, Boolean.FALSE));
        assertEquals(new Integer(100), converter.convert(Integer.class, "100"));
    }
}
