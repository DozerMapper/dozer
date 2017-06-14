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
package com.github.dozermapper.osgitests.karaf;

import org.ops4j.pax.exam.Option;

import static com.github.dozermapper.osgitests.support.OptionsSupport.localBundle;
import static org.ops4j.pax.exam.CoreOptions.composite;

public final class BundleOptions {

    public static Option coreBundles() {
        return composite(
                // Commons
                localBundle("org.apache.commons.beanutils.link"),
                localBundle("org.apache.commons.collections.link"),
                localBundle("org.apache.commons.lang3.link"),
                localBundle("org.apache.commons.io.link"),
                // Core
                localBundle("com.github.dozermapper.dozer-core.link"),
                localBundle("com.github.dozermapper.tests.dozer-osgi-tests-model.link"),
                // Objenesis
                localBundle("org.objenesis.link")
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
                localBundle("com.sun.el.javax.el.link")
        );
    }
}
