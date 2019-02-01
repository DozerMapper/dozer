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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.xml.sax.SAXException;

import com.github.dozermapper.core.builder.BeanMappingsBuilder;
import com.github.dozermapper.core.builder.model.elengine.ELMappingsDefinition;
import com.github.dozermapper.core.builder.model.jaxb.MappingsDefinition;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.ELEngine;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.MappingValidator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a mapping definition based on a XML file
 */
public class BeanMappingXMLBuilder implements BeanMappingsBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(BeanMappingXMLBuilder.class);

    private final List<MappingsDefinition> mappingsDefinitions = new ArrayList<>();
    private final JAXBModelParser<MappingsDefinition> factory;
    private final ELEngine elEngine;
    private final BeanContainer beanContainer;

    public BeanMappingXMLBuilder(BeanContainer beanContainer, ELEngine elEngine) {
        this.factory = new DefaultJAXBModelParser<>(beanContainer);
        this.elEngine = elEngine;
        this.beanContainer = beanContainer;
    }

    /**
     * Loads the collection of files into memory and parses the XML
     *
     * @param files collection of file paths
     */
    public void loadFiles(List<String> files) {
        for (String path : files) {
            URL url = MappingValidator.validateURL(path, beanContainer);

            LOG.info("Using URL [" + path + "] to load custom xml mappings");

            try (InputStream stream = new BufferedInputStream(url.openStream())) {
                load(stream);
            } catch (IOException ex) {
                MappingUtils.throwMappingException(ex);
            }
        }
    }

    /**
     * Loads the collection of streams into memory and parses the XML
     *
     * @param xmlMappingSuppliers collection of streams to xml file/content
     */
    public void loadInputStreams(List<Supplier<InputStream>> xmlMappingSuppliers) {
        for (Supplier<InputStream> supplier : xmlMappingSuppliers) {
            try (InputStream stream = new BufferedInputStream(supplier.get())) {
                load(stream);
            } catch (IOException ex) {
                MappingUtils.throwMappingException(ex);
            }
        }
    }

    /**
     * Load the XML file from the stream
     *
     * @param stream stream to xml file/content - caller must close
     */
    private void load(InputStream stream) {
        try {
            String xmlContent = IOUtils.toString(stream, Charset.forName("UTF-8"));

            MappingsDefinition result = factory.readXML(xmlContent, MappingsDefinition.class);
            if (result == null) {
                LOG.error("Failed to load custom xml mappings for: {}", xmlContent);
            } else {
                factory.validateXML(xmlContent);

                mappingsDefinitions.add(new ELMappingsDefinition(elEngine, result));

                LOG.info("Successfully loaded custom xml mapping.");
            }
        } catch (IOException ex) {
            MappingUtils.throwMappingException(ex);
        } catch (SAXException ex) {
            MappingUtils.throwMappingException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MappingFileData> build(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        List<MappingFileData> answer = new ArrayList<>();
        for (MappingsDefinition mappingsDefinition : mappingsDefinitions) {
            Configuration configuration = null;
            if (mappingsDefinition.getConfiguration() != null) {
                configuration = mappingsDefinition.getConfiguration().build(beanContainer);
            }

            MappingFileData data = new MappingFileData();
            data.setConfiguration(configuration);
            data.getClassMaps().addAll(mappingsDefinition.build(configuration, beanContainer, destBeanCreator, propertyDescriptorFactory));

            answer.add(data);
        }

        return answer;
    }
}
