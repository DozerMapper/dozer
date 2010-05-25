/*
 * Copyright 2005-2010 the original author or authors.
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

import java.lang.reflect.Method;

import org.dozer.AbstractDozerTest;
import org.dozer.fieldmap.DozerField;
import org.dozer.vo.deep2.Dest;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class GetterSetterPropertyDescriptorTest extends AbstractDozerTest {

  @Test
  public void testGetReadMethod() throws Exception {
    DozerField dozerField = new DozerField("destField", "generic");

    JavaBeanPropertyDescriptor pd = new JavaBeanPropertyDescriptor(Dest.class, dozerField.getName(), dozerField.isIndexed(),
        dozerField.getIndex(), null, null);
    Method method = pd.getReadMethod();

    assertNotNull("method should not be null", method);
    assertEquals("incorrect method found", "getDestField", method.getName());
  }
}
