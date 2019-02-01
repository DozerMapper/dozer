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
 * Proxy resolution strategy for mappings. Check implementing classes for available options.
 * It is possible to provide custom resolution strategy by registering that in dozer.properties.
 */
public interface DozerProxyResolver {

    boolean isProxy(Class<?> clazz);

    <T> T unenhanceObject(T object);

    Class<?> getRealClass(Class<?> clazz);

}
