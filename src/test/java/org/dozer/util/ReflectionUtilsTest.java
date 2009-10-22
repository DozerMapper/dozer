/*
 * Copyright 2005-2010 the original author or authors.
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
package org.dozer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;


import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.dozer.util.ReflectionUtils;
import org.dozer.vo.SimpleObj;
import org.dozer.vo.A;
import org.dozer.vo.B;
import org.dozer.vo.inheritance.ChildChildIF;
import org.junit.Test;
import junit.framework.Assert;

/**
 * @author tierney.matt
 */
public class ReflectionUtilsTest extends AbstractDozerTest {

  @Test(expected = MappingException.class)
  public void testGetMethod_NotFound() throws Exception {
    SimpleObj src = new SimpleObj();
    ReflectionUtils.getMethod(src, String.valueOf(System.currentTimeMillis()));
  }

  @Test(expected = MappingException.class)
  public void testGetDeepFieldHierarchy_NonDeepField() throws Exception {
    ReflectionUtils.getDeepFieldHierarchy(SimpleObj.class, "test", null);
  }

  @Test(expected = MappingException.class)
  public void testGetDeepFieldHierarchy_NotExists() throws Exception {
    ReflectionUtils.getDeepFieldHierarchy(SimpleObj.class, String.valueOf(System.currentTimeMillis()) + "."
        + String.valueOf(System.currentTimeMillis()), null);
  }

  @Test
  public void testGetPropertyDescriptors_InterfaceInheritance() throws Exception {
    // Should walk the inheritance hierarchy all the way up to the super interface and find all properties along the way
    PropertyDescriptor[] pds = ReflectionUtils.getPropertyDescriptors(ChildChildIF.class);
    assertNotNull("prop descriptors should not be null", pds);
    assertEquals("3 prop descriptors should have been found", 3, pds.length);
  }

  @Test
  public void testFindPropertyDescriptor_InterfaceInheritance() throws Exception {
    // Should walk the inheritance hierarchy all the way up to the super interface and find the property along the way
    String fieldName = "parentField";
    PropertyDescriptor pd = ReflectionUtils.findPropertyDescriptor(ChildChildIF.class, fieldName, null);
    assertNotNull("prop descriptor should not be null", pd);
    assertEquals("invalid prop descriptor name found", fieldName, pd.getName());
  }

  @Test
  public void testGetInterfacePropertyDescriptors() {
    PropertyDescriptor[] descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestIF1.class);
    assertEquals(1, descriptors.length);

    descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestIF2.class);
    assertEquals(1, descriptors.length);

    descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestClass.class);
    assertEquals(4, descriptors.length);
  }

  @Test
  public void testIllegalMethodType() {
    A a = new A();
    String methodName = "setB";
    try {
      Method method = a.getClass().getMethod(methodName, B.class);
      ReflectionUtils.invoke(method, a, new Object[] {"wrong param"});
    } catch (NoSuchMethodException e) {
      Assert.fail("Method " + methodName + "missed");
    } catch (MappingException e) {
      if(!e.getMessage().contains("Illegal object type for the method '" + methodName +"'")) {
        Assert.fail("Wrong exception message");
      }
    }
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
