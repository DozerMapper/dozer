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

import java.util.List;

import com.github.dozermapper.core.MappingException;

/**
 * Event manager which simply fires to the corrasponding {@link EventListener}
 */
public final class DefaultEventManager implements EventManager {

    private final List<? extends EventListener> eventListeners;

    /**
     * Event manager which simply fires to the corrasponding {@link EventListener}
     *
     * @param eventListeners event listeners to callback to
     */
    public DefaultEventManager(List<? extends EventListener> eventListeners) {
        this.eventListeners = eventListeners;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void on(Event event) {
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                switch (event.getType()) {
                    case MAPPING_STARTED:
                        listener.onMappingStarted(event);
                        break;
                    case MAPPING_PRE_WRITING_DEST_VALUE:
                        listener.onPreWritingDestinationValue(event);
                        break;
                    case MAPPING_POST_WRITING_DEST_VALUE:
                        listener.onPostWritingDestinationValue(event);
                        break;
                    case MAPPING_FINISHED:
                        listener.onMappingFinished(event);
                        break;
                    default:
                        throw new MappingException("Unsupported event type: " + event.getType());
                }
            }
        }
    }
}
