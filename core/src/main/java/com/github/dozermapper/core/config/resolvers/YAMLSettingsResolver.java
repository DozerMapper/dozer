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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.dozermapper.core.config.SettingsKeys;
import com.github.dozermapper.core.util.DozerClassLoader;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Settings resolver which loads up a yaml file, such as dozer.yaml
 */
public class YAMLSettingsResolver implements SettingsResolver {

    private static final Logger LOG = LoggerFactory.getLogger(YAMLSettingsResolver.class);

    private static final String ROOT_PARENT = "dozer";
    private static final String CACHE_PARENT = "cache";
    private static final String BEANS_PARENT = "beans";

    private static final String ENABLED_PROPERTY = "enabled";
    private static final String CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE_PROPERTY = "converter-by-dest-type-maxsize";
    private static final String SUPER_TYPE_CHECK_CACHE_MAX_SIZE_PROPERTY = "super-type-maxsize";
    private static final String CLASS_LOADER_BEAN_PROPERTY = "class-loader-bean";
    private static final String PROXY_RESOLVER_BEAN_PROPERTY = "proxy-resolver";

    private final DozerClassLoader classLoader;
    private final String configFile;
    private Map<String, Object> properties = new HashMap<>();

    public YAMLSettingsResolver(DozerClassLoader classLoader, String configFile) {
        this.classLoader = classLoader;
        this.configFile = configFile;
    }

    /**
     * Loads dozer.yaml from classpath
     */
    @Override
    public void init() {
        Map<String, Map> settings = processFile();

        processSettingsMap(settings);
    }

    private Map<String, Map> processFile() {
        Map<String, Map> answer = new HashMap<>();

        String extension = FilenameUtils.getExtension(configFile);
        if (!(extension.equalsIgnoreCase("yaml") || extension.equalsIgnoreCase("yml"))) {
            LOG.info("Ignoring, as file extension is not correct for: {}", configFile);
        } else {
            LOG.info("Trying to find Dozer configuration file: {}", configFile);
            URL url = classLoader.loadResource(configFile);
            if (url == null) {
                LOG.info("Failed to find {} via {}.", configFile, getClass().getName());
            } else {
                LOG.info("Using URL [{}] for Dozer settings", url);

                try (InputStream inputStream = url.openStream()) {
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    answer = (Map<String, Map>)mapper.readValue(inputStream, Map.class);
                } catch (IOException ex) {
                    LOG.error("Failed to load: {} because: {}", configFile, ex.getMessage());
                    LOG.debug("Exception: ", ex);
                }
            }
        }

        return answer;
    }

    private void processSettingsMap(Map<String, Map> settings) {
        if (settings.containsKey(ROOT_PARENT)) {
            Map<String, Map> root = (Map<String, Map>)settings.get(ROOT_PARENT);
            if (root.containsKey(CACHE_PARENT)) {
                Map<String, Integer> cache = (Map<String, Integer>)root.get(CACHE_PARENT);
                if (cache.containsKey(CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE_PROPERTY)) {
                    properties.put(SettingsKeys.CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE, cache.get(CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE_PROPERTY));
                }

                if (cache.containsKey(SUPER_TYPE_CHECK_CACHE_MAX_SIZE_PROPERTY)) {
                    properties.put(SettingsKeys.SUPER_TYPE_CHECK_CACHE_MAX_SIZE, cache.get(SUPER_TYPE_CHECK_CACHE_MAX_SIZE_PROPERTY));
                }
            }

            if (root.containsKey(BEANS_PARENT)) {
                Map<String, String> classes = (Map<String, String>)root.get(BEANS_PARENT);
                if (classes.containsKey(CLASS_LOADER_BEAN_PROPERTY)) {
                    properties.put(SettingsKeys.CLASS_LOADER_BEAN, classes.get(CLASS_LOADER_BEAN_PROPERTY));
                }

                if (classes.containsKey(PROXY_RESOLVER_BEAN_PROPERTY)) {
                    properties.put(SettingsKeys.PROXY_RESOLVER_BEAN, classes.get(PROXY_RESOLVER_BEAN_PROPERTY));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String key, Object defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}
