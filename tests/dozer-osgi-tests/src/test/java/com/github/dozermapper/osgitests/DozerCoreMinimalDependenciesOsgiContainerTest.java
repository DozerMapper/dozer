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

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import static org.ops4j.pax.exam.CoreOptions.url;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class DozerCoreMinimalDependenciesOsgiContainerTest extends DozerCoreOsgiContainerTest {

    @Configuration
    @Override
    public Option[] config() {

        return options(
                // Commons
                url("link:classpath:org.apache.commons.beanutils.link"),
                url("link:classpath:org.apache.commons.collections.link"),
                url("link:classpath:org.apache.commons.lang3.link"),
                url("link:classpath:org.apache.commons.io.link"),
                // Core
                url("link:classpath:com.github.dozermapper.dozer-core.link"),
                url("link:classpath:com.github.dozermapper.dozer-schema.link"),
                url("link:classpath:com.github.dozermapper.tests.dozer-osgi-tests-model.link"),
                junitBundles(),
                systemPackage("javax.xml.stream")
        );
    }
}
