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

import java.util.HashMap;

import com.github.dozermapper.core.vo.ValueObject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CopyByReferenceFromMapTest extends AbstractFunctionalTest {

    @Override
    @Before
    public void setUp() throws Exception {
        mapper = getMapper("mappings/mapMapping7.xml");
    }

    @Test
    public void testCopyByReferenceFromMap() {
        HashMap<String, ValueObject> hashMap = newInstance(HashMap.class);
        hashMap.put("1", new ValueObject());

        ValueObject destination = newInstance(ValueObject.class);
        mapper.map(hashMap, destination);

        assertNotNull(destination);
        assertNotNull(destination.getValue());
    }

}
