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

import com.github.dozermapper.osgitests.support.OsgiTestSupport;
import com.github.dozermapper.protobuf.ProtobufSupportModule;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.DozerModule;
import org.dozer.Mapper;
import org.dozer.osgi.Activator;
import org.dozer.osgi.OSGiClassLoader;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackages;
import static org.ops4j.pax.exam.CoreOptions.url;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class DozerProtoOsgiContainerTest extends OsgiTestSupport {

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] config() {

        return options(
                // ServiceLoader
                url("link:classpath:org.apache.aries.spifly.dynamic.bundle.link"),
                url("link:classpath:org.apache.aries.util.link"),
                url("link:classpath:org.objectweb.asm.all.debug.link"),
                // Commons
                url("link:classpath:org.apache.commons.beanutils.link"),
                url("link:classpath:org.apache.commons.collections.link"),
                url("link:classpath:org.apache.commons.lang3.link"),
                url("link:classpath:org.apache.commons.io.link"),
                // Jackson
                url("link:classpath:com.fasterxml.jackson.core.jackson-annotations.link"),
                url("link:classpath:com.fasterxml.jackson.core.jackson-core.link"),
                url("link:classpath:com.fasterxml.jackson.core.jackson-databind.link"),
                url("link:classpath:com.fasterxml.jackson.dataformat.jackson-dataformat-xml.link"),
                url("link:classpath:com.fasterxml.jackson.dataformat.jackson-dataformat-yaml.link"),
                url("link:classpath:com.fasterxml.jackson.module.jackson-module-jaxb-annotations.link"),
                url("link:classpath:com.fasterxml.woodstox.woodstox-core.link"),
                url("link:classpath:stax2-api.link"),
                url("link:classpath:org.yaml.snakeyaml.link"),
                // Optional; Javassist
                url("link:classpath:javassist.link"),
                // Optional; EL
                url("link:classpath:javax.el-api.link"),
                url("link:classpath:com.sun.el.javax.el.link"),
                // Proto3
                url("link:classpath:com.google.guava.link"),
                url("link:classpath:com.google.protobuf.link"),
                // Core
                url("link:classpath:com.github.dozermapper.dozer-core.link"),
                url("link:classpath:com.github.dozermapper.dozer-schema.link"),
                url("link:classpath:com.github.dozermapper.tests.dozer-osgi-tests-model.link"),
                // Proto
                url("link:classpath:com.github.dozermapper.dozer-proto.link"),
                junitBundles(),
                systemPackages("sun.misc")
        );
    }

    @Test
    public void canGetBundleFromDozerCore() {
        assertNotNull(bundleContext);
        assertNotNull(Activator.getContext());
        assertNotNull(Activator.getBundle());

        Bundle core = getBundle(bundleContext, "com.github.dozermapper.dozer-core");
        assertNotNull(core);
        assertEquals(Bundle.ACTIVE, core.getState());

        Bundle proto = getBundle(bundleContext, "com.github.dozermapper.dozer-proto");
        assertNotNull(proto);
        assertEquals(Bundle.ACTIVE, proto.getState());

        for (Bundle current : bundleContext.getBundles()) {
            assertEquals(current.getSymbolicName(), Bundle.ACTIVE, current.getState());
        }
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
    public void canResolveProtobufSupportModule() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/mapping.xml")
                .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
                .build();

        assertNotNull(mapper);

        ServiceReference<DozerModule> dozerModuleServiceReference = bundleContext.getServiceReference(DozerModule.class);
        assertNotNull(dozerModuleServiceReference);

        DozerModule dozerModule = bundleContext.getService(dozerModuleServiceReference);
        assertNotNull(dozerModule);
        assertTrue(dozerModule instanceof ProtobufSupportModule);
    }
}
