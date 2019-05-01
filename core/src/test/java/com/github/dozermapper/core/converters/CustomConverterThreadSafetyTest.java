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
package com.github.dozermapper.core.converters;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.DozerConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import org.junit.Before;
import org.junit.Test;

import static com.github.dozermapper.core.loader.api.FieldsMappingOptions.customConverter;
import static org.junit.Assert.assertEquals;

public class CustomConverterThreadSafetyTest extends AbstractDozerTest {

    private Mapper mapper;

    @Before
    public void semtUp() {
        BeanMappingBuilder builder = new BeanMappingBuilder() {
            protected void configure() {
                mapping(Bean.class, Bean.class)
                        .fields("property1", "property1",
                                customConverter(ConstantConverter.class, "param1")
                        )
                        .fields("property2", "property2",
                                customConverter(ConstantConverter.class, "param2")
                        );
            }
        };

        mapper = DozerBeanMapperBuilder.create()
                .withCustomConverter(new ConstantConverter())
                .withMappingBuilder(builder)
                .build();
    }

    @Test
    public void testParameterThreadSafety() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        final Bean input = new Bean();
        executorService.submit(() -> Stream.generate(() -> mapper.map(input, Bean.class)).limit(100).parallel())
                .get()
                .forEach(output -> {
                    assertEquals("param1", output.property1);
                    assertEquals("param2", output.property2);
                });
    }

    private static class Bean {

        private String property1;
        private String property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }

    }

    private static class ConstantConverter extends DozerConverter<String, String> {


        ConstantConverter() {
            super(String.class, String.class);
        }

        @Override
        public String convertTo(String s, String s2) {
            return getParameter();
        }

        @Override
        public String convertFrom(String s, String s2) {
            return getParameter();
        }

    }
}