/*
 * Copyright 2005-2024 Dozer Project
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

import java.lang.reflect.Method;
import java.util.Arrays;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MapPropertyDescriptorTest extends AbstractDozerTest {

    private MapPropertyDescriptor descriptor;
    private BeanContainer beanContainer;
    private DestBeanCreator destBeanCreator;

    @Before
    public void setUp() throws Exception {
        beanContainer = new BeanContainer();
        destBeanCreator = new DestBeanCreator(beanContainer);
        descriptor = new MapPropertyDescriptor(MapStructure.class, "", false, 0, "set", "get", "key", null, null, beanContainer, destBeanCreator);
    }

    @Test
    public void testGetWriteMethod() {
        Method method = descriptor.getWriteMethod();
        assertEquals(2, method.getParameterTypes().length);
        assertTrue(Arrays.equals(new Class[] {String.class, Object.class}, method.getParameterTypes()));
    }

    @Test(expected = MappingException.class)
    public void testGetWriteMethod_NotFound() {
        descriptor = new MapPropertyDescriptor(MapStructure.class, "", false, 0, "missing_set", "get", "key", null, null, beanContainer, destBeanCreator);
        descriptor.getWriteMethod();
        fail();
    }

    @Test
    public void testGetReadMethod() {
        Method method = descriptor.getReadMethod();
        assertEquals(1, method.getParameterTypes().length);
        assertTrue(Arrays.equals(new Class[] {String.class}, method.getParameterTypes()));
    }

    private static class MapStructure {

        public Object get() {
            return null;
        }

        public Object get(String id) {
            return null;
        }

        public Object get(String id1, String id2) {
            return null;
        }

        public void set(String id) {
        }

        public void set(String id, Object object) {
        }

        public void set(String id, Object object, String id2) {
        }

    }

}
