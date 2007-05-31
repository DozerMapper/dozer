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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.dozer.util.mapping.converters.StringAppendCustomConverter;
import net.sf.dozer.util.mapping.vo.AnotherTestObject;
import net.sf.dozer.util.mapping.vo.AnotherTestObjectPrime;
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
  
  //Defect #1728385
  public void testSimpleCustomConverter_ImplicitMapping() throws Exception {
    mapper = getNewMapper(new String[]{"simpleCustomConverter.xml"});
    AnotherTestObject src = new AnotherTestObject();
    src.setField3(String.valueOf(System.currentTimeMillis()));
    
    AnotherTestObjectPrime dest = (AnotherTestObjectPrime) mapper.map(src, AnotherTestObjectPrime.class);

    //Custom converter specified for the field1 mapping, so verify custom converter was actually used
    assertNotNull("dest field3 should not be null", dest.getField3());
    StringTokenizer st = new StringTokenizer(dest.getField3(), "-");
    assertEquals("dest field3 value should contain a hyphon", 2, st.countTokens());
    String token1 = st.nextToken();
    assertEquals("1st portion of dest field3 value should equal src field value", src.getField3(), token1);
    String token2 = st.nextToken();
    assertEquals("dest field3 value should have been appended to by the cust converter", 
        StringAppendCustomConverter.APPENDED_VALUE, token2);
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