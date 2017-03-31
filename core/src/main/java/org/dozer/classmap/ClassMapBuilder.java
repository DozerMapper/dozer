/**
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
package org.dozer.classmap;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapping;
import org.dozer.classmap.generator.BeanMappingGenerator;
import org.dozer.classmap.generator.ClassLevelFieldMappingGenerator;
import org.dozer.classmap.generator.GeneratorUtils;
import org.dozer.classmap.generator.MappingType;
import org.dozer.fieldmap.DozerField;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.GenericFieldMap;
import org.dozer.fieldmap.MapFieldMap;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Internal class for adding implicit field mappings to a ClassMap. Also, builds implicit ClassMap for class mappings
 * that don't have an explicit custom xml mapping. Only intended for internal use.
 *
 * @author tierney.matt
 * @author garsombke.franz
 */
public final class ClassMapBuilder {

  static final List<ClassMappingGenerator> buildTimeGenerators = new ArrayList<ClassMappingGenerator>();
  static final List<ClassMappingGenerator> runTimeGenerators = new ArrayList<ClassMappingGenerator>();

  static {
    buildTimeGenerators.add(new ClassLevelFieldMappingGenerator());
    buildTimeGenerators.add(new AnnotationPropertiesGenerator());
    buildTimeGenerators.add(new AnnotationFieldsGenerator());
    buildTimeGenerators.add(new MapMappingGenerator());
    buildTimeGenerators.add(new BeanMappingGenerator());
    buildTimeGenerators.add(new CollectionMappingGenerator());

    runTimeGenerators.add(new ClassLevelFieldMappingGenerator());
    runTimeGenerators.add(new AnnotationPropertiesGenerator());
    runTimeGenerators.add(new AnnotationFieldsGenerator());
    runTimeGenerators.add(new MapMappingGenerator());
    runTimeGenerators.add(new BeanMappingGenerator());
  }

  private ClassMapBuilder() {
  }

  // TODO Cover with test cases
  // TODO Remove duplication
  // TODO Use Dozer Builder if possible ?
  // TODO Add Exclude Annotation process by separate generator
  // TODO Add Pluggable Buidlers
  // TODO Add field matcher based builder
  // TODO Add annotation based builder

  /**
   * Builds new default mapping on-the-fly for previously unknown mapped class pairs.
   *
   * @param globalConfiguration
   * @param srcClass
   * @param destClass
   * @return
   */
  public static ClassMap createDefaultClassMap(Configuration globalConfiguration, Class<?> srcClass, Class<?> destClass) {
    ClassMap classMap = new ClassMap(globalConfiguration);
    classMap.setSrcClass(new DozerClass(srcClass.getName(), srcClass, globalConfiguration.getBeanFactory(), null, null, null, null,
        globalConfiguration.getMapNull(), globalConfiguration.getMapEmptyString(), false));
    classMap.setDestClass(new DozerClass(destClass.getName(), destClass, globalConfiguration.getBeanFactory(), null, null, null,
        null, globalConfiguration.getMapNull(), globalConfiguration.getMapEmptyString(), false));

    generateMapping(classMap, globalConfiguration, buildTimeGenerators);
    return classMap;
  }

  /**
   * Prepares default mappings based on provided mapping definition
   *
   * @param classMappings
   * @param globalConfiguration
   */
  public static void addDefaultFieldMappings(ClassMappings classMappings, Configuration globalConfiguration) {
    Set<Entry<String, ClassMap>> entries = classMappings.getAll().entrySet();
    for (Entry<String, ClassMap> entry : entries) {
      ClassMap classMap = entry.getValue();
      generateMapping(classMap, globalConfiguration, runTimeGenerators);
    }
  }

  private static void generateMapping(ClassMap classMap, Configuration configuration, List<ClassMappingGenerator> mappingGenerators) {
    if (!classMap.isWildcard()) {
      return;
    }

    for (ClassMappingGenerator generator : mappingGenerators) {
      if (generator.accepts(classMap)) {
        if (generator.apply(classMap, configuration)) {
          return;
        }
      }
    }
  }

  public static interface ClassMappingGenerator {

    boolean accepts(ClassMap classMap);

    /**
     *
     * @return true if we should stop after applied
     */
    boolean apply(ClassMap classMap, Configuration configuration);

  }

  public static class MapMappingGenerator implements ClassMappingGenerator {

    public boolean accepts(ClassMap classMap) {
      Class<?> srcClass = classMap.getSrcClassToMap();
      Class<?> destClass = classMap.getDestClassToMap();

      return MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null
          || MappingUtils.isSupportedMap(destClass) || classMap.getDestClassMapGetMethod() != null;
    }

