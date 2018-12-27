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
package com.github.dozermapper.core.functional_tests.annotation;

import com.github.dozermapper.core.Excluding;
import com.github.dozermapper.core.Mapping;
import com.github.dozermapper.core.builder.model.jaxb.Type;
import com.github.dozermapper.core.functional_tests.AbstractFunctionalTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AnnotationExcludeFieldTest extends AbstractFunctionalTest {

    @Override
    @Before
    public void setUp() throws Exception {
        mapper = getMapper();
    }

    @Test
    public void testExcludedField_SimpleLevel() {
        ZeroB zeroB = newInstance(ZeroB.class);
        zeroB.setId(Integer.valueOf("10"));
        ZeroA zeroA = mapper.map(zeroB, ZeroA.class);

        assertNull(zeroA.getId());
        assertEquals(Integer.valueOf("10"), zeroB.getId());
    }

    @Test
    public void testExcludedField_SimpleReverse() {
        ZeroA zeroA = newInstance(ZeroA.class);
        zeroA.setId(Integer.valueOf("5"));
        ZeroB zeroB = mapper.map(zeroA, ZeroB.class);

        assertEquals(Integer.valueOf("5"), zeroA.getId());
        assertNull(zeroB.getId());
    }

    @Test
    public void testExcludedField_OneLevel() {
        OneB oneB = newInstance(OneB.class);
        oneB.setId(Integer.valueOf("10"));
        OneA oneA = mapper.map(oneB, OneA.class);

        assertNull(oneA.getId());
        assertEquals(Integer.valueOf("10"), oneB.getId());
    }

    @Test
    public void testExcludedField_OneLevelReverse() {
        OneA oneA = newInstance(OneA.class);
        oneA.setId(Integer.valueOf("5"));
        OneB oneB = mapper.map(oneA, OneB.class);

        assertEquals(Integer.valueOf("5"), oneA.getId());
        assertNull(oneB.getId());
    }

    @Test
    public void testExcludedField_TwoLevel() {
        TwoB twoB = new TwoB();
        twoB.setId(Integer.valueOf("10"));
        TwoA twoA = mapper.map(twoB, TwoA.class);
        assertNull(twoA.getId());
        assertEquals(Integer.valueOf("10"), twoB.getId());
    }

    @Test
    public void testExcludedField_TwoLevelReverse() {
        TwoA twoA = newInstance(TwoA.class);
        twoA.setId(Integer.valueOf("5"));
        TwoB twoB = mapper.map(twoA, TwoB.class);
        assertEquals(Integer.valueOf("5"), twoA.getId());
        assertNull(twoB.getId());
    }

    @Test
    public void testExcludedField() {
        ZeroA zeroA = new ZeroA();
        zeroA.field2 = 35;
        ZeroB zeroB = mapper.map(zeroA, ZeroB.class);
        assertEquals((Integer) 35, zeroB.field1);
        assertNull(zeroB.field2);
    }

    @Test
    public void testOneWayExclusion() {
        ZeroA zeroA = new ZeroA();
        zeroA.field3 = 15;
        ZeroB zeroB = mapper.map(zeroA, ZeroB.class);
        assertNull(zeroB.field3);
        zeroB = new ZeroB();
        zeroB.field3 = 20;
        zeroA = mapper.map(zeroB, ZeroA.class);
        assertEquals((Integer) 20, zeroA.field3);
    }

    @Test
    public void testTwoWayExclusion() {
        ZeroA zeroA = new ZeroA();
        zeroA.field4 = 15;
        ZeroB zeroB = mapper.map(zeroA, ZeroB.class);
        assertNull(zeroB.field4);
        zeroB = new ZeroB();
        zeroB.field4 = 20;
        zeroA = mapper.map(zeroB, ZeroA.class);
        assertNull(zeroA.field4);
    }

    @Test
    public void testTwoWayGetterExclusion() {
        ZeroA zeroA = new ZeroA();
        zeroA.field5 = 15;
        ZeroB zeroB = mapper.map(zeroA, ZeroB.class);
        assertNull(zeroB.field5);
        zeroB = new ZeroB();
        zeroB.field5 = 20;
        zeroA = mapper.map(zeroB, ZeroA.class);
        assertNull(zeroA.field5);
    }

    public static class ZeroA {

        @Excluding
        private Integer id;
        @Excluding
        Integer field1;
        @Excluding
        @Mapping("field1")
        Integer field2;
        @Excluding(type = Type.ONE_WAY)
        Integer field3;
        @Excluding(type = Type.BI_DIRECTIONAL)
        Integer field4;
        private Integer field5;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setField1(Integer field1) {
            this.field1 = field1;
        }

        public Integer getField1() {
            return field1;
        }

        public Integer getField2() {
            return field2;
        }

        public void setField3(Integer field3) {
            this.field3 = field3;
        }

        public Integer getField3() {
            return field3;
        }

        public void setField4(Integer field4) {
            this.field4 = field4;
        }

        public Integer getField4() {
            return field4;
        }

        @Excluding
        public Integer getField5() {
            return field5;
        }

        public void setField5(Integer field5) {
            this.field5 = field5;
        }

    }

    public static class ZeroB {

        @Excluding
        private Integer id;
        Integer field1;
        Integer field2;
        Integer field3;
        Integer field4;
        Integer field5;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setField1(Integer field1) {
            this.field1 = field1;
        }

        public Integer getField1() {
            return field1;
        }

        public void setField2(Integer field2) {
            this.field2 = field2;
        }

        public void setField3(Integer field3) {
            this.field3 = field3;
        }

        public Integer getField3() {
            return field3;
        }

        public void setField4(Integer field4) {
            this.field4 = field4;
        }

        public Integer getField4() {
            return field4;
        }

        public Integer getField5() {
            return field5;
        }

        public void setField5(Integer field5) {
            this.field5 = field5;
        }

    }

    public static class OneA extends ZeroA {

    }

    public static class OneB extends ZeroB {

    }

    public static class TwoA extends OneA {

    }

    public static class TwoB extends OneB {

    }

}
