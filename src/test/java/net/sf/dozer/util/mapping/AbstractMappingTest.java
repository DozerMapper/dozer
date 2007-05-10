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

import net.sf.dozer.util.mapping.vo.abstractinheritance.A;
import net.sf.dozer.util.mapping.vo.abstractinheritance.B;

/**
 * @author tierney.matt
 */
public class AbstractMappingTest extends DozerTestBase {

  public void testCustomMappingForAbstractClasses() throws Exception {
    // Test that the explicit abstract custom mapping definition is used when mapping sub classes
    mapper = getNewMapper(new String[] { "abstractMapping.xml" });

    A src = new A();
    src.setField1("field1value");
    src.setFieldA("fieldAValue");
    src.setAbstractAField("abstractAFieldValue");
    src.setAbstractField1("abstractField1Value");

    B dest = (B) mapper.map(src, B.class);

    assertNull("abstractField1 should have been excluded", dest.getAbstractField1());
    assertEquals("abstractBField not mapped correctly", src.getAbstractAField(), dest.getAbstractBField());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
    assertEquals("fieldB not mapped correctly", src.getFieldA(), dest.getFieldB());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = (A) mapper.map(dest, A.class);
    B mappedDest = (B) mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  public void testNoCustomMappingForAbstractClasses() throws Exception {
    // Test that wildcard fields in abstract classes are mapped when there is no explicit abstract custom mapping definition
    mapper = new DozerBeanMapper();

    A src = new A();
    src.setField1("field1value");
    src.setFieldA("fieldAValue");
    src.setAbstractAField("abstractAFieldValue");
    src.setAbstractField1("abstractField1Value");

    B dest = (B) mapper.map(src, B.class);

    assertEquals("abstractField1 not mapped correctly", src.getAbstractField1(), dest.getAbstractField1());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
    assertNull("abstractBField should not have been mapped", dest.getAbstractBField());
    assertNull("fieldB should not have been mapped", dest.getFieldB());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = (A) mapper.map(dest, A.class);
    B mappedDest = (B) mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

}