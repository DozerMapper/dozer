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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.karaf.container.internal.JavaVersionUtil;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.extra.VMOption;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;

public abstract class PaxExamTestSupport {

    private static final String FRAMEWORK_GROUP_ID = "org.apache.karaf";
    private static final String FRAMEWORK_ARTIFACT_ID = "apache-karaf";

    protected static final Logger LOG = LoggerFactory.getLogger(PaxExamTestSupport.class);

    @Inject
    protected BundleContext bundleContext;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        // Makes sure the generated Test-Bundle contains this import!
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

    protected Option karaf4ContainerConfigOptions() {
        return karafContainerConfigOptions(System.getProperty("karafVersion"));
    }

    protected Option karafContainerConfigOptions(String version) {
        return composite(
                karafDistributionConfiguration()
                    .frameworkUrl(maven().groupId(FRAMEWORK_GROUP_ID)
                                          .artifactId(FRAMEWORK_ARTIFACT_ID)
                                          .version(version)
                                          .type("zip")
                                          .versionAsInProject())
                    .karafVersion(version)
                    .name("Apache Karaf " + version)
                    .unpackDirectory(new File("target/paxexam/unpack"))
                    .useDeployFolder(false),

                logLevel(LogLevelOption.LogLevel.INFO),

                // Keep the folder so we can look inside when something fails
                keepRuntimeFolder(),

                // Disable the SSH port
                configureConsole().ignoreRemoteShell(),

                // Replace karaf logging with our own
                replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", getConfigFile("etc/org.ops4j.pax.logging.cfg")),

                // Disable the Karaf shutdown port
                editConfigurationFilePut("etc/custom.properties", "karaf.shutdown.port", "-1"),

                // Make sure everything is UTF8
                vmOption("-Dfile.encoding=UTF-8"),

                // Get any Java >= 9 options
                getJavaModuleOptions()

                // Suspend startup until debugger attached
                //vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
        );
    }

    private Option getJavaModuleOptions() {
        if (JavaVersionUtil.getMajorVersion() >= 9) {
            return composite(
                    vmOption("--add-reads=java.xml=java.logging"),
                    vmOption("--add-exports=java.base/org.apache.karaf.specs.locator=java.xml,ALL-UNNAMED"),

                    vmOption("--patch-module"),
                    vmOption("java.base=lib/endorsed/org.apache.karaf.specs.locator-" + System.getProperty("karaf.version") + ".jar"),
                    vmOption("--patch-module"),
                    vmOption("java.xml=lib/endorsed/org.apache.karaf.specs.java.xml-" + System.getProperty("karaf.version") + ".jar"),

                    vmOption("--add-opens"),
                    vmOption("java.base/java.security=ALL-UNNAMED"),
                    vmOption("--add-opens"),
                    vmOption("java.base/java.net=ALL-UNNAMED"),
                    vmOption("--add-opens"),
                    vmOption("java.base/java.lang=ALL-UNNAMED"),
                    vmOption("--add-opens"),
                    vmOption("java.base/java.util=ALL-UNNAMED"),
                    vmOption("--add-opens"),
                    vmOption("java.naming/javax.naming.spi=ALL-UNNAMED"),
                    vmOption("--add-opens"),
                    vmOption("java.rmi/sun.rmi.transport.tcp=ALL-UNNAMED"),

                    vmOption("--add-exports=java.base/sun.net.www.protocol.http=ALL-UNNAMED"),
                    vmOption("--add-exports=java.base/sun.net.www.protocol.https=ALL-UNNAMED"),
                    vmOption("--add-exports=java.base/sun.net.www.protocol.jar=ALL-UNNAMED"),
                    vmOption("--add-exports=jdk.naming.rmi/com.sun.jndi.url.rmi=ALL-UNNAMED"),
                    vmOption("-classpath"),
                    vmOption("lib/jdk9plus/*" + File.pathSeparator + "lib/boot/*")
            );
        } else {
            return composite();
        }
    }

    private File getConfigFile(String path) {
        URL res = getClass().getClassLoader().getResource(path);
        if (res == null) {
            throw new RuntimeException("Resource " + path + " not found", new FileNotFoundException("URL is null for " + path));
        }

        return new File(res.getFile());
    }

    protected InputStream getLocalResource(String path) {
        return new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(path));
    }

    protected Bundle getBundle(BundleContext bundleContext, String symbolicName) {
        Bundle answer = null;
        for (Bundle current : bundleContext.getBundles()) {
            if (current.getSymbolicName().startsWith(symbolicName)) {
                answer = current;
                break;
            }
        }

        return answer;
    }
}
