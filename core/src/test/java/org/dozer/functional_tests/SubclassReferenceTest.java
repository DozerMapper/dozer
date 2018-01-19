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

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.vo.copybyreference.Reference;
import org.dozer.vo.copybyreference.TestA;
import org.dozer.vo.copybyreference.TestB;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubclassReferenceTest extends AbstractFunctionalTest {
  private Mapper mapper;
  private TestA testA;
  private TestB testB;

  @Before
  public void setUp() {
    mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles(getMappingFile())
            .build();
    testA = newInstance(TestA.class);
    testA.setOne("one");
    testA.setOneA("oneA");
    testB = null;
  }

  protected String getMappingFile() {
    return "mappings/reference-mapping.xml";
  }

  @Test
  public void testBase() {
    testB = mapper.map(testA, TestB.class);
    assertEquals(testA.getOne(), testB.getOne());
    assertEquals(testA.getOneA(), testB.getOneB());
  }

  @Test
  public void testSubclassSource() {
    TestA testA = new TestA() {
    }; // anonymous subclass
    testA.setOne("one");
    testA.setOneA("oneA");
    testB = mapper.map(testA, TestB.class);
    assertEquals(testA.getOne(), testB.getOne());
    assertEquals(testA.getOneA(), testB.getOneB());
  }

  @Test
  public void testReference() {
    testA.setTestReference(Reference.FOO);
    testB = mapper.map(testA, TestB.class);
    assertEquals(testA.getOne(), testB.getOne());
    assertEquals(testA.getOneA(), testB.getOneB());
    assertEquals(testA.getTestReference(), testB.getTestReference());
  }

  @Test
  public void testReferenceSubclassSource() {
    TestA testASubclass = new TestA() {
    }; // anonymous subclass
    testASubclass.setTestReference(Reference.FOO);
    testB = mapper.map(testASubclass, TestB.class);
    assertEquals(testASubclass.getOne(), testB.getOne());
    assertEquals(testASubclass.getOneA(), testB.getOneB());
    assertEquals(testASubclass.getTestReference(), testB.getTestReference());
  }

}
