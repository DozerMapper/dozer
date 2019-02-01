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
package com.github.dozermapper.osgitests;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.DozerModule;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.osgi.OSGiClassLoader;
import com.github.dozermapper.osgitests.support.BundleOptions;
import com.github.dozermapper.protobuf.ProtobufSupportModule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import static com.github.dozermapper.osgitests.support.BundleOptions.localBundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackages;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Proto3KarafContainerTest extends CoreKarafContainerTest {

    @Configuration
    public Option[] config() {
        return options(
                // Framework
                karaf4ContainerConfigOptions(),

                // ServiceLoader
                localBundle("org.apache.aries.spifly.dynamic.bundle.link"),

                // Bundles
                BundleOptions.optionalBundles(),
                BundleOptions.coreBundles(),

                // Proto3
                localBundle("com.google.guava.link"),
                localBundle("com.google.protobuf.link"),

                // Proto
                localBundle("com.github.dozermapper.dozer-proto3.link"),
                junitBundles(),
                systemPackages("sun.misc")
        );
    }

    @Test
    @Override
    public void canGetBundleFromDozerCore() {
        super.canGetBundleFromDozerCore();

        Bundle proto = getBundle(bundleContext, "com.github.dozermapper.dozer-proto3");
        assertNotNull(proto);
        assertEquals(Bundle.ACTIVE, proto.getState());
    }

    @Test
    public void canResolveProtobufSupportModule() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withXmlMapping(() -> getLocalResource("mappings/mapping.xml"))
                .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
                .build();

        assertNotNull(mapper);
        assertNotNull(mapper.getMappingMetadata());

        ServiceReference<DozerModule> dozerModuleServiceReference = bundleContext.getServiceReference(DozerModule.class);
        assertNotNull(dozerModuleServiceReference);

        DozerModule dozerModule = bundleContext.getService(dozerModuleServiceReference);
        assertNotNull(dozerModule);
        assertTrue(dozerModule instanceof ProtobufSupportModule);
    }
}
