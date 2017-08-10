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
package com.github.dozermapper.osgitests;

import javax.inject.Inject;

import com.github.dozermapper.osgitestsmodel.Person;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.osgi.Activator;
import org.dozer.osgi.OSGiClassLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackages;
import static org.ops4j.pax.exam.CoreOptions.url;

/**
 * @author Dmitry Buzdin
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OsgiContainerTest {

    @Inject
    private BundleContext context;

    @Configuration
    public Option[] config() {

        return options(
            url("link:classpath:com.github.dozermapper.dozer-core.link"),
            url("link:classpath:com.github.dozermapper.dozer-schema.link"),
            url("link:classpath:com.github.dozermapper.tests.dozer-osgi-tests-model.link"),
            url("link:classpath:org.apache.commons.beanutils.link"),
            url("link:classpath:org.apache.commons.collections.link"),
            url("link:classpath:org.apache.commons.lang3.link"),
            url("link:classpath:org.apache.commons.io.link"),
            url("link:classpath:com.fasterxml.jackson.core.jackson-annotations.link"),
            url("link:classpath:com.fasterxml.jackson.core.jackson-core.link"),
            url("link:classpath:com.fasterxml.jackson.core.jackson-databind.link"),
            url("link:classpath:com.fasterxml.jackson.dataformat.jackson-dataformat-xml.link"),
            url("link:classpath:com.fasterxml.jackson.dataformat.jackson-dataformat-yaml.link"),
            url("link:classpath:com.fasterxml.jackson.module.jackson-module-jaxb-annotations.link"),
            url("link:classpath:com.fasterxml.woodstox.woodstox-core.link"),
            url("link:classpath:stax2-api.link"),
            url("link:classpath:org.yaml.snakeyaml.link"),
            junitBundles(),
            systemPackages("javax.net.ssl", "javax.xml.parsers", "javax.management", "javax.xml.datatype",
                           "org.w3c.dom", "org.xml.sax", "org.xml.sax.helpers")
        );
    }

    @Test
    public void canGetBundleFromDozerCore() {
        assertNotNull(Activator.getContext());
        assertNotNull(Activator.getBundle());
    }

    @Test
    public void canConstructDozerBeanMapper() {
        Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles("mappings/mapping.xml")
            .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
            .build();

        assertNotNull(mapper);
    }

    @Test
    public void canMap() {
        Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles("mappings/mapping.xml")
            .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
            .build();

        Person answer = mapper.map(new Person("bob"), Person.class);

        assertNotNull(answer);
        assertNotNull(answer.getName());
        assertEquals("bob", answer.getName());
    }

    @Test
    public void canMapUsingXML() {
        Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles("mappings/mapping.xml")
            .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
            .build();

        Person answer = mapper.map(new Person("bob"), Person.class);

        assertNotNull(answer);
        assertNotNull(answer.getName());
        assertEquals("bob", answer.getName());
    }
}
