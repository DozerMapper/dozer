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
package org.dozer.functional_tests;

import java.util.Map;

import org.dozer.vo.inheritance.A;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Oxenham
 */
public class InheritanceMappingMapBackedTest extends AbstractFunctionalTest {
              
  @SuppressWarnings("unchecked")
  @Test
  public void testInheritedToMappedBacked() throws Exception {
    
    /*
     *  Test two things.
     *  1) When wildcards are turned off and the destination is a map then the 
     *    sub class fields where not being mapped because of a bug in mappedParentFields key generation
     *  2) If one side of a mapping is defined via an interface then the super class mapping was not being processed
     *    because the super classes and the interface classes were searched separately.   
     */
    
    mapper = getMapper("mappings/inheritanceMappingMapBacked.xml");

    A src = createA();
    Map dest = mapper.map(src, Map.class);

    
    assertEquals(src.getSuperAField(), dest.get("superAField"));
    assertEquals(src.getSuperField1(), dest.get("superField1"));
    assertEquals(src.getField1(), dest.get("field1"));
    assertEquals(src.getFieldA(), dest.get("fieldA"));

    // Remap to each other to test bi-directional mapping
    A mappedSrc = mapper.map(dest, A.class);
    Map mappedDest = mapper.map(mappedSrc, Map.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  private A createA() {
    A result = newInstance(A.class);
    result.setField1("field1value");
    result.setFieldA("fieldAValue");
    result.setSuperAField("superAFieldValue");
    result.setSuperField1("superField1Value");
    return result;
  }

}
