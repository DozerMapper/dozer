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
package net.sf.dozer.util.mapping.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.classmap.Mappings;
import net.sf.dozer.util.mapping.fieldmap.ExcludeFieldMap;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.GenericFieldMap;
import net.sf.dozer.util.mapping.fieldmap.MapFieldMap;

import org.apache.commons.lang.StringUtils;

/**
 * Internal class that decorates raw ClassMap objects and performs various validations on the explicit field mappings.
 * It applies global configuration and class level attributes to raw class mappings. It also creates the ClassMap
 * "prime" instance for bi-directional mappings. The ClassMap prime is created by copying the original ClassMap and
 * reversing the attributes. Only intended for internal use.
 * 
 * @author garsombke.franz
 */
public class MappingsParser {

  public Map processMappings(Mappings mappings) {
    Map result = new HashMap();
    FieldMap fieldMapPrime = null;
    // verify that we even have any mappings
    if (mappings.getMapping() == null || mappings.getMapping().size() == 0) {
      return result;
    }
    Iterator iter = mappings.getMapping().iterator();
    // need to create bi-directional mappings now.
    ClassMap classMap = null;
    ClassMap classMapPrime = null;
    Set mapIds = new HashSet();
    while (iter.hasNext()) {
      classMap = (ClassMap) iter.next();
      
      // add our first class map to the result map
      // initialize PropertyDescriptor Cache
      ReflectionUtils.findPropertyDescriptor(classMap.getSrcClassToMap(), "");
      ReflectionUtils.findPropertyDescriptor(classMap.getDestClassToMap(), "");
      String theClassMapKey = ClassMapKeyFactory.createKey(classMap.getSrcClassToMap(), classMap
          .getDestClassToMap(), classMap.getMapId());

      /*
       * Check to see if this is a duplicate mapping. If so, throw an Exception
       */
      if (result.containsKey(theClassMapKey)) {
        throw new IllegalArgumentException("Duplicate Class Mapping Found. Source: "
            + classMap.getSrcClassToMap().getName() + " Destination: "
            + classMap.getDestClassToMap().getName());
      }

      // Check to see if this is a duplicate map id, irregardless of src and dest class names. Duplicate map-ids are
      // not allowed
      if (!MappingUtils.isBlankOrNull(classMap.getMapId())) {
        if (mapIds.contains(classMap.getMapId())) {
          throw new IllegalArgumentException("Duplicate Map Id's Found. Map Id: " + classMap.getMapId());
        }
        mapIds.add(classMap.getMapId());
      }

      result.put(theClassMapKey, classMap);
      // now create class map prime
      classMapPrime = new ClassMap(mappings.getConfiguration());
      MappingUtils.reverseFields(classMap, classMapPrime);
      
      if (classMap.getFieldMaps() != null) {
        Object[] fms = classMap.getFieldMaps().toArray();
        // iterate through the fields and see wether or not they should be mapped
        // one way class mappings we do not need to add any fields
        if (!StringUtils.equals(classMap.getType(), MapperConstants.ONE_WAY)) {
          for (int i = 0; i < fms.length; i++) {
            FieldMap fieldMap = (FieldMap) fms[i];
            fieldMap.validate();

            //If we are dealing with a Map data type, transform the field map into a MapFieldMap type
            if (!(fieldMap instanceof ExcludeFieldMap)) {
              if (MappingUtils.isSupportedMap(classMap.getDestClassToMap())
                  || MappingUtils.isSupportedMap(classMap.getSrcClassToMap())
                  || MappingUtils.isSupportedMap(fieldMap.getDestFieldType(classMap.getDestClassToMap()))
                  || MappingUtils.isSupportedMap(fieldMap.getSrcFieldType(classMap.getSrcClassToMap()))) {
                FieldMap fm = new MapFieldMap(classMap, fieldMap);
                classMap.removeFieldMapping(fieldMap);
                classMap.addFieldMapping(fm);
                fieldMap = fm;
              }
            }

            if (!(StringUtils.equals(fieldMap.getType(), MapperConstants.ONE_WAY) && !(fieldMap instanceof ExcludeFieldMap))) {
              // make a prime field map
              fieldMapPrime = (FieldMap) fieldMap.clone();
              fieldMapPrime.setClassMap(classMapPrime);
              // check to see if it is only an exclude one way
              if (fieldMapPrime instanceof ExcludeFieldMap
                  && StringUtils.equals(fieldMap.getType(), MapperConstants.ONE_WAY)) {
                // need to make a generic field map for the other direction
                fieldMapPrime = new GenericFieldMap(classMapPrime);
              }
              // reverse the fields
              MappingUtils.reverseFields(fieldMap, fieldMapPrime);

              // iterate through copyByReferences and set accordingly
              if (!(fieldMap instanceof ExcludeFieldMap)) {
                MappingUtils.applyGlobalCopyByReference(mappings.getConfiguration(), fieldMap, classMap);
              }
              if (!(fieldMapPrime instanceof ExcludeFieldMap)) {
                MappingUtils.applyGlobalCopyByReference(mappings.getConfiguration(), fieldMapPrime, classMapPrime);
              }
            } else { // if it is a one-way field map make the other field map excluded
              // make a prime field map
              fieldMapPrime = new ExcludeFieldMap(classMapPrime);
              MappingUtils.reverseFields(fieldMap, fieldMapPrime);
            }
            classMapPrime.addFieldMapping((FieldMap) fieldMapPrime);
          }
        } else {
          // since it is one-way...we still need to validate if it has some type of method mapping and validate the
          // field maps
          for (int i = 0; i < fms.length; i++) {
            FieldMap oneWayFieldMap = (FieldMap) fms[i];
            oneWayFieldMap.validate();
            
            MappingUtils.applyGlobalCopyByReference(mappings.getConfiguration(), oneWayFieldMap, classMap);
            // check to see if we need to exclude the map
            if ((StringUtils.equals(oneWayFieldMap.getType(), MapperConstants.ONE_WAY))) {
              fieldMapPrime = new ExcludeFieldMap(classMapPrime);
              MappingUtils.reverseFields(oneWayFieldMap, fieldMapPrime);
              classMapPrime.addFieldMapping(fieldMapPrime);
            }
          }
        }
      }
      // if it is a one way mapping or a method/iterate method mapping we can not bi-directionally map
      // Map Prime could actually be empty
      if (!StringUtils.equals(classMap.getType(), MapperConstants.ONE_WAY)) {
        result.put(ClassMapKeyFactory.createKey(classMap.getDestClassToMap(), classMap.getSrcClassToMap(), classMap.getMapId()), classMapPrime);
      }
    }
    return result;
  }
  

}
