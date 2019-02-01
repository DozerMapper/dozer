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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultEventManagerTest {

    @Test
    public void handlesEmptyListeners() {
        Event mockedEvent = mock(DefaultEvent.class);
        when(mockedEvent.getType()).thenReturn(EventTypes.MAPPING_STARTED);

        EventManager manager = new DefaultEventManager(new ArrayList<>(0));
        manager.on(mockedEvent);

        assertTrue(true);
    }

    @Test
    public void handlesNullListeners() {
        Event mockedEvent = mock(DefaultEvent.class);
        when(mockedEvent.getType()).thenReturn(EventTypes.MAPPING_FINISHED);

        EventManager manager = new DefaultEventManager(null);
        manager.on(mockedEvent);

        assertTrue(true);
    }

    @Test
    public void handlesOnMappingStarted() {
        Event mockedEvent = mock(DefaultEvent.class);
        when(mockedEvent.getType()).thenReturn(EventTypes.MAPPING_STARTED);

        EventListener listener = mock(EventListener.class);

        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);

        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);

        verify(listener).onMappingStarted(mockedEvent);
        assertTrue(true);
    }

    @Test
    public void handlesOnMappingPreWrite() {
        Event mockedEvent = mock(DefaultEvent.class);
        when(mockedEvent.getType()).thenReturn(EventTypes.MAPPING_PRE_WRITING_DEST_VALUE);

        EventListener listener = mock(EventListener.class);

        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);

        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);

        verify(listener).onPreWritingDestinationValue(mockedEvent);
        assertTrue(true);
    }

    @Test
    public void handlesOnMappingPostWrite() {
        Event mockedEvent = mock(DefaultEvent.class);
        when(mockedEvent.getType()).thenReturn(EventTypes.MAPPING_POST_WRITING_DEST_VALUE);

        EventListener listener = mock(EventListener.class);

        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);

        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);

        verify(listener).onPostWritingDestinationValue(mockedEvent);
        assertTrue(true);
    }

    @Test
    public void handlesOnMappingFinished() {
        Event mockedEvent = mock(DefaultEvent.class);
        when(mockedEvent.getType()).thenReturn(EventTypes.MAPPING_FINISHED);

        EventListener listener = mock(EventListener.class);

        List<EventListener> listeners = new ArrayList<>(1);
        listeners.add(listener);

        EventManager manager = new DefaultEventManager(listeners);
        manager.on(mockedEvent);

        verify(listener).onMappingFinished(mockedEvent);
        assertTrue(true);
    }
}
