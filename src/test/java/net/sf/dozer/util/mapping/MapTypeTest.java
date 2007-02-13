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

import net.sf.dozer.util.mapping.vo.map.NestedObj;
import net.sf.dozer.util.mapping.vo.map.NestedObjPrime;
import net.sf.dozer.util.mapping.vo.map.SimpleObj;
import net.sf.dozer.util.mapping.vo.map.SimpleObjPrime;


/**
 * @author tierney.matt
 */
public class MapTypeTest extends DozerTestBase {
  
  public void testMapType_MapToVo() throws Exception {
    //Test simple Map --> Vo with custom mappings defined.
    mapper = getNewMapper(new String[] {"mapMapping2.xml"});

    NestedObj nestedObj = new NestedObj();
    nestedObj.setField1("nestedfield1value");
    Map src = new HashMap();
    src.put("field1", "mapnestedfield1value");
    src.put("nested", nestedObj);
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseA");
    assertEquals(src.get("field1"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
    
    //Map result2 = (Map) mapper.map(result, Map.class);
    //assertEquals(src.get("field1"), result2.get("field1"));
    //assertNotNull(result2.get("nested"));
    //assertEquals(src.get("nested"), result2.get("nested"));
  }
  
  public void testMapType_MapToVo_NoCustomMappings() throws Exception {
    //Test simple Map --> Vo with custom mappings defined.
    mapper = getNewMapper(new String[] {"mapMapping2.xml"});
    Map src = new HashMap();
    src.put("field1", "field1value");
    src.put("field2", "field2value");
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseB");
    assertNull(result.getField1());
    assertEquals(src.get("field2"), result.getField2());
  }  
  
  public void testMapType_MapToVoUsingMapId() {
    //Simple map --> vo using a map-id
    mapper = super.getNewMapper(new String[]{"mapMapping.xml"});
    Map src = new HashMap();
    src.put("field1", "field1value");
    
    NestedObjPrime dest = (NestedObjPrime) mapper.map(src, NestedObjPrime.class, "caseB");
    assertEquals(src.get("field1"), dest.getField1());
  }
  
  public void testMapType_NestedMapToVoUsingMapId() {
    //Another test for nested Map --> Vo using <field map-id=....>
    mapper = super.getNewMapper(new String[]{"mapMapping.xml"});

    Map nested2 = new HashMap();
    nested2.put("field1", "field1MapValue");
    
    SimpleObj src = new SimpleObj();
    src.setNested2(nested2);
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseA2");
    
    assertNull(result.getNested2().getField1());
  }
}