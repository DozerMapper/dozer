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

import static junit.framework.Assert.assertTrue;
import org.dozer.util.MappingUtils;
import org.dozer.vo.TestObject;
import org.dozer.vo.jaxb.employee.EmployeeType;
import org.dozer.vo.jaxb.employee.EmployeeWithInnerClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

  @Test
  public void testJAXBListWithNoSetter() {
    ListContainer source = new ListContainer();
    source.getList().add(1);
    source.getList().add(2);

    EmployeeType result = mapper.map(source, EmployeeType.class);

    assertNotNull(result);
    assertEquals(2, result.getIds().size());
    assertTrue(result.getIds().contains(1));
    assertTrue(result.getIds().contains(2));
  }


  public static class ListContainer {
    private List<Integer> list = new ArrayList<Integer>();

    public List<Integer> getList() {
      return list;
    }

  }

}
