/**
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

import org.dozer.AbstractDozerTest;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.MappingFileData;
import org.dozer.fieldmap.FieldMap;
import org.dozer.loader.MappingsSource;
import org.dozer.util.ResourceLoader;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.net.URL;
import java.util.List;

/**
 * @author garsombke.franz
 * @author johnsen.knut-erik
 */
public class XMLParserTest extends AbstractDozerTest {

  MappingsSource<Document> parser;
  ResourceLoader loader;

  @Before
  public void setUp() {
    loader = new ResourceLoader(getClass().getClassLoader());
  }

  @Test
  public void testParse() throws Exception {
    URL url = loader.getResource("dozerBeanMapping.xml");

    Document document = XMLParserFactory.getInstance().createParser().parse(url.openStream());
    parser = new XMLParser();

    MappingFileData mappings = parser.read(document);
    assertNotNull(mappings);
  }

  /**
   * This tests checks that the customconverterparam reaches the
   * fieldmapping.      
   */
  @Test
  public void testParseCustomConverterParam() throws Exception {
    URL url = loader.getResource("fieldCustomConverterParam.xml");

    Document document = XMLParserFactory.getInstance().createParser().parse(url.openStream());
    parser = new XMLParser();
    
    MappingFileData mappings = parser.read(document);

    assertNotNull("The mappings should not be null", mappings);

    List<ClassMap> mapping = mappings.getClassMaps();

    assertNotNull("The list of mappings should not be null", mapping);

    assertEquals("There should be one mapping", 3, mapping.size());

    ClassMap classMap = mapping.get(0);

    assertNotNull("The classmap should not be null", classMap);

    List<FieldMap> fieldMaps = classMap.getFieldMaps();

    assertNotNull("The fieldmaps should not be null", fieldMaps);
    assertEquals("The fieldmap should have one mapping", 1, fieldMaps.size());

    FieldMap fieldMap = fieldMaps.get(0);

    assertNotNull("The fieldmap should not be null", fieldMap);
    assertEquals("The customconverterparam should be correct", "CustomConverterParamTest", fieldMap.getCustomConverterParam());
  }

}
