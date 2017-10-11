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

import com.github.dozermapper.osgitests.support.OsgiTestSupport;
import com.github.dozermapper.osgitestsmodel.Person;
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.DozerModule;
import org.dozer.Mapper;
import org.dozer.osgi.Activator;
import org.dozer.osgi.OSGiClassLoader;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.InputStream;

import static com.github.dozermapper.osgitests.support.OptionsSupport.localBundle;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

public abstract class AbstractDozerCoreOsgiContainerTest extends OsgiTestSupport {

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] config() {

        return options(
                // Framework
                containerConfigOptions(),
                // Commons
                localBundle("org.apache.commons.beanutils.link"),
                localBundle("org.apache.commons.collections.link"),
                localBundle("org.apache.commons.lang3.link"),
                localBundle("org.apache.commons.io.link"),
                // JAXB
                localBundle("org.apache.geronimo.specs.geronimo-stax-api_1.0_spec.link"),
                localBundle("org.apache.geronimo.specs.geronimo-activation_1.1_spec.link"),
                localBundle("org.apache.servicemix.specs.jaxb-api-2.2.link"),
                localBundle("org.apache.servicemix.bundles.jaxb-impl.link"),
                // Jackson
                localBundle("com.fasterxml.jackson.core.jackson-annotations.link"),
                localBundle("com.fasterxml.jackson.core.jackson-core.link"),
                localBundle("com.fasterxml.jackson.core.jackson-databind.link"),
                localBundle("com.fasterxml.jackson.dataformat.jackson-dataformat-xml.link"),
                localBundle("com.fasterxml.jackson.dataformat.jackson-dataformat-yaml.link"),
                localBundle("com.fasterxml.jackson.module.jackson-module-jaxb-annotations.link"),
                localBundle("com.fasterxml.woodstox.woodstox-core.link"),
                localBundle("stax2-api.link"),
                localBundle("org.yaml.snakeyaml.link"),
                // Javassist
                localBundle("javassist.link"),
                // Optional;
                optionalBundles(),
                // Core
                localBundle("com.github.dozermapper.dozer-core.link"),
                localBundle("com.github.dozermapper.dozer-core.link"),
                localBundle("com.github.dozermapper.dozer-schema.link"),
                localBundle("com.github.dozermapper.tests.dozer-osgi-tests-model.link"),
                junitBundles()
        );
    }

    protected abstract Option containerConfigOptions();

    protected Option optionalBundles() {
        return composite();
    }

    @Test
    public void canGetBundleFromDozerCore() {
        assertNotNull(bundleContext);
        assertNotNull(Activator.getContext());
        assertNotNull(Activator.getBundle());

        Bundle core = getBundle(bundleContext, "com.github.dozermapper.dozer-core");
        assertNotNull(core);
        assertEquals(Bundle.ACTIVE, core.getState());

        assertNull(bundleContext.getServiceReference(DozerModule.class));
    }

    @Test
    public void canConstructDozerBeanMapper() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withXmlMapping(() -> getLocalResource("mappings/mapping.xml"))
                .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
                .build();

        assertNotNull(mapper);
    }

    @Test
    public void canMapUsingXML() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withXmlMapping(() -> getLocalResource("mappings/mapping.xml"))
                .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
                .build();

        Person answer = mapper.map(new Person("bob"), Person.class);

        assertNotNull(answer);
        assertNotNull(answer.getName());
        assertEquals("bob", answer.getName());
    }

    InputStream getLocalResource(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

}
