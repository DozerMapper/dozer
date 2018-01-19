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
package org.dozer.loader;

import org.dozer.AbstractDozerTest;
import org.dozer.classmap.ClassMappings;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingFileData;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.loader.xml.MappingFileReader;
import org.dozer.loader.xml.XMLParser;
import org.dozer.loader.xml.XMLParserFactory;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class MappingsParserTest extends AbstractDozerTest {

  private MappingsParser parser;
  private BeanContainer beanContainer;
  private DestBeanCreator destBeanCreator;
  private PropertyDescriptorFactory propertyDescriptorFactory;

  @Override
  @Before
  public void setUp() throws Exception {
    beanContainer = new BeanContainer();
    destBeanCreator = new DestBeanCreator(beanContainer);
    propertyDescriptorFactory = new PropertyDescriptorFactory();
    parser = new MappingsParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testDuplicateMapIds() throws Exception {
    MappingFileReader fileReader = new MappingFileReader(new XMLParserFactory(beanContainer), new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory), beanContainer);
    MappingFileData mappingFileData = fileReader.read("mappings/duplicateMapIdsMapping.xml");

    parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
    fail("should have thrown exception");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testDetectDuplicateMapping() throws Exception {
    MappingFileReader fileReader = new MappingFileReader(new XMLParserFactory(beanContainer), new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory), beanContainer);
    MappingFileData mappingFileData = fileReader.read("mappings/duplicateMapping.xml");
    parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
    fail("should have thrown exception");
  }

  @Test
  public void testEmptyMappings() throws Exception {
    MappingFileData mappingFileData = new MappingFileData();
    ClassMappings result = parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
    assertNotNull("result should not be null", result);
    assertEquals("result should be empty", 0, result.size());
  }
}
