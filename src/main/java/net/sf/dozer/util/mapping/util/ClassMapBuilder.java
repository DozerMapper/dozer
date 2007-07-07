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

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.classmap.Configuration;
import net.sf.dozer.util.mapping.classmap.DozerClass;
import net.sf.dozer.util.mapping.converters.CustomConverterContainer;
import net.sf.dozer.util.mapping.fieldmap.CustomGetSetMethodFieldMap;
import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.GenericFieldMap;
import net.sf.dozer.util.mapping.fieldmap.MapFieldMap;

import org.apache.commons.lang.StringUtils;


/**
 * Internal class for adding implicit field mappings to a ClassMap.  Also, builds implicit ClassMap for class mappings that dont
 * have an explicit custom xml mapping.  Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class ClassMapBuilder {
  
  public static ClassMap createDefaultClassMap(Configuration globalConfiguration, Class sourceClass, Class destClass) {
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
      addDefaultFieldMappings(classMap, globalConfiguration);
    }
    // add global custom converters per defect #1728385
    if(globalConfiguration.getCustomConverters() != null) {
      classMap.setCustomConverters(new CustomConverterContainer());
	  classMap.getCustomConverters().setConverters(globalConfiguration.getCustomConverters().getConverters());
    }
    return classMap;
  }
  
  public static void addDefaultFieldMappings(Map customMappings, Configuration globalConfiguration) {
    Set entries = customMappings.entrySet();
    Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      ClassMap classMap = (ClassMap) entry.getValue();

      if (classMap.isWildcard()) {
        addDefaultFieldMappings(classMap, globalConfiguration);
      }
    }
  }
  
  public static void addDefaultFieldMappings(ClassMap classMap, Configuration globalConfiguration) {
    Class sourceClass = classMap.getSourceClass().getClassToMap();
    Class destClass = classMap.getDestClass().getClassToMap();

    if (MappingUtils.isSupportedMap(sourceClass) || classMap.getSourceClass().getMapGetMethod() != null || 
        MappingUtils.isSupportedMap(destClass) || classMap.getDestClass().getMapGetMethod() != null) {
      addMapDefaultFieldMappings(classMap);
      return;
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
      
      DozerField field = new DozerField(destFieldName, null);
      FieldMap map;
      //TODO: remove the if and always create Generic
      if (MappingUtils.isCustomGetterSetterField(field)) {
        map = new CustomGetSetMethodFieldMap();
      } else {
        map = new GenericFieldMap();
      }
      map.setSourceField(new DozerField(destFieldName, null));
      map.setDestField(new DozerField(destFieldName, null));
      // add CopyByReferences per defect #1728159
      MappingValidator.validateCopyByReference(globalConfiguration, map, classMap);
      classMap.addFieldMapping(map);
    }
  }  

  private static void addMapDefaultFieldMappings(ClassMap classMap) {
    Class sourceClass = classMap.getSourceClass().getClassToMap();
    Class destClass = classMap.getDestClass().getClassToMap();
    PropertyDescriptor[] properties = null;
    boolean destIsMap = false;
    if (MappingUtils.isSupportedMap(sourceClass) || classMap.getSourceClass().getMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(destClass);
    } else if (MappingUtils.isSupportedMap(destClass) || classMap.getDestClass().getMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(sourceClass);
      destIsMap = true;
    }
    
    for (int i = 0; i < properties.length; i++) {
      String fieldName = properties[i].getName();
      if (fieldName.equals("class"))  {
        continue;
      }
      
      if (destIsMap && classMap.getFieldMapUsingSource(fieldName) != null) {
        continue;
      }
      
      if (!destIsMap && classMap.getFieldMapUsingDest(fieldName) != null) {
        continue;
      }
    
      FieldMap map = new MapFieldMap();
      DozerField srcField = new DozerField(MappingUtils.isSupportedMap(sourceClass) ? MapperConstants.SELF_KEYWORD : fieldName, null);
      srcField.setKey(fieldName);
      
      if (StringUtils.isNotEmpty(classMap.getSourceClass().getMapGetMethod()) || StringUtils.isNotEmpty(classMap.getSourceClass().getMapSetMethod())) {
        srcField.setMapGetMethod(classMap.getSourceClass().getMapGetMethod());
        srcField.setMapSetMethod(classMap.getSourceClass().getMapSetMethod());
        srcField.setName(MapperConstants.SELF_KEYWORD);
      }
      
      DozerField destField = new DozerField(MappingUtils.isSupportedMap(destClass) ? MapperConstants.SELF_KEYWORD : fieldName, null);
      srcField.setKey(fieldName);
      
      if (StringUtils.isNotEmpty(classMap.getDestClass().getMapGetMethod()) || StringUtils.isNotEmpty(classMap.getDestClass().getMapSetMethod())) {
        destField.setMapGetMethod(classMap.getDestClass().getMapGetMethod());
        destField.setMapSetMethod(classMap.getDestClass().getMapSetMethod());
        destField.setName(MapperConstants.SELF_KEYWORD);
      }
      
      map.setSourceField(srcField);
      map.setDestField(destField);
      
      classMap.addFieldMapping(map);
    }
  }

}