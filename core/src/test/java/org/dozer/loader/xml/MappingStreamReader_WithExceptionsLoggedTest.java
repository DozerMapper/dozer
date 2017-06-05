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
package org.dozer.loader.xml;

import java.io.IOException;

import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingStreamReader_WithExceptionsLoggedTest {

    @Rule
    public ExpectedException nullLoadFromStreamsTest = ExpectedException.none();

    private final Logger LOG = LoggerFactory.getLogger(MappingStreamReader_WithExceptionsLoggedTest.class);
    private MappingStreamReader streamReader;

    @Before
    public void setUp() throws Exception {
        BeanContainer beanContainer = new BeanContainer();
        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        streamReader = new MappingStreamReader(new XMLParserFactory(beanContainer), new XMLParser(beanContainer, destBeanCreator));
    }

    @Test
    public void nullLoadFromStreamsTest() throws IOException {
        LOG.error("WithExceptionsLoggedTest; 'IllegalArgumentException: InputStream cannot be null'");

        nullLoadFromStreamsTest.expectMessage("InputStream cannot be null");
        nullLoadFromStreamsTest.expect(IllegalArgumentException.class);

        streamReader.read(null);
    }
}
