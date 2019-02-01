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

import org.xml.sax.SAXException;

/**
 * Parses XML content to a jaxb model
 *
 * @param <T> type of JAXB model
 */
public interface JAXBModelParser<T> {

    /**
     * Validates xml content against resolved schema
     *
     * @param xmlContent xml content to validate
     * @throws IOException  content related exception
     * @throws SAXException validation related execption
     */
    void validateXML(String xmlContent) throws IOException, SAXException;

    /**
     * Loads XML into memory and converts to a type
     *
     * @param xmlContent xml content to load
     * @param type       type to convert xml to
     * @return parsed XML to type
     * @throws IOException  content related exception
     * @throws SAXException parsing related execption
     */
    T readXML(String xmlContent, Class<T> type) throws IOException, SAXException;
}
