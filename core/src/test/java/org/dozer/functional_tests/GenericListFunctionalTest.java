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

import org.dozer.Mapper;
import org.dozer.vo.Person1;
import org.dozer.vo.Person2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GenericListFunctionalTest extends AbstractFunctionalTest {

  @Test
  public void testMappingForList() {
    Mapper mapper = getMapper("mappings/listMapping.xml");

    Person1 person1 = new Person1();

    person1.setFamilyName("Ganga");
    person1.setSecondFamilyName("Chacon");

    Person2 result = mapper.map(person1, Person2.class);

    assertNotNull(result);
    assertEquals("Ganga", result.getPersonNames().get(0).getFamilyName());
    assertEquals("Chacon", result.getPersonNames().get(1).getFamilyName());
  }

}
