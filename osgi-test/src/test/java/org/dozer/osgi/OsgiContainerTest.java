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
package org.dozer.osgi;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackages;
import static org.ops4j.pax.exam.CoreOptions.url;

/**
 * @author Dmitry Buzdin
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OsgiContainerTest {

    @Configuration
    public Option[] config() {

        return options(
            url("link:classpath:com.github.dozermapper.dozer-core.link"),
            url("link:classpath:com.github.dozermapper.dozer-schema.link"),
            url("link:classpath:org.apache.commons.beanutils.link"),
            url("link:classpath:org.apache.commons.collections.link"),
            url("link:classpath:org.apache.commons.lang3.link"),
            junitBundles(),
            systemPackages("javax.net.ssl", "javax.xml.parsers", "javax.management", "javax.xml.datatype",
                           "org.w3c.dom", "org.xml.sax", "org.xml.sax.helpers")
        );
    }

    @Test
    public void shouldInstantiate() {
        Mapper beanMapper = DozerBeanMapperBuilder.buildDefault();
        assertThat(beanMapper, notNullValue());
    }

    @Test
    public void shouldMap() {
        Mapper beanMapper = DozerBeanMapperBuilder.buildDefault();
        Object result = beanMapper.map(new Object(), Object.class);
        assertThat(result, notNullValue());
    }

    @Test
    public void shouldLoadMappingFile() {
        Mapper beanMapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/mapping.xml")
                .build();
        Object result = beanMapper.map(new Object(), Object.class);
        assertThat(result, notNullValue());
    }
}
