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
package com.github.dozermapper.core.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Activator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    private static BundleContext context;
    private static Bundle bundle;

    public static BundleContext getContext() {
        return context;
    }

    private static void setContext(BundleContext context) {
        Activator.context = context;
    }

    public static Bundle getBundle() {
        return bundle;
    }

    private static void setBundle(Bundle bundle) {
        Activator.bundle = bundle;
    }

    @Override
    public void start(BundleContext context) {
        LOG.info("Starting Dozer OSGi bundle");

        setContext(context);
        setBundle(context.getBundle());
    }

    @Override
    public void stop(BundleContext bundleContext) {
        setContext(null);
        setBundle(null);

        LOG.info("Dozer OSGi bundle stopped");
    }
}
