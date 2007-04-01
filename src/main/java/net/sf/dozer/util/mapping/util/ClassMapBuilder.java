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

import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Configuration;
import net.sf.dozer.util.mapping.fieldmap.DozerClass;
import net.sf.dozer.util.mapping.fieldmap.ExcludeFieldMap;
import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.GenericFieldMap;
import net.sf.dozer.util.mapping.fieldmap.MapFieldMap;


/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class ClassMapBuilder {
  private final ReflectionUtils reflectionUtils = new ReflectionUtils();
  private final MappingUtils mappingUtils = new MappingUtils();
  
  public ClassMap createDefaultClassMap(Configuration globalConfiguration, Class sourceClass, Class destClass) {
    ClassMap classMap = new ClassMap();
    classMap.setSourceClass(new DozerClass(sourceClass.getName(), sourceClass, null, null, null, null, 
        Boolean.valueOf(MapperConstants.DEFAULT_MAP_NULL_POLICY), Boolean.valueOf(MapperConstants.DEFAULT_MAP_EMPTY_STRING_POLICY)));
    classMap.setDestClass(new DozerClass(destClass.getName(), destClass, null, null, null, null,
        Boolean.valueOf(MapperConstants.DEFAULT_MAP_NULL_POLICY), Boolean.valueOf(MapperConstants.DEFAULT_MAP_EMPTY_STRING_POLICY)));

    if (globalConfiguration == null) {
      // a default class map inherits the global properties
      classMap.setWildcard(classMap.getConfiguration().getWildcard());
      classMap.setStopOnErrors(classMap.getConfiguration().getStopOnErrors());
      classMap.setDateFormat(classMap.getConfiguration().getDateFormat());
    } else {
      classMap.setWildcard(globalConfiguration.getWildcard());
      classMap.setStopOnErrors(globalConfiguration.getStopOnErrors());
      classMap.setDateFormat(globalConfiguration.getDateFormat());
      classMap.setConfiguration(globalConfiguration);
      if (globalConfiguration.getAllowedExceptions() != null) {
        classMap.setAllowedExceptions(globalConfiguration.getAllowedExceptions().getExceptions());
      }
    }
    // Add default field mappings if wildcard policy is true
    if (classMap.isWildcard()) {
      addDefaultFieldMappings(classMap);
    }

    return classMap;
  }
  
  public void addDefaultFieldMappings(Map customMappings) {
    Set entries = customMappings.entrySet();
    Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      ClassMap classMap = (ClassMap) entry.getValue();

      if (classMap.isWildcard()) {
        addDefaultFieldMappings(classMap);
      }
    }
  }

  public void addMapDefaultFieldMappings(ClassMap classMap) {
    Class sourceClass = classMap.getSourceClass().getClassToMap();
    Class destClass = classMap.getDestClass().getClassToMap();
    PropertyDescriptor[] properties = null;
    boolean destIsMap = false;
    // determine which is the map
    if (mappingUtils.isSupportedMap(sourceClass) || classMap.getSourceClass().getMapGetMethod() != null) {
      properties = reflectionUtils.getPropertyDescriptors(destClass);
      destIsMap = false;
    } else if (mappingUtils.isSupportedMap(destClass) || classMap.getDestClass().getMapGetMethod() != null) {
      properties = reflectionUtils.getPropertyDescriptors(sourceClass);
      destIsMap = true;
    } else {
      return;
    }
    for (int i = 0; i < properties.length; i++) {
      String fieldName = properties[i].getName();
      // if the sourceProperty is null we know that there is not a
      // corresponding property to map to.
      if (fieldName.equals("class")) {
        continue;
      }
      MapFieldMap map = new MapFieldMap();
      if (destIsMap) {
        map.setSourceField(new DozerField(fieldName, null));
        DozerField df = new DozerField(MapperConstants.SELF_KEYWORD, null);
        if (StringUtils.isNotEmpty(classMap.getDestClass().getMapGetMethod())) {
          df.setMapGetMethod(classMap.getDestClass().getMapGetMethod());
          df.setTheGetMethod(classMap.getDestClass().getMapGetMethod());
        } else {
          df.setMapGetMethod("get");
          df.setTheGetMethod("get");
        }
        if (StringUtils.isNotEmpty(classMap.getDestClass().getMapSetMethod())) {
          df.setMapSetMethod(classMap.getDestClass().getMapSetMethod());
          df.setTheSetMethod(classMap.getDestClass().getMapSetMethod());
        } else {
          df.setMapSetMethod("put");
          df.setTheSetMethod("put");
        }
        map.setDestField(df);
        FieldMap fieldMap = classMap.getFieldMapUsingSource(fieldName);
        // this means we have an existing fieldmap. set default values accordingly
        if (fieldMap != null && !(fieldMap instanceof ExcludeFieldMap)) {
          map.getSourceField().setTheGetMethod(fieldMap.getSourceField().getTheGetMethod());
          map.getSourceField().setTheSetMethod(fieldMap.getSourceField().getTheSetMethod());
          df.setKey(fieldMap.getDestField().getKey());
          map.getSourceField().setKey(fieldMap.getDestField().getKey());
          classMap.removeFieldMapping(fieldMap);
        } else if (fieldMap instanceof ExcludeFieldMap) {
          fieldMap.setDestField(df);
          continue;
        }
      } else {
        map.setDestField(new DozerField(fieldName, null));
        DozerField sourceField = new DozerField(MapperConstants.SELF_KEYWORD, null);
        if (StringUtils.isNotEmpty(classMap.getSourceClass().getMapGetMethod())) {
          sourceField.setMapGetMethod(classMap.getSourceClass().getMapGetMethod());
          sourceField.setTheGetMethod(classMap.getSourceClass().getMapGetMethod());
        } else {
          sourceField.setMapGetMethod("get");
          sourceField.setTheGetMethod("get");
        }
        if (StringUtils.isNotEmpty(classMap.getSourceClass().getMapSetMethod())) {
          sourceField.setMapSetMethod(classMap.getSourceClass().getMapSetMethod());
          sourceField.setTheSetMethod(classMap.getSourceClass().getMapSetMethod());
        } else {
          sourceField.setMapSetMethod("put");
          sourceField.setTheSetMethod("put");
        }
        map.setSourceField(sourceField);
        FieldMap fieldMap = classMap.getFieldMapUsingDest(fieldName);
        // this means we have an existing fieldmap. set default values accordingly
        if (fieldMap != null && !(fieldMap instanceof ExcludeFieldMap)) {
          map.getDestField().setTheGetMethod(fieldMap.getDestField().getTheGetMethod());
          map.getDestField().setTheSetMethod(fieldMap.getDestField().getTheSetMethod());
          sourceField.setKey(fieldMap.getSourceField().getKey());
          map.getDestField().setKey(fieldMap.getSourceField().getKey());
          classMap.removeFieldMapping(fieldMap);
        } else if (fieldMap instanceof ExcludeFieldMap) {
          fieldMap.setSourceField(sourceField);
          continue;
        }
      }
      classMap.addFieldMapping(map);
    }
  }

  private void addDefaultFieldMappings(ClassMap classMap) {
    Class sourceClass = classMap.getSourceClass().getClassToMap();
    Class destClass = classMap.getDestClass().getClassToMap();
    PropertyDescriptor[] destProperties = reflectionUtils.getPropertyDescriptors(destClass);
    for (int i = 0; i < destProperties.length; i++) {
      String destFieldName = destProperties[i].getName();
      PropertyDescriptor sourceProperty = reflectionUtils.getPropertyDescriptor(sourceClass, destFieldName);
      // if the sourceProperty is null we know that there is not a
      // corresponding property to map to.
      if (destFieldName.equals("class") || sourceProperty == null) {
        continue;
      }

      if (classMap.getFieldMapUsingDest(destFieldName) != null) {
        continue;
      }
      GenericFieldMap map = new GenericFieldMap();
      map.setSourceField(new DozerField(destFieldName, null));
      map.setDestField(new DozerField(destFieldName, null));
      classMap.addFieldMapping(map);
    }
  }
}