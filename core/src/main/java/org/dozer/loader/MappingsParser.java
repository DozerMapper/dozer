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
package org.dozer.loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMappings;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingDirection;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.fieldmap.DozerField;
import org.dozer.fieldmap.ExcludeFieldMap;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.GenericFieldMap;
import org.dozer.fieldmap.MapFieldMap;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

import static org.dozer.util.MappingUtils.isSupportedMap;

/**
 * Internal class that decorates raw ClassMap objects and performs various validations on the explicit field mappings.
 * It applies global configuration and class level attributes to raw class mappings. It also creates the ClassMap
 * "prime" instance for bi-directional mappings. The ClassMap prime is created by copying the original ClassMap and
 * reversing the attributes. Only intended for internal use.
 * 
 * @author garsombke.franz
 */
public final class MappingsParser {

  private final BeanContainer beanContainer;
  private final DestBeanCreator destBeanCreator;
  private final PropertyDescriptorFactory propertyDescriptorFactory;

  public MappingsParser(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
    this.beanContainer = beanContainer;
    this.destBeanCreator = destBeanCreator;
    this.propertyDescriptorFactory = propertyDescriptorFactory;
  }

  /**
   * Decorates raw ClassMap objects and performs various validations on the explicit field mappings.
   * It applies global configuration and class level attributes to raw class mappings.
   * @param classMaps Input class maps.
   * @param globalConfiguration Global configuration.
   * @return Resulting class mappings.
   */
  public ClassMappings processMappings(List<ClassMap> classMaps, Configuration globalConfiguration) {
    if (globalConfiguration == null) {
      throw new IllegalArgumentException("Global configuration parameter cannot be null");
    }
    ClassMappings result = new ClassMappings(beanContainer);
    if (classMaps == null || classMaps.size() == 0) {
      return result;
    }
    FieldMap fieldMapPrime;
    // need to create bi-directional mappings now.
    ClassMap classMapPrime;
    Set<String> mapIds = new HashSet<String>();
    for (ClassMap classMap : classMaps) {
      classMap.setGlobalConfiguration(globalConfiguration);

      // add our first class map to the result map and initialize PropertyDescriptor Cache
      ReflectionUtils.findPropertyDescriptor(classMap.getSrcClassToMap(), "", null);
      ReflectionUtils.findPropertyDescriptor(classMap.getDestClassToMap(), "", null);

      // Check to see if this is a duplicate map id, irregardless of src and dest class names. 
      // Duplicate map-ids are not allowed
      if (!MappingUtils.isBlankOrNull(classMap.getMapId())) {
        if (mapIds.contains(classMap.getMapId())) {
          throw new IllegalArgumentException("Duplicate Map Id's Found. Map Id: " + classMap.getMapId());
        }
        mapIds.add(classMap.getMapId());
      }

      result.add(classMap.getSrcClassToMap(), classMap.getDestClassToMap(), classMap.getMapId(), classMap);
      // now create class map prime
      classMapPrime = new ClassMap(globalConfiguration);
      MappingUtils.reverseFields(classMap, classMapPrime, beanContainer);

      if (classMap.getFieldMaps() != null) {
        List<FieldMap> fms = classMap.getFieldMaps();
        // iterate through the fields and see wether or not they should be mapped
        // one way class mappings we do not need to add any fields
        if (!MappingDirection.ONE_WAY.equals(classMap.getType())) {
          for (FieldMap fieldMap : fms.toArray(new FieldMap[]{})) {
            fieldMap.validate();

            // If we are dealing with a Map data type, transform the field map into a MapFieldMap type
            // only apply transformation if it is map to non-map mapping.
            if (!(fieldMap instanceof ExcludeFieldMap)) {
              if ( ( isSupportedMap(classMap.getDestClassToMap()) ^ isSupportedMap(classMap.getSrcClassToMap()) )
               || ( isSupportedMap(fieldMap.getDestFieldType(classMap.getDestClassToMap()))
                    ^ isSupportedMap(fieldMap.getSrcFieldType(classMap.getSrcClassToMap())) ) ) {
                FieldMap fm = new MapFieldMap(fieldMap, beanContainer, destBeanCreator, propertyDescriptorFactory);
                classMap.removeFieldMapping(fieldMap);
                classMap.addFieldMapping(fm);
                fieldMap = fm;
              }
            }
            
            // if the source is a java.util.Map, and not already mapped as key=>value,
            // map the field as key=>value, not as bean property
            if (isSupportedMap(classMap.getSrcClassToMap()) && fieldMap.getSrcFieldKey() == null) {
              DozerField newSrcField = fieldMap.getSrcFieldCopy();
              newSrcField.setName(DozerConstants.SELF_KEYWORD);
              newSrcField.setKey(fieldMap.getSrcFieldName());
              fieldMap.setSrcField(newSrcField);
            }
            // like above but the reverse: 
            // if the destination is a java.util.Map, and not already mapped as key=>value,
            // map the field as key=>value, not as bean property
            if (isSupportedMap(classMap.getDestClassToMap()) && fieldMap.getDestFieldKey() == null) {
              DozerField newDestField = fieldMap.getDestFieldCopy();
              newDestField.setName(DozerConstants.SELF_KEYWORD);
              newDestField.setKey(fieldMap.getDestFieldName());
              fieldMap.setDestField(newDestField);
            }

            if (!(MappingDirection.ONE_WAY.equals(fieldMap.getType()) && !(fieldMap instanceof ExcludeFieldMap))) {
              // make a prime field map
              fieldMapPrime = (FieldMap) fieldMap.clone();
              fieldMapPrime.setClassMap(classMapPrime);
              // check to see if it is only an exclude one way
              if (fieldMapPrime instanceof ExcludeFieldMap && MappingDirection.ONE_WAY.equals(fieldMap.getType())) {
                // need to make a generic field map for the other direction
                fieldMapPrime = new GenericFieldMap(classMapPrime, beanContainer, destBeanCreator, propertyDescriptorFactory);
              }
              // reverse the fields
              MappingUtils.reverseFields(fieldMap, fieldMapPrime);

              // iterate through copyByReferences and set accordingly
              if (!(fieldMap instanceof ExcludeFieldMap)) {
                MappingUtils.applyGlobalCopyByReference(globalConfiguration, fieldMap, classMap);
              }
              if (!(fieldMapPrime instanceof ExcludeFieldMap)) {
                MappingUtils.applyGlobalCopyByReference(globalConfiguration, fieldMapPrime, classMapPrime);
              }
            } else { // if it is a one-way field map make the other field map excluded
              // make a prime field map
              fieldMapPrime = new ExcludeFieldMap(classMapPrime, beanContainer, destBeanCreator, propertyDescriptorFactory);
              MappingUtils.reverseFields(fieldMap, fieldMapPrime);
              MappingUtils.applyGlobalCopyByReference(globalConfiguration, fieldMap, classMap);
            }
            classMapPrime.addFieldMapping(fieldMapPrime);
          }
        } else {
          // since it is one-way...we still need to validate if it has some type of method mapping and validate the
          // field maps
          for (FieldMap oneWayFieldMap : fms.toArray(new FieldMap[]{})) {
            oneWayFieldMap.validate();

            MappingUtils.applyGlobalCopyByReference(globalConfiguration, oneWayFieldMap, classMap);
            // check to see if we need to exclude the map
            if (MappingDirection.ONE_WAY.equals(oneWayFieldMap.getType())) {
              fieldMapPrime = new ExcludeFieldMap(classMapPrime, beanContainer, destBeanCreator, propertyDescriptorFactory);
              MappingUtils.reverseFields(oneWayFieldMap, fieldMapPrime);
              classMapPrime.addFieldMapping(fieldMapPrime);
            }
          }
        }
      }
      // if it is a one way mapping or a method/iterate method mapping we can not bi-directionally map
      // Map Prime could actually be empty
      if (!MappingDirection.ONE_WAY.equals(classMap.getType())) {
        result.add(classMap.getDestClassToMap(), classMap.getSrcClassToMap(), classMap.getMapId(), classMapPrime);
      }
    }
    return result;
  }

}
