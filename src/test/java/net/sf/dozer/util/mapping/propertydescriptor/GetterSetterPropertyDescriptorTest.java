/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.propertydescriptor;

import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.vo.deep2.Dest;

/**
 * @author tierney.matt
 */
public class GetterSetterPropertyDescriptorTest extends AbstractDozerTest {

  public void testGetReadMethod() throws Exception {
    DozerField dozerField = new DozerField("destField", "generic");

    JavaBeanPropertyDescriptor pd = new JavaBeanPropertyDescriptor(Dest.class, dozerField.getName(), dozerField.isIndexed(),
        dozerField.getIndex());
    Method method = pd.getReadMethod();

    assertNotNull("method should not be null", method);
    assertEquals("incorrect method found", "getDestField", method.getName());
  }
}
