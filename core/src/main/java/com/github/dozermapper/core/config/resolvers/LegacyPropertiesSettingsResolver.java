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
import java.util.Properties;

import com.github.dozermapper.core.util.DozerClassLoader;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Settings resolver which loads up a properties file, such as dozer.properties
 */
public class LegacyPropertiesSettingsResolver implements SettingsResolver {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyPropertiesSettingsResolver.class);

    private final DozerClassLoader classLoader;
    private final String configFile;
    private Properties properties;

    public LegacyPropertiesSettingsResolver(DozerClassLoader classLoader, String configFile) {
        this.classLoader = classLoader;
        this.configFile = configFile;
    }

    /**
     * Loads dozer.properties from classpath
     */
    @Override
    public void init() {
        properties = processFile();
    }

    private Properties processFile() {
        Properties properties = new Properties();

        String extension = FilenameUtils.getExtension(configFile);
        if (!extension.equalsIgnoreCase("properties")) {
            LOG.info("Ignoring, as file extension is not correct for: {}", configFile);
        } else {
            LOG.info("Trying to find Dozer configuration file: {}", configFile);
            URL url = classLoader.loadResource(configFile);
            if (url == null) {
                LOG.info("Failed to find {} via {}.", configFile, getClass().getName());
            } else {
                LOG.info("Using URL [{}] for Dozer settings", url);

                try (InputStream inputStream = url.openStream()) {
                    properties.load(inputStream);
                } catch (IOException ex) {
                    LOG.error("Failed to load: {} because: {}", configFile, ex.getMessage());
                    LOG.debug("Exception: ", ex);
                }
            }
        }

        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String key, Object defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
}
