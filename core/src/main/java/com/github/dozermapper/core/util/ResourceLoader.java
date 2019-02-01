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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

/**
 * Internal class that loads resources from the classpath. Also supports loading
 * resources outside of the classpath if it is prepended with "file:". Only
 * intended for internal use.
 */
public class ResourceLoader {

    private final ClassLoader classLoader;

    public ResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public URL getResource(String resource) {
        resource = resource.trim();

        URL result = Thread.currentThread().getContextClassLoader().getResource(resource);

        // Could not find resource. Try with the classloader that loaded this class.
        if (result == null) {
            ClassLoader classLoader = ResourceLoader.class.getClassLoader();
            if (classLoader != null) {
                result = classLoader.getResource(resource);
            }
        }

        // Last ditch attempt searching classpath
        if (result == null) {
            result = ClassLoader.getSystemResource(resource);
        }

        // one more time
        if (result == null && StringUtils.contains(resource, ":")) {
            try {
                result = new URL(resource);
            } catch (MalformedURLException e) {
                MappingUtils.throwMappingException(e);
            }
        }

        return result;
    }

}
