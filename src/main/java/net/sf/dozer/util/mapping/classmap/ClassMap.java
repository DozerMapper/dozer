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
package net.sf.dozer.util.mapping.classmap;

import java.util.ArrayList;
import java.util.List;

import net.sf.dozer.util.mapping.converters.CustomConverterContainer;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Internal class that represents a class mapping definition. Holds all of the information about a single class mapping.
 * Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class ClassMap {

  private Configuration globalConfiguration;
  private DozerClass srcClass;
  private DozerClass destClass;
  private List fieldMaps = new ArrayList();
  private List allowedExceptions = new ArrayList();
  private String type;
  private String dateFormat;
  private String beanFactory;
  private boolean mapNull = MapperConstants.DEFAULT_MAP_NULL_POLICY;
  private boolean mapEmptyString = MapperConstants.DEFAULT_MAP_EMPTY_STRING_POLICY;
  private boolean wildcard = MapperConstants.DEFAULT_WILDCARD_POLICY;
  private boolean stopOnErrors = MapperConstants.DEFAULT_ERROR_POLICY;
  private boolean trimStrings = MapperConstants.DEFAULT_TRIM_STRINGS_POLICY;
  private boolean wildcardOveridden = false;
  private boolean stopOnErrorsOveridden = false;
  private boolean trimStringsOveridden = false;
  private CustomConverterContainer customConverters;
  private String mapId;

  public ClassMap(Configuration globalConfiguration) {
    this.globalConfiguration = globalConfiguration;
  }

  public List getFieldMaps() {
    return fieldMaps;
  }

  public boolean isStopOnErrors() {
    return stopOnErrorsOveridden ? stopOnErrors : globalConfiguration.isStopOnErrors();
  }

  public void setStopOnErrors(boolean stopOnErrors) {
    this.stopOnErrors = stopOnErrors;
    this.setStopOnErrorsOveridden(true);
  }
  
  public boolean isTrimStrings() {
    return trimStringsOveridden ? trimStrings : globalConfiguration.isTrimStrings();
  }

  public void setTrimStrings(boolean trimStrings) {
    this.trimStrings = trimStrings;
    this.setTrimStringsOveridden(true);
  }

  public List getAllowedExceptions() {
    if (!allowedExceptions.isEmpty()) {
      return allowedExceptions;
    } else {
      return globalConfiguration.getAllowedExceptions() != null ? globalConfiguration.getAllowedExceptions().getExceptions()
          : allowedExceptions;
    }
  }

  public void setAllowedExceptions(List allowedExceptions) {
    this.allowedExceptions = allowedExceptions;
  }

  public FieldMap getFieldMapUsingDest(String destFieldName) {
    FieldMap result = null;

    if (fieldMaps != null) {
      int size = fieldMaps.size();
      for (int i = 0; i < size; i++) {
        FieldMap fieldMap = (FieldMap) fieldMaps.get(i);
        String fieldName = fieldMap.getDestFieldName();
        String alternateFieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

        // Check for exact match on field name. Also, check against alternate field name. The alternate field
        // name is used just in case the attribute was specified in the dozer xml file starting in a Capital letter.
        // This prevents the field from getting double mapped in the case that the class attr is named "field1" but in
        // the dozer xml is it specified as "Field1". This should never happen, but check just in case since the use case
        // doesnt actually error out. It just double maps which is a problem when the data type is a Collections.
        if (fieldName.equals(destFieldName) || alternateFieldName.equals(destFieldName)) {
          result = fieldMap;
          break;
        }
      }
    }
    return result;
  }

  public FieldMap getFieldMapUsingSrc(String srcFieldName) {
    FieldMap result = null;

    if (fieldMaps != null) {
      for (int i = 0; i < fieldMaps.size(); i++) {
        FieldMap fieldMap = (FieldMap) fieldMaps.get(i);
        String fieldName = fieldMap.getSrcFieldName();

        if ((fieldName != null) && fieldName.equals(srcFieldName)) {
          result = fieldMap;
          break;
        }
      }
    }
    return result;
  }

  public void setFieldMaps(List fieldMaps) {
    this.fieldMaps = fieldMaps;
  }

  public void addFieldMapping(FieldMap fieldMap) {
    fieldMaps.add(fieldMap);
  }

  public void removeFieldMapping(FieldMap fieldMap) {
    fieldMaps.remove(fieldMap);
  }

  public boolean isWildcard() {
    return wildcardOveridden ? wildcard : globalConfiguration.isWildcard();
  }

  public void setWildcard(boolean wildcardPolicy) {
    this.wildcard = wildcardPolicy;
    this.setWildcardOveridden(true);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDateFormat() {
    return !MappingUtils.isBlankOrNull(dateFormat) ? dateFormat : globalConfiguration.getDateFormat();
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public void setStopOnErrorsOveridden(boolean stopOnErrorsOveridden) {
    this.stopOnErrorsOveridden = stopOnErrorsOveridden;
  }
  
  public void setTrimStringsOveridden(boolean trimStringsOveridden) {
    this.trimStringsOveridden = trimStringsOveridden;
  }

  public void setWildcardOveridden(boolean wildcardOveridden) {
    this.wildcardOveridden = wildcardOveridden;
  }

  public CustomConverterContainer getCustomConverters() {
    return customConverters;
  }

  public void setCustomConverters(CustomConverterContainer customConverters) {
    this.customConverters = customConverters;
  }

  public Class getSrcClassToMap() {
    return srcClass.getClassToMap();
  }

  public Class getDestClassToMap() {
    return destClass.getClassToMap();
  }

  public boolean isDestClassMapNull() {
    return destClass.getMapNull() != null ? destClass.getMapNull().booleanValue() : mapNull;
  }

  public boolean isSrcClassMapNull() {
    return srcClass.getMapNull() != null ? srcClass.getMapNull().booleanValue() : mapNull;
  }

  public boolean isDestClassMapEmptyString() {
    return destClass.getMapEmptyString() != null ? destClass.getMapEmptyString().booleanValue() : mapEmptyString;
  }

  public boolean isSrcClassMapEmptyString() {
    return srcClass.getMapEmptyString() != null ? srcClass.getMapEmptyString().booleanValue() : mapEmptyString;
  }

  public String getDestClassBeanFactory() {
    return !MappingUtils.isBlankOrNull(destClass.getBeanFactory()) ? destClass.getBeanFactory() : getBeanFactory();
  }

  public String getSrcClassBeanFactory() {
    return !MappingUtils.isBlankOrNull(srcClass.getBeanFactory()) ? srcClass.getBeanFactory() : getBeanFactory();
  }

  public String getDestClassBeanFactoryId() {
    return destClass.getFactoryBeanId();
  }

  public String getSrcClassBeanFactoryId() {
    return srcClass.getFactoryBeanId();
  }

  public String getSrcClassMapGetMethod() {
    return srcClass.getMapGetMethod();
  }

  public String getSrcClassMapSetMethod() {
    return srcClass.getMapSetMethod();
  }

  public String getDestClassMapGetMethod() {
    return destClass.getMapGetMethod();
  }

  public String getDestClassMapSetMethod() {
    return destClass.getMapSetMethod();
  }

  public String getDestClassCreateMethod() {
    return destClass.getCreateMethod();
  }

  public String getSrcClassCreateMethod() {
    return srcClass.getCreateMethod();
  }

  public void setSrcClassCreateMethod(String createMethod) {
    srcClass.setCreateMethod(createMethod);
  }

  public void setDestClassCreateMethod(String createMethod) {
    destClass.setCreateMethod(createMethod);
  }

  public boolean isDestClassMapTypeCustomGetterSetter() {
    return destClass.isMapTypeCustomGetterSetterClass();
  }

  public boolean isSrcClassMapTypeCustomGetterSetter() {
    return srcClass.isMapTypeCustomGetterSetterClass();
  }

  public void setSrcClass(DozerClass srcClass) {
    this.srcClass = srcClass;
  }

  public void setDestClass(DozerClass destClass) {
    this.destClass = destClass;
  }

  public String getDestClassName() {
    return destClass.getName();
  }

  public String getSrcClassName() {
    return srcClass.getName();
  }

  public String getBeanFactory() {
    return !MappingUtils.isBlankOrNull(beanFactory) ? beanFactory : globalConfiguration.getBeanFactory();
  }

  public void setBeanFactory(String beanFactory) {
    this.beanFactory = beanFactory;
  }

  public String getMapId() {
    return mapId;
  }

  public void setMapId(String mapId) {
    this.mapId = mapId;
  }

  public void setMapNull(boolean mapNull) {
    this.mapNull = mapNull;
  }

  public void setMapEmptyString(boolean mapEmptyString) {
    this.mapEmptyString = mapEmptyString;
  }
  
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
    append("source class", getSrcClassName()).
    append("destination class", getDestClassName()).
    append("map-id", mapId).
    toString();
  }
}
