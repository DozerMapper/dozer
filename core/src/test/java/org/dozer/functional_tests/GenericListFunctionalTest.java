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
    Mapper mapper = getMapper("listMapping.xml");

    Person1 person1 = new Person1();

    person1.setFamilyName("Ganga");
    person1.setSecondFamilyName("Chacon");

    Person2 result = mapper.map(person1, Person2.class);

    assertNotNull(result);
    assertEquals("Ganga", result.getPersonNames().get(0).getFamilyName());
    assertEquals("Chacon", result.getPersonNames().get(1).getFamilyName());
  }

}
