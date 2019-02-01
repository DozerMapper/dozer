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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import com.github.dozermapper.core.config.BeanContainer;

/**
 * Parses XML content to a jaxb model using {@link JAXBContext}
 *
 * @param <T> type of jaxb model
 */
public class DefaultJAXBModelParser<T> implements JAXBModelParser<T> {

    private static final String JAXB_CONTEXT_PACKAGES = "com.github.dozermapper.core.builder.model.jaxb";

    private final LSResourceResolver resourceResolver;
    private JAXBContext jaxbContext;

    public DefaultJAXBModelParser(BeanContainer beanContainer) {
        this.resourceResolver = new SchemaLSResourceResolver(beanContainer);

    }

    private JAXBContext getOrCreateJAXBContext() throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(JAXB_CONTEXT_PACKAGES, getClass().getClassLoader());
        }

        return jaxbContext;
    }

    private Schema newSchema() throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setResourceResolver(resourceResolver);

        return factory.newSchema();
    }

    private Validator newValidator(Schema schema) {
        Validator validator = schema.newValidator();
        validator.setResourceResolver(resourceResolver);

        return validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateXML(String xmlContent) throws IOException, SAXException {
        try (StringReader stringReader = new StringReader(xmlContent)) {
            StreamSource streamSource = new StreamSource(stringReader);

            Validator validator = newValidator(newSchema());
            validator.validate(streamSource);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T readXML(String xmlContent, Class<T> type) throws SAXException {
        JAXBElement<T> answer;

        try (StringReader stringReader = new StringReader(xmlContent)) {
            StreamSource streamSource = new StreamSource(stringReader);

            JAXBContext jaxbContext = getOrCreateJAXBContext();
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            answer = jaxbUnmarshaller.unmarshal(streamSource, type);
        } catch (JAXBException ex) {
            throw new SAXException(ex);
        }

        return answer.getValue();
    }
}
