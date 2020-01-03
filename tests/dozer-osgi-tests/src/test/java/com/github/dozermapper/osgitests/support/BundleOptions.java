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
package com.github.dozermapper.osgitests.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.UrlProvisionOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.url;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

public final class BundleOptions {

    public static Option coreBundles() {
        return composite(
                // Commons
                localBundle("org.apache.commons.commons-beanutils.link"),
                localBundle("org.apache.commons.collections.link"),
                localBundle("org.apache.commons.lang3.link"),
                localBundle("org.apache.commons.io.link"),

                // Objenesis
                localBundle("org.objenesis.link"),

                // Core
                localBundle("com.github.dozermapper.dozer-core.link"),
                localBundle("com.github.dozermapper.tests.dozer-osgi-tests-model.link")
        );
    }

    public static Option optionalBundles() {
        return composite(
                // Optional; Jackson
                localBundle("com.fasterxml.jackson.core.jackson-annotations.link"),
                localBundle("com.fasterxml.jackson.core.jackson-core.link"),
                localBundle("com.fasterxml.jackson.core.jackson-databind.link"),
                localBundle("com.fasterxml.jackson.dataformat.jackson-dataformat-yaml.link"),
                localBundle("woodstox-core-asl.link"),
                localBundle("stax2-api.link"),
                localBundle("org.yaml.snakeyaml.link"),

                // Optional; Javassist
                localBundle("javassist.link"),

                // Optional; EL
                localBundle("javax.el-api.link"),
                localBundle("com.sun.el.javax.el.link"),

                // Optional; Hibernate
                features(maven().groupId("org.apache.karaf.features")
                                 .artifactId("enterprise")
                                 .type("xml")
                                 .classifier("features")
                                 .versionAsInProject(), "hibernate"),

                wrappedBundle(mavenBundle().groupId("org.apache.felix")
                                            .artifactId("org.apache.felix.utils")
                                            .versionAsInProject())
        );
    }

    public static UrlProvisionOption localBundle(String link) {
        try (InputStream resourceAsStream = BundleOptions.class.getClassLoader().getResourceAsStream(link)) {
            assertNotNull("Invalid link file was provided: " + link, resourceAsStream);

            List<String> urls = IOUtils.readLines(resourceAsStream, Charset.forName("UTF-8"));

            assertEquals("Invalid link file was provided: " + link, 1, urls.size());

            return url(urls.get(0));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
