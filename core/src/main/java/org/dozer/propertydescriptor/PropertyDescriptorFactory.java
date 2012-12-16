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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Internal factory responsible for determining which property descriptor should
 * be used. Only intended for internal use.
 * 
 * @author garsombke.franz
 */
public class PropertyDescriptorFactory {

  private static final List<PropertyDescriptorCreationStrategy> pluggedDescriptorCreationStrategies =
          new ArrayList<PropertyDescriptorCreationStrategy>();

  private PropertyDescriptorFactory() {
  }

  public static DozerPropertyDescriptor getPropertyDescriptor(Class<?> clazz, String theGetMethod, String theSetMethod,
      String mapGetMethod, String mapSetMethod, boolean isAccessible, boolean isIndexed, int index, String name, String key,
      boolean isSelfReferencing, String oppositeFieldName, HintContainer srcDeepIndexHintContainer,
      HintContainer destDeepIndexHintContainer, String beanFactory) {
    DozerPropertyDescriptor desc = null;

    // Raw Map types or custom map-get-method/set specified
    boolean isMapProperty = MappingUtils.isSupportedMap(clazz);
    if (name.equals(DozerConstants.SELF_KEYWORD) &&
            (mapSetMethod != null || mapGetMethod != null || isMapProperty)) {
      String setMethod = isMapProperty ? "put" : mapSetMethod;
      String getMethod = isMapProperty ? "get" : mapGetMethod;

      desc = new MapPropertyDescriptor(clazz, name, isIndexed, index, setMethod,
              getMethod, key != null ? key : oppositeFieldName,
              srcDeepIndexHintContainer, destDeepIndexHintContainer);

      // Copy by reference(Not mapped backed properties which also use 'this'
      // identifier for a different purpose)
    } else if (isSelfReferencing) {
      desc = new SelfPropertyDescriptor(clazz);

      // Access field directly and bypass getter/setters
    } else if (isAccessible) {
      desc = new FieldPropertyDescriptor(clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);

      // Custom get-method/set specified
    } else if (theSetMethod != null || theGetMethod != null) {
      desc = new CustomGetSetPropertyDescriptor(clazz, name, isIndexed, index, theSetMethod, theGetMethod,
          srcDeepIndexHintContainer, destDeepIndexHintContainer);

      // If this object is an XML Bean - then use the XmlBeanPropertyDescriptor  
    } else if (beanFactory != null && beanFactory.equals(DozerConstants.XML_BEAN_FACTORY)) {
      desc = new XmlBeanPropertyDescriptor(clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
    }

    if (desc != null) return desc;

    for (PropertyDescriptorCreationStrategy propertyDescriptorBuilder :
            new CopyOnWriteArrayList<PropertyDescriptorCreationStrategy>(pluggedDescriptorCreationStrategies)) {
      if (propertyDescriptorBuilder.isAssignable(clazz, name)) {
        desc = propertyDescriptorBuilder.buildFor(
                clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
        if (desc != null) break;
      }
    }

    if (desc == null) {
      // Everything else. It must be a normal bean with normal custom get/set methods
      desc = new JavaBeanPropertyDescriptor(clazz, name, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
    }

    return desc;
  }

  public static void addPluggedPropertyDescriptorCreationStrategy(PropertyDescriptorCreationStrategy strategy) {
    pluggedDescriptorCreationStrategies.add(strategy);
  }
}