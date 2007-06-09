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

/**
 * Internal class that represents a class mapping definition.  Holds all of the information about a single class mapping.
 * Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class ClassMap {

  private DozerClass sourceClass;
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
  private boolean wildcardOveridden = false;
  private boolean stopOnErrorsOveridden = false;
  private CustomConverterContainer customConverters;
  private String mapId;

  public List getFieldMaps() {
    return fieldMaps;
  }

  public boolean getStopOnErrors() {
    return stopOnErrors;
  }

  public void setStopOnErrors(boolean stopOnErrors) {
    this.stopOnErrors = stopOnErrors;
    this.setStopOnErrorsOveridden(true);
  }

  public List getAllowedExceptions() {
    return allowedExceptions;
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
        String fieldName = fieldMap.getDestField().getName();
        String alternateFieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        
        //Check for exact match on field name.  Also, check against alternate field name.  The alternate field
        //name is used just in case the attribute was specified in the dozer xml file starting in a Capital letter.
        //This prevents the field from getting double mapped in the case that the class attr is named "field1" but in the
        //dozer xml is it specified as "Field1".  This should never happen, but check just in case since the use case doesnt
        //actually error out.  It just double maps which is a problem when the data type is a Collections.
        if (fieldName.equals(destFieldName) || alternateFieldName.equals(destFieldName)) {
          result = fieldMap;
          break;
        }
      }
    }
    return result;
  }

  public FieldMap getFieldMapUsingSource(String sourceFieldName) {
    FieldMap result = null;

    if (fieldMaps != null) {
      for (int i = 0; i < fieldMaps.size(); i++) {
        FieldMap fieldMap = (FieldMap) fieldMaps.get(i);
        String fieldName = fieldMap.getSourceField().getName();

        if ((fieldName != null) && fieldName.equals(sourceFieldName)) {
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
    return wildcard;
  }

  public boolean getWildcard() {
    return wildcard;
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
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public boolean getStopOnErrorsOveridden() {
    return stopOnErrorsOveridden;
  }

  public void setStopOnErrorsOveridden(boolean stopOnErrorsOveridden) {
    this.stopOnErrorsOveridden = stopOnErrorsOveridden;
  }

  public boolean getWildcardOveridden() {
    return wildcardOveridden;
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

  public DozerClass getSourceClass() {
    return sourceClass;
  }

  public void setSourceClass(DozerClass sourceClass) {
    this.sourceClass = sourceClass;
  }

  public DozerClass getDestClass() {
    return destClass;
  }

  public void setDestClass(DozerClass destClass) {
    this.destClass = destClass;
  }

  public String getBeanFactory() {
    return beanFactory;
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

  public boolean getMapNull() {
    return mapNull;
  }

  public void setMapNull(boolean mapNull) {
    this.mapNull = mapNull;
  }

  public boolean getMapEmptyString() {
    return mapEmptyString;
  }

  public void setMapEmptyString(boolean mapEmptyString) {
    this.mapEmptyString = mapEmptyString;
  }
}
