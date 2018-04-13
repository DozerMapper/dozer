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
package com.github.dozermapper.osgitests;

import javax.inject.Inject;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.osgi.Activator;
import com.github.dozermapper.core.osgi.OSGiClassLoader;
import com.github.dozermapper.osgitests.karaf.BundleOptions;
import com.github.dozermapper.osgitests.support.OsgiTestSupport;
import com.github.dozermapper.osgitestsmodel.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

public abstract class AbstractDozerCoreOsgiContainerTest extends OsgiTestSupport {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractDozerCoreOsgiContainerTest.class);

    @Inject
    protected BundleContext bundleContext;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        // makes sure the generated Test-Bundle contains this import!
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*");
        return probe;
    }

    @Before
    public void setUp() {
        LOG.info("setUp() using BundleContext: {}", bundleContext);
    }

    @After
    public void tearDown() {
        LOG.info("tearDown()");
    }

    @Configuration
    public Option[] config() {
        return options(
                // Framework
                containerConfigOptions(),
                // Bundles
                optionalBundles(),
                BundleOptions.coreBundles(),
                junitBundles()
        );
    }

    protected abstract Option containerConfigOptions();

    protected Option optionalBundles() {
        return BundleOptions.optionalBundles();
    }

    @Test
    public void canGetBundleFromDozerCore() {
        assertNotNull(bundleContext);
        assertNotNull(Activator.getContext());
        assertNotNull(Activator.getBundle());

        Bundle core = getBundle(bundleContext, "com.github.dozermapper.dozer-core");
        assertNotNull(core);
        assertEquals(Bundle.ACTIVE, core.getState());

        for (Bundle current : bundleContext.getBundles()) {
            //Ignore any Karaf bundles
            if (current.getSymbolicName().startsWith("org.apache.karaf")
                || current.getSymbolicName().startsWith("org.jline")) {
                continue;
            }

            assertEquals(current.getSymbolicName(), Bundle.ACTIVE, current.getState());
        }
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
}
