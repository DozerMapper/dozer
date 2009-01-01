/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.classmap.ClassMappings;
import net.sf.dozer.classmap.Configuration;
import net.sf.dozer.classmap.MappingFileData;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class MappingsParserTest extends AbstractDozerTest {

  private MappingsParser parser;

  @Override
  @Before
  public void setUp() throws Exception {
    parser = MappingsParser.getInstance();
  }

  @Test
  public void testDuplicateMapIds() throws Exception {
    MappingFileReader fileReader = new MappingFileReader("duplicateMapIdsMapping.xml");
    MappingFileData mappingFileData = fileReader.read();

    try {
      parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
      fail("should have thrown exception");
    } catch (Exception e) {
      assertTrue("invalid exception thrown", e.getMessage().indexOf("Duplicate Map Id") != -1);
    }
  }

  @Test
  public void testDetectDuplicateMapping() throws Exception {
    MappingFileReader fileReader = new MappingFileReader("duplicateMapping.xml");
    MappingFileData mappingFileData = fileReader.read();
    try {
      parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
      fail("should have thrown exception");
    } catch (Exception e) {
      assertTrue("invalid exception", e.getMessage().indexOf("Duplicate Class Mapping Found") != -1);
    }
  }

  @Test
  public void testEmptyMappings() throws Exception {
    MappingFileData mappingFileData = new MappingFileData();
    ClassMappings result = parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
    assertNotNull("result should not be null", result);
    assertEquals("result should be empty", 0, result.size());
  }
}
