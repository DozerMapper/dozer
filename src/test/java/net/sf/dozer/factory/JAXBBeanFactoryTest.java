package net.sf.dozer.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.dozer.MappingException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Vincent Jassogne
 */
public class JAXBBeanFactoryTest  {

  private JAXBBeanFactory factory;
  
  @Before
  public void setUp() throws Exception {
    factory = new JAXBBeanFactory();
  }
  /*
   * Test method for 'net.sf.dozer.util.mapping.factory.JAXBFactory.createBean(Object, Class, String)'
   */
  @Test
  public void testCreateBeanForSimpleJaxbClass() {

    Object obj = factory.createBean(null, null, "net.sf.dozer.vo.jaxb.employee.Employee");
    assertNotNull("Object can not be null", obj);
    assertEquals("Invalid class build", obj.getClass().getName(), "net.sf.dozer.vo.jaxb.employee.impl.EmployeeImpl");
  }
  
  @Test(expected = MappingException.class)
  public void testCreateBeanClassNotFoundException() {
    factory.createBean(null, null, "ve.ve.DE");
  }
  /*
   * Test method for 'net.sf.dozer.util.mapping.factory.JAXBFactory.createBean(Object, Class, String)'
   */
  @Test
  public void testCreateBeanForInnerJaxbClass() {

    Object obj = factory
        .createBean(null, null, "net.sf.dozer.vo.jaxb.employee.EmployeeWithInnerClassType$AddressType");
    assertNotNull("Object can not be null", obj);
    assertEquals("Invalid class build", obj.getClass().getName(),
        "net.sf.dozer.vo.jaxb.employee.impl.EmployeeWithInnerClassTypeImpl$AddressTypeImpl");
  }

}
