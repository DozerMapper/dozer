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
package com.github.dozermapper.core.loader;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MappingsBuilderTest extends AbstractDozerTest {

    private BeanContainer beanContainer = new BeanContainer();
    private DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);

    @Test
    public void testBuild() {
        DozerBuilder builder = new DozerBuilder(beanContainer, destBeanCreator, new PropertyDescriptorFactory());
        MappingFileData result = builder.build();
        assertNotNull(result);
    }

    @Test
    public void testGetFieldNameOfIndexedField() {
        assertEquals("aaa", DozerBuilder.getFieldNameOfIndexedField("aaa[0]"));
        assertEquals("aaa[0].bbb", DozerBuilder.getFieldNameOfIndexedField("aaa[0].bbb[1]"));
        assertEquals("aaa[0].bbb[1].ccc", DozerBuilder.getFieldNameOfIndexedField("aaa[0].bbb[1].ccc[2]"));
    }

}
