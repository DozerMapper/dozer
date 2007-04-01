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
package net.sf.dozer.util.mapping.util;

import java.beans.PropertyDescriptor;

import net.sf.dozer.util.mapping.DozerTestBase;
import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.inheritance.ChildChildIF;

/**
 * @author tierney.matt
 */
public class ReflectionUtilsTest extends DozerTestBase {
  private ReflectionUtils utils = new ReflectionUtils();

  public void testGetMethod_NotFound() throws Exception {
    SimpleObj src = new SimpleObj();
    try {
      utils.getMethod(src, String.valueOf(System.currentTimeMillis()));
      fail("Should have thrown exception");
    } catch (MappingException e) {
    }
  }
  
  public void testGetDeepFieldHierarchy_NonDeepField() throws Exception {
    try {
      utils.getDeepFieldHierarchy(SimpleObj.class, "test");
      fail("Should have thrown exception");
    } catch (MappingException e) {
      assertEquals("invalid exception thrown", "Field does not contain deep field delimitor", e.getMessage());
    }
  }
  
  public void testGetDeepFieldHierarchy_NotExists() throws Exception {
    try {
      utils.getDeepFieldHierarchy(SimpleObj.class, String.valueOf(System.currentTimeMillis()) + "." + String.valueOf(System.currentTimeMillis()));
      fail("Should have thrown exception");
    } catch (MappingException e) {
    }
  }
  
  public void testGetPropertyDescriptors_InterfaceInheritance() throws Exception {
    //Should walk the inheritance hierarchy all the way up to the super interface and find all properties along the way
    PropertyDescriptor[] pds = utils.getPropertyDescriptors(ChildChildIF.class);
    assertNotNull("prop descriptors should not be null", pds);
    assertEquals("3 prop descriptors should have been found", 3, pds.length);
  }
  
  public void testFindPropertyDescriptor_InterfaceInheritance() throws Exception {
    //Should walk the inheritance hierarchy all the way up to the super interface and find the property along the way
    String fieldName = "parentField";
    PropertyDescriptor pd = utils.findPropertyDescriptor(ChildChildIF.class, fieldName);
    assertNotNull("prop descriptor should not be null", pd);
    assertEquals("invalid prop descriptor name found", fieldName, pd.getName());
  }
}
