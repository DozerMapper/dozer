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
package com.github.dozermapper.core.functional_tests.builder;

import java.util.HashMap;
import java.util.Map;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.vo.SimpleObj;
import com.github.dozermapper.core.vo.SimpleObjPrime;
import com.github.dozermapper.core.vo.deep2.Dest;
import com.github.dozermapper.core.vo.deep2.NestedDest;
import com.github.dozermapper.core.vo.deep2.NestedNestedDest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleTest {

    @Test
    public void shouldPerformSimpleMapping() {
        Mapper beanMapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(
                                type(Source.class),
                                type(Destination.class)
                        )
                                .fields(
                                        field("stringValue").accessible(true),
                                        field("destStringValue")
                                );
                    }
                })
                .build();
        Source source = new Source();
        source.setStringValue("A");

        Destination result = beanMapper.map(source, Destination.class);
        assertEquals("A", result.getDestStringValue());
    }

    @Test
    public void shouldPerformMapBasedMapping() {
        Mapper beanMapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(
                                Source.class,
                                Map.class
                        ).fields(
                                field("stringValue").accessible(true),
                                this_().mapKey("key").mapMethods("get", "put")
                        );
                    }
                })
                .build();
        Source source = new Source();
        source.setStringValue("A");

        Map result = beanMapper.map(source, HashMap.class);
        assertEquals("A", result.get("key"));
    }

    @Test
    public void shouldPerformMapWithLineBreaksInField() {
        Mapper beanMapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/fieldWithLineBreaks.xml")
                .build();
        SimpleObj simpleObj = new SimpleObj();
        simpleObj.setField1("Field1 Value");

        Dest dest = new Dest();
        NestedDest nestedDest = new NestedDest();
        NestedNestedDest nestedNestedDest = new NestedNestedDest();
        nestedNestedDest.setNestedNestedDestField("Nested Field Value");
        nestedDest.setNestedDestField(nestedNestedDest);
        dest.setDestField(nestedDest);

        SimpleObjPrime simpleObjPrime = beanMapper.map(simpleObj, SimpleObjPrime.class);
        assertEquals("Field1 Value", simpleObjPrime.getField1());

        NestedNestedDest result = beanMapper.map(dest, NestedNestedDest.class);
        assertEquals("Nested Field Value", result.getNestedNestedDestField());
    }

    public static class Source {
        private String stringValue;

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }

    public static class Destination {
        private String destStringValue;

        public String getDestStringValue() {
            return destStringValue;
        }

        public void setDestStringValue(String destStringValue) {
            this.destStringValue = destStringValue;
        }
    }
}
