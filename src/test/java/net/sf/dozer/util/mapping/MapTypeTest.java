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
  
  public void testMapToVo() throws Exception {
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
  }
  
  public void testMapToVo_CustomMappings() throws Exception {
    //Test simple Map --> Vo with custom mappings defined.
    mapper = getNewMapper(new String[] {"mapMapping2.xml"});
    Map src = new HashMap();
    src.put("field1", "field1value");
    src.put("field2", "field2value");
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseB");
    assertNull(result.getField1());
    assertEquals(src.get("field2"), result.getField2());
  }
  
  public void testMapToVoUsingMapId() {
    //Simple map --> vo using a map-id
    mapper = super.getNewMapper(new String[]{"mapMapping.xml"});
    Map src = new HashMap();
    src.put("field1", "field1value");
    src.put("field2", "field2value");
    
    NestedObjPrime dest = (NestedObjPrime) mapper.map(src, NestedObjPrime.class, "caseB");
    assertEquals(src.get("field1"), dest.getField1());
    assertEquals(src.get("field2"), dest.getField2());
  }
  
  public void testMapToVoUsingMapId_FieldExclude() {
    //Simple map --> vo using a map-id
    mapper = super.getNewMapper(new String[]{"mapMapping.xml"});
    Map src = new HashMap();
    src.put("field1", "field1value");
    src.put("field2", "field2value");
    
    NestedObjPrime dest = (NestedObjPrime) mapper.map(src, NestedObjPrime.class, "caseC");
    assertNull("field was excluded and should be null", dest.getField1());
    assertEquals(src.get("field2"), dest.getField2());
  }
  
  public void testNestedMapToVoUsingMapId() {
    //Another test for nested Map --> Vo using <field map-id=....>
    mapper = super.getNewMapper(new String[]{"mapMapping.xml"});

    SimpleObj src = new SimpleObj();

    src.setField1("field1");
    
    NestedObj nested = new NestedObj();
    nested.setField1("nestedfield1");
    src.setNested(nested);
    
    Map nested2 = new HashMap();
    nested2.put("field1", "field1MapValue");
    src.setNested2(nested2);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseA2");
    
    assertNull(result.getNested2().getField1());//field was excluded
    assertEquals(src.getField1(), result.getField1());
    assertEquals(src.getNested().getField1(), result.getNested().getField1());
  }
  
  public void testMapToVo_NoCustomMappings() throws Exception {
    //Test simple Map --> Vo without any custom mappings defined.
    NestedObj nestedObj = new NestedObj();
    nestedObj.setField1("nestedfield1value");
    Map src = new HashMap();
    src.put("field1", "mapnestedfield1value");
    src.put("nested", nestedObj);
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertEquals(src.get("field1"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
  }
  
  public void testVoToMap_NoCustomMappings() throws Exception {
    //Test simple Vo --> Map without any custom mappings defined.
    SimpleObjPrime src = new SimpleObjPrime();
    src.setField1("someValueField1");
    src.setField2("someValueField2");
    src.setSimpleobjprimefield("someOtherValue");
    
    NestedObjPrime nested = new NestedObjPrime();
    nested.setField1("field1Value");
    nested.setField2("field2Value");
    src.setNested(nested);
    
    NestedObjPrime nested2 = new NestedObjPrime();
    src.setNested2(nested2);
    
    //Map complex object to HashMap
    Map destMap = new HashMap();
    mapper.map(src, destMap);
    
    //Map HashMap back to new instance of the complex object
    SimpleObjPrime mappedSrc = (SimpleObjPrime) mapper.map(destMap, SimpleObjPrime.class);
    
    //Remapped complex type should equal original src if all fields were mapped both ways.
    assertEquals(src, mappedSrc);
  }
  
}