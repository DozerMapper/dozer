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
package com.github.dozermapper.spring.functional_tests;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class SchemaLocationTest {

    @Test
    public void ensureHttpsSchemaAccessible() throws IOException {
        URI uri = URI.create("https://dozermapper.github.io/schema/dozer-spring.xsd");
        String value = IOUtils.toString(uri, "utf-8");
        Assert.assertTrue(value.contains("http://dozermapper.github.io/schema/dozer-spring"));
    }
}
