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
package com.github.dozermapper.core.event;

import java.util.ArrayList;

import com.github.dozermapper.core.DozerEventListener;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DozerEventManagerTest {

    private DozerEventManager manager;
    private ArrayList<DozerEventListener> listeners;

    @Before
    public void setUp() throws Exception {
        listeners = new ArrayList<>();
        manager = new DozerEventManager(listeners);
    }

    @Test
    public void testFireEvent_NoListeners() {
        DozerEventListener listener = mock(DozerEventListener.class);
        listeners.add(listener);

        DozerEvent dozerEvent = mock(DozerEvent.class);
        when(dozerEvent.getType()).thenReturn(DozerEventType.MAPPING_STARTED);

        manager.fireEvent(dozerEvent);

        verify(listener).mappingStarted(dozerEvent);
    }
}
