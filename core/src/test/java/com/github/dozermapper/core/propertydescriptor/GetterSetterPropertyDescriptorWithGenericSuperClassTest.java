/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.propertydescriptor;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.DozerField;
import com.github.dozermapper.core.vo.deep3.Dest;
import com.github.dozermapper.core.vo.deep3.NestedDest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetterSetterPropertyDescriptorWithGenericSuperClassTest extends AbstractDozerTest {
    @Test
    public void testGetReadMethod() {
        DozerField dozerField = new DozerField("nestedList", "generic");

        BeanContainer beanContainer = new BeanContainer();
        JavaBeanPropertyDescriptor pd = new JavaBeanPropertyDescriptor(Dest.class, dozerField.getName(), dozerField.isIndexed(),
                                                                       dozerField.getIndex(), null, null, beanContainer, new DestBeanCreator(beanContainer));
        Class<?> clazz = pd.genericType();

        assertNotNull("clazz should not be null", clazz);
        assertEquals("NestedDest generic type", NestedDest.class, clazz);
    }
}
