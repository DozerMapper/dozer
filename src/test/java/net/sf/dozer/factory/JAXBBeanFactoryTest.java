package net.sf.dozer.factory;

import junit.framework.TestCase;
import net.sf.dozer.MappingException;
import net.sf.dozer.factory.JAXBBeanFactory;

/**
 * @author Vincent Jassogne
 */
public class JAXBBeanFactoryTest extends TestCase {

  private JAXBBeanFactory factory;
  protected void setUp() throws Exception {
    super.setUp();
    factory = new JAXBBeanFactory();
  }
  /*
   * Test method for 'net.sf.dozer.util.mapping.factory.JAXBFactory.createBean(Object, Class, String)'
   */
  public void testCreateBeanForSimpleJaxbClass() {

    Object obj = factory.createBean(null, null, "net.sf.dozer.functional_tests.vo.jaxb.employee.Employee");
    assertNotNull("Object can not be null", obj);
    assertEquals("Invalid class build", obj.getClass().getName(), "net.sf.dozer.functional_tests.vo.jaxb.employee.impl.EmployeeImpl");
  }
  /*
   * 
   */
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
  public void testCreateBeanForInnerJaxbClass() {

    Object obj = factory
        .createBean(null, null, "net.sf.dozer.functional_tests.vo.jaxb.employee.EmployeeWithInnerClassType$AddressType");
    assertNotNull("Object can not be null", obj);
    assertEquals("Invalid class build", obj.getClass().getName(),
        "net.sf.dozer.functional_tests.vo.jaxb.employee.impl.EmployeeWithInnerClassTypeImpl$AddressTypeImpl");
  }

}
