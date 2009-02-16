package org.dozer.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.dozer.MappingException;
import org.dozer.factory.JAXBBeanFactory;
import org.junit.Before;
import org.junit.Test;

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
  public void testCreateBeanForSimpleJaxbClass() {

    Object obj = factory.createBean(null, null, "org.dozer.vo.jaxb.employee.Employee");
    assertNotNull("Object can not be null", obj);
    assertEquals("Invalid class build", obj.getClass().getName(), "org.dozer.vo.jaxb.employee.impl.EmployeeImpl");
  }

  @Test(expected = MappingException.class)
  public void testCreateBeanClassNotFoundException() {
    factory.createBean(null, null, "ve.ve.DE");
  }

  @Test
  public void testCreateBeanForInnerJaxbClass() {

    Object obj = factory.createBean(null, null, "org.dozer.vo.jaxb.employee.EmployeeWithInnerClassType$AddressType");
    assertNotNull("Object can not be null", obj);
    assertEquals("Invalid class build", obj.getClass().getName(),
        "org.dozer.vo.jaxb.employee.impl.EmployeeWithInnerClassTypeImpl$AddressTypeImpl");
  }

}
