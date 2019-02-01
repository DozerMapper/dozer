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
package com.github.dozermapper.core;

import java.util.List;
import java.util.Map;

import com.github.dozermapper.core.events.EventListener;

public interface MapperModelContext {

    /**
     * Returns list of provided mapping file URLs
     *
     * @return unmodifiable list of mapping files
     */
    List<String> getMappingFiles();

    /**
     * Returns a list of registered {@link CustomConverter}
     *
     * @return unmodifiable list of converters
     */
    List<CustomConverter> getCustomConverters();

    /**
     * Returns a list of registered {@link CustomConverter} which can be referenced in mapping by provided ID.
     *
     * @return unmodifiable list of converters
     */
    Map<String, CustomConverter> getCustomConvertersWithId();

    /**
     * Returns a list of registered {@link EventListener}
     *
     * @return unmodifiable list of listeners
     */
    List<? extends EventListener> getEventListeners();

    /**
     * Returns a list of registered {@link CustomFieldMapper}
     *
     * @return a custom field mapper
     */
    CustomFieldMapper getCustomFieldMapper();
}
