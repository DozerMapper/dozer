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
package net.sf.dozer.util.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.util.mapping.vo.FieldValue;
import net.sf.dozer.util.mapping.vo.MessageHeaderDTO;
import net.sf.dozer.util.mapping.vo.MessageHeaderVO;
import net.sf.dozer.util.mapping.vo.MessageIdVO;
import net.sf.dozer.util.mapping.vo.index.Mccoy;
import net.sf.dozer.util.mapping.vo.index.MccoyPrime;
import net.sf.dozer.util.mapping.vo.map.NestedObj;
import net.sf.dozer.util.mapping.vo.map.SimpleObj;
import net.sf.dozer.util.mapping.vo.map.SimpleObjPrime;

/**
 * This is a holding grounds for test cases that reproduce known bugs, features, or gaps discovered during development.
 * As the use cases are resolved, these tests should be moved to the live unit test classes.
 * 
 * @author tierney.matt
 */
public class KnownFailures extends AbstractDozerTest {

  /*
   * 7-06-07 This test started failing as a result of the Mapped backed property refactoring.  Need to resolve prior to releasing 3.5
   */
  public void testStringToIndexedSet_UsingMapSetMethod() {
    mapper = getNewMapper(new String[] { "indexMapping.xml" });
    Mccoy src = new Mccoy();
    src.setStringProperty(String.valueOf(System.currentTimeMillis()));

    MccoyPrime dest = (MccoyPrime) mapper.map(src, MccoyPrime.class);
    Set destSet = dest.getFieldValueObjects();
    assertNotNull("dest set should not be null", destSet);
    assertEquals("dest set should contain 1 entry", 1, destSet.size());
    Object entry = destSet.iterator().next();
    assertTrue("dest set entry should be instance of FieldValue", entry instanceof FieldValue);
    assertEquals("invalid value for dest object", src.getStringProperty(), ((FieldValue) entry).getValue());
    assertEquals("invalid key for dest object", "stringProperty", ((FieldValue) entry).getKey());
  }

  
  
  /*
   * Feature Request #1731158.  Need a way to explicitly specify a mapping between a custom data object 
   * and String.  Not sure the best way to do this.  Copy by reference doesnt seem like a good fit. 
   */
  public void testListOfCustomObjectsToStringArray() {
    List mappingFiles = new ArrayList();
    mappingFiles.add("knownFailures.xml");
    MapperIF mapper = new DozerBeanMapper(mappingFiles);

    MessageHeaderVO vo = new MessageHeaderVO();
    List ids = new ArrayList();
    ids.add(new MessageIdVO("1"));
    ids.add(new MessageIdVO("2"));
    vo.setMsgIds(ids);
    MessageHeaderDTO result = null;

    result = (MessageHeaderDTO) mapper.map(vo, MessageHeaderDTO.class);
    assertEquals("1", result.getIdList().getMsgIdsArray()[0]);
    assertEquals("2", result.getIdList().getMsgIdsArray()[1]);
  }
  
  // Failure discovered during development of an unrelated map type feature request
  public void testMapType_NestedMapToVo_NoCustomMappings() throws Exception {
    // Simple test checking that Maps get mapped to a VO without any custom mappings or map-id.
    // Should behave like Vo --> Vo, matching on common attr(key) names.
    Map nested2 = new HashMap();
    nested2.put("field1", "mapnestedfield1");

    SimpleObj src = new SimpleObj();
    src.setNested2(nested2);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertNotNull(result.getNested2());
    assertEquals(nested2.get("field1"), result.getNested2().getField1());

    SimpleObj result2 = (SimpleObj) mapper.map(result, SimpleObj.class);
    assertEquals(src, result2);
  }

  public void testMapType_MapToVo_CustomMapping_NoMapId() {
    // Test nested Map --> Vo using custom mappings without map-id
    mapper = getNewMapper(new String[] { "mapMapping3.xml" });

    NestedObj nested = new NestedObj();
    nested.setField1("field1Value");

    Map nested2 = new HashMap();
    nested2.put("field1", "mapnestedfield1value");
    nested2.put("field2", "mapnestedfield2value");

    SimpleObj src = new SimpleObj();
    // src.setField1("field1value");
    // src.setNested(nested);
    src.setNested2(nested2);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertNull(result.getNested2().getField1());// field exclude in mappings file
    assertEquals(nested2.get("field2"), result.getNested2().getField2());
  }
  
}