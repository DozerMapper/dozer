/*
 * Copyright 2005-2009 the original author or authors.
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

import org.apache.commons.lang.StringUtils;
import org.dozer.CustomConverter;
import org.dozer.classmap.AllowedExceptionContainer;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.CopyByReference;
import org.dozer.classmap.CopyByReferenceContainer;
import org.dozer.classmap.DozerClass;
import org.dozer.classmap.MappingDirection;
import org.dozer.classmap.MappingFileData;
import org.dozer.classmap.RelationshipType;
import org.dozer.converters.CustomConverterContainer;
import org.dozer.converters.CustomConverterDescription;
import org.dozer.fieldmap.CustomGetSetMethodFieldMap;
import org.dozer.fieldmap.DozerField;
import org.dozer.fieldmap.ExcludeFieldMap;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.GenericFieldMap;
import org.dozer.fieldmap.HintContainer;
import org.dozer.fieldmap.MapFieldMap;
import org.dozer.util.MappingUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Not thread safe
 *
 * @author dmitry.buzdin
 */
public class MappingBuilder {

  MappingFileData data = new MappingFileData();
  private final List<MappingDefinitionBuilder> mappingBuilders = new ArrayList<MappingDefinitionBuilder>();

  public MappingFileData build() {
    for (MappingDefinitionBuilder builder : mappingBuilders) {
      builder.build();
    }
    return data;
  }

  public MappingConfigurationBuilder configuration() {
    Configuration configuration = new Configuration();
    configuration.setCustomConverters(new CustomConverterContainer());
    configuration.setCopyByReferences(new CopyByReferenceContainer());
    configuration.setAllowedExceptions(new AllowedExceptionContainer());
    data.setConfiguration(configuration);
    return new MappingConfigurationBuilder(configuration);
  }

  public MappingDefinitionBuilder mapping() {
    Configuration configuration = data.getConfiguration();
    ClassMap classMap = new ClassMap(configuration);
    data.getClassMaps().add(classMap);
    MappingDefinitionBuilder mappingDefinitionBuilder = new MappingDefinitionBuilder(classMap);
    mappingBuilders.add(mappingDefinitionBuilder);
    return mappingDefinitionBuilder;
  }

  public static class MappingDefinitionBuilder {

    private ClassMap classMap;
    private final List<FieldContainerBuider> fieldBuilders = new ArrayList<FieldContainerBuider>();

    public MappingDefinitionBuilder(ClassMap classMap) {
      this.classMap = classMap;
    }

    public MappingDefinitionBuilder dateFormat(String dateFormat) {
      classMap.setDateFormat(dateFormat);
      return this;
    }

    public MappingDefinitionBuilder mapNull(boolean value) {
      classMap.setMapNull(value);
      return this;
    }

    public MappingDefinitionBuilder mapEmptyString(boolean value) {
      classMap.setMapEmptyString(value);
      return this;
    }

    // TODO Load class ?
    public MappingDefinitionBuilder beanFactory(String typeName) {
      classMap.setBeanFactory(typeName);
      return this;
    }

    public MappingDefinitionBuilder relationshipType(RelationshipType type) {
      classMap.setRelationshipType(type);
      return this;
    }

    public MappingDefinitionBuilder wildcard(Boolean value) {
      classMap.setWildcard(value);
      return this;
    }

    public MappingDefinitionBuilder trimStrings(Boolean value) {
      classMap.setTrimStrings(value);
      return this;
    }

    public MappingDefinitionBuilder stopOnErrors(Boolean value) {
      classMap.setStopOnErrors(value);
      return this;
    }

    public MappingDefinitionBuilder mapId(String id) {
      classMap.setMapId(id);
      return this;
    }

    public MappingDefinitionBuilder type(MappingDirection type) {
      classMap.setType(type);
      return this;
    }

    public ClassDefinitionBuilder classA(String typeName) {
      Class<?> type = MappingUtils.loadClass(typeName);
      return classA(type);
    }

    public ClassDefinitionBuilder classA(Class type) {
      DozerClass classDefinition = new DozerClass();
      classDefinition.setName(type.getName());
      classMap.setSrcClass(classDefinition);
      return new ClassDefinitionBuilder(classDefinition);
    }

