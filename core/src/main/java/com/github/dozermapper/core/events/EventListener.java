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
package com.github.dozermapper.core.events;

import com.github.dozermapper.core.Mapper;

/**
 * Event listener callback handler which if implemented allows listening to triggered events
 * while the {@link Mapper} is processing.
 * <p>
 * See: <a href="https://dozermapper.github.io/gitbook/documentation/events.html">
 * https://dozermapper.github.io/gitbook/documentation/events.html</a>
 */
public interface EventListener {

    /**
     * Triggered when {@link Mapper} has started
     *
     * @param event event details
     */
    void onMappingStarted(Event event);

    /**
     * Triggered when {@link Mapper} is about to write the destination value
     *
     * @param event event details
     */
    void onPreWritingDestinationValue(Event event);

    /**
     * Triggered when {@link Mapper} has written the destination value
     *
     * @param event event details
     */
    void onPostWritingDestinationValue(Event event);

    /**
     * Triggered when {@link Mapper} has finished
     *
     * @param event event details
     */
    void onMappingFinished(Event event);
}
