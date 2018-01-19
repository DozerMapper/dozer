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
package org.dozer.event;

import java.util.List;

import org.dozer.DozerEventListener;
import org.dozer.util.MappingUtils;


/**
 * Internal class that handles dozer events and invokes any public event listeners. Only intended for internal use.
 * 
 * @author garsombke.franz
 */
public final class DozerEventManager implements EventManager {
  
  private final List<? extends DozerEventListener> eventListeners;

  public DozerEventManager(List<? extends DozerEventListener> eventListeners) {
    this.eventListeners = eventListeners;
  }

  public void fireEvent(DozerEvent event) {
    // If no listeners were specified, then just return.
    if (eventListeners == null) {
      return;
    }
    DozerEventType eventType = event.getType();
    for (DozerEventListener listener : eventListeners) {
      switch (eventType) {
       case MAPPING_STARTED:
         listener.mappingStarted(event);
         break;
       case MAPPING_PRE_WRITING_DEST_VALUE:
         listener.preWritingDestinationValue(event);
         break;
       case MAPPING_POST_WRITING_DEST_VALUE:
         listener.postWritingDestinationValue(event);
         break;
       case MAPPING_FINISHED:
         listener.mappingFinished(event);
         break;
       default:
         MappingUtils.throwMappingException("Unsupported event type: " + eventType);
       break;
      }
    }
  }

}
