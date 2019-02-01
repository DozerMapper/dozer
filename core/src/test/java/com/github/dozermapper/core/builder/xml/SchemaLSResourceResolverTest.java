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
package com.github.dozermapper.core.builder.xml;

import java.net.URL;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNotNull;

public class SchemaLSResourceResolverTest {

    @Rule
    public ExpectedException handlesNullSystemId = ExpectedException.none();

    @Rule
    public ExpectedException handlesV5Schema = ExpectedException.none();

    private String nullSystemIdMessage = "System ID is empty. Expected: https://dozermapper.github.io/schema/bean-mapping.xsd"
                                         + "'. Please see migration guide @ https://dozermapper.github.io/gitbook";

    private String v5Message = "Dozer >= v6.0.0 uses a new XSD location. "
                               + "Your current config needs to be upgraded. "
                               + "Found v5 XSD: 'http://dozer.sourceforge.net/schema/beanmapping.xsd'. "
                               + "Expected v6 XSD: 'https://dozermapper.github.io/schema/bean-mapping.xsd'. "
                               + "Please see migration guide @ https://dozermapper.github.io/gitbook";

    @Test
    public void handlesNullSystemId() {
        handlesNullSystemId.expectMessage(Matchers.containsString(nullSystemIdMessage));
        handlesNullSystemId.expect(MappingException.class);

        LSResourceResolver resolver = new SchemaLSResourceResolver(new BeanContainer());
        resolver.resolveResource(null, null, null, null, null);
    }

    @Test
    public void handlesV5Schema() {
        handlesV5Schema.expectMessage(Matchers.containsString(v5Message));
        handlesV5Schema.expect(MappingException.class);

        String systemId = "http://dozer.sourceforge.net/schema/beanmapping.xsd";

        LSResourceResolver resolver = new SchemaLSResourceResolver(new BeanContainer());
        resolver.resolveResource(null, null, null, systemId, null);
    }

    @Test
    public void canResolveViaClasspath() {
        URL systemId = getClass().getClassLoader().getResource("schema/bean-mapping.xsd");

        LSResourceResolver resolver = new SchemaLSResourceResolver(new BeanContainer());
        LSInput answer = resolver.resolveResource(null, null, null, systemId.toString(), null);

        assertNotNull(answer);
        assertNotNull(answer.getByteStream());
        assertNotNull(answer.getCharacterStream());
    }

    @Test
    public void canResolveViaURL() {
        String systemId = "https://dozermapper.github.io/schema/dozer-spring.xsd";

        LSResourceResolver resolver = new SchemaLSResourceResolver(new BeanContainer());
        LSInput answer = resolver.resolveResource(null, null, null, systemId, null);

        assertNotNull(answer);
        assertNotNull(answer.getByteStream());
        assertNotNull(answer.getCharacterStream());
    }
}