    public ClassDefinitionBuilder classB(String typeName) {
      Class<?> type = MappingUtils.loadClass(typeName);
      return classB(type);
    }

    public ClassDefinitionBuilder classB(Class type) {
      DozerClass classDefinition = new DozerClass();
      classDefinition.setName(type.getName());
      classMap.setDestClass(classDefinition);
      return new ClassDefinitionBuilder(classDefinition);
    }

    public ExcludeFieldBuilder fieldExclude() {
      ExcludeFieldMap excludeFieldMap = new ExcludeFieldMap(classMap);
      ExcludeFieldBuilder builder = new ExcludeFieldBuilder(excludeFieldMap);
      fieldBuilders.add(builder);
      return builder;
    }

    public FieldMapBuilder field() {
      FieldMapBuilder builder = new FieldMapBuilder(classMap);
      fieldBuilders.add(builder);
      return builder;
    }

    public void build() {
      for (FieldContainerBuider builder : fieldBuilders) {
        builder.build();
      }
    }

  }

  public interface FieldContainerBuider {
    FieldBuilder a(String name, String type);

    FieldBuilder b(String name, String type);

    void build();
  }

  public static class ExcludeFieldBuilder implements FieldContainerBuider {

    private ExcludeFieldMap fieldMap;

    public ExcludeFieldBuilder(ExcludeFieldMap fieldMap) {
      this.fieldMap = fieldMap;
    }

    public void type(MappingDirection type) {
      fieldMap.setType(type);
    }

    public FieldBuilder a(String name, String type) {
      DozerField field = prepareField(name, type);
      fieldMap.setSrcField(field);
      return new FieldBuilder(field);
    }

    public FieldBuilder b(String name, String type) {
      DozerField field = prepareField(name, type);
      fieldMap.setDestField(field);
      return new FieldBuilder(field);
    }


    public void build() {
      ClassMap classMap = fieldMap.getClassMap();
      classMap.addFieldMapping(fieldMap);
    }

  }

  public static class FieldMapBuilder implements FieldContainerBuider {

    private ClassMap classMap;
    private DozerField srcField;
    private DozerField destField;
    private MappingDirection type;
    private RelationshipType relationshipType;
    private boolean removeOrphans;
    private HintContainer srcHintContainer;
    private HintContainer destHintContainer;
    private HintContainer srcDeepIndexHintContainer;
    private HintContainer destDeepIndexHintContainer;
    private boolean copyByReference;
    private String mapId;
    private String customConverter;
    private String customConverterId;
    private String customConverterParam;
    private boolean copyByReferenceSet;

    public FieldMapBuilder(ClassMap classMap) {
      this.classMap = classMap;
    }

    public FieldBuilder a(String name, String type) {
      DozerField field = MappingBuilder.prepareField(name, type);
      this.srcField = field;
      return new FieldBuilder(field);
    }

    public FieldBuilder b(String name, String type) {
      DozerField field = prepareField(name, type);
      this.destField = field;
      return new FieldBuilder(field);
    }

    public FieldMapBuilder type(MappingDirection type) {
      this.type = type;
      return this;
    }

    public FieldMapBuilder relationshipType(RelationshipType relationshipType) {
      this.relationshipType = relationshipType;
      return this;
    }

    public FieldMapBuilder removeOrphans(boolean value) {
      this.removeOrphans = value;
      return this;
    }

    public FieldMapBuilder srcHintContainer(String hint) {
      HintContainer hintContainer = new HintContainer();
      hintContainer.setHintName(hint);
      this.srcHintContainer = hintContainer;
      return this;
    }

    public FieldMapBuilder destHintContainer(String hint) {
      HintContainer hintContainer = new HintContainer();
      hintContainer.setHintName(hint);
      this.destHintContainer = hintContainer;
      return this;
    }

    public FieldMapBuilder srcDeepIndexHintContainer(String hint) {
      HintContainer hintContainer = new HintContainer();
      hintContainer.setHintName(hint);
      this.srcDeepIndexHintContainer = hintContainer;
      return this;
    }

    public FieldMapBuilder destDeepIndexHintContainer(String hint) {
      HintContainer hintContainer = new HintContainer();
      hintContainer.setHintName(hint);
      this.destDeepIndexHintContainer = hintContainer;
      return this;
    }

    public FieldMapBuilder copyByReference(boolean value) {
      this.copyByReferenceSet = true;
      this.copyByReference = value;
      return this;
    }

    public FieldMapBuilder mapId(String attribute) {
      this.mapId = attribute;
      return this;
    }

    public FieldMapBuilder customConverter(Class<? extends CustomConverter> type) {
      return customConverter(type.getName());
    }
    
    public FieldMapBuilder customConverter(String typeName) {
      this.customConverter = typeName;
      return this;
    }

    public FieldMapBuilder customConverterId(String attribute) {
      this.customConverterId = attribute;
      return this;
    }

    public FieldMapBuilder customConverterParam(String attribute) {
      this.customConverterParam = attribute;
      return this;
    }

    public void build() {
      // TODO Check Map to Map mapping
      FieldMap result;
      if (srcField.isMapTypeCustomGetterSetterField() || destField.isMapTypeCustomGetterSetterField()
          || classMap.isSrcClassMapTypeCustomGetterSetter() || classMap.isDestClassMapTypeCustomGetterSetter()) {
        result = new MapFieldMap(classMap);
      } else if (srcField.isCustomGetterSetterField() || destField.isCustomGetterSetterField()) {
        result = new CustomGetSetMethodFieldMap(classMap);
      } else {
        result = new GenericFieldMap(classMap);
      }

      result.setSrcField(srcField);
      result.setDestField(destField);
      result.setType(type);
      result.setRelationshipType(relationshipType);
      result.setRemoveOrphans(removeOrphans);

      result.setSrcHintContainer(srcHintContainer);
      result.setDestHintContainer(destHintContainer);
      result.setSrcDeepIndexHintContainer(srcDeepIndexHintContainer);
      result.setDestDeepIndexHintContainer(destDeepIndexHintContainer);

      if (copyByReferenceSet) {
        result.setCopyByReference(copyByReference);
      }
      result.setMapId(mapId);

      result.setCustomConverter(customConverter);
      result.setCustomConverterId(customConverterId);
      result.setCustomConverterParam(customConverterParam);

      classMap.addFieldMapping(result);
    }

  }

  public static class FieldBuilder {
    private DozerField field;

    public FieldBuilder(DozerField field) {
      this.field = field;
    }

    public void dateFormat(String attribute) {
      field.setDateFormat(attribute);
    }

    public void theGetMethod(String attribute) {
      field.setTheGetMethod(attribute);
    }

    public void theSetMethod(String attribute) {
      field.setTheSetMethod(attribute);
    }

    public void mapGetMethod(String attribute) {
      field.setMapGetMethod(attribute);
    }

    public void mapSetMethod(String attribute) {
      field.setMapSetMethod(attribute);
    }

    public void key(String attribute) {
      field.setKey(attribute);
    }

    public void createMethod(String attribute) {
      field.setCreateMethod(attribute);
    }

    public void accessible(boolean b) {
      field.setAccessible(b);
    }

    public DozerField build() {
      return field;
    }

  }

  private static DozerField prepareField(String name, String type) {
    String fieldName;
    String fieldType = null;
    if (isIndexed(name)) {
      fieldName = getFieldNameOfIndexedField(name);
    } else {
      fieldName = name;
    }
    if (StringUtils.isNotEmpty(type)) {
      fieldType = type;
    }
    DozerField field = new DozerField(fieldName, fieldType);
    if (isIndexed(name)) {
      field.setIndexed(true);
      field.setIndex(getIndexOfIndexedField(name));
    }
    return field;
  }


  private static boolean isIndexed(String fieldName) {
    return (fieldName != null) && (fieldName.matches(".+\\[\\d+\\]"));
  }

  static String getFieldNameOfIndexedField(String fieldName) {
    return fieldName == null ? null : fieldName.replaceAll("\\[\\d+\\]$", "");
  }

