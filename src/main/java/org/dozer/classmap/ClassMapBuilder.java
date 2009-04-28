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
package org.dozer.classmap;

import java.beans.PropertyDescriptor;
import java.util.Set;
import java.util.Collection;
import java.util.Map.Entry;


import org.apache.commons.lang.StringUtils;
import org.dozer.cache.CacheEntry;
import org.dozer.fieldmap.CustomGetSetMethodFieldMap;
import org.dozer.fieldmap.DozerField;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.GenericFieldMap;
import org.dozer.fieldmap.MapFieldMap;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

/**
 * Internal class for adding implicit field mappings to a ClassMap. Also, builds implicit ClassMap for class mappings
 * that dont have an explicit custom xml mapping. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public final class ClassMapBuilder {

  private static final String CLASS = "class";
  private static final String CALLBACK = "callback";
  private static final String CALLBACKS = "callbacks";

  private ClassMapBuilder() {}

  public static ClassMap createDefaultClassMap(Configuration globalConfiguration, Class<?> srcClass, Class<?> destClass) {
    ClassMap classMap = new ClassMap(globalConfiguration);
    classMap.setSrcClass(new DozerClass(srcClass.getName(), srcClass, globalConfiguration.getBeanFactory(), null, null, null,
        DozerConstants.DEFAULT_MAP_NULL_POLICY, DozerConstants.DEFAULT_MAP_EMPTY_STRING_POLICY));
    classMap.setDestClass(new DozerClass(destClass.getName(), destClass, globalConfiguration.getBeanFactory(), null, null, null,
        DozerConstants.DEFAULT_MAP_NULL_POLICY, DozerConstants.DEFAULT_MAP_EMPTY_STRING_POLICY));

    // Add default field mappings if wildcard policy is true
    if (classMap.isWildcard()) {
      addDefaultFieldMappings(classMap, globalConfiguration);
    }

    return classMap;
  }

  public static void addDefaultFieldMappings(ClassMappings classMappings, Configuration globalConfiguration) {
    Set<Entry<String, ClassMap>> entries = classMappings.getAll().entrySet();
    for (Entry<String, ClassMap> entry : entries) {
      ClassMap classMap = entry.getValue();
      if (classMap.isWildcard()) {
        addDefaultFieldMappings(classMap, globalConfiguration);
      }
    }
  }
  
  private static void addDefaultFieldMappings(ClassMap classMap, Configuration globalConfiguration) {
    Class<?> srcClass = classMap.getSrcClassToMap();
    Class<?> destClass = classMap.getDestClassToMap();

    if (MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null
        || MappingUtils.isSupportedMap(destClass) || classMap.getDestClassMapGetMethod() != null) {
      addMapDefaultFieldMappings(classMap);
      return;
    }

    PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(destClass);
    for (PropertyDescriptor destPropertyDescriptor : destProperties) {
      String destFieldName = destPropertyDescriptor.getName();

      // If field has already been accounted for, then skip
      if (CLASS.equals(destFieldName) || classMap.getFieldMapUsingDest(destFieldName) != null) {
        continue;
      }

      // If CGLIB proxied class, dont add internal CGLIB field named "callbacks"
      if ((CALLBACK.equals(destFieldName) || CALLBACKS.equals(destFieldName)) && MappingUtils.isProxy(destClass)) {
        continue;
      }

      // If destination field does not have a write method, then skip
      if (destPropertyDescriptor.getWriteMethod() == null) {
        continue;
      }

      PropertyDescriptor srcProperty = ReflectionUtils.findPropertyDescriptor(srcClass, destFieldName, null);

      // If the sourceProperty is null we know that there is not a corresponding property to map to.
      // If source property does not have a read method, then skip
      if (srcProperty == null || srcProperty.getReadMethod() == null) {
        continue;
      }

      FieldMap map;
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
    Class<?> srcClass = classMap.getSrcClassToMap();
    Class<?> destClass = classMap.getDestClassToMap();
    PropertyDescriptor[] properties;
    boolean destinationIsMap = false;
    if (MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(destClass);
    } else if (MappingUtils.isSupportedMap(destClass) || classMap.getDestClassMapGetMethod() != null) {
      properties = ReflectionUtils.getPropertyDescriptors(srcClass);
      destinationIsMap = true;
    } else {
      return;
    }

    for (PropertyDescriptor property : properties) {
      String fieldName = property.getName();
      if (CLASS.equals(fieldName)) {
        continue;
      }

      if ((CALLBACK.equals(fieldName) || CALLBACKS.equals(fieldName))
              && (MappingUtils.isProxy(destClass) || MappingUtils.isProxy(srcClass))) {
        continue;
      }

      if (destinationIsMap && classMap.getFieldMapUsingSrc(fieldName) != null) {
        continue;
      }

      if (!destinationIsMap && classMap.getFieldMapUsingDest(fieldName, true) != null) {
        continue;
      }

      FieldMap map = new MapFieldMap(classMap);
      DozerField srcField = new DozerField(MappingUtils.isSupportedMap(srcClass) ? DozerConstants.SELF_KEYWORD : fieldName, null);
      srcField.setKey(fieldName);

      if (StringUtils.isNotEmpty(classMap.getSrcClassMapGetMethod()) || StringUtils.isNotEmpty(classMap.getSrcClassMapSetMethod())) {
        srcField.setMapGetMethod(classMap.getSrcClassMapGetMethod());
        srcField.setMapSetMethod(classMap.getSrcClassMapSetMethod());
        srcField.setName(DozerConstants.SELF_KEYWORD);
      }

      DozerField destField = new DozerField(MappingUtils.isSupportedMap(destClass) ? DozerConstants.SELF_KEYWORD : fieldName, null);
      srcField.setKey(fieldName);

      if (StringUtils.isNotEmpty(classMap.getDestClassMapGetMethod())
              || StringUtils.isNotEmpty(classMap.getDestClassMapSetMethod())) {
        destField.setMapGetMethod(classMap.getDestClassMapGetMethod());
        destField.setMapSetMethod(classMap.getDestClassMapSetMethod());
        destField.setName(DozerConstants.SELF_KEYWORD);
      }

      map.setSrcField(srcField);
      map.setDestField(destField);

      classMap.addFieldMapping(map);
    }
  }

}