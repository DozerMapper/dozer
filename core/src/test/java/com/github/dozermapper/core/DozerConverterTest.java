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
package com.github.dozermapper.core;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DozerConverterTest extends AbstractDozerTest {

    private DozerConverter<String, Integer> converter;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        converter = new DozerConverter<String, Integer>(String.class,
                                                        Integer.class) {

            public Integer convertTo(String source, Integer destination) {
                return Integer.parseInt(source);
            }

            public String convertFrom(Integer source, String destination) {
                return source.toString();
            }
        };
    }

    @Test(expected = IllegalStateException.class)
    public void test_parameterNotSet() {
        converter.getParameter();
        fail();
    }

    @Test(expected = MappingException.class)
    public void test_convert_exception() {
        converter.convert(Boolean.TRUE, new BigDecimal(1), Boolean.class,
                          BigDecimal.class);
        fail();
    }

    @Test
    public void test_gettingParameter() {
        converter.setParameter("A");
        assertEquals("A", converter.getParameter());
    }

    @Test
    public void test_convertFromTo() {
        assertEquals("1", converter.convertFrom(new Integer(1)));
        assertEquals(new Integer(2), converter.convertTo("2"));

        assertEquals("1", converter.convertFrom(new Integer(1), "0"));
        assertEquals(new Integer(2), converter.convertTo("2", new Integer(0)));
    }

    @Test
    public void test_FullCycle() {
        assertEquals(1, converter.convert(null, "1", Integer.class,
                                          String.class));
        assertEquals("1", converter.convert(null, new Integer(1), String.class,
                                            Integer.class));
    }

    @Test
    public void testObjectType() {
        assertEquals(1, converter
                .convert(null, "1", Object.class, String.class));
        assertEquals("1", converter.convert(null, new Integer(1), Object.class,
                                            Integer.class));
    }

    @Test
    public void testAutoboxing() {
        assertEquals(1, converter.convert(null, "1", int.class, String.class));
    }

    @Test
    public void testPrimitiveToPrimitive() {
        DozerConverter<Integer, Double> converter = new DozerConverter<Integer, Double>(
                Integer.class, Double.class) {

            @Override
            public Double convertTo(Integer source, Double destination) {
                return new Double(Integer.toString(source));
            }

            @Override
            public Integer convertFrom(Double source, Integer destination) {
                return new Integer(Double.toString(source));
            }
        };

        converter.convert(1d, 2, double.class, int.class);
    }

    @Test
    public void test_hierarchy() {
        DozerConverter<Number, Integer> converter = new DozerConverter<Number, Integer>(
                Number.class, Integer.class) {

            public Integer convertTo(Number source, Integer destination) {
                return source.intValue();
            }

            public Number convertFrom(Integer source, Number destination) {
                return source;
            }
        };

        assertEquals(new Integer(1), converter.convert(null, new Integer(1),
                                                       Number.class, Integer.class));

        assertEquals(new Integer(1), converter.convert(null, new Double(1),
                                                       Integer.class, Number.class));
    }

    @Test
    public void testAssignments() {
        DozerConverter<Number, Number> converter = new DozerConverter<Number, Number>(
                Number.class, Number.class) {

            @Override
            public Number convertFrom(Number source, Number destination) {
                return source;
            }

            @Override
            public Number convertTo(Number source, Number destination) {
                return source;
            }
        };
        assertEquals(new Integer(1), converter.convert(null, new Integer(1),
                                                       Long.class, Integer.class));
        assertEquals(new Integer(11), converter.convert(null, new Integer(11),
                                                        Object.class, Integer.class));
    }

    @Test
    public void testAssignments2() {
        DozerConverter<String, Number> converter = new DozerConverter<String, Number>(
                String.class, Number.class) {

            @Override
            public String convertFrom(Number source, String destination) {
                return source.toString();
            }

            @Override
            public Number convertTo(String source, Number destination) {
                return Long.parseLong(source);
            }

        };

        assertEquals(new Long(1L), converter.convert(null, new String("1"),
                                                     Long.class, Object.class));
        assertEquals(new String("1"), converter.convert(null, new Integer(1),
                                                        Object.class, Integer.class));
    }

}