  private static int getIndexOfIndexedField(String fieldName) {
    return Integer.parseInt(fieldName.replaceAll(".*\\[", "").replaceAll("\\]", ""));
  }

  public static class ClassDefinitionBuilder {

    private DozerClass definition;

    public ClassDefinitionBuilder(DozerClass definition) {
      this.definition = definition;
    }

    public ClassDefinitionBuilder mapGetMethod(String name) {
      definition.setMapGetMethod(name);
      return this;
    }

    public ClassDefinitionBuilder mapSetMethod(String name) {
      definition.setMapSetMethod(name);
      return this;
    }

    public ClassDefinitionBuilder beanFactory(String beanFactory) {
      definition.setBeanFactory(beanFactory);
      return this;
    }

    public ClassDefinitionBuilder factoryBeanId(String id) {
      definition.setFactoryBeanId(id);
      return this;
    }

    public ClassDefinitionBuilder createMethod(String name) {
      definition.setCreateMethod(name);
      return this;
    }

    public ClassDefinitionBuilder mapNull(Boolean value) {
      definition.setMapNull(value);
      return this;
    }

    public ClassDefinitionBuilder mapEmptyString(Boolean value) {
      definition.setMapEmptyString(value);
      return this;
    }

  }

  public static class MappingConfigurationBuilder {

    private Configuration configuration;

    private CustomConverterDescription converterDescription;

    public MappingConfigurationBuilder(Configuration configuration) {
      this.configuration = configuration;
    }

    public MappingConfigurationBuilder stopOnErrors(Boolean value) {
      configuration.setStopOnErrors(value);
      return this;
    }

    public MappingConfigurationBuilder dateFormat(String format) {
      configuration.setDateFormat(format);
      return this;
    }

    public MappingConfigurationBuilder wildcard(Boolean value) {
      configuration.setWildcard(value);
      return this;
    }

    public MappingConfigurationBuilder trimStrings(Boolean value) {
      configuration.setTrimStrings(value);
      return this;
    }

    public MappingConfigurationBuilder relationshipType(RelationshipType value) {
      configuration.setRelationshipType(value);
      return this;
    }

    public MappingConfigurationBuilder beanFactory(String name) {
      configuration.setBeanFactory(name);
      return this;
    }

    public CustomConverterBuilder customConverter(String type) {
      Class<?> aClass = MappingUtils.loadClass(type);
      return customConverter(aClass);
    }

    // TODO Constraint with Generic
    public CustomConverterBuilder customConverter(Class type) {
      converterDescription = new CustomConverterDescription();
      converterDescription.setType(type);
      configuration.getCustomConverters().addConverter(converterDescription);
      return new CustomConverterBuilder(converterDescription);
    }

    public MappingConfigurationBuilder copyByReference(String typeMask) {
      CopyByReference copyByReference = new CopyByReference(typeMask);
      configuration.getCopyByReferences().add(copyByReference);
      return this;
    }

    public MappingConfigurationBuilder allowedException(String type) {
      Class<?> exceptionType = MappingUtils.loadClass(type);
      return allowedException(exceptionType);
    }

    // TODO Restrict with generic
    public MappingConfigurationBuilder allowedException(Class type) {
      if (!RuntimeException.class.isAssignableFrom(type)) {
        MappingUtils.throwMappingException("allowed-exception Type must extend java.lang.RuntimeException: "
            + type.getName());
      }
      configuration.getAllowedExceptions().getExceptions().add(type);
      return this;
    }

  }

  public static class CustomConverterBuilder {
    private CustomConverterDescription converterDescription;

    public CustomConverterBuilder(CustomConverterDescription converterDescription) {
      this.converterDescription = converterDescription;
    }

    public CustomConverterBuilder classA(String type) {
      Class<?> aClass = MappingUtils.loadClass(type);
      return classA(aClass);
    }

    public CustomConverterBuilder classA(Class type) {
      converterDescription.setClassA(type);
      return this;
    }

    public CustomConverterBuilder classB(String type) {
      Class<?> aClass = MappingUtils.loadClass(type);
      return classB(aClass);
    }

    public CustomConverterBuilder classB(Class type) {
      converterDescription.setClassB(type);
      return this;
    }

  }

}
