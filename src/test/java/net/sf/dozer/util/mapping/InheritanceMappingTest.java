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

import net.sf.dozer.util.mapping.vo.inheritance.A;
import net.sf.dozer.util.mapping.vo.inheritance.B;

/**
 * @author tierney.matt
 */
public class InheritanceMappingTest extends DozerTestBase {

  public void testCustomMappingForSuperClasses() throws Exception {
    // Test that the explicit super custom mapping definition is used when mapping sub classes
    mapper = getNewMapper(new String[] { "inheritanceMapping.xml" });

    A src = getA();
    B dest = (B) mapper.map(src, B.class);

    assertNull("superField1 should have been excluded", dest.getSuperField1());
    assertEquals("superBField not mapped correctly", src.getSuperAField(), dest.getSuperBField());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
    assertEquals("fieldB not mapped correctly", src.getFieldA(), dest.getFieldB());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = (A) mapper.map(dest, A.class);
    B mappedDest = (B) mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  public void testNoCustomMappingForSuperClasses() throws Exception {
    // Test that wildcard fields in super classes are mapped when there is no explicit super custom mapping definition
    mapper = new DozerBeanMapper();

    A src = getA();
    B dest = (B) mapper.map(src, B.class);

    assertEquals("superField1 not mapped correctly", src.getSuperField1(), dest.getSuperField1());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
    assertNull("superBField should not have been mapped", dest.getSuperBField());
    assertNull("fieldB should not have been mapped", dest.getFieldB());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = (A) mapper.map(dest, A.class);
    B mappedDest = (B) mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }
  
  
  public void testNoCustomMappingForSuperClasses_SubclassAttrsAppliedToSuperClasses() throws Exception {
    // Test that when there isnt an explicit super custom mapping definition the subclass mapping def attrs are 
    // applied to the super class mapping.  In this use case, wildcard="false" for the A --> B mapping definition
    mapper = getNewMapper(new String[] { "inheritanceMapping2.xml" });

    A src = getA();
    B dest = (B) mapper.map(src, B.class);

    assertNull("fieldB should not have been mapped", dest.getSuperField1());
    assertNull("superBField should have not been mapped", dest.getSuperBField());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = (A) mapper.map(dest, A.class);
    B mappedDest = (B) mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }
  
  public void testNoCustomMappingForSubclasses_CustomMappingForSuperClasses() throws Exception {
    //Tests that custom mappings for super classes are used when there are no custom mappings
    //for subclasses.  Also tests that a default class map is properly created and used for the subclass
    //field mappings
    mapper = getNewMapper(new String[] { "inheritanceMapping3.xml" });

    A src = getA();
    B dest = (B) mapper.map(src, B.class);

    assertNull("superField1 should have been excluded", dest.getSuperField1());
    assertEquals("superBField not mapped correctly", src.getSuperAField(), dest.getSuperBField());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = (A) mapper.map(dest, A.class);
    B mappedDest = (B) mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }
  
  private A getA() {
    A result = new A();
    result.setField1("field1value");
    result.setFieldA("fieldAValue");
    result.setSuperAField("superAFieldValue");
    result.setSuperField1("superField1Value");
    return result;
  }
}