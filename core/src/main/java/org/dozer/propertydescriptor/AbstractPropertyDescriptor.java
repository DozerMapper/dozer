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
package org.dozer.propertydescriptor;

import org.dozer.fieldmap.HintContainer;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;

/**
 * Internal abstract property descriptor containing common property descriptor logic. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class AbstractPropertyDescriptor implements DozerPropertyDescriptor {
  protected final Class<?> clazz;
  protected final String fieldName;
  protected boolean isIndexed = false;
  protected int index;
  protected HintContainer srcDeepIndexHintContainer;
  protected HintContainer destDeepIndexHintContainer;

  // TODO Delete this class
  public AbstractPropertyDescriptor(final Class<?> clazz, final String fieldName, boolean isIndexed, int index,
      HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    this.clazz = clazz;
    this.fieldName = fieldName;
    this.isIndexed = isIndexed;
    this.index = index;
    this.srcDeepIndexHintContainer = srcDeepIndexHintContainer;
    this.destDeepIndexHintContainer = destDeepIndexHintContainer;
  }  

}
