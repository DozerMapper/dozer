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

import net.sf.dozer.util.mapping.vo.deep2.Dest;
import net.sf.dozer.util.mapping.vo.deep2.Src;
import net.sf.dozer.util.mapping.vo.km.SomeVo;
import net.sf.dozer.util.mapping.vo.km.Sub;
import net.sf.dozer.util.mapping.vo.km.Super;
import net.sf.dozer.util.mapping.vo.map.NestedObj;
import net.sf.dozer.util.mapping.vo.map.SimpleObj;
import net.sf.dozer.util.mapping.vo.map.SimpleObjPrime;
import net.sf.dozer.util.mapping.vo.set.SomeDTO;
import net.sf.dozer.util.mapping.vo.set.SomeOtherDTO;
import net.sf.dozer.util.mapping.vo.set.SomeVO;


/**
 * This is a holding grounds for test cases that reproduce known bugs, features, or gaps discovered during development.
 * As the use cases are resolved, these tests should be moved to the live unit test classes. 
 * 
 * @author tierney.matt
 */
public class KnownFailures extends DozerTestBase {
  
  //Failure discovered during development of an unrelated map type feature request
  public void testMapType_MapToVo_NoCustomMappings() throws Exception {
    //Test simple Map --> Vo without any custom mappings defined.
    NestedObj nestedObj = new NestedObj();
    nestedObj.setField1("nestedfield1value");
    Map src = new HashMap();
    src.put("field1", "mapnestedfield1value");
    src.put("nested", nestedObj);
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertEquals(src.get("field1"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
    
    SimpleObj result2 = (SimpleObj) mapper.map(result, Map.class);
    assertEquals(src, result2);
  }
  
  //Failure discovered during development of an unrelated map type feature request  
  public void testMapType_NestedMapToVo_NoCustomMappings() throws Exception {
    //Simple test checking that Maps get mapped to a VO without any custom mappings or map-id.
    //Should behave like Vo --> Vo, matching on common attr(key) names.
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
  
  //Failure discovered during development of an unrelated map type feature request
  public void testMapType_NestedMapToVoUsingMapId() {
    //Test nested Map --> Vo using <field map-id=....>
    mapper = super.getNewMapper(new String[]{"mapMapping.xml"});
    NestedObj nested = new NestedObj();
    nested.setField1("nestedfield1");
    Map nested2 = new HashMap();
    nested2.put("field1", "field1MapValue");
    
    SimpleObj src = new SimpleObj();
    src.setField1("field1");
    src.setNested(nested);
    src.setNested2(nested2);
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    
    assertEquals(src.getField1(), result.getField1());
    assertEquals(src.getNested().getField1(), result.getNested().getField1());
    assertEquals(src.getNested2().get("field1"), result.getNested2().getField1());
    
    SimpleObj result2 = (SimpleObj) mapper.map(result, SimpleObj.class, "caseA");   
    
    assertEquals(src, result2);
  }
  
  public void testMapType_MapToVo_CustomMapping_NoMapId() {
    //Test nested Map --> Vo using custom mappings without map-id
    mapper = getNewMapper(new String[]{"mapMapping3.xml"});
    
    Map nested2 = new HashMap();
    nested2.put("field1", "mapnestedfield1value");
    nested2.put("field2", "mapnestedfield2value");
    
    SimpleObj src = new SimpleObj();
    src.setNested2(nested2);
    src.setField1("field1value");
    
    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertNull(result.getNested().getField1());//field exclude in mappings file
    assertEquals(nested2.get("field2"), result.getNested2().getField2());
  }

  /*
   * Related to bug #1486105.  This test is successful, but provides context for testKM2 which fails
   */
  public void testKM1() {
    SomeVo request = new SomeVo();
    request.setUserName("yo");
    request.setAge("2");
    request.setColor("blue");
    
    MapperIF mapper = getNewMapper(new String[]{"kmmapping.xml"});
    
    Super afterMapping = (Super) mapper.map(request,Super.class);
    
    assertNotNull("login name should not be null", afterMapping.getLoginName());
    assertNotNull("age should not be null", afterMapping.getAge());
    assertEquals("should map SuperClass.name to SubClassPrime userName.",request.getUserName(),afterMapping.getLoginName());
    assertEquals(request.getAge(), afterMapping.getAge());
  }
  
  /*
   * Bug #1486105 
   */  
  public void testKM2() {
    Sub request = new Sub();
    request.setAge("2");
    request.setColor("blue");
    request.setLoginName("fred");
        
    MapperIF mapper = getNewMapper(new String[] {"kmmapping.xml"});
      
    SomeVo afterMapping = (SomeVo) mapper.map(request,SomeVo.class);
       
    assertNotNull("un should not be null", afterMapping.getUserName());
    assertNotNull("color should not be null",afterMapping.getColor());
    assertNotNull("age should not be null", afterMapping.getAge());
    assertEquals("should map SuperClass.name to SubClassPrime userName.",request.getLoginName(),afterMapping.getUserName());
    assertEquals(request.getColor(),afterMapping.getColor());
    assertEquals(request.getAge(), afterMapping.getAge());
  }

  /*
   * Bug #1549738 
   */  
  public void testSetMapping() throws Exception {
    //For some reason the resulting SomeVO contains a Set with 4 objects.  2 SomeOtherDTO's and 2 SomeOtherVO's.  I believe it
    //should only contain 2 SomeOtherVO's.  It has something to do with specifying the field name starting with cap in the mapping file.  If
    //you change the field mapping to start with lower case it seems to map correctly.
    MapperIF mapper = getNewMapper(new String[] { "knownFailures.xml" }); 
    
    SomeDTO someDto = new SomeDTO(); 
    someDto.setField1(new Integer("1"));
    
    SomeOtherDTO someOtherDto = new SomeOtherDTO();
    someOtherDto.setOtherField2(someDto);
    someOtherDto.setOtherField3("value1"); 
    
    SomeDTO someDto2 = new SomeDTO(); 
    someDto2.setField1(new Integer("2")); 
    
    SomeOtherDTO someOtherDto2 = new SomeOtherDTO();
    someOtherDto2.setOtherField2(someDto2); 
    someOtherDto2.setOtherField3("value2");
    
    SomeDTO src = new SomeDTO();
    src.setField2(new SomeOtherDTO[] { someOtherDto2,someOtherDto });
    
    SomeVO dest = (SomeVO) mapper.map(src, SomeVO.class);
    
    assertEquals("incorrect resulting set size", src.getField2().length, dest.getField2().size());
    //TODO: add more asserts
  } 
  
}