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
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.api.TypeMappingOptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class CaseInsensitiveWildcardMappingTest extends AbstractFunctionalTest {

    @Test
    public void testDefaultMappingIsCaseSensitive() {
        A a = newInstance(A.class);

        a.setCamelCaseValue("test");

        A selfResult = mapper.map(a, A.class);
        B result = mapper.map(a, B.class);

        assertThat(selfResult, notNullValue());
        assertThat(selfResult.getCamelCaseValue(), equalTo("test"));
        assertThat(result, notNullValue());
        assertThat(result.getCamelcasevalue(), nullValue());
    }

    @Test
    public void testCaseInsensitiveGlobalMappingEnabled() {
        mapper = getMapper("mappings/caseInsensitiveWildcardMappingGlobal.xml");

        A a = newInstance(A.class);

        a.setCamelCaseValue("test");
        B result = mapper.map(a, B.class);

        assertThat(result, notNullValue());
        assertThat(result.getCamelcasevalue(), equalTo("test"));
    }

    @Test
    public void testCaseInsensitiveClassLevelMappingEnabled() {
        mapper = getMapper("mappings/caseInsensitiveWildcardMappingClassLevel.xml");

        A a = newInstance(A.class);

        a.setCamelCaseValue("test");
        B result1 = mapper.map(a, B.class);
        CNoMappingConfigured result2 = mapper.map(a, CNoMappingConfigured.class);

        assertThat(result1, notNullValue());
        assertThat(result1.getCamelcasevalue(), equalTo("test"));
        assertThat(result2, notNullValue());
        assertThat(result2.getCamelcasevalue(), nullValue());
    }

    @Test
    public void testCaseInsensitiveBeanMapperBuilder() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(type(A.class),
                                type(B.class),
                                TypeMappingOptions.wildcardCaseInsensitive(true)
                        );
                    }
                })
                .build();

        A a = newInstance(A.class);

        a.setCamelCaseValue("test");
        B result = mapper.map(a, B.class);

        assertThat(result, notNullValue());
        assertThat(result.getCamelcasevalue(), equalTo("test"));
    }

    @Test
    public void testCaseInsensitiveWithIsAccessible() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(type(A.class),
                                type(DNoSetter.class).accessible(),
                                TypeMappingOptions.wildcardCaseInsensitive(true)
                        );
                    }
                })
                .build();

        A a = newInstance(A.class);

        a.setCamelCaseValue("test");
        DNoSetter result = mapper.map(a, DNoSetter.class);

        assertThat(result, notNullValue());
        assertThat(result.getCamelcasevalue(), equalTo("test"));
    }

    public static class A {
        private String camelCaseValue;

        public String getCamelCaseValue() {
            return camelCaseValue;
        }

        public void setCamelCaseValue(String camelCaseValue) {
            this.camelCaseValue = camelCaseValue;
        }
    }

    public static class B {
        private String camelcasevalue;

        public String getCamelcasevalue() {
            return camelcasevalue;
        }

        public void setCamelcasevalue(String camelcasevalue) {
            this.camelcasevalue = camelcasevalue;
        }
    }

    public static class CNoMappingConfigured {
        private String camelcasevalue;

        public String getCamelcasevalue() {
            return camelcasevalue;
        }

        public void setCamelcasevalue(String camelcasevalue) {
            this.camelcasevalue = camelcasevalue;
        }
    }

    public static class DNoSetter {
        private String camelcasevalue;

        public String getCamelcasevalue() {
            return camelcasevalue;
        }
    }
}
