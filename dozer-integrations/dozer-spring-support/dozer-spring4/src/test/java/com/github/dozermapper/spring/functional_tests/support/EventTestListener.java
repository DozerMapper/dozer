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
package com.github.dozermapper.spring.functional_tests.support;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.dozermapper.core.events.Event;
import com.github.dozermapper.core.events.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventTestListener implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventTestListener.class);

    private final AtomicInteger invocationCount = new AtomicInteger();

    @Override
    public void onMappingStarted(Event event) {
        LOG.debug("mappingStarted Called with:" + event.getClassMap().getDestClassToMap());

        invocationCount.incrementAndGet();
    }

    @Override
    public void onPreWritingDestinationValue(Event event) {
        LOG.debug("preWritingDestinationValue Called with:" + event.getClassMap().getDestClassToMap());

        invocationCount.incrementAndGet();
    }

    @Override
    public void onPostWritingDestinationValue(Event event) {
        LOG.debug("postWritingDestinationValue Called with:" + event.getClassMap().getDestClassToMap());

        invocationCount.incrementAndGet();
    }

    @Override
    public void onMappingFinished(Event event) {
        LOG.debug("mappingFinished Called with:" + event.getClassMap().getDestClassToMap());

        invocationCount.incrementAndGet();
    }

    public int getInvocationCount() {
        return invocationCount.intValue();
    }
}
