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
package net.sf.dozer.util;

import java.beans.PropertyDescriptor;

import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.MappingException;
import net.sf.dozer.util.ReflectionUtils;
import net.sf.dozer.vo.SimpleObj;
import net.sf.dozer.vo.inheritance.ChildChildIF;

/**
 * @author tierney.matt
 */
public class ReflectionUtilsTest extends AbstractDozerTest {

  public void testGetMethod_NotFound() throws Exception {
    SimpleObj src = new SimpleObj();
    try {
      ReflectionUtils.getMethod(src, String.valueOf(System.currentTimeMillis()));
      fail("Should have thrown exception");
    } catch (MappingException e) {
    }
  }

  public void testGetDeepFieldHierarchy_NonDeepField() throws Exception {
    try {
      ReflectionUtils.getDeepFieldHierarchy(SimpleObj.class, "test", null);
      fail("Should have thrown exception");
    } catch (MappingException e) {
      assertEquals("invalid exception thrown", "Field does not contain deep field delimitor", e.getMessage());
    }
  }

  public void testGetDeepFieldHierarchy_NotExists() throws Exception {
    try {
      ReflectionUtils.getDeepFieldHierarchy(SimpleObj.class, String.valueOf(System.currentTimeMillis()) + "."
          + String.valueOf(System.currentTimeMillis()), null);
      fail("Should have thrown exception");
    } catch (MappingException e) {
    }
  }

  public void testGetPropertyDescriptors_InterfaceInheritance() throws Exception {
    // Should walk the inheritance hierarchy all the way up to the super interface and find all properties along the way
    PropertyDescriptor[] pds = ReflectionUtils.getPropertyDescriptors(ChildChildIF.class);
    assertNotNull("prop descriptors should not be null", pds);
    assertEquals("3 prop descriptors should have been found", 3, pds.length);
  }

  public void testFindPropertyDescriptor_InterfaceInheritance() throws Exception {
    // Should walk the inheritance hierarchy all the way up to the super interface and find the property along the way
    String fieldName = "parentField";
    PropertyDescriptor pd = ReflectionUtils.findPropertyDescriptor(ChildChildIF.class, fieldName, null);
    assertNotNull("prop descriptor should not be null", pd);
    assertEquals("invalid prop descriptor name found", fieldName, pd.getName());
  }

  public void testGetInterfacePropertyDescriptors() {
    PropertyDescriptor[] descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestIF1.class);
    assertEquals(1, descriptors.length);

    descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestIF2.class);
    assertEquals(1, descriptors.length);

    descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestClass.class);
    assertEquals(4, descriptors.length);
  }

  private abstract static class TestClass implements TestIF1, TestIF2 {
    public String getC() {
      return null;
    }
  }

  private static interface TestIF1 {
    String getA();
    void setA(String a);
  }

  private static interface TestIF2 {
    Integer getB();
  }

}
