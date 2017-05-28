/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds an instance of {@link DozerBeanMapper}.
 * Provides fluent interface to configure every aspect of the mapper. Everything which is not explicitly specified
 * will receive its default value. Please refer to class methods for possible configuration options.
 */
public final class DozerBeanMapperBuilder {

    private List<String> mappingFiles = new ArrayList<>(1);

    private DozerBeanMapperBuilder() {
    }

    /**
     * Creates new builder. All the configuration has its default values.
     *
     * @return new instance of the builder.
     */
    public static DozerBeanMapperBuilder create() {
        return new DozerBeanMapperBuilder();
    }

    /**
     * Creates an instance of {@link DozerBeanMapper}, with all the configuration set to its default values.
     * <p>
     * Is a shortcut for {@code DozerBeanMapperBuilder.create().build()}.
     *
     * @return new instance of {@link DozerBeanMapper} with default configuration.
     */
    public static DozerBeanMapper buildDefaultImplicit() {
        return create().build();
    }

    /**
     * Adds {@code mappingFiles} to the list of files to be used as mapping configuration.
     * Multiple calls of this method will result in all the files being added to the list
     * of mappings in the order methods were called.
     * <p>
     * If not called, no files will be added to the mapping configuration, and mapper will use implicit mode.
     *
     * @param mappingFiles mapping files to be added.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withMappingFiles(String... mappingFiles) {
        this.mappingFiles.addAll(Arrays.asList(mappingFiles));
        return this;
    }

    /**
     * Creates an instance of {@link DozerBeanMapper}. Mapper is configured according to the current builder state.
     * <p>
     * Subsequent calls of this method will return new instances.
     *
     * @return new instance of {@link DozerBeanMapper}.
     */
    public DozerBeanMapper build() {
        return new DozerBeanMapper(mappingFiles);
    }

}
