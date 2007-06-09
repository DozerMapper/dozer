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

import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.util.MapperConstants;

/**
 * Internal factory responsible for determining which property descriptor should be used.
 * Only intended for internal use.
 * 
 * @author garsombke.franz
 */
public class PropertyDescriptorFactory {

  private PropertyDescriptorFactory() {
  }

  public static DozerPropertyDescriptorIF getPropertyDescriptor(DozerField dozerField, Class clazz) {
    DozerPropertyDescriptorIF desc = null;
    // is it 'this'
    if (dozerField.getName().equals(MapperConstants.SELF_KEYWORD) && dozerField.getTheGetMethod() == null
        && dozerField.getTheSetMethod() == null) {
      desc = new SelfPropertyDescriptor(clazz);
    } else if (dozerField.isAccessible()) {
      // accesses fields directly and bypass get/set methods
      desc = new FieldPropertyDescriptor(clazz, dozerField.getName(), dozerField.isAccessible(), 
          dozerField.isIndexed(), dozerField.getIndex());
    } else {
      // it must be a normal bean with normal or custom get/set methods
      desc = new GetterSetterPropertyDescriptor(clazz, dozerField.getName(), dozerField.isIndexed(),
          dozerField.getIndex(), dozerField.getTheSetMethod(), dozerField.getTheGetMethod());

    }
    return desc;
  }
}