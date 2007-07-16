/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.propertydescriptor;

import net.sf.dozer.util.mapping.fieldmap.HintContainer;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;

/**
 * Internal abstract property descriptor containing common property descriptor logic. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class AbstractPropertyDescriptor implements DozerPropertyDescriptorIF {
  protected final Class clazz;
  protected final String fieldName;
  protected boolean isIndexed = false;
  protected int index;
  protected HintContainer srcDeepIndexHintContainer;
  protected HintContainer destDeepIndexHintContainer;

  public AbstractPropertyDescriptor(final Class clazz, final String fieldName, boolean isIndexed, int index, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    this.clazz = clazz;
    this.fieldName = fieldName;
    this.isIndexed = isIndexed;
    this.index = index;
    this.srcDeepIndexHintContainer = srcDeepIndexHintContainer;
    this.destDeepIndexHintContainer = destDeepIndexHintContainer;
  }

  public abstract Class getPropertyType();

  protected Object prepareIndexedCollection(Object existingCollection, Object collectionEntry) {
    return MappingUtils.prepareIndexedCollection(getPropertyType(), existingCollection, collectionEntry, index);
  }
  
  protected boolean isDeepField() {
    return fieldName.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) > 0;
  }

}
