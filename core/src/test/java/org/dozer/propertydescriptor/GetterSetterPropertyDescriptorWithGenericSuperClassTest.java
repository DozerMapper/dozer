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
package org.dozer.propertydescriptor;

import org.dozer.AbstractDozerTest;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.fieldmap.DozerField;
import org.dozer.vo.deep3.Dest;
import org.dozer.vo.deep3.NestedDest;
import org.junit.Test;

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

        BeanContainer beanContainer = new BeanContainer();
        JavaBeanPropertyDescriptor pd = new JavaBeanPropertyDescriptor(Dest.class, dozerField.getName(), dozerField.isIndexed(),
          dozerField.getIndex(), null, null, beanContainer, new DestBeanCreator(beanContainer));
      Class<?> clazz = pd.genericType();

      assertNotNull("clazz should not be null", clazz);
      assertEquals("NestedDest generic type", NestedDest.class, clazz);
    }
}
