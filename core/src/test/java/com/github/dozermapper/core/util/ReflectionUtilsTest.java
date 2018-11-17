/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.vo.A;
import com.github.dozermapper.core.vo.B;
import com.github.dozermapper.core.vo.NoReadMethod;
import com.github.dozermapper.core.vo.NoVoidSetters;
import com.github.dozermapper.core.vo.SimpleObj;
import com.github.dozermapper.core.vo.inheritance.ChildChildIF;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ReflectionUtilsTest extends AbstractDozerTest {

    private BeanContainer beanContainer = new BeanContainer();

    @Test(expected = MappingException.class)
    public void testGetMethod_NotFound() {
        SimpleObj src = new SimpleObj();
        ReflectionUtils.getMethod(src, String.valueOf(System.currentTimeMillis()));
    }

    @Test(expected = MappingException.class)
    public void testGetDeepFieldHierarchy_NonDeepField() {
        ReflectionUtils.getDeepFieldHierarchy(SimpleObj.class, "test", null);
    }

    @Test(expected = MappingException.class)
    public void testGetDeepFieldHierarchy_NotExists() {
        ReflectionUtils.getDeepFieldHierarchy(SimpleObj.class,
                                              String.valueOf(System.currentTimeMillis()) + "." + String.valueOf(System.currentTimeMillis()), null);
    }

    @Test
    public void testGetPropertyDescriptors_InterfaceInheritance() {
        // Should walk the inheritance hierarchy all the way up to the super interface and find all properties along the way
        PropertyDescriptor[] pds = ReflectionUtils.getPropertyDescriptors(ChildChildIF.class);
        assertNotNull("prop descriptors should not be null", pds);
        assertEquals("3 prop descriptors should have been found", 3, pds.length);
    }

    @Test
    public void testFindPropertyDescriptor_InterfaceInheritance() {
        // Should walk the inheritance hierarchy all the way up to the super interface and find the property along the way
        String fieldName = "parentField";

        PropertyDescriptor pd = ReflectionUtils.findPropertyDescriptor(ChildChildIF.class, fieldName, null);

        assertNotNull("prop descriptor should not be null", pd);
        assertEquals("invalid prop descriptor name found", fieldName, pd.getName());
    }

    @Test
    public void shouldReturnBestMatch_ambigousIgonreCase() {
        // both timezone and timeZone properties exists on XMLGregorianCalendar
        PropertyDescriptor result = ReflectionUtils.findPropertyDescriptor(XMLGregorianCalendar.class, "timezone", null);

        assertThat(result.getName(), equalTo("timezone"));
    }

    @Test
    public void testGetInterfacePropertyDescriptors() {
        PropertyDescriptor[] descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestIF1.class);
        assertEquals(1, descriptors.length);

        descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestIF2.class);
        assertEquals(1, descriptors.length);

        descriptors = ReflectionUtils.getInterfacePropertyDescriptors(TestClass.class);
        assertEquals(4, descriptors.length);
    }

    @Test
    public void testIllegalMethodType() {
        A a = new A();
        String methodName = "setB";
        try {
            Method method = a.getClass().getMethod(methodName, B.class);
            ReflectionUtils.invoke(method, a, new Object[] {"wrong param"});
        } catch (NoSuchMethodException e) {
            Assert.fail("Method " + methodName + "missed");
        } catch (MappingException e) {
            if (!e.getMessage().contains("Illegal object type for the method '" + methodName + "'")) {
                Assert.fail("Wrong exception message");
            }
        }
    }

    @Test
    public void testGetNonVoidSetterMethod() {
        Method method = ReflectionUtils.getNonStandardSetter(new NoVoidSetters().getClass(), "description");
        assertNotNull(method);

        method = ReflectionUtils.getNonStandardSetter(new NoReadMethod().getClass(), "noReadMethod");
        assertNull(method);
    }

    @Test
    public void shouldGetField() {
        Field field = ReflectionUtils.getFieldFromBean(GrandChild.class, "c");
        assertNotNull(field);
    }

    @Test
    public void shouldGoToSuperclass() {
        Field field = ReflectionUtils.getFieldFromBean(GrandChild.class, "a");
        assertNotNull(field);

        field = ReflectionUtils.getFieldFromBean(GrandChild.class, "b");
        assertNotNull(field);
    }

    @Test
    public void shouldModifyAccessor() {
        Field field = ReflectionUtils.getFieldFromBean(BaseBean.class, "a");
        assertNotNull(field);
    }

    @Test(expected = MappingException.class)
    public void shouldFailWhenFieldMissing() {
        ReflectionUtils.getFieldFromBean(GrandChild.class, "d");
    }

    @Test
    public void shouldThrowNoSuchMethodFound() throws NoSuchMethodException {
        Method result = ReflectionUtils.findAMethod(TestClass.class, "getC()", beanContainer);
        assertThat(result, notNullValue());
    }

    @Test
    public void shouldThrowNoSuchMethodFound_NoBrackets() throws NoSuchMethodException {
        Method result = ReflectionUtils.findAMethod(TestClass.class, "getC", beanContainer);
        assertThat(result, notNullValue());
    }

    @Test(expected = NoSuchMethodException.class)
    public void shouldThrowNoSuchMethodFound_Missing() throws Exception {
        ReflectionUtils.findAMethod(TestClass.class, "noSuchMethod()", beanContainer);
        fail();
    }

    @Test(expected = NoSuchMethodException.class)
    public void shouldThrowNoSuchMethodFound_MissingNoBrackets() throws Exception {
        ReflectionUtils.findAMethod(TestClass.class, "noSuchMethod", beanContainer);
        fail();
    }

    @Test
    public void shouldHandleBeanWithGenericInterface() {
        PropertyDescriptor propertyDescriptor = ReflectionUtils.findPropertyDescriptor(Y.class, "x", null);
        assertEquals("com.github.dozermapper.core.util.ReflectionUtilsTest$ClassInheritsClassX", propertyDescriptor.getReadMethod().getReturnType().getName());
    }

    @Test
    public void shouldDetermineGenericTypeForSimpleGenericType() throws Exception {
        Class<?> clazz = ReflectionUtils.determineGenericsType(ClassWithGenericFields.class.getField("simpleGenericListString").getGenericType());
        assertEquals("java.lang.String", clazz.getCanonicalName());
    }

    @Test
    public void shouldDetermineGenericTypeForNestedGenericType() throws Exception {
        Class<?> clazz = ReflectionUtils.determineGenericsType(ClassWithGenericFields.class.getField("nestedGenericListMapStringString").getGenericType());
        assertEquals("java.util.Map", clazz.getCanonicalName());
    }

    @Test
    public void shouldDetermineReadMethodForSyntheticOnlyMethod() throws Exception {
        PropertyDescriptor propertyDescriptor = ReflectionUtils.findPropertyDescriptor(ListHolderWrapperImpl.class, "list", null);
        assertNotNull(propertyDescriptor.getReadMethod());
    }

    public class Y implements HasX<ClassInheritsClassX> {
        private ClassInheritsClassX x;

        @Override
        public void setX(ClassInheritsClassX x) {
            this.x = x;
        }

        @Override
        public ClassInheritsClassX getX() {
            return x;
        }
    }

    public interface HasX<X extends ClassX> {
        void setX(X x);

        X getX();
    }

    public class ClassWithGenericFields {
        public List<String> simpleGenericListString;
        public List<Map<String, String>> nestedGenericListMapStringString;
    }

    public class ClassInheritsClassX extends ClassX {
    }

    public class ClassX {
    }

    public static class BaseBean {
        private String a;
    }

    public static class ChildBean extends BaseBean {
        private String b;
    }

    public static class GrandChild extends ChildBean {
        public String c;
    }

    private abstract static class TestClass implements TestIF1, TestIF2 {
        public String getC() {
            return null;
        }
    }

    private interface TestIF1 {
        String getA();

        void setA(String a);
    }

    private interface TestIF2 {
        Integer getB();
    }

    public class ListHolderWrapperImpl<T> extends AbstractListHolder<T> implements ListHolderWrapper<T> {
        public ListHolderWrapperImpl(List<T> content) {
            super(content);
        }
    }

    abstract class AbstractListHolder<T> implements ListHolder<T> {
        private List<T> list;

        AbstractListHolder(List<T> list) {
            this.list = list;
        }

        @Override
        public List<T> getList() {
            return list;
        }
    }

    interface ListHolder<T> {
        List<T> getList();
    }

    interface ListHolderWrapper<T> extends ListHolder<T> {

    }

}
