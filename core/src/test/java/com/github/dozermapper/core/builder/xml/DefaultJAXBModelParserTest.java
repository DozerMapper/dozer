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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.xml.sax.SAXException;

import com.github.dozermapper.core.builder.model.jaxb.MappingsDefinition;
import com.github.dozermapper.core.config.BeanContainer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

public class DefaultJAXBModelParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJAXBModelParserTest.class);

    @Test
    public void canConstruct() {
        JAXBModelParser factory = new DefaultJAXBModelParser(new BeanContainer());

        assertNotNull(factory);
    }

    @Test
    public void testCurrentMappingXML() throws IOException, SAXException {
        JAXBModelParser<MappingsDefinition> factory = new DefaultJAXBModelParser<>(new BeanContainer());

        File folder = new File(new File(".").getCanonicalPath() + "/src/test/resources/mappings");
        File[] listOfFiles = folder.listFiles();

        LOG.info("Found {} to validate.", listOfFiles.length);

        for (File file : listOfFiles) {
            if (file.isFile()) {
                LOG.info("Validating {}", file.getAbsoluteFile());

                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    String xmlContent = IOUtils.toString(fileInputStream, Charset.forName("UTF-8"));

                    MappingsDefinition mapping = factory.readXML(xmlContent, MappingsDefinition.class);

                    assertNotNull(mapping);

                    factory.validateXML(xmlContent);
                }
            }
        }
    }
}
