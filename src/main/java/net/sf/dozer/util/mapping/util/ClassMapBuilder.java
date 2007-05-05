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
 * Internal class for adding implicit field mappings to a ClassMap.  Also, builds implicit ClassMap for class mappings that dont
 * have an explicit custom xml mapping.  Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class ClassMapBuilder {
  
  public ClassMap createDefaultClassMap(Configuration globalConfiguration, Class sourceClass, Class destClass) {
    ClassMap classMap = new ClassMap();
    classMap.setSourceClass(new DozerClass(sourceClass.getName(), sourceClass, globalConfiguration.getBeanFactory(), null, null, null, 
        Boolean.valueOf(MapperConstants.DEFAULT_MAP_NULL_POLICY), Boolean.valueOf(MapperConstants.DEFAULT_MAP_EMPTY_STRING_POLICY)));
    classMap.setDestClass(new DozerClass(destClass.getName(), destClass, globalConfiguration.getBeanFactory(), null, null, null,
        Boolean.valueOf(MapperConstants.DEFAULT_MAP_NULL_POLICY), Boolean.valueOf(MapperConstants.DEFAULT_MAP_EMPTY_STRING_POLICY)));

      classMap.setWildcard(globalConfiguration.getWildcard());
      classMap.setStopOnErrors(globalConfiguration.getStopOnErrors());
      classMap.setDateFormat(globalConfiguration.getDateFormat());
      classMap.setBeanFactory(globalConfiguration.getBeanFactory());
      if (globalConfiguration.getAllowedExceptions() != null) {
        classMap.setAllowedExceptions(globalConfiguration.getAllowedExceptions().getExceptions());
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
  
  public void addDefaultFieldMappings(ClassMap classMap) {
    Class sourceClass = classMap.getSourceClass().getClassToMap();
    Class destClass = classMap.getDestClass().getClassToMap();

    if (MappingUtils.isSupportedMap(sourceClass) || classMap.getSourceClass().getMapGetMethod() != null || 
        MappingUtils.isSupportedMap(destClass) || classMap.getDestClass().getMapGetMethod() != null) {
      addMapDefaultFieldMappings(classMap);
    }
    
    PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(destClass);
    for (int i = 0; i < destProperties.length; i++) {
      PropertyDescriptor destPropertyDescriptor = destProperties[i]; 
      String destFieldName = destPropertyDescriptor.getName();

      //If field has already been accounted for, then skip
      if (destFieldName.equals("class") || classMap.getFieldMapUsingDest(destFieldName) != null) {
        continue;
      }
      
      //If destination field does not have a write method, then skip
      if (destPropertyDescriptor.getWriteMethod() == null) {
        continue;
      }
      
      PropertyDescriptor sourceProperty = ReflectionUtils.findPropertyDescriptor(sourceClass, destFieldName);

      // If the sourceProperty is null we know that there is not a corresponding property to map to.  
      // If source property does not have a read method, then skip
      if (sourceProperty == null || sourceProperty.getReadMethod() == null) { 
        continue;
      }

      GenericFieldMap map = new GenericFieldMap();
      map.setSourceField(new DozerField(destFieldName, null));
      map.setDestField(new DozerField(destFieldName, null));
      classMap.addFieldMapping(map);
    }
  }  

  private void addMapDefaultFieldMappings(ClassMap classMap) {
    Class sourceClass = classMap.getSourceClass().getClassToMap();
    Class destClass = classMap.getDestClass().getClassToMap();
    PropertyDescriptor[] properties = null;
    boolean destIsMap = false;
    // determine which is the map
    if (MappingUtils.isSupportedMap(sourceClass) || classMap.getSourceClass().getMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(destClass);
      destIsMap = false;
    } else if (MappingUtils.isSupportedMap(destClass) || classMap.getDestClass().getMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(sourceClass);
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


}