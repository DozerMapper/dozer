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

import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Configuration;
import net.sf.dozer.util.mapping.fieldmap.DozerClass;
import net.sf.dozer.util.mapping.fieldmap.ExcludeFieldMap;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.GenericFieldMap;
import net.sf.dozer.util.mapping.fieldmap.Mappings;

import org.apache.commons.lang.StringUtils;

/**
 * @author garsombke.franz
 */
public class MappingsParser {

  private final MappingUtils mappingUtils = new MappingUtils();
  private final ReflectionUtils reflectionUtils = new ReflectionUtils();
  private final MappingValidator mappingValidator = new MappingValidator();
  private final ClassMapBuilder classMapBuilder = new ClassMapBuilder();

  public Map parseMappings(Mappings mappings) throws IllegalArgumentException {
    Iterator iterator = null;
    Map result = new HashMap();
    FieldMap fieldMapPrime = null;
    try {
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
        if (mappings.getConfiguration() != null) {
          classMap.setConfiguration(mappings.getConfiguration());
        } else {
          classMap.setConfiguration(new Configuration());
        }

        // Apply top level config attrs to ClassMap unless it has been overridden
        if (mappingUtils.isBlankOrNull(classMap.getDateFormat())) {
          classMap.setDateFormat(classMap.getConfiguration().getDateFormat());
        }

        if (!classMap.getWildcardOveridden()) {
          classMap.setWildcard(classMap.getConfiguration().getWildcard());
        }

        if (!classMap.getStopOnErrorsOveridden()) {
          classMap.setStopOnErrors(classMap.getConfiguration().getStopOnErrors());
        }
        
        if (classMap.getAllowedExceptions().isEmpty() && classMap.getConfiguration().getAllowedExceptions() != null) {
        	classMap.setAllowedExceptions(classMap.getConfiguration().getAllowedExceptions().getExceptions());
        }

        if (mappingUtils.isBlankOrNull(classMap.getBeanFactory())) {
          classMap.setBeanFactory(classMap.getConfiguration().getBeanFactory());
        }

        // Apply ClassMap(Mapping) attributes to Dest and Source Class obj's unless it has been overridden
        if (mappingUtils.isBlankOrNull(classMap.getSourceClass().getBeanFactory())) {
          classMap.getSourceClass().setBeanFactory(classMap.getBeanFactory());
        }

        if (mappingUtils.isBlankOrNull(classMap.getDestClass().getBeanFactory())) {
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
        reflectionUtils.findPropertyDescriptor(classMap.getSourceClass().getClassToMap(), "");
        reflectionUtils.findPropertyDescriptor(classMap.getDestClass().getClassToMap(), "");
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
        
        //Check to see if this is a duplicate map id, irregardless of src and dest class names.  Duplicate map-ids are
        //not allowed
        if (!mappingUtils.isBlankOrNull(classMap.getMapId())) {
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
        classMapPrime.setDestClass(new DozerClass(srcClass.getName(), srcClass.getClassToMap(), srcClass
            .getBeanFactory(), srcClass.getFactoryBeanId(), srcClass.getMapGetMethod(), srcClass.getMapSetMethod(),
            srcClass.getMapNull(), srcClass.getMapEmptyString()));
        classMapPrime.setType(classMap.getType());
        classMapPrime.setWildcard(classMap.isWildcard());
        classMapPrime.setDateFormat(classMap.getDateFormat());
        classMapPrime.setStopOnErrors(classMap.getStopOnErrors());
        classMapPrime.setAllowedExceptions(classMap.getAllowedExceptions());//TODO *NEW*
        classMapPrime.setConfiguration(classMap.getConfiguration());
        if (classMap.getSourceClass().getMapGetMethod() != null) {
          classMap.getSourceClass().setCustomMap(true);
        }
        if (classMap.getDestClass().getMapGetMethod() != null) {
          classMap.getDestClass().setCustomMap(true);
        }
        classMapPrime.getSourceClass().setCustomMap(classMap.getDestClass().isCustomMap());
        classMapPrime.getDestClass().setCustomMap(classMap.getSourceClass().isCustomMap());
        classMapPrime.getSourceClass().setCreateMethod(classMap.getDestClass().getCreateMethod());
        classMapPrime.getDestClass().setCreateMethod(classMap.getSourceClass().getCreateMethod());
        if (StringUtils.isNotEmpty(classMap.getMapId())) {
          classMapPrime.setMapId(classMap.getMapId());
        }
        // if it is mapping map backed object need to create fieldmaps
        if (StringUtils.isNotEmpty(classMap.getMapId())) {
          classMapBuilder.addMapDefaultFieldMappings(classMap);
        }
        if (classMap.getFieldMaps() != null) {
          iterator = classMap.getFieldMaps().iterator();
          // iterate through the fields and see wether or not they should be mapped
          // one way class mappings we do not need to add any fields
          if (!StringUtils.equals(classMap.getType(), MapperConstants.ONE_WAY)) {
            while (iterator.hasNext()) {
              FieldMap fieldMap = (FieldMap) iterator.next();
              mappingValidator.validateFieldMapping(fieldMap, classMap);
              mappingUtils.isMethodMap(fieldMap);
              mappingUtils.isCustomMap(fieldMap);
              if (!(StringUtils.equals(fieldMap.getType(), MapperConstants.ONE_WAY) && !(fieldMap instanceof ExcludeFieldMap))) {
                // make a prime field map
                fieldMapPrime = (FieldMap) fieldMap.clone();
                // check to see if it is only an exclude one way
                if (fieldMapPrime instanceof ExcludeFieldMap
                    && StringUtils.equals(fieldMap.getType(), MapperConstants.ONE_WAY)) {
                  // need to make a generic field map for the other direction
                  fieldMapPrime = new GenericFieldMap();
                }
                // reverse the fields
                mappingUtils.reverseFields(fieldMap, fieldMapPrime);
                // determine if we have method mapping
                mappingUtils.isMethodMap(fieldMapPrime);
                mappingUtils.isCustomMap(fieldMapPrime);

                if (fieldMapPrime instanceof GenericFieldMap && !(fieldMap instanceof ExcludeFieldMap)) {
                  ((GenericFieldMap) fieldMapPrime).setRelationshipType(((GenericFieldMap) fieldMap)
                      .getRelationshipType());
                }
                // reverse the hints
                fieldMapPrime.setSourceTypeHint(fieldMap.getDestinationTypeHint());
                fieldMapPrime.setDestinationTypeHint(fieldMap.getSourceTypeHint());
                // iterate through copyByReferences and set accordingly
                if (!(fieldMap instanceof ExcludeFieldMap)) {
                  mappingValidator.validateCopyByReference(fieldMap, classMap);
                }
                if (!(fieldMapPrime instanceof ExcludeFieldMap)) {
                  mappingValidator.validateCopyByReference(fieldMapPrime, classMapPrime);
                }
              } else { // if it is a one-way field map make the other field map excluded
                // make a prime field map
                fieldMapPrime = new ExcludeFieldMap();
                mappingUtils.reverseFields(fieldMap, fieldMapPrime);
              }
              classMapPrime.addFieldMapping((FieldMap) fieldMapPrime);
            }
          } else {
            // since it is one-way...we still need to validate if it has some type of method mapping and validate the
            // field maps
            while (iterator.hasNext()) {
              FieldMap oneWayFieldMap = (FieldMap) iterator.next();
              mappingValidator.validateFieldMapping(oneWayFieldMap, classMap);
              mappingUtils.isMethodMap(oneWayFieldMap);
              mappingUtils.isCustomMap(oneWayFieldMap);
              mappingValidator.validateCopyByReference(oneWayFieldMap, classMap);
              // check to see if we need to exclude the map
              if ((StringUtils.equals(oneWayFieldMap.getType(), MapperConstants.ONE_WAY))) {
                fieldMapPrime = new ExcludeFieldMap();
                mappingUtils.reverseFields(oneWayFieldMap, fieldMapPrime);
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
    } catch (Throwable t) {
      mappingUtils.throwMappingException(t);
    }
    return result;
  }

}
