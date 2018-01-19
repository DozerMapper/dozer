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

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.MappingException;
import org.dozer.vo.abstractinheritance.A;
import org.dozer.vo.abstractinheritance.AbstractA;
import org.dozer.vo.abstractinheritance.AbstractACollectionContainer;
import org.dozer.vo.abstractinheritance.AbstractAContainer;
import org.dozer.vo.abstractinheritance.AbstractB;
import org.dozer.vo.abstractinheritance.AbstractBCollectionContainer;
import org.dozer.vo.abstractinheritance.AbstractBContainer;
import org.dozer.vo.abstractinheritance.B;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for data objects that have Abstract Class(s) in their object hierarchy
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class InheritanceAbstractClassMappingTest extends AbstractFunctionalTest {

  @Test
  public void testCustomMappingForAbstractClasses() throws Exception {
    // Test that the explicit abstract custom mapping definition is used when mapping sub classes
    mapper = getMapper(new String[] {"mappings/abstractMapping.xml"});

    A src = getA();
    B dest = mapper.map(src, B.class);

    assertNull("abstractField1 should have been excluded", dest.getAbstractField1());
    assertEquals("abstractBField not mapped correctly", src.getAbstractAField(), dest.getAbstractBField());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
    assertEquals("fieldB not mapped correctly", src.getFieldA(), dest.getFieldB());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = mapper.map(dest, A.class);
    B mappedDest = mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  @Test
  public void testNoCustomMappingForAbstractClasses() throws Exception {
    // Test that wildcard fields in abstract classes are mapped when there is no explicit abstract custom mapping
    // definition
    mapper = DozerBeanMapperBuilder.buildDefault();

    A src = getA();
    B dest = mapper.map(src, B.class);

    assertEquals("abstractField1 not mapped correctly", src.getAbstractField1(), dest.getAbstractField1());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
    assertNull("abstractBField should not have been mapped", dest.getAbstractBField());
    assertNull("fieldB should not have been mapped", dest.getFieldB());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = mapper.map(dest, A.class);
    B mappedDest = mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  @Test(expected = MappingException.class)
  public void testAbstractDestClassThrowsException() throws Exception {
    mapper.map(newInstance(A.class), AbstractB.class);
  }
  
  @Test
  public void testCustomMappingForAbstractDestClass() throws Exception {
      mapper = getMapper("mappings/abstractMapping.xml");
      A src = getA();
      AbstractB dest = mapper.map(src, AbstractB.class);
      
      assertTrue(dest instanceof B);
      
      assertNull("abstractField1 should have been excluded", dest.getAbstractField1());
      assertEquals("abstractBField not mapped correctly", src.getAbstractAField(), dest.getAbstractBField());
      assertEquals("field1 not mapped correctly", src.getField1(), ((B)dest).getField1());
      assertEquals("fieldB not mapped correctly", src.getFieldA(), ((B)dest).getFieldB());

      // Remap to each other to test bi-directional mapping
      AbstractA mappedSrc = mapper.map(dest, AbstractA.class);
      AbstractB mappedDest = mapper.map(mappedSrc, AbstractB.class);
      
      assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }
  
  @Test
  public void testCustomMappingForAbstractDestClassLevelTwo() throws Exception {
      mapper = getMapper("mappings/abstractMapping.xml");
      AbstractAContainer src = getAWrapper();
      AbstractBContainer dest = mapper.map(src, AbstractBContainer.class);
      assertTrue(dest.getB() instanceof B);

      assertNull("abstractField1 should have been excluded", dest.getB().getAbstractField1());
      assertEquals("abstractBField not mapped correctly", src.getA().getAbstractAField(), dest.getB().getAbstractBField());
      assertEquals("field1 not mapped correctly", ((A)src.getA()).getField1(), ((B)dest.getB()).getField1());
      assertEquals("fieldB not mapped correctly", ((A)src.getA()).getFieldA(), ((B)dest.getB()).getFieldB());
      
      // Remap to each other to test bi-directional mapping
      AbstractAContainer mappedSrc = mapper.map(dest, AbstractAContainer.class);
      AbstractBContainer mappedDest = mapper.map(mappedSrc, AbstractBContainer.class);
      
      assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }
  
  @Test
  public void testCustomMappingForAsbstractDestClassCollection() throws Exception {
      mapper = getMapper("mappings/abstractMapping.xml");
      AbstractACollectionContainer src = getAsContainer();
      AbstractBCollectionContainer dest = mapper.map(src, AbstractBCollectionContainer.class);
      assertTrue(dest.getBs().get(0) instanceof B);

      assertNull("abstractField1 should have been excluded", dest.getBs().get(0).getAbstractField1());
      assertEquals("abstractBField not mapped correctly", src.getAs().get(0).getAbstractAField(), dest.getBs().get(0).getAbstractBField());
      assertEquals("field1 not mapped correctly", ((A)src.getAs().get(0)).getField1(), ((B)dest.getBs().get(0)).getField1());
      assertEquals("fieldB not mapped correctly", ((A)src.getAs().get(0)).getFieldA(), ((B)dest.getBs().get(0)).getFieldB());
      
      // Remap to each other to test bi-directional mapping
      AbstractACollectionContainer mappedSrc = mapper.map(dest, AbstractACollectionContainer.class);
      AbstractBCollectionContainer mappedDest = mapper.map(mappedSrc, AbstractBCollectionContainer.class);
      
      assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
}

  @Test
  public void testNoCustomMappingForAbstractClasses_SubclassAttrsAppliedToAbstractClasses() throws Exception {
    // Test that when there isnt an explicit abstract custom mapping definition the subclass mapping def attrs are
    // applied to the abstract class mapping. In this use case, wildcard="false" for the A --> B mapping definition
    mapper = getMapper(new String[] {"mappings/abstractMapping2.xml"});

    A src = getA();
    B dest = mapper.map(src, B.class);

    assertNull("fieldB should not have been mapped", dest.getAbstractField1());
    assertNull("abstractBField should have not been mapped", dest.getAbstractBField());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = mapper.map(dest, A.class);
    B mappedDest = mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  @Test
  public void testNoCustomMappingForSubclasses_CustomMappingForAbstractClasses() throws Exception {
    // Tests that custom mappings for abstract classes are used when there are no custom mappings
    // for subclasses. Also tests that a default class map is properly created and used for the subclass
    // field mappings
    mapper = getMapper(new String[] {"mappings/abstractMapping3.xml"});

    A src = getA();
    B dest = mapper.map(src, B.class);

    assertNull("abstractField1 should have been excluded", dest.getAbstractField1());
    assertEquals("abstractBField not mapped correctly", src.getAbstractAField(), dest.getAbstractBField());
    assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());

    // Remap to each other to test bi-directional mapping
    A mappedSrc = mapper.map(dest, A.class);
    B mappedDest = mapper.map(mappedSrc, B.class);

    assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
  }

  private A getA() {
    A result = newInstance(A.class);
    result.setField1("field1value");
    result.setFieldA("fieldAValue");
    result.setAbstractAField("abstractAFieldValue");
    result.setAbstractField1("abstractField1Value");
    return result;
  }
  
  private AbstractAContainer getAWrapper() {
      AbstractAContainer result = newInstance(AbstractAContainer.class);
      result.setA(getA());
      return result;
  }
  
  private AbstractACollectionContainer getAsContainer() {
      AbstractACollectionContainer result = newInstance(AbstractACollectionContainer.class);
      List<AbstractA> list = new ArrayList<AbstractA>();
      list.add(getA());
      result.setAs(list);
      return result;
  }

}
