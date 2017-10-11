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
package com.github.dozermapper.osgitests.support;

import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public abstract class OsgiTestSupport {

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

    protected InputStream getLocalResource(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
}
