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

/**
 * Default implementation. Supports only simple proxy cases of Cglib and Javassist.
 * For more complicated scenarios consider using framework specific ProxyResolver.
 */
public class DefaultProxyResolver implements DozerProxyResolver {

    @Override
    public boolean isProxy(Class<?> clazz) {
        if (clazz.isInterface()) {
            return false;
        }
        String className = clazz.getName();
        return className.contains(DozerConstants.CGLIB_ID)
               || className.startsWith(DozerConstants.JAVASSIST_PACKAGE)
               || className.contains(DozerConstants.JAVASSIST_SYMBOL)
               || className.contains(DozerConstants.JAVASSIST_SYMBOL_2);
    }

    @Override
    public <T> T unenhanceObject(T object) {
        return object;
    }

    @Override
    public Class<?> getRealClass(Class<?> clazz) {
        if (isProxy(clazz)) {
            Class<?> superclass = clazz.getSuperclass();
            // Proxy could be created based on set of interfaces. In this case we will rely on inheritance mappings.
            if (DozerConstants.BASE_CLASS.equals(superclass.getName())) {
                return clazz;
            }
            return superclass;
        }
        return clazz;
    }

}
