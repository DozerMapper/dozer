package org.dozer.factory;

import org.dozer.MappingException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Vincent Jassogne
 */
public class JAXBBeanFactoryTest {

  private JAXBBeanFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new JAXBBeanFactory();
  }

  @Test
  @Ignore("org.dozer.vo.jaxb child packages missed")
  public void testCreateBeanForSimpleJaxbClass() {
    Object obj = factory.createBean(null, null, "org.dozer.vo.jaxb.employee.EmployeeType");
    assertNotNull("Object can not be null", obj);
    assertEquals("org.dozer.vo.jaxb.employee.EmployeeType", obj.getClass().getName());
  }

  @Test(expected = MappingException.class)
  public void testCreateBeanClassNotFoundException() {
    factory.createBean(null, null, "ve.ve.DE");
  }

  @Test
  @Ignore("org.dozer.vo.jaxb child packages missed")
  public void testCreateBeanForInnerJaxbClass() {
    Object obj = factory.createBean(null, null, "org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address");
    assertNotNull(obj);
    assertEquals("org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address", obj.getClass().getName());
  }

  @Test
  @Ignore("org.dozer.vo.jaxb child packages missed")
  public void testCreateBeanForNestedInnerJaxbClass() {
    Object obj = factory.createBean(null, null, "org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address$State");
    assertNotNull(obj);
    assertEquals("org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address$State", obj.getClass().getName());
  }

}
