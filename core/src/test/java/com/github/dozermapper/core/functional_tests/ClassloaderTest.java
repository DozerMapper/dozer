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

import com.github.dozermapper.core.DozerBeanMapperBuilder;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ClassloaderTest extends AbstractFunctionalTest {

    @Test
    public void testClassloader() {
        mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("non-strict/classloader.xml")
                .build();
        assertNotNull(mapper);
    }

}
