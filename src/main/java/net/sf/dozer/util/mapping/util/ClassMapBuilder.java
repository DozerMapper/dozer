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
import net.sf.dozer.util.mapping.fieldmap.CustomGetSetMethodFieldMap;
import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.GenericFieldMap;
import net.sf.dozer.util.mapping.fieldmap.MapFieldMap;

import org.apache.commons.lang.StringUtils;

/**
 * Internal class for adding implicit field mappings to a ClassMap. Also, builds implicit ClassMap for class mappings
 * that dont have an explicit custom xml mapping. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class ClassMapBuilder {

  public static ClassMap createDefaultClassMap(Configuration globalConfiguration, Class srcClass, Class destClass) {
    ClassMap classMap = new ClassMap(globalConfiguration);
    classMap.setSrcClass(new DozerClass(srcClass.getName(), srcClass, globalConfiguration.getBeanFactory(), null, null, null,
        MapperConstants.DEFAULT_MAP_NULL_POLICY, MapperConstants.DEFAULT_MAP_EMPTY_STRING_POLICY));
    classMap.setDestClass(new DozerClass(destClass.getName(), destClass, globalConfiguration.getBeanFactory(), null, null, null,
        MapperConstants.DEFAULT_MAP_NULL_POLICY, MapperConstants.DEFAULT_MAP_EMPTY_STRING_POLICY));

    // Add default field mappings if wildcard policy is true
    if (classMap.isWildcard()) {
      addDefaultFieldMappings(classMap, globalConfiguration);
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
    Class srcClass = classMap.getSrcClassToMap();
    Class destClass = classMap.getDestClassToMap();

    if (MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null
        || MappingUtils.isSupportedMap(destClass) || classMap.getDestClassMapGetMethod() != null) {
      addMapDefaultFieldMappings(classMap);
      return;
    }

    PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(destClass);
    for (int i = 0; i < destProperties.length; i++) {
      PropertyDescriptor destPropertyDescriptor = destProperties[i];
      String destFieldName = destPropertyDescriptor.getName();

      // If field has already been accounted for, then skip
      if (destFieldName.equals("class") || classMap.getFieldMapUsingDest(destFieldName) != null) {
        continue;
      }

      // If destination field does not have a write method, then skip
      if (destPropertyDescriptor.getWriteMethod() == null) {
        continue;
      }

      PropertyDescriptor srcProperty = ReflectionUtils.findPropertyDescriptor(srcClass, destFieldName);

      // If the sourceProperty is null we know that there is not a corresponding property to map to.
      // If source property does not have a read method, then skip
      if (srcProperty == null || srcProperty.getReadMethod() == null) {
        continue;
      }

      FieldMap map = null;
      DozerField field = new DozerField(destFieldName, null);
      if (field.isCustomGetterSetterField()) {
        map = new CustomGetSetMethodFieldMap(classMap);
      } else {
        map = new GenericFieldMap(classMap);
      }
      map.setSrcField(new DozerField(destFieldName, null));
      map.setDestField(new DozerField(destFieldName, null));
      // add CopyByReferences per defect #1728159
      MappingUtils.applyGlobalCopyByReference(globalConfiguration, map, classMap);
      classMap.addFieldMapping(map);
    }
  }

  private static void addMapDefaultFieldMappings(ClassMap classMap) {
    Class srcClass = classMap.getSrcClassToMap();
    Class destClass = classMap.getDestClassToMap();
    PropertyDescriptor[] properties = null;
    boolean destIsMap = false;
    if (MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(destClass);
    } else if (MappingUtils.isSupportedMap(destClass) || classMap.getDestClassMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(srcClass);
      destIsMap = true;
    } else {
      return;
    }

    for (int i = 0; i < properties.length; i++) {
      String fieldName = properties[i].getName();
      if (fieldName.equals("class")) {
        continue;
      }

      if (destIsMap && classMap.getFieldMapUsingSrc(fieldName) != null) {
        continue;
      }

      if (!destIsMap && classMap.getFieldMapUsingDest(fieldName) != null) {
        continue;
      }

      FieldMap map = new MapFieldMap(classMap);
      DozerField srcField = new DozerField(MappingUtils.isSupportedMap(srcClass) ? MapperConstants.SELF_KEYWORD : fieldName, null);
      srcField.setKey(fieldName);

      if (StringUtils.isNotEmpty(classMap.getSrcClassMapGetMethod()) || StringUtils.isNotEmpty(classMap.getSrcClassMapSetMethod())) {
        srcField.setMapGetMethod(classMap.getSrcClassMapGetMethod());
        srcField.setMapSetMethod(classMap.getSrcClassMapSetMethod());
        srcField.setName(MapperConstants.SELF_KEYWORD);
      }

      DozerField destField = new DozerField(MappingUtils.isSupportedMap(destClass) ? MapperConstants.SELF_KEYWORD : fieldName, null);
      srcField.setKey(fieldName);

      if (StringUtils.isNotEmpty(classMap.getDestClassMapGetMethod())
          || StringUtils.isNotEmpty(classMap.getDestClassMapSetMethod())) {
        destField.setMapGetMethod(classMap.getDestClassMapGetMethod());
        destField.setMapSetMethod(classMap.getDestClassMapSetMethod());
        destField.setName(MapperConstants.SELF_KEYWORD);
      }

      map.setSrcField(srcField);
      map.setDestField(destField);

      classMap.addFieldMapping(map);
    }
  }

}