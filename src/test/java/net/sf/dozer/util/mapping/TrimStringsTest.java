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

import net.sf.dozer.util.mapping.vo.AnotherTestObject;
import net.sf.dozer.util.mapping.vo.AnotherTestObjectPrime;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.SimpleObjPrime;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;


/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class TrimStringsTest extends AbstractDozerTest {
  
  protected void setUp() throws Exception {
    super.setUp();
    mapper = getNewMapper(new String[] { "trimStringsMapping.xml" }); 
  }
  
  public void testTrimStrings_Global() {
    AnotherTestObject src = new AnotherTestObject();
    src.setField3("      valueNeedingTrimmed       ");
    src.setField4("      anotherValueNeedingTrimmed       ");
    
    AnotherTestObjectPrime dest = (AnotherTestObjectPrime) mapper.map(src, AnotherTestObjectPrime.class);
    
    assertEquals("valueNeedingTrimmed", dest.getField3());
    assertEquals("anotherValueNeedingTrimmed", dest.getTo().getOne());
  }

  public void testTrimStrings_ClassMapLevel() {
    TestObject src = new TestObject();
    String value = "    shouldNotBeNeedingTrimmed     ";
    src.setOne(value);
    
    TestObjectPrime dest = (TestObjectPrime) mapper.map(src, TestObjectPrime.class);
    
    assertEquals(value, dest.getOnePrime());
  }
  
  public void testTrimStrings_ImplicitMapping() {
    SimpleObj src = new SimpleObj();
    src.setField1("      valueNeedingTrimmed       ");
    
    SimpleObjPrime dest = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    
    assertEquals("valueNeedingTrimmed", dest.getField1());
  }
  
}