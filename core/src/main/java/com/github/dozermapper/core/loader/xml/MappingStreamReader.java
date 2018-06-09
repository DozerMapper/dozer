/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.loader.xml;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.loader.MappingsSource;
import com.github.dozermapper.core.util.MappingUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class that reads and parses a single custom mapping XML stream into
 * raw ClassMap objects. Only intended for internal use.
 */
@Deprecated
public class MappingStreamReader implements MappingsSource<InputStream> {

    private static final Logger log = LoggerFactory
            .getLogger(MappingStreamReader.class);

    private final DocumentBuilder documentBuilder;
    private final MappingsSource<Document> parser;

    public MappingStreamReader(XMLParserFactory parserFactory, XMLParser xmlParser) {
        this.documentBuilder = parserFactory.createParser();
        this.parser = xmlParser;
    }

    public MappingFileData read(InputStream xmlStream) {
        MappingFileData result = null;
        try {
            Document document = documentBuilder.parse(xmlStream);
            result = parser.read(document);
        } catch (Throwable e) {
            log.error("Error while loading dozer mapping InputStream: ["
                      + xmlStream + "]", e);
            MappingUtils.throwMappingException(e);
        }
        return result;
    }

}
