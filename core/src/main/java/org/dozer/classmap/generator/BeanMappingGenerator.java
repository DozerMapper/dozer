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
package org.dozer.classmap.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMapBuilder;
import org.dozer.classmap.Configuration;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.util.CollectionUtils;

/**
* @author Dmitry Spikhalskiy
*/
public class BeanMappingGenerator implements ClassMapBuilder.ClassMappingGenerator {

  final List<BeanFieldsDetector> pluggedFieldDetectors = new ArrayList<BeanFieldsDetector>();

  final List<BeanFieldsDetector> availableFieldDetectors = new ArrayList<BeanFieldsDetector>() {{
    add(new JavaBeanFieldsDetector());
  }};

  private final BeanContainer beanContainer;
  private final DestBeanCreator destBeanCreator;
  private final PropertyDescriptorFactory propertyDescriptorFactory;

  public BeanMappingGenerator(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
    this.beanContainer = beanContainer;
    this.destBeanCreator = destBeanCreator;
    this.propertyDescriptorFactory = propertyDescriptorFactory;
  }

  public boolean accepts(ClassMap classMap) {
    return true;
  }

  public boolean apply(ClassMap classMap, Configuration configuration) {
    Class<?> srcClass = classMap.getSrcClassToMap();
    Class<?> destClass = classMap.getDestClassToMap();

    Set<String> destFieldNames = getAcceptsFieldsDetector(destClass).getWritableFieldNames(destClass);
    Set<String> srcFieldNames = getAcceptsFieldsDetector(srcClass).getReadableFieldNames(srcClass);
    Set<String> commonFieldNames = CollectionUtils.intersection(srcFieldNames, destFieldNames);

    for (String fieldName : commonFieldNames) {
      if (GeneratorUtils.shouldIgnoreField(fieldName, srcClass, destClass, beanContainer)) {
        continue;
      }

      // If field has already been accounted for, then skip
      if (classMap.getFieldMapUsingDest(fieldName) != null || classMap.getFieldMapUsingSrc(fieldName) != null) {
        continue;
      }

      GeneratorUtils.addGenericMapping(MappingType.GETTER_TO_SETTER, classMap, configuration, fieldName, fieldName, beanContainer, destBeanCreator, propertyDescriptorFactory);
    }
    return false;
  }

  private BeanFieldsDetector getAcceptsFieldsDetector(Class<?> clazz) {
    BeanFieldsDetector detector = getAcceptsFieldDetector(clazz, pluggedFieldDetectors);
    if (detector == null) {
      detector = getAcceptsFieldDetector(clazz, availableFieldDetectors);
    }

    return detector;
  }

  private BeanFieldsDetector getAcceptsFieldDetector(Class<?> clazz, List<BeanFieldsDetector> detectors) {
    for (BeanFieldsDetector detector : new CopyOnWriteArrayList<BeanFieldsDetector>(detectors)) {
      if (detector.accepts(clazz)) {
        return detector;
      }
    }

    return null;
  }

  public void addPluggedFieldDetectors(Collection<BeanFieldsDetector> beanFieldsDetectors) {
    pluggedFieldDetectors.addAll(beanFieldsDetectors);
  }
}
