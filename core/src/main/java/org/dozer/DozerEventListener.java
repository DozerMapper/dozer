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
 * href="http://dozer.sourceforge.net/documentation/events.html">http://dozer.sourceforge.net/documentation/events.html</a>
 * 
 * @author garsombke.franz
 * 
 */
public interface DozerEventListener {

  public void mappingStarted(DozerEvent event);

  public void preWritingDestinationValue(DozerEvent event);

  public void postWritingDestinationValue(DozerEvent event);

  public void mappingFinished(DozerEvent event);
}
