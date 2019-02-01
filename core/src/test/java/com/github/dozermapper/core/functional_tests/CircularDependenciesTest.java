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
package com.github.dozermapper.core.functional_tests;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CircularDependenciesTest extends AbstractFunctionalTest {

    private Mapper mapper;

    @Before
    public void setUp() {
        mapper = DozerBeanMapperBuilder.buildDefault();
    }

    @Test
    public void shouldHandleCircularDependencies() {
        A a = newInstance(A.class);
        B b = newInstance(B.class);
        C c = newInstance(C.class);

        a.setC(c);
        b.setA(a);
        c.setB(b);

        A result = mapper.map(a, A.class);

        assertThat(result, notNullValue());
        assertThat(result.getC(), notNullValue());
        assertThat(result.getC().getB(), notNullValue());
        assertThat(result.getC().getB().getA(), notNullValue());
        assertThat(result.getC().getB().getA(), equalTo(result));
    }

    public static class A {
        C c;

        public C getC() {
            return c;
        }

        public void setC(C c) {
            this.c = c;
        }
    }

    public static class B {
        A a;

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }

    public static class C {
        B b;

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

}
