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

import java.net.URL;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.util.DozerClassLoader;

import org.osgi.framework.BundleContext;

public final class OSGiClassLoader implements DozerClassLoader {

    private final BundleContext context;

    public OSGiClassLoader(BundleContext context) {
        this.context = context;
    }

    @Override
    public Class<?> loadClass(String className) {
        try {
            return context.getBundle().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public URL loadResource(String uri) {
        return context.getBundle().getResource(uri);
    }

}
