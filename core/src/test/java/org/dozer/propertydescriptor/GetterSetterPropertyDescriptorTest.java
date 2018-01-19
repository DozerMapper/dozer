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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.dozer.AbstractDozerTest;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.fieldmap.DozerField;
import org.dozer.vo.deep2.Dest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * @author tierney.matt
 */
public class GetterSetterPropertyDescriptorTest extends AbstractDozerTest {
    private JavaBeanPropertyDescriptor javaBeanPropertyDescriptor;

    @Before
    public void setup() {
        DozerField dozerField = new DozerField("destField", "generic");

        BeanContainer beanContainer = new BeanContainer();
        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        this.javaBeanPropertyDescriptor = new JavaBeanPropertyDescriptor(
            Dest.class, dozerField.getName(), dozerField.isIndexed(), dozerField.getIndex(), null, null, beanContainer, destBeanCreator);
    }

    @Test
    public void testGetReadMethod() throws Exception {
        Method method = javaBeanPropertyDescriptor.getReadMethod();

        assertNotNull("method should not be null", method);
        assertEquals("incorrect method found", "getDestField", method.getName());
    }

    /**
     * {@link PropertyDescriptor} holds {@link java.lang.ref.SoftReference}s to write
     * and read methods. The references may get {@code null}ed out during garbage collection.
     * We must be able to recover if that happens.
     *
     * @see <a href="https://github.com/DozerMapper/dozer/issues/118">Dozer mapping stops working</a>
     */
    @Test
    public void testGetWriteMethodRecoversFromWriteMethodGarbageCollection() throws Exception {
        initializePropertyDescriptor();
        nullPropertyDescriptorMethod(true);

        Method method = javaBeanPropertyDescriptor.getWriteMethod();

        assertEquals("incorrect method found", "setDestField", method.getName());
    }

    /**
     * See {@link #testGetWriteMethodRecoversFromWriteMethodGarbageCollection()}. If we
     * attempt recovery and don't find anything, we must give up instead of endlessly
     * trying to recover.
     */
    @Test(expected = NoSuchMethodException.class)
    public void testGetWriteMethodOnlyAttemptsRecoveryOnce() throws Exception {
        initializePropertyDescriptor();
        nullPropertyDescriptorMethod(true);
        setRefreshAlreadyAttempted();

        javaBeanPropertyDescriptor.getWriteMethod();
    }

    /**
     * See {@link #testGetWriteMethodRecoversFromWriteMethodGarbageCollection()}
     */
    @Test
    public void testGetReadMethodRecoversFromReadMethodGarbageCollection() throws Exception {
        initializePropertyDescriptor();

        Method method = javaBeanPropertyDescriptor.getReadMethod();

        assertEquals("incorrect method found", "getDestField", method.getName());
    }

    /**
     * See {@link #testGetWriteMethodOnlyAttemptsRecoveryOnce()}
     */
    @Test(expected = NoSuchMethodException.class)
    public void testGetReadMethodOnlyAttemptsRecoveryOnce() throws Exception {
        initializePropertyDescriptor();
        nullPropertyDescriptorMethod(false);
        setRefreshAlreadyAttempted();

        javaBeanPropertyDescriptor.getReadMethod();
    }

    private void initializePropertyDescriptor() throws Exception {
        javaBeanPropertyDescriptor.getWriteMethod();
    }

    private void setRefreshAlreadyAttempted() throws Exception {
        Whitebox.setInternalState(javaBeanPropertyDescriptor, "propertyDescriptorsRefreshed", true);
    }

    /**
     * Simulates that a garbage collection has happened and a reference in the
     * {@link PropertyDescriptor} has been {@code null}ed.
     *
     * @param writeMethod {@code true} to null out the write method, {@code false}
     *                    to null out the read method
     */
    private void nullPropertyDescriptorMethod(boolean writeMethod) throws Exception {
        PropertyDescriptor internalPropertyDescriptor = (PropertyDescriptor)Whitebox.getInternalState(javaBeanPropertyDescriptor, "pd");
        if (writeMethod) {
            internalPropertyDescriptor.setWriteMethod(null);
        } else {
            internalPropertyDescriptor.setReadMethod(null);
        }
    }
}
