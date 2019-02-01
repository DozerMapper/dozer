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

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Settings resolver which are system environment variables
 */
public class SystemEnvironmentSettingsResolver implements SettingsResolver {

    /**
     * No-op
     */
    @Override
    public void init() {
        //noop
    }

    /**
     * Gets value from environment via {@link System#getenv}
     * @param key key to retrieve
     * @param defaultValue value to default to if none found
     * @return value found or default if none
     */
    @Override
    public Object get(String key, Object defaultValue) {
        Object value = System.getenv(getEnvironmentSafeKey(key));
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }

    private String getEnvironmentSafeKey(String key) {
        String environmentSafeKey = StringUtils.replace(key, ".", "_");
        environmentSafeKey = StringUtils.replace(environmentSafeKey, "-", "_");

        return environmentSafeKey.toUpperCase(Locale.ENGLISH);
    }
}
