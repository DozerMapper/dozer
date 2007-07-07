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
import net.sf.dozer.util.mapping.classmap.DozerClass;
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
      // set the global configuration for each classmap

      // Apply top level config attrs to ClassMap unless it has been overridden
      if (MappingUtils.isBlankOrNull(classMap.getDateFormat())) {
        classMap.setDateFormat(mappings.getConfiguration().getDateFormat());
      }

      if (!classMap.getWildcardOveridden()) {
        classMap.setWildcard(mappings.getConfiguration().getWildcard());
      }

      if (!classMap.getStopOnErrorsOveridden()) {
        classMap.setStopOnErrors(mappings.getConfiguration().getStopOnErrors());
      }

      if (classMap.getAllowedExceptions().isEmpty() && mappings.getConfiguration().getAllowedExceptions() != null) {
        classMap.setAllowedExceptions(mappings.getConfiguration().getAllowedExceptions().getExceptions());
      }

      if (MappingUtils.isBlankOrNull(classMap.getBeanFactory())) {
        classMap.setBeanFactory(mappings.getConfiguration().getBeanFactory());
      }

      // Apply ClassMap(Mapping) attributes to Dest and Source Class obj's unless it has been overridden
      if (MappingUtils.isBlankOrNull(classMap.getSourceClass().getBeanFactory())) {
        classMap.getSourceClass().setBeanFactory(classMap.getBeanFactory());
      }

      if (MappingUtils.isBlankOrNull(classMap.getDestClass().getBeanFactory())) {
        classMap.getDestClass().setBeanFactory(classMap.getBeanFactory());
      }

      if (classMap.getSourceClass().getMapNull() == null) {
        classMap.getSourceClass().setMapNull(Boolean.valueOf(classMap.getMapNull()));
      }

      if (classMap.getSourceClass().getMapEmptyString() == null) {
        classMap.getSourceClass().setMapEmptyString(Boolean.valueOf(classMap.getMapEmptyString()));
      }

      if (classMap.getDestClass().getMapNull() == null) {
        classMap.getDestClass().setMapNull(Boolean.valueOf(classMap.getMapNull()));
      }

      if (classMap.getDestClass().getMapEmptyString() == null) {
        classMap.getDestClass().setMapEmptyString(Boolean.valueOf(classMap.getMapEmptyString()));
      }

      // add our first class map to the result map
      // initialize PropertyDescriptor Cache
      ReflectionUtils.findPropertyDescriptor(classMap.getSourceClass().getClassToMap(), "");
      ReflectionUtils.findPropertyDescriptor(classMap.getDestClass().getClassToMap(), "");
      String theClassMapKey = ClassMapKeyFactory.createKey(classMap.getSourceClass().getClassToMap(), classMap
          .getDestClass().getClassToMap(), classMap.getMapId());

      /*
       * Check to see if this is a duplicate mapping. If so, throw an Exception
       */
      if (result.containsKey(theClassMapKey)) {
        throw new IllegalArgumentException("Duplicate Class Mapping Found. Source: "
            + classMap.getSourceClass().getClassToMap().getName() + " Destination: "
            + classMap.getDestClass().getClassToMap().getName());
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
      classMapPrime = new ClassMap();
      DozerClass destClass = classMap.getDestClass();
      DozerClass srcClass = classMap.getSourceClass();
      classMapPrime.setSourceClass(new DozerClass(destClass.getName(), destClass.getClassToMap(), destClass
          .getBeanFactory(), destClass.getFactoryBeanId(), destClass.getMapGetMethod(), destClass.getMapSetMethod(),
          destClass.getMapNull(), destClass.getMapEmptyString()));
      classMapPrime.setDestClass(new DozerClass(srcClass.getName(), srcClass.getClassToMap(),
          srcClass.getBeanFactory(), srcClass.getFactoryBeanId(), srcClass.getMapGetMethod(), srcClass
              .getMapSetMethod(), srcClass.getMapNull(), srcClass.getMapEmptyString()));
      classMapPrime.setType(classMap.getType());
      classMapPrime.setWildcard(classMap.isWildcard());
      classMapPrime.setDateFormat(classMap.getDateFormat());
      classMapPrime.setStopOnErrors(classMap.getStopOnErrors());
      classMapPrime.setAllowedExceptions(classMap.getAllowedExceptions());// TODO *NEW*
      classMapPrime.getSourceClass().setCreateMethod(classMap.getDestClass().getCreateMethod());
      classMapPrime.getDestClass().setCreateMethod(classMap.getSourceClass().getCreateMethod());
      if (StringUtils.isNotEmpty(classMap.getMapId())) {
        classMapPrime.setMapId(classMap.getMapId());
      }

      if (classMap.getFieldMaps() != null) {
        Object[] fms = classMap.getFieldMaps().toArray();
        // iterate through the fields and see wether or not they should be mapped
        // one way class mappings we do not need to add any fields
        if (!StringUtils.equals(classMap.getType(), MapperConstants.ONE_WAY)) {
          for (int i = 0; i < fms.length; i++) {
            FieldMap fieldMap = (FieldMap) fms[i];
            MappingValidator.validateFieldMapping(fieldMap);

            /*
             * Apply class level map-get/set-method attributes to each of the field maps
             */
            if (classMap.getSourceClass().getMapGetMethod() != null) {
              fieldMap.getSourceField().setMapGetMethod(classMap.getSourceClass().getMapGetMethod());
            }
            if (classMap.getSourceClass().getMapSetMethod() != null) {
              fieldMap.getSourceField().setMapSetMethod(classMap.getSourceClass().getMapSetMethod());
            }
            if (classMap.getDestClass().getMapGetMethod() != null) {
              fieldMap.getDestField().setMapGetMethod(classMap.getDestClass().getMapGetMethod());
            }
            if (classMap.getDestClass().getMapSetMethod() != null) {
              fieldMap.getDestField().setMapSetMethod(classMap.getDestClass().getMapSetMethod());
            }

            //If we are dealing with a Map data type, transform the field map into a MapFieldMap type
            if (!(fieldMap instanceof ExcludeFieldMap)) {
              if (MappingUtils.isSupportedMap(classMap.getDestClass().getClassToMap())
                  || MappingUtils.isSupportedMap(classMap.getSourceClass().getClassToMap())
                  || MappingUtils.isSupportedMap(fieldMap.getDestFieldType(classMap.getDestClass().getClassToMap()))
                  || MappingUtils.isSupportedMap(fieldMap.getSourceFieldType(classMap.getSourceClass().getClassToMap()))) {
                FieldMap fm = new MapFieldMap(fieldMap);
                classMap.removeFieldMapping(fieldMap);
                classMap.addFieldMapping(fm);
                fieldMap = fm;
              }
            }

            if (!(StringUtils.equals(fieldMap.getType(), MapperConstants.ONE_WAY) && !(fieldMap instanceof ExcludeFieldMap))) {
              // make a prime field map
              fieldMapPrime = (FieldMap) fieldMap.clone();
              // check to see if it is only an exclude one way
              if (fieldMapPrime instanceof ExcludeFieldMap
                  && StringUtils.equals(fieldMap.getType(), MapperConstants.ONE_WAY)) {
                // need to make a generic field map for the other direction
                fieldMapPrime = new GenericFieldMap(classMapPrime);
              }
              // reverse the fields
              MappingUtils.reverseFields(fieldMap, fieldMapPrime);

              if (!(fieldMap instanceof ExcludeFieldMap)) {
                fieldMapPrime.setRelationshipType(fieldMap.getRelationshipType());
              }
              // reverse the hints
              fieldMapPrime.setSourceTypeHint(fieldMap.getDestinationTypeHint());
              fieldMapPrime.setDestinationTypeHint(fieldMap.getSourceTypeHint());
              // iterate through copyByReferences and set accordingly
              if (!(fieldMap instanceof ExcludeFieldMap)) {
                MappingValidator.validateCopyByReference(mappings.getConfiguration(), fieldMap, classMap);
              }
              if (!(fieldMapPrime instanceof ExcludeFieldMap)) {
                MappingValidator.validateCopyByReference(mappings.getConfiguration(), fieldMapPrime, classMapPrime);
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
            MappingValidator.validateFieldMapping(oneWayFieldMap);

            MappingValidator.validateCopyByReference(mappings.getConfiguration(), oneWayFieldMap, classMap);
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
        result.put(ClassMapKeyFactory.createKey(classMap.getDestClass().getClassToMap(), classMap.getSourceClass()
            .getClassToMap(), classMap.getMapId()), classMapPrime);
      }
    }
    return result;
  }

}
