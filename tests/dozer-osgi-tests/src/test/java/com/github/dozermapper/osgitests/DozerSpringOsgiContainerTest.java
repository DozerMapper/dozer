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

import com.github.dozermapper.osgitests.karaf.BundleOptions;
import com.github.dozermapper.osgitests.karaf.KarafOptions;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.osgi.OSGiClassLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import static com.github.dozermapper.osgitests.support.OptionsSupport.localBundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class DozerSpringOsgiContainerTest extends DozerCoreOsgiContainerTest {

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] config() {
        return options(
                // Framework
                containerConfigOptions(),
                // Bundles
                BundleOptions.optionalBundles(),
                BundleOptions.coreBundles(),
                // Spring
                localBundle("org.apache.servicemix.bundles.spring-beans.link"),
                localBundle("org.apache.servicemix.bundles.spring-context.link"),
                localBundle("org.apache.servicemix.bundles.spring-core.link"),
                // Spring4
                localBundle("com.github.dozermapper.dozer-spring4.link"),
                junitBundles()
        );
    }

    @Override
    protected Option containerConfigOptions() {
        return KarafOptions.karaf4ContainerConfigOptions();
    }

    @Test
    @Override
    public void canGetBundleFromDozerCore() {
        super.canGetBundleFromDozerCore();

        Bundle spring = getBundle(bundleContext, "com.github.dozermapper.dozer-spring4");
        assertNotNull(spring);
        assertEquals(Bundle.ACTIVE, spring.getState());
    }

    @Test
    public void canConstructDozerBeanMapper() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withXmlMapping(() -> getLocalResource("mappings/mapping.xml"))
                .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
                .build();

        assertNotNull(mapper);
        assertNotNull(mapper.getMappingMetadata());
    }
}
