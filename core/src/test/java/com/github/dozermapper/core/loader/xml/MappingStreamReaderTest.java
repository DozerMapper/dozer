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
package com.github.dozermapper.core.loader.xml;

import java.io.IOException;
import java.io.InputStream;

import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MappingStreamReaderTest {

    private MappingStreamReader streamReader;

    @Before
    public void setUp() throws Exception {
        BeanContainer beanContainer = new BeanContainer();
        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();
        streamReader = new MappingStreamReader(new XMLParserFactory(beanContainer), new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory));
    }

    @Test
    public void loadFromStreamTest() throws IOException {
        InputStream xmlStream = getClass().getClassLoader().getResourceAsStream("mappings/testDozerBeanMapping.xml");
        MappingFileData data = streamReader.read(xmlStream);
        xmlStream.close();

        assertThat(data, notNullValue());
    }
}
