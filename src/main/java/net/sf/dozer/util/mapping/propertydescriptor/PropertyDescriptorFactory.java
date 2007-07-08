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

import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;

/**
 * Internal factory responsible for determining which property descriptor should be used. Only intended for internal
 * use.
 * 
 * @author garsombke.franz
 */
public class PropertyDescriptorFactory {

  private PropertyDescriptorFactory() {
  }

  public static DozerPropertyDescriptorIF getPropertyDescriptor(Class clazz, String theGetMethod, String theSetMethod,
      String mapGetMethod, String mapSetMethod, boolean isAccessible, boolean isIndexed, int index, String name, String key,
      boolean isSelfReferencing, String oppositeFieldName) {
    DozerPropertyDescriptorIF desc = null;
    // basic 'this' (Not mapped backed properties which also use 'this' identifier
    if (isSelfReferencing && theGetMethod == null && theSetMethod == null && mapGetMethod == null && mapSetMethod == null
        && !MappingUtils.isSupportedMap(clazz)) {
      desc = new SelfPropertyDescriptor(clazz);

      // bypass getter/setters and access field directly
    } else if (isAccessible) {
      // accesses fields directly and bypass get/set methods
      desc = new FieldPropertyDescriptor(clazz, name, isAccessible, isIndexed, index);

      // custom get-method/set specified
    } else if (!MappingUtils.isSupportedMap(clazz) && (theSetMethod != null || theGetMethod != null)) {
      desc = new CustomGetSetPropertyDescriptor(clazz, name, isIndexed, index, theSetMethod, theGetMethod);

      // custom map-get-method/set spefied
    } else if (name.equals(MapperConstants.SELF_KEYWORD)
        && (mapSetMethod != null || mapGetMethod != null || MappingUtils.isSupportedMap(clazz))) {
      desc = new MapPropertyDescriptor(clazz, name, isIndexed, index, MappingUtils.isSupportedMap(clazz) ? "put" : mapSetMethod,
          MappingUtils.isSupportedMap(clazz) ? "get" : mapGetMethod, key != null ? key : oppositeFieldName);

      // everything else. it must be a normal bean with normal or custom get/set methods
    } else {
      desc = new JavaBeanPropertyDescriptor(clazz, name, isIndexed, index);
    }
    return desc;
  }
}