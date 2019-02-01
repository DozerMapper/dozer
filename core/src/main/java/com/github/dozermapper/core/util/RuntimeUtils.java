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
package com.github.dozermapper.core.util;

import com.github.dozermapper.core.osgi.Activator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to provide various information about runtime in which Dozer is executed.
 */
public final class RuntimeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeUtils.class);

    private static Boolean isOSGi;

    private RuntimeUtils() {

    }

    /**
     * Returns {@code true} if Dozer is executed in OSGi container.
     *
     * @return if Dozer is in OSGi container.
     */
    public static boolean isOSGi() {
        if (isOSGi == null) {
            try {
                // first verify that framework classes are available (otherwise loading of Activator will fail)
                Class.forName("org.osgi.framework.Bundle");

                // then verify that bundle is started (client code could add framework classes into classpath)
                isOSGi = Activator.getBundle() != null;
            } catch (ClassNotFoundException e) {
                isOSGi = Boolean.FALSE;
            }

            LOG.info("OSGi support is {}", isOSGi);
        }

        return isOSGi;
    }
}
