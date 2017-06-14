/*
 * Copyright 2005-2018 Dozer Project
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
package org.dozer.config.processors;

import java.util.ArrayList;
import java.util.List;

import org.dozer.config.Settings;
import org.dozer.config.SettingsDefaults;
import org.dozer.config.SettingsKeys;
import org.dozer.config.resolvers.LegacyPropertiesSettingsResolver;
import org.dozer.config.resolvers.SettingsResolver;
import org.dozer.config.resolvers.SystemEnvironmentSettingsResolver;
import org.dozer.config.resolvers.SystemPropertySettingsResolver;
import org.dozer.util.DozerClassLoader;
import org.dozer.util.MappingUtils;

public class DefaultSettingsProcessor implements SettingsProcessor {

    private final DozerClassLoader classLoader;
    private List<SettingsResolver> resolvers = new ArrayList<SettingsResolver>();

    public DefaultSettingsProcessor(DozerClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Settings process() {
        createSettingsResolvers();
        checkForDeprecatedKeys();

        String classLoaderBeanName = String.valueOf(getValue(SettingsKeys.CLASS_LOADER_BEAN, SettingsDefaults.CLASS_LOADER_BEAN));
        String proxyResolverBeanName = String.valueOf(getValue(SettingsKeys.PROXY_RESOLVER_BEAN, SettingsDefaults.PROXY_RESOLVER_BEAN));

        Integer converterByDestTypeCacheMaxSize = Integer.valueOf(getValue(SettingsKeys.CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE,
                                                                           SettingsDefaults.CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE).toString());

        Integer superTypesCacheMaxSize = Integer.valueOf(getValue(SettingsKeys.SUPER_TYPE_CHECK_CACHE_MAX_SIZE,
                                                                  SettingsDefaults.SUPER_TYPE_CHECK_CACHE_MAX_SIZE).toString());

        Boolean useJaxbMappingEngine = Boolean.valueOf(getValue(SettingsKeys.USE_JAXB_MAPPING_ENGINE,
                                                                SettingsDefaults.USE_JAXB_MAPPING_ENGINE).toString());

        return new Settings(converterByDestTypeCacheMaxSize, superTypesCacheMaxSize, classLoaderBeanName, proxyResolverBeanName, useJaxbMappingEngine);
    }

    private void createSettingsResolvers() {
        String fileName = System.getProperty(SettingsKeys.CONFIG_FILE_SYS_PROP);
        
        resolvers.add(new LegacyPropertiesSettingsResolver(classLoader, getConfigFileName(fileName, SettingsDefaults.LEGACY_PROPERTIES_FILE)));
        resolvers.add(new SystemPropertySettingsResolver());
        resolvers.add(new SystemEnvironmentSettingsResolver());

        for (SettingsResolver current : resolvers) {
            current.init();
        }
    }

    private String getConfigFileName(String fileName, String defaultValue) {
        if (MappingUtils.isBlankOrNull(fileName)) {
            fileName = defaultValue;
        }

        return fileName;
    }

    private void checkForDeprecatedKeys() {
        for (SettingsResolver current : resolvers) {
            if (current.get(SettingsKeys.DEPRECATED_EL_ENABLED, null) != null) {
                throw new IllegalArgumentException("Found key in properties for " + SettingsKeys.DEPRECATED_EL_ENABLED
                                                   + " via "
                                                   + current.getClass().getName()
                                                   + ". This is deprecated, please use: DozerBeanMapperBuilder.withELEngine()");
            }

            if (current.get(SettingsKeys.DEPRECATED_CLASS_LOADER_BEAN, null) != null) {
                throw new IllegalArgumentException("Found key in properties for " + SettingsKeys.DEPRECATED_CLASS_LOADER_BEAN
                                                   + " via "
                                                   + current.getClass().getName()
                                                   + ". This is deprecated, please use: "
                                                   + SettingsKeys.CLASS_LOADER_BEAN);
            }

            if (current.get(SettingsKeys.DEPRECATED_PROXY_RESOLVER_BEAN, null) != null) {
                throw new IllegalArgumentException("Found key in properties for " + SettingsKeys.DEPRECATED_PROXY_RESOLVER_BEAN
                                                   + " via "
                                                   + current.getClass().getName()
                                                   + ". This is deprecated, please use: "
                                                   + SettingsKeys.PROXY_RESOLVER_BEAN);
            }

            if (current.get(SettingsKeys.DEPRECATED_CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE, null) != null) {
                throw new IllegalArgumentException("Found key in properties for " + SettingsKeys.DEPRECATED_CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE
                                                   + " via "
                                                   + current.getClass().getName()
                                                   + ". This is deprecated, please use: "
                                                   + SettingsKeys.CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE);
            }

            if (current.get(SettingsKeys.DEPRECATED_SUPER_TYPE_CHECK_CACHE_MAX_SIZE, null) != null) {
                throw new IllegalArgumentException("Found key in properties for " + SettingsKeys.DEPRECATED_SUPER_TYPE_CHECK_CACHE_MAX_SIZE
                                                   + " via "
                                                   + current.getClass().getName()
                                                   + ". This is deprecated, please use: "
                                                   + SettingsKeys.SUPER_TYPE_CHECK_CACHE_MAX_SIZE);
            }
        }
    }

    private Object getValue(String key, Object defaultValue) {
        Object answer = null;
        for (SettingsResolver current : resolvers) {
            answer = current.get(key, null);
            if (answer != null) {
                break;
            }
        }

        if (answer == null) {
            answer = defaultValue;
        }

        return answer;
    }
}
