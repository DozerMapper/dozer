package org.dozer.propertydescriptor;

import org.dozer.AbstractDozerTest;
import org.dozer.fieldmap.DozerField;
import org.dozer.vo.deep3.Dest;
import org.dozer.vo.deep3.NestedDest;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * GetterSetterPropertyDescriptorWithGenericSuperClassTest
 *
 * @author tetra
 * @version $Id$
 */
public class GetterSetterPropertyDescriptorWithGenericSuperClassTest extends AbstractDozerTest {
    @Test
    public void testGetReadMethod() throws Exception {
      DozerField dozerField = new DozerField("nestedList", "generic");

      JavaBeanPropertyDescriptor pd = new JavaBeanPropertyDescriptor(Dest.class, dozerField.getName(), dozerField.isIndexed(),
          dozerField.getIndex(), null, null);
      Class<?> clazz = pd.genericType();

      assertNotNull("clazz should not be null", clazz);
      assertEquals("NestedDest generic type", NestedDest.class, clazz);
    }
}
