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
package com.github.dozermapper.core.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DestBeanCreatorTest extends AbstractDozerTest {

    private DestBeanCreator destBeanCreator = new DestBeanCreator(new BeanContainer());

    @Test
    public void testCreatDestBeanNoFactory() {
        TestObject bean = (TestObject)destBeanCreator.create(new BeanCreationDirective(null, null, TestObject.class, null, null, null, null, null));

        assertNotNull(bean);
        assertNull(bean.getCreatedByFactoryName());
    }

    @Test
    public void testCreatBeanFromFactory() {
        String factoryName = "com.github.dozermapper.core.functional_tests.support.SampleCustomBeanFactory";
        TestObject bean = (TestObject)destBeanCreator.create(
                new BeanCreationDirective(new TestObjectPrime(), TestObjectPrime.class, TestObject.class, null, factoryName, null, null, null));

        assertNotNull(bean);
        assertEquals(factoryName, bean.getCreatedByFactoryName());
    }

    @Test
    public void testMap() {
        Map map = destBeanCreator.create(Map.class);
        assertTrue(map instanceof HashMap);

        TreeMap treeMap = destBeanCreator.create(TreeMap.class);
        assertNotNull(treeMap);
    }

}
