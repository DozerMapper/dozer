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
package com.github.dozermapper.core.config.resolvers;

/**
 * Resolves key/values from a backing store
 */
public interface SettingsResolver {

    /**
     * Initializes backing store and retrieves values into memory
     */
    void init();

    /**
     * Gets value from in memory store
     * @param key key to retrieve
     * @param defaultValue value to default to if none found
     * @return value found or default if none
     */
    Object get(String key, Object defaultValue);
}
