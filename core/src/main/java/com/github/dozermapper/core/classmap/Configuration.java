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

import com.github.dozermapper.core.converters.CustomConverterContainer;
import com.github.dozermapper.core.util.DozerConstants;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Internal class that represents the configuration block specified in the mapping xml file(s). Only intended for
 * internal use.
 */
public class Configuration {

    private Boolean wildcard;
    private Boolean wildcardCaseInsensitive;
    private Boolean stopOnErrors;
    private Boolean trimStrings;
    private Boolean mapNull;
    private Boolean mapEmptyString;
    private String dateFormat;
    private String beanFactory;
    private RelationshipType relationshipType;

    private final CustomConverterContainer customConverters = new CustomConverterContainer();
    private final CopyByReferenceContainer copyByReferences = new CopyByReferenceContainer();
    private final AllowedExceptionContainer allowedExceptions = new AllowedExceptionContainer();

    public AllowedExceptionContainer getAllowedExceptions() {
        return allowedExceptions;
    }

    public CustomConverterContainer getCustomConverters() {
        return customConverters;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String format) {
        dateFormat = format;
    }

    public Boolean getWildcard() {
        return wildcard != null ? wildcard : Boolean.valueOf(DozerConstants.DEFAULT_WILDCARD_POLICY);
    }

    public void setWildcard(Boolean globalWildcardPolicy) {
        wildcard = globalWildcardPolicy;
    }

    public Boolean getWildcardCaseInsensitive() {
        return wildcardCaseInsensitive != null ? wildcardCaseInsensitive : Boolean.valueOf(DozerConstants.DEFAULT_WILDCARD_CASE_INSENSITIVE_POLICY);
    }

    public void setWildcardCaseInsensitive(Boolean wildcardCaseInsensitive) {
        this.wildcardCaseInsensitive = wildcardCaseInsensitive;
    }

    public Boolean getStopOnErrors() {
        return stopOnErrors != null ? stopOnErrors : Boolean.valueOf(DozerConstants.DEFAULT_ERROR_POLICY);
    }

    public void setStopOnErrors(Boolean stopOnErrors) {
        this.stopOnErrors = stopOnErrors;
    }

    public Boolean getMapNull() {
        return mapNull != null ? mapNull : Boolean.valueOf(DozerConstants.DEFAULT_MAP_NULL_POLICY);
    }

    public void setMapNull(Boolean globalMapNullPolicy) {
        mapNull = globalMapNullPolicy;
    }

    public Boolean getMapEmptyString() {
        return mapEmptyString != null ? mapEmptyString : Boolean.valueOf(DozerConstants.DEFAULT_MAP_EMPTY_STRING_POLICY);
    }

    public void setMapEmptyString(Boolean globalMapEmptyStringPolicy) {
        mapEmptyString = globalMapEmptyStringPolicy;
    }

    public String getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(String beanFactory) {
        this.beanFactory = beanFactory;
    }

    public CopyByReferenceContainer getCopyByReferences() {
        return copyByReferences;
    }

    public Boolean getTrimStrings() {
        return trimStrings != null ? trimStrings : Boolean.valueOf(DozerConstants.DEFAULT_TRIM_STRINGS_POLICY);
    }

    public void setTrimStrings(Boolean trimStrings) {
        this.trimStrings = trimStrings;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
