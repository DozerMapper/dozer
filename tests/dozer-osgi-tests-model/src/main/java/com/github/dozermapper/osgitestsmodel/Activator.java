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
package com.github.dozermapper.osgitestsmodel;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private static BundleContext bundleContext;
    private static Bundle bundle;

    public static BundleContext getBundleContext() {
        return bundleContext;
    }

    private static void setBundleContext(BundleContext context) {
        Activator.bundleContext = context;
    }

    public static Bundle getBundle() {
        return bundle;
    }

    private static void setBundle(Bundle bundle) {
        Activator.bundle = bundle;
    }

    public void start(BundleContext context) throws Exception {
        setBundleContext(context);
        setBundle(context.getBundle());
    }

    public void stop(BundleContext context) throws Exception {
        setBundleContext(null);
        setBundle(null);
    }
}
