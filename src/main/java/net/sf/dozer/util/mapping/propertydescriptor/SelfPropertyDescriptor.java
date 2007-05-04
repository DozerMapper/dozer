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

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Hint;

/**
 * Internal class used for copy by reference mappings.  Only intended for internal use.
 * 
 * @author garsombke.franz
 */
public class SelfPropertyDescriptor implements DozerPropertyDescriptorIF {

  private final Class self;

  public SelfPropertyDescriptor(Class self) {
    this.self = self;
  }

  public Class getPropertyType() throws MappingException {
    return self;
  }

  public void setPropertyValue(Object bean, Object value, Hint hint, ClassMap classMap) throws MappingException {
    // do nothing
  }

  public Object getPropertyValue(Object bean) throws MappingException {
    return bean;
  }

}
