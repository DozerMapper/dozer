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

import org.dozer.vo.recursive.ClassAA;
import org.dozer.vo.recursive.ClassAAPrime;
import org.dozer.vo.recursive.ClassB;
import org.dozer.vo.recursive.ClassBPrime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the dozer behaviour when confronted with structures similar to ours. As of now (dozer 3.0) the behaviour is not
 * optimal and still require special treatments in special cases.
 * 
 * @author ADE
 */
public class RecursiveTest extends AbstractFunctionalTest {

  private ClassAA createTestClassAA() {

    // Create sample assureSocialDTO
    ClassAA classA = newInstance(ClassAA.class);

    classA.setNom("gbs");
    classA.setPrenom("prn");

    ClassB classB = newInstance(ClassB.class);
    classA.addSubs(classB);
    classB.setRue("rue");
    classB.setVille("ville");
    return classA;
  }

  /**
   * this test should validate dozerXX correct behaviour in front of recursive class references in a subclass. With
   * dozer3.0 the first reference is not used but the recursion is correct on the next levels.
   * 
   */
  @Test
  public void testConvertWithSubClass() {
    mapper = getMapper("mappings/recursivemappings.xml", "mappings/recursivemappings2.xml");
    ClassAA testAA = createTestClassAA();
    // the == is on purpose, we test that the referenced parent of the first item of the subs is the parent instance
    // itself
    ClassB testClassB = testAA.getSubs().iterator().next();
    assertTrue(testClassB.getParent() == testAA);
    ClassAAPrime testAAPrime = mapper.map(testAA, ClassAAPrime.class, null);
    // testing the new dozer3.0 bi-directionnal reference through a set
    assertEquals(testAA.getSubs().size(), testAAPrime.getSubs().size());
    // the equality is true at the data level
    ClassBPrime testClassBPrime = testAAPrime.getSubs().iterator().next();
    assertTrue(testClassBPrime.getParent().equals(testAAPrime));
    // we want the referenced parent of the first item of the subs to be the parent instance itself
    ClassBPrime testClassBPrime2 = testAAPrime.getSubs().iterator().next();
    assertTrue(testClassBPrime2.getParent() == testAAPrime);
  }

  @Test
  public void testMirroredSelfReferencingTypes() {
    TypeA src = new TypeA();
    src.setId("1");
    TypeA parent = new TypeA();
    parent.setId("2");
    src.setParent(parent);

    TypeB result = new TypeB();
    mapper.map(src, result);
    
    assertNotNull(result);
    assertEquals("1", result.getId());
    assertEquals("2", result.getParent().getId());
  }

  public static class TypeA {
    private TypeA parent;
    private String id;

    public TypeA getParent() {
      return parent;
    }

    public void setParent(TypeA parent) {
      this.parent = parent;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  public static class TypeB {
    private TypeB parent;
    private String id;

    public TypeB getParent() {
      return parent;
    }

    public void setParent(TypeB parent) {
      this.parent = parent;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }


}
