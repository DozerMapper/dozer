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

import org.dozer.event.DozerEvent;

/**
 * Public event listener interface. By implementing the DozerEventListener interface Dozer allows you to listen to 4
 * different events: mappingStarted, mappingFinished, preWritingDestinationValue, postWritingDestinationValue.
 *
 * <p>
 * A DozerEvent object is passed into these callback methods which stores information about the ClassMap, FieldMap,
 * Source object, destination object, and destination value. This will allow you to extend dozer and manipulate mapped
 * objects at run-time.
 *
 * <p>
 * <a
 * href="https://dozermapper.github.io/gitbook/documentation/events.html">
 * https://dozermapper.github.io/gitbook/documentation/events.html</a>
 *
 * @author garsombke.franz
 *
 */
public interface DozerEventListener {

    void mappingStarted(DozerEvent event);

    void preWritingDestinationValue(DozerEvent event);

    void postWritingDestinationValue(DozerEvent event);

    void mappingFinished(DozerEvent event);
}
