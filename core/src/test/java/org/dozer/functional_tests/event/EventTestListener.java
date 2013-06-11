/*
 * Copyright 2005-2010 the original author or authors.
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
package org.dozer.functional_tests.event;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerEventListener;
import org.dozer.event.DozerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTestListener implements DozerEventListener {

  private static final Logger log = LoggerFactory.getLogger(EventTestListener.class);

  private final List<DozerEvent> firedEvents = new ArrayList<DozerEvent>();

  public void mappingStarted(DozerEvent event) {
    log.debug("mappingStarted Called with:" + event.getClassMap());
    firedEvents.add(event);
  }

  public void preWritingDestinationValue(DozerEvent event) {
    log.debug("preWritingDestinationValue Called with:" + event.getClassMap());
    firedEvents.add(event);
  }

  public void postWritingDestinationValue(DozerEvent event) {
    log.debug("postWritingDestinationValue Called with:" + event.getClassMap());
    firedEvents.add(event);
  }

  public void mappingFinished(DozerEvent event) {
    log.debug("mappingFinished Called with:" + event.getClassMap());
    firedEvents.add(event);
  }

  public List<DozerEvent> getFiredEvents() {
    return firedEvents;
  }

}
