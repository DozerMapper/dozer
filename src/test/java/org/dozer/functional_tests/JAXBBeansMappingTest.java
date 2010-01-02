/*
 * Copyright 2005-2009 the original author or authors.
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

import org.dozer.util.MappingUtils;
import org.dozer.vo.TestObject;
import org.dozer.vo.jaxb.employee.EmployeeWithInnerClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class JAXBBeansMappingTest extends AbstractFunctionalTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("jaxbBeansMapping.xml");
  }

  @Test
  public void testTrivial() {
    Class<?> type = MappingUtils.loadClass("org.dozer.vo.jaxb.employee.EmployeeType");
    assertNotNull(type);
  }

  @Test
  public void testSimple() {
    TestObject source = new TestObject();
    source.setOne("ABC");
    EmployeeWithInnerClass result = mapper.map(source, EmployeeWithInnerClass.class);
    assertNotNull(result);
    assertEquals("ABC", result.getFirstName());
  }
  
  @Test
  public void testNestedInnerClass() {
    TestObject source = new TestObject();
    source.setOne("Name");
    EmployeeWithInnerClass.Address.State result = mapper.map(source, EmployeeWithInnerClass.Address.State.class);
    assertNotNull(result);
    assertEquals("Name", result.getName());
  }

}
