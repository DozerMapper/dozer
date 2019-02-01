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

import java.net.URL;

import org.apache.commons.lang3.ClassUtils;

public class DefaultClassLoader implements DozerClassLoader {

    private final ClassLoader classLoader;
    private final ResourceLoader resourceLoader;

    public DefaultClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.resourceLoader = new ResourceLoader(classLoader);
    }

    public Class<?> loadClass(String className) {
        Class<?> result = null;
        try {
            result = ClassUtils.getClass(classLoader, className);
        } catch (ClassNotFoundException e) {
            try {
                result = ClassUtils.getClass(Thread.currentThread().getContextClassLoader(), className);
            } catch (ClassNotFoundException cnfe) {
                MappingUtils.throwMappingException(e);
            }
        }
        return result;
    }

    public URL loadResource(String uri) {
        return resourceLoader.getResource(uri);
    }

}