    public boolean apply(ClassMap classMap, Configuration configuration) {
      Class<?> srcClass = classMap.getSrcClassToMap();
      Class<?> destClass = classMap.getDestClassToMap();
      PropertyDescriptor[] properties;
      boolean destinationIsMap = false;

      if (MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null) {
        properties = ReflectionUtils.getPropertyDescriptors(destClass);
      } else {
        properties = ReflectionUtils.getPropertyDescriptors(srcClass);
        destinationIsMap = true;
      }

      for (PropertyDescriptor property : properties) {
        String fieldName = property.getName();

        if (GeneratorUtils.shouldIgnoreField(fieldName, srcClass, destClass)) {
          continue;
        }

        // already mapped
        if (destinationIsMap && classMap.getFieldMapUsingSrc(fieldName) != null) {
          continue;
        }

        // already mapped
        if (!destinationIsMap && classMap.getFieldMapUsingDest(fieldName, true) != null) {
          continue;
        }

        FieldMap fieldMap = new MapFieldMap(classMap);
        DozerField srcField = new DozerField(MappingUtils.isSupportedMap(srcClass) ? DozerConstants.SELF_KEYWORD : fieldName, null);
        srcField.setKey(fieldName);

        if (StringUtils.isNotEmpty(classMap.getSrcClassMapGetMethod())
            || StringUtils.isNotEmpty(classMap.getSrcClassMapSetMethod())) {
          srcField.setMapGetMethod(classMap.getSrcClassMapGetMethod());
          srcField.setMapSetMethod(classMap.getSrcClassMapSetMethod());
          srcField.setName(DozerConstants.SELF_KEYWORD);
        }

        DozerField destField = new DozerField(MappingUtils.isSupportedMap(destClass) ? DozerConstants.SELF_KEYWORD : fieldName,
            null);
        srcField.setKey(fieldName);

        if (StringUtils.isNotEmpty(classMap.getDestClassMapGetMethod())
            || StringUtils.isNotEmpty(classMap.getDestClassMapSetMethod())) {
          destField.setMapGetMethod(classMap.getDestClassMapGetMethod());
          destField.setMapSetMethod(classMap.getDestClassMapSetMethod());
          destField.setName(DozerConstants.SELF_KEYWORD);
        }

        fieldMap.setSrcField(srcField);
        fieldMap.setDestField(destField);

        classMap.addFieldMapping(fieldMap);
      }
      return true;
    }
  }

  public static class CollectionMappingGenerator implements ClassMappingGenerator {

    public boolean accepts(ClassMap classMap) {
      Class<?> srcClass = classMap.getSrcClassToMap();
      Class<?> destClass = classMap.getDestClassToMap();
      return MappingUtils.isSupportedCollection(srcClass) && MappingUtils.isSupportedCollection(destClass);
    }

    public boolean apply(ClassMap classMap, Configuration configuration) {
      FieldMap fieldMap = new GenericFieldMap(classMap);
      DozerField selfReference = new DozerField(DozerConstants.SELF_KEYWORD, null);
      fieldMap.setSrcField(selfReference);
      fieldMap.setDestField(selfReference);
      classMap.addFieldMapping(fieldMap);
      return true;
    }
  }

  public static class AnnotationPropertiesGenerator implements ClassMappingGenerator {

    public boolean accepts(ClassMap classMap) {
      return true;
    }

    public boolean apply(ClassMap classMap, Configuration configuration) {
      Class<?> srcType = classMap.getSrcClassToMap();
      PropertyDescriptor[] srcProperties = ReflectionUtils.getPropertyDescriptors(srcType);
      for (PropertyDescriptor property : srcProperties) {
        Method readMethod = property.getReadMethod();
        if (readMethod != null) {
          Mapping mapping = readMethod.getAnnotation(Mapping.class);
          String propertyName = property.getName();
          if (mapping != null) {
            String pairName = mapping.value().trim();
            GeneratorUtils.addGenericMapping(MappingType.GETTER_TO_SETTER, classMap, configuration,
                    propertyName, pairName.isEmpty() ? propertyName : pairName);
          }
        }
      }

      Class<?> destType = classMap.getDestClassToMap();
      PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(destType);
      for (PropertyDescriptor property : destProperties) {
        Method readMethod = property.getReadMethod();
        if (readMethod != null) {
          Mapping mapping = readMethod.getAnnotation(Mapping.class);
          String propertyName = property.getName();
          if (mapping != null) {
            String pairName = mapping.value().trim();
            GeneratorUtils.addGenericMapping(MappingType.GETTER_TO_SETTER, classMap, configuration,
                    pairName.isEmpty() ? propertyName : pairName, propertyName);
          }
        }
      }

      return false;
    }
  }

  public static class AnnotationFieldsGenerator implements ClassMappingGenerator {

    public boolean accepts(ClassMap classMap) {
      return true;
    }

    public boolean apply(ClassMap classMap, Configuration configuration) {
      Class<?> srcType = classMap.getSrcClassToMap();
      do {
        for (Field field : srcType.getDeclaredFields()) {
          Mapping mapping = field.getAnnotation(Mapping.class);
          String fieldName = field.getName();
          if (mapping != null) {
            String pairName = mapping.value().trim();
            GeneratorUtils.addGenericMapping(MappingType.FIELD_TO_FIELD, classMap, configuration,
                    fieldName, pairName.isEmpty() ? fieldName : pairName);
          }
        }
        srcType = srcType.getSuperclass();
      } while (srcType != null);
      
      Class<?> destType = classMap.getDestClassToMap();
      do {
        for (Field field : destType.getDeclaredFields()) {
          Mapping mapping = field.getAnnotation(Mapping.class);
          String fieldName = field.getName();
          if (mapping != null) {
            String pairName = mapping.value().trim();
            GeneratorUtils.addGenericMapping(MappingType.FIELD_TO_FIELD, classMap, configuration,
                    pairName.isEmpty() ? fieldName : pairName, fieldName);
          }
        }
        destType = destType.getSuperclass();
      } while (destType != null);
      
      return false;
    }
  }
}