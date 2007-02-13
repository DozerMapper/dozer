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
package net.sf.dozer.util.mapping.util;

import java.util.Map;

import net.sf.dozer.util.mapping.DozerTestBase;
import net.sf.dozer.util.mapping.fieldmap.Mappings;
/**
 * @author tierney.matt
 */
public class MappingsParserTest extends DozerTestBase {
  
  private MappingsParser parser;
  
  protected void setUp() throws Exception {
    super.setUp();
    parser = new MappingsParser();
  }

  public void testDuplicateMapIds() throws Exception {
    MappingFileReader fileReader = new MappingFileReader("duplicateMapIdsMapping.xml");
    Mappings mappings = fileReader.read();
    
    try {
      parser.parseMappings(mappings);
      fail("should have thrown exception");
    } catch (Exception e) {
      assertTrue("invalid exception thrown", e.getMessage().indexOf("Duplicate Map Id") != -1);
    }
  }
  
  public void testDetectDuplicateMapping() throws Exception {
    MappingFileReader fileReader = new MappingFileReader("duplicateMapping.xml");
    Mappings mappings = fileReader.read();
    try {
      parser.parseMappings(mappings);
      fail("should have thrown exception");
    } catch (Exception e) {
      assertTrue("invalid exception", e.getMessage().indexOf("Duplicate Class Mapping Found") != -1);
    }
  }
  
  public void testEmptyMappings() throws Exception {
    Mappings mappings = new Mappings();
    Map result = parser.parseMappings(mappings);
    assertNotNull("result should not be null", result);
    assertEquals("result should be empty", 0, result.size());
  }
}
