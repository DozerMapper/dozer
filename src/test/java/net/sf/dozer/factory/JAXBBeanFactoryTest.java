package net.sf.dozer.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
  
  @Test
  public void testCreateBeanClassNotFoundException() {
    try {
      factory.createBean(null, null, "ve.ve.DE");
    } catch (MappingException e) {
      assertTrue(true);
      // Success
    } catch (Throwable e) {
      fail("Not existing class should throw MappingException " + e.toString());
    }

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
