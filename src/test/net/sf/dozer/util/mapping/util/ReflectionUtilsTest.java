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

import net.sf.dozer.util.mapping.DozerTestBase;
import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.vo.SimpleObj;

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
  
  
}
