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

import java.util.ArrayList;
import java.util.List;

import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.fieldmap.FieldMap;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author dmitry.buzdin
 */
public class FieldPropertyDescriptorTest extends AbstractDozerTest {

    private DestBeanCreator destBeanCreator = new DestBeanCreator(new BeanContainer());

    @Test(expected = MappingException.class)
    public void testNoSuchField() {
        new FieldPropertyDescriptor(String.class, "nosuchfield", false, 0, null, null, destBeanCreator);
        fail();
    }

    @Test
    public void testConstructor() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "hidden", false, 0, null, null, destBeanCreator);
        assertNotNull(descriptor);
        assertEquals(Integer.TYPE, descriptor.getPropertyType());
        assertNotNull(descriptor.getPropertyValue(new Container()));
    }

    @Test
    public void getPropertyType() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "hidden", false, 0, null, null, destBeanCreator);
        assertEquals(Integer.TYPE, descriptor.getPropertyType());
    }

    @Test
    public void getPropertyTypeChained() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "container.value", false, 0, null, null, destBeanCreator);
        assertEquals(String.class, descriptor.getPropertyType());
    }

    @Test
    public void getPropertyValue() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "hidden", false, 0, null, null, destBeanCreator);
        Object result = descriptor.getPropertyValue(new Container());
        assertEquals(42, result);
    }

    @Test
    public void getPropertyValueChained() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "container.hidden", false, 0, null, null, destBeanCreator);
        Object result = descriptor.getPropertyValue(new Container("A"));
        assertEquals(42, result);
    }

    @Test
    public void setPropertyValue() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "value", false, 0, null, null, destBeanCreator);
        Container bean = new Container("A");
        descriptor.setPropertyValue(bean, "B", mock(FieldMap.class));
        assertEquals("B", bean.value);
    }

    @Test
    public void setPropertyValueChained() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "container.value", false, 0, null, null, destBeanCreator);
        Container bean = new Container("");
        bean.container = new Container("");

        descriptor.setPropertyValue(bean, "A", mock(FieldMap.class));

        assertEquals("A", bean.container.value);
    }

    @Test
    public void setPropertyValueChained_ThirdLevel() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "container.container.value", false, 0, null, null, destBeanCreator);
        Container bean = new Container("X");
        bean.container = new Container("X");
        bean.container.container = new Container("X");

        descriptor.setPropertyValue(bean, "Y", mock(FieldMap.class));

        assertEquals("Y", bean.container.container.value);
    }

    @Test
    public void setPropertyValueChained_IntermediateNull() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "container.value", false, 0, null, null, destBeanCreator);
        Container bean = new Container("");

        descriptor.setPropertyValue(bean, "A", mock(FieldMap.class));

        assertEquals("A", bean.container.value);
    }

    @Test
    public void getPropertyValueChained_IntermediateNull() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "container.value", false, 0, null, null, destBeanCreator);
        Container bean = new Container("");
        Object value = descriptor.getPropertyValue(bean);
        assertEquals("", value);
    }

    @Test
    public void getPropertyValueIndexed() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "values", true, 0, null, null, destBeanCreator);
        Container container = new Container("");
        container.values.add("A");
        Object value = descriptor.getPropertyValue(container);
        assertEquals("A", value);
    }

    @Test
    public void setPropertyValueIndexed() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(Container.class, "values", true, 0, null, null, destBeanCreator);
        Container container = new Container("");
        descriptor.setPropertyValue(container, "A", mock(FieldMap.class));
        assertEquals(1, container.values.size());
        assertEquals("A", container.values.get(0));
    }

    public static class Container {

        public Container() {
        }

        public Container(String value) {
            this.value = value;
        }

        private int hidden = 42;
        String value;
        Container container = this;
        List<String> values = new ArrayList<String>();

    }
}
