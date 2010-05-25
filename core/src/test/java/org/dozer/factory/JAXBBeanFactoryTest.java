package org.dozer.factory;

import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Vincent Jassogne
 */
public class JAXBBeanFactoryTest extends AbstractDozerTest {

  private JAXBBeanFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new JAXBBeanFactory();
  }

  @Test
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
  public void testCreateBeanForInnerJaxbClass() {
    Object obj = factory.createBean(null, null, "org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address");
    assertNotNull(obj);
    assertEquals("org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address", obj.getClass().getName());
  }

  @Test
  public void testCreateBeanForNestedInnerJaxbClass() {
    Object obj = factory.createBean(null, null, "org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address$State");
    assertNotNull(obj);
    assertEquals("org.dozer.vo.jaxb.employee.EmployeeWithInnerClass$Address$State", obj.getClass().getName());
  }

}
