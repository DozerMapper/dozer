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
package com.github.dozermapper.springboot.autoconfigure;

import java.util.Arrays;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Configuration properties for Dozer.
 */
@ConfigurationProperties(prefix = "dozer")
public class DozerProperties {

    /**
     * Mapping files configuration.
     * For example "classpath:*.dozer.xml".
     */
    private Resource[] mappingFiles = new Resource[] {};

    /**
     * Mapping files configuration.
     *
     * @return mapping files
     */
    public Resource[] getMappingFiles() {
        return Arrays.copyOf(mappingFiles, mappingFiles.length);
    }

    /**
     * Set mapping files configuration. For example <code>classpath:*.dozer.xml</code>.
     *
     * @param mappingFiles dozer mapping files
     * @return dozer properties
     */
    public DozerProperties setMappingFiles(Resource[] mappingFiles) {
        this.mappingFiles = Arrays.copyOf(mappingFiles, mappingFiles.length);
        return this;
    }
}
