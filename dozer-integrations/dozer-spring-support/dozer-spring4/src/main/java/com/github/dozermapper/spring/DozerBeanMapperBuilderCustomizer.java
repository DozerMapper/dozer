/*
 * Copyright 2005-2024 Dozer Project
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
package com.github.dozermapper.spring;

import com.github.dozermapper.core.DozerBeanMapperBuilder;

/**
 * The customizer interface for the {@link DozerBeanMapperBuilder}.
 *
 * @since 6.5.0
 */
public interface DozerBeanMapperBuilderCustomizer {

    /**
     * Customize a builder.
     *
     * @param builder a builder that created by {@link DozerBeanMapperFactoryBean}.
     */
    void customize(DozerBeanMapperBuilder builder);

}
