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
package com.github.dozermapper.core.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.DozerClass;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldMapTest extends AbstractDozerTest {

    private ClassMap classMap;
    private FieldMap fieldMap;
    private DozerField field;

    @Before
    public void setUp() {
        classMap = mock(ClassMap.class);
        BeanContainer beanContainer = new BeanContainer();
        fieldMap = new FieldMap(classMap, beanContainer, new DestBeanCreator(beanContainer), new PropertyDescriptorFactory()) {
        };
        DozerClass dozerClass = mock(DozerClass.class);
        when(classMap.getDestClass()).thenReturn(dozerClass);
        field = mock(DozerField.class);
        fieldMap.setDestField(field);
    }

    @Test
    public void shouldSurviveConcurrentAccess() throws InterruptedException {
        DozerField dozerField = mock(DozerField.class);
        when(dozerField.getName()).thenReturn("");
        fieldMap.setSrcField(dozerField);
        fieldMap.setDestField(dozerField);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<Object>> callables = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            callables.add(new Callable<Object>() {
                public Object call() {
                    return fieldMap.getSrcPropertyDescriptor(String.class);
                }
            });
        }

        executorService.invokeAll(callables);
        Thread.sleep(1000);
        executorService.shutdown();
    }

    @Test
    public void shouldBeAccessible_ClassLevel() {
        when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        when(field.isAccessible()).thenReturn(Boolean.FALSE);

        assertFalse(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Both() {
        when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        when(field.isAccessible()).thenReturn(Boolean.TRUE);

        assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_FieldLevel() {
        when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.FALSE);
        when(field.isAccessible()).thenReturn(Boolean.TRUE);

        assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Override() {
        when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        when(field.isAccessible()).thenReturn(Boolean.FALSE);

        assertFalse(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Null() {
        when(classMap.getDestClass().isAccessible()).thenReturn(false);
        when(field.isAccessible()).thenReturn(Boolean.TRUE);

        assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_FieldLevelNull() {
        when(classMap.getDestClass().isAccessible()).thenReturn(Boolean.TRUE);
        when(field.isAccessible()).thenReturn(null);

        assertTrue(fieldMap.isDestFieldAccessible());
    }

    @Test
    public void shouldBeAccessible_Default() {
        when(classMap.getDestClass().isAccessible()).thenReturn(false);
        when(field.isAccessible()).thenReturn(null);

        assertFalse(fieldMap.isDestFieldAccessible());
    }

}
