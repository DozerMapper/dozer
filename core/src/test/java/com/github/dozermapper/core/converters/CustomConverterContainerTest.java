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

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.cache.CacheKeyFactory;
import com.github.dozermapper.core.cache.DefaultCache;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CustomConverterContainerTest extends AbstractDozerTest {

    private CustomConverterContainer ccc;
    private DefaultCache cache;
    private List<CustomConverterDescription> converters;

    @Before
    public void setUp() throws Exception {
        ccc = new CustomConverterContainer();
        cache = new DefaultCache("NAME", 10);
        converters = new ArrayList<>();
        ccc.setConverters(converters);
    }

    @Test
    public void testSetConverters() {
        CustomConverterDescription description = new CustomConverterDescription();
        converters.add(description);
        ccc.setConverters(converters);

        assertNotNull(ccc.getConverters());
        assertTrue(ccc.getConverters().size() == converters.size());
        assertEquals(ccc.getConverters().get(0), converters.get(0));
    }

    @Test
    public void testGetCustomConverter_Cached() {
        CustomConverterDescription description = new CustomConverterDescription();
        converters.add(description);
        cache.put(CacheKeyFactory.createKey(String.class, Integer.class), Object.class);

        Class result;

        result = ccc.getCustomConverter(Integer.class, String.class, cache);
        assertEquals(Object.class, result);
    }

    @Test
    public void testGetCustomConverter_NotCached() {
        CustomConverterDescription description = new CustomConverterDescription();
        description.setClassA(String.class);
        description.setClassB(Integer.class);
        description.setType(Void.class);
        converters.add(description);

        Class result;

        result = ccc.getCustomConverter(String.class, Integer.TYPE, cache);
        assertEquals(Void.class, result);

        result = ccc.getCustomConverter(String.class, Integer.class, cache);
        assertEquals(Void.class, result);
    }

    @Test
    public void testGetCustomConverter_Miss() {
        cache.put(CacheKeyFactory.createKey(String.class, Integer.class), Object.class);

        Class result;

        result = ccc.getCustomConverter(Integer.class, Double.class, cache);
        assertEquals(null, result);

        result = ccc.getCustomConverter(Double.class, String.class, cache);
        assertEquals(null, result);
    }

    @Test
    public void testGetCustomConverter_IsEmpty() {
        converters.clear();
        Class result = ccc.getCustomConverter(Integer.class, Double.class, cache);

        assertEquals(null, result);
    }

    @Test
    public void shouldPutNullInCache() {
        CustomConverterDescription description = new CustomConverterDescription();
        description.setClassA(String.class);
        description.setClassB(String.class);
        ccc.addConverter(description);

        assertNull(ccc.getCustomConverter(Integer.class, Double.class, cache));

        assertEquals(1, cache.getSize());
    }
}
