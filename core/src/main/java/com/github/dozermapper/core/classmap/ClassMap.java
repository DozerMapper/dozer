/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.classmap;

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.converters.CustomConverterContainer;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.util.DozerConstants;
import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Internal class that represents a class mapping definition. Holds all of the information about a single class mapping.
 * Only intended for internal use.
 */
public class ClassMap {

    private Configuration globalConfiguration;
    private DozerClass srcClass;
    private DozerClass destClass;
    private List<FieldMap> fieldMaps = new ArrayList<>();
    private List<Class<RuntimeException>> allowedExceptions = new ArrayList<>();
    private MappingDirection type;
    private String dateFormat;
    private String beanFactory;
    private boolean mapNull = DozerConstants.DEFAULT_MAP_NULL_POLICY;
    private boolean mapEmptyString = DozerConstants.DEFAULT_MAP_EMPTY_STRING_POLICY;
    private Boolean wildcard;
    private Boolean wildcardCaseInsensitive;
    private Boolean stopOnErrors;
    private Boolean trimStrings;
    private CustomConverterContainer customConverters;
    private String mapId;
    private RelationshipType relationshipType;

    public ClassMap(Configuration globalConfiguration) {
        this.globalConfiguration = globalConfiguration;
    }

    public List<FieldMap> getFieldMaps() {
        return fieldMaps;
    }

    public boolean isStopOnErrors() {
        return stopOnErrors != null ? stopOnErrors.booleanValue() : globalConfiguration.getStopOnErrors().booleanValue();
    }

    public void setStopOnErrors(Boolean stopOnErrors) {
        this.stopOnErrors = stopOnErrors;
    }

    public boolean isTrimStrings() {
        return trimStrings != null ? trimStrings.booleanValue() : globalConfiguration.getTrimStrings().booleanValue();
    }

    public void setTrimStrings(Boolean trimStrings) {
        this.trimStrings = trimStrings;
    }

    public List<Class<RuntimeException>> getAllowedExceptions() {
        if (!allowedExceptions.isEmpty()) {
            return allowedExceptions;
        } else {
            return globalConfiguration.getAllowedExceptions().getExceptions();
        }
    }

    public void setAllowedExceptions(List<Class<RuntimeException>> allowedExceptions) {
        this.allowedExceptions = allowedExceptions;
    }

    public FieldMap getFieldMapUsingDest(String destFieldName) {
        return getFieldMapUsingDest(destFieldName, false);
    }

    public FieldMap getFieldMapUsingDest(String destFieldName, boolean isMap) {
        if (fieldMaps == null) {
            return null;
        }

        FieldMap result = null;

        for (FieldMap fieldMap : fieldMaps) {
            String fieldName = fieldMap.getDestFieldName();

            if (isMap && MappingUtils.isDeepMapping(fieldName)) {
                fieldName = fieldName.split("\\.")[0];
            }

            String alternateFieldName = provideAlternateName(fieldName);

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

        return result;
    }

    String provideAlternateName(String fieldName) {
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    public FieldMap getFieldMapUsingSrc(String srcFieldName) {
        FieldMap result = null;

        if (fieldMaps != null) {
            for (FieldMap fieldMap : fieldMaps) {
                String fieldName = fieldMap.getSrcFieldName();

                if ((fieldName != null) && fieldName.equals(srcFieldName)) {
                    result = fieldMap;
                    break;
                }
            }
        }
        return result;
    }

    public void setFieldMaps(List<FieldMap> fieldMaps) {
        this.fieldMaps = fieldMaps;
    }

    public void addFieldMapping(FieldMap fieldMap) {
        fieldMaps.add(fieldMap);
    }

    public void removeFieldMapping(FieldMap fieldMap) {
        fieldMaps.remove(fieldMap);
    }

    public boolean isWildcard() {
        return wildcard != null ? wildcard.booleanValue() : globalConfiguration.getWildcard().booleanValue();
    }

    public void setWildcard(Boolean wildcardPolicy) {
        this.wildcard = wildcardPolicy;
    }

    public boolean isWildcardCaseInsensitive() {
        return wildcardCaseInsensitive != null ? wildcardCaseInsensitive.booleanValue() : globalConfiguration.getWildcardCaseInsensitive().booleanValue();
    }

    public void setWildcardCaseInsensitive(Boolean wildcardCaseInsensitive) {
        this.wildcardCaseInsensitive = wildcardCaseInsensitive;
    }

    public MappingDirection getType() {
        return type;
    }

    public void setType(MappingDirection type) {
        this.type = type;
    }

    public String getDateFormat() {
        return !MappingUtils.isBlankOrNull(dateFormat) ? dateFormat : globalConfiguration.getDateFormat();
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public CustomConverterContainer getCustomConverters() {
        return customConverters != null ? customConverters : globalConfiguration.getCustomConverters();
    }

    public void setCustomConverters(CustomConverterContainer customConverters) {
        this.customConverters = customConverters;
    }

    public Class<?> getSrcClassToMap() {
        return srcClass.getClassToMap();
    }

    public Class<?> getDestClassToMap() {
        return destClass.getClassToMap();
    }

    public boolean isDestMapNull() {
        return destClass.getMapNull() != null ? destClass.getMapNull().booleanValue() : mapNull;
    }

    public boolean isSrcMapNull() {
        return srcClass.getMapNull() != null ? srcClass.getMapNull().booleanValue() : mapNull;
    }

    public boolean isDestMapEmptyString() {
        return destClass.getMapEmptyString() != null ? destClass.getMapEmptyString().booleanValue() : mapEmptyString;
    }

    public boolean isSrcMapEmptyString() {
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

    public Configuration getGlobalConfiguration() {
        return globalConfiguration;
    }

    public void setGlobalConfiguration(Configuration globalConfiguration) {
        this.globalConfiguration = globalConfiguration;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType != null ? relationshipType : globalConfiguration.getRelationshipType();
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public DozerClass getSrcClass() {
        return srcClass;
    }

    public DozerClass getDestClass() {
        return destClass;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("source class", getSrcClassName()).append(
                "destination class", getDestClassName()).append("map-id", mapId).toString();
    }
}
