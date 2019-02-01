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
package com.github.dozermapper.core.functional_tests;

import java.util.StringTokenizer;

import com.github.dozermapper.core.vo.AnotherTestObject;
import com.github.dozermapper.core.vo.Fruit;
import com.github.dozermapper.core.vo.Individual;
import com.github.dozermapper.core.vo.SimpleObj;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CustomConverterParamMappingTest extends AbstractFunctionalTest {

    @Override
    @Before
    public void setUp() throws Exception {
        mapper = getMapper("mappings/fieldCustomConverterParam.xml");
    }

    @Test
    public void testSimpleCustomConverter() {
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1(String.valueOf(System.currentTimeMillis()));

        AnotherTestObject dest = mapper.map(src, AnotherTestObject.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField3());
        StringTokenizer st = new StringTokenizer(dest.getField3(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("1st portion of dest field1 value should equal src field value", src.getField1(), token1);
        String token2 = st.nextToken();
        assertEquals("custom converter param should have been appended to by the cust converter", "CustomConverterParamTest", token2);

    }

    @Test
    public void testGlobalCustomConverter() {
        Individual individual = newInstance(Individual.class);
        individual.setUsername("ABC");
        Fruit result = mapper.map(individual, Fruit.class, "1");
        assertNotNull("", result.getName());
        assertTrue(result.getName().startsWith("ABC-null"));
    }

    @Test
    public void testGlobalCustomConverter_ParamProvided() {
        Individual individual = newInstance(Individual.class);
        individual.setUsername("ABC");
        Fruit result = mapper.map(individual, Fruit.class, "2");
        assertNotNull("", result.getName());
        assertTrue(result.getName().startsWith("ABC-PARAM"));
    }

}
