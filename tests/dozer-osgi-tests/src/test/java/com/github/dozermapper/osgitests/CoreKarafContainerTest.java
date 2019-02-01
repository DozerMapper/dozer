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
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.el.ELExpressionFactory;
import com.github.dozermapper.core.osgi.OSGiClassLoader;
import com.github.dozermapper.osgitestsmodel.Person;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static com.github.dozermapper.osgitests.support.BundleOptions.coreBundles;
import static com.github.dozermapper.osgitests.support.BundleOptions.optionalBundles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CoreKarafContainerTest extends AbstractDozerCoreOsgiContainerTest {

    @Configuration
    public Option[] config() {
        return options(
                // Framework
                karaf4ContainerConfigOptions(),

                // Bundles
                optionalBundles(),
                coreBundles(),
                junitBundles()
        );
    }

    @Test
    public void canMapUsingXMLWithVariables() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withXmlMapping(() -> getLocalResource("mappings/mapping-with-el.xml"))
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
    public void elSupported() {
        assertTrue(ELExpressionFactory.isSupported());
    }
}
