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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.DozerConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperAware;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CustomConverterMapperAwareTest extends AbstractFunctionalTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mapper = getMapper("mappings/customConverterMapperAware.xml");
    }

    @Test
    public void test_convert_withInjectedMapper() {
        List<BeanA> list = new ArrayList<>();
        BeanA b1 = new BeanA("1");
        BeanA b2 = new BeanA("2");
        BeanA b3 = new BeanA("3");

        list.add(b1);
        list.add(b2);
        list.add(b3);

        Map map = mapper.map(list, HashMap.class);

        assertNotNull(map);
        assertEquals(3, map.size());
        assertTrue(map.keySet().contains(b1));
        assertTrue(map.keySet().contains(b2));
        assertTrue(map.keySet().contains(b3));
        assertEquals(b1.getA(), ((BeanB)map.get(b1)).getA().toString());
        assertEquals(b2.getA(), ((BeanB)map.get(b2)).getA().toString());
        assertEquals(b3.getA(), ((BeanB)map.get(b3)).getA().toString());
    }

    @Test
    public void test_convert_withSubclassedConverterInstance() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/customConverterMapperAware.xml")
                .withCustomConverter(new Converter() {
                    @Override
                    public Map convertTo(List source, Map destination) {
                        return new HashMap() {{
                                put("foo", "bar");
                            }
                        };
                    }
                })
                .build();
        HashMap result = mapper.map(new ArrayList<String>(), HashMap.class);
        assertEquals("bar", result.get("foo"));
    }

    @Test
    public void test_stackOverflow() {
        BeanA a = new BeanA();
        BeanB b = new BeanB();
        a.setBeanB(b);
        b.setBeanA(a);

        Container container = new Container();
        container.setBeanA(a);
        container.setBeanB(b);

        Container result = mapper.map(container, Container.class);

        assertNotNull(result);
        assertNotNull(result.getBeanA());
        assertNotNull(result.getBeanB());
    }

    public static class Converter extends DozerConverter<List, Map> implements MapperAware {

        private Mapper mapper;

        public Converter() {
            super(List.class, Map.class);
        }

        public Map convertTo(List source, Map destination) {
            Map result = new HashMap();
            for (Object item : source) {
                BeanB mappedItem = mapper.map(item, BeanB.class);
                result.put(item, mappedItem);
            }
            return result;
        }

        public List convertFrom(Map source, List destination) {
            return destination;
        }

        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }

    }

    public static class ConverterRecursion extends DozerConverter<BeanA, BeanA> implements MapperAware {

        private Mapper mapper;

        public ConverterRecursion() {
            super(BeanA.class, BeanA.class);
        }

        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }

        public BeanA convertTo(BeanA source, BeanA destination) {
            return mapper.map(source, BeanA.class);
        }

        public BeanA convertFrom(BeanA source, BeanA destination) {
            return mapper.map(source, BeanA.class);
        }
    }

    public static class Container {
        BeanA beanA;
        BeanB beanB;

        public BeanA getBeanA() {
            return beanA;
        }

        public void setBeanA(BeanA beanA) {
            this.beanA = beanA;
        }

        public BeanB getBeanB() {
            return beanB;
        }

        public void setBeanB(BeanB beanB) {
            this.beanB = beanB;
        }
    }

    public static class BeanA {

        public BeanA() {
        }

        public BeanA(String a) {
            this.a = a;
        }

        private BeanB beanB;
        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public BeanB getBeanB() {
            return beanB;
        }

        public void setBeanB(BeanB beanB) {
            this.beanB = beanB;
        }
    }

    public static class BeanB {

        private BeanA beanA;
        private Integer a;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public BeanA getBeanA() {
            return beanA;
        }

        public void setBeanA(BeanA beanA) {
            this.beanA = beanA;
        }
    }

}
