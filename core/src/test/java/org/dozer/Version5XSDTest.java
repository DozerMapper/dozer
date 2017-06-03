/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class Version5XSDTest {

    @Rule
    public ExpectedException testOldXSDNaming = ExpectedException.none();

    private String message = "Dozer >= v6.0.0 uses a new XSD location. " +
                             "Your current config needs to be upgraded. " +
                             "Found v5 XSD: 'http://dozer.sourceforge.net/schema/beanmapping.xsd'. " +
                             "Expected v6 XSD: 'http://dozermapper.github.io/schema/bean-mapping.xsd'. " +
                             "Please see migration guide @ https://dozermapper.github.io/gitbook";

    @Test
    public void testOldXSDNaming() {
        testOldXSDNaming.expectMessage(Matchers.containsString(message));
        testOldXSDNaming.expect(MappingException.class);

        DozerBeanMapper mapper = new DozerBeanMapper(Arrays.asList("non-strict/v5-xsd.xml"));
        mapper.getMappingMetadata();
    }
}
