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

import java.util.Dictionary;
import java.util.List;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.osgi.Activator;
import com.github.dozermapper.core.osgi.OSGiClassLoader;
import com.github.dozermapper.osgitests.support.PaxExamTestSupport;
import com.github.dozermapper.osgitestsmodel.Person;

import org.apache.felix.utils.manifest.Clause;
import org.apache.felix.utils.manifest.Parser;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractDozerCoreOsgiContainerTest extends PaxExamTestSupport {

    @Test
    public void canGetBundleFromDozerCore() {
        assertNotNull(bundleContext);
        assertNotNull(Activator.getContext());
        assertNotNull(Activator.getBundle());

        Bundle core = getBundle(bundleContext, "com.github.dozermapper.dozer-core");
        assertNotNull(core);
        assertEquals(Bundle.ACTIVE, core.getState());

        for (Bundle current : bundleContext.getBundles()) {
            LOG.info("Bundle: {}", current.getSymbolicName());

            //Ignore any Karaf bundles
            if (current.getSymbolicName().startsWith("org.apache.karaf")
                    || current.getSymbolicName().startsWith("org.apache.aries.blueprint.core.compatibility")
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
        assertNotNull(mapper.getMappingMetadata());
    }

    @Test
    public void canMapUsingXML() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withXmlMapping(() -> getLocalResource("mappings/mapping.xml"))
                .withClassLoader(new OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext()))
                .build();

        assertNotNull(mapper);
        assertNotNull(mapper.getMappingMetadata());

        Person answer = mapper.map(new Person("bob"), Person.class);

        assertNotNull(answer);
        assertNotNull(answer.getName());
        assertEquals("bob", answer.getName());
    }

    @Test
    public void canResolveAllImports() {
        Bundle core = getBundle(bundleContext, "com.github.dozermapper.dozer-core");
        assertNotNull(core);
        assertEquals(Bundle.ACTIVE, core.getState());

        Dictionary<String, String> headers = core.getHeaders();
        assertNotNull(headers);

        Clause[] clauses = Parser.parseHeader(headers.get("Import-Package"));
        for (Clause clause : clauses) {
            LOG.info("Package: {}", clause.getName());

            assertTrue(checkPackage(clause.getName()));
        }
    }

    private boolean checkPackage(String packageName) {
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; (bundles != null) && (i < bundles.length); i++) {
            BundleWiring wiring = bundles[i].adapt(BundleWiring.class);
            List<BundleCapability> caps = wiring == null ? null : wiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
            if (caps != null) {
                for (BundleCapability cap : caps) {
                    String n = getAttribute(cap, BundleRevision.PACKAGE_NAMESPACE);
                    if (packageName.equals(n)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String getAttribute(BundleCapability cap, String name)  {
        Object obj = cap.getAttributes().get(name);
        return obj != null ? obj.toString() : null;
    }
}
