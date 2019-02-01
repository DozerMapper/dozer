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
package com.github.dozermapper.core.classmap;

import java.util.Map;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassMappingsTest extends AbstractDozerTest {

    private ClassMappings classMappings;

    @Before
    public void setUp() {
        classMappings = new ClassMappings(new BeanContainer());
    }

    @Test
    public void testFind_Null() {
        assertNull(classMappings.find(String.class, Integer.class));
    }

    @Test
    public void testFind_Simple() {
        classMappings.add(String.class, Integer.class, mock(ClassMap.class));
        ClassMap result = classMappings.find(String.class, Integer.class);
        assertNotNull(result);
    }

    @Test
    public void testFind_Nested() {
        classMappings.add(NestedClass.class, String.class, mock(ClassMap.class));
        ClassMap result = classMappings.find(NestedClass.class, String.class);
        assertNotNull(result);
    }

    @Test(expected = MappingException.class)
    public void testNotFoundByMapid() {
        classMappings.find(NestedClass.class, String.class, "A");
    }

    @Test
    public void shouldAdd() {
        classMappings.add(String.class, Integer.class, mock(ClassMap.class));
        classMappings.add(String.class, Integer.class, "id", mock(ClassMap.class));

        Map<String, ClassMap> result = classMappings.getAll();
        assertEquals(2, result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnDuplicate() {
        ClassMap classMap = mock(ClassMap.class);

        when(classMap.getSrcClassName()).thenReturn(String.class.getName());
        when(classMap.getDestClassName()).thenReturn(Integer.class.getName());

        classMappings.add(String.class, Integer.class, classMap);
        classMappings.add(String.class, Integer.class, classMap);
    }

    @Test
    public void shouldNotFailOnDuplicatesForSameSrcAndDest() {
        ClassMap classMap = mock(ClassMap.class);

        when(classMap.getSrcClassName()).thenReturn(String.class.getName());
        when(classMap.getDestClassName()).thenReturn(String.class.getName());

        classMappings.add(String.class, String.class, classMap);
        classMappings.add(String.class, String.class, classMap);
    }

    public static class NestedClass {

    }

}
