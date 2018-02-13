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

import java.io.File;

import org.ops4j.pax.exam.Option;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.doNotModifyLogConfiguration;

public final class KarafOptions {

    private static final String FRAMEWORK_GROUP_ID = "org.apache.karaf";
    private static final String FRAMEWORK_ARTIFACT_ID = "apache-karaf";

    public static Option karaf4ContainerConfigOptions() {
        return karafContainerConfigOptions("4.1.2");
    }

    public static Option karaf2ContainerConfigOptions() {
        return karafContainerConfigOptions("2.4.4");
    }

    private static Option karafContainerConfigOptions(String version) {
        return composite(
                karafDistributionConfiguration()
                        .frameworkUrl(
                                maven().groupId(FRAMEWORK_GROUP_ID)
                                        .artifactId(FRAMEWORK_ARTIFACT_ID)
                                        .version(version)
                                        .type("zip")
                        )
                        .karafVersion(version)
                        .name("Apache Karaf " + version)
                        .unpackDirectory(new File("target/paxexam/unpack"))
                        .useDeployFolder(false),
                doNotModifyLogConfiguration()
        );
    }
}
