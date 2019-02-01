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
package com.github.dozermapper.core.fieldmap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.dozermapper.core.BeanBuilder;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.builder.BuilderUtil;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.DozerClass;
import com.github.dozermapper.core.classmap.MappingDirection;
import com.github.dozermapper.core.classmap.RelationshipType;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.DozerPropertyDescriptor;
import com.github.dozermapper.core.propertydescriptor.GetterSetterPropertyDescriptor;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.DozerConstants;
import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class that represents a field mapping definition. Holds all of the information about a single field mapping
 * definition. Only intended for internal use.
 */
public abstract class FieldMap implements Cloneable {

    private final Logger log = LoggerFactory.getLogger(FieldMap.class);

    protected final BeanContainer beanContainer;
    protected final DestBeanCreator destBeanCreator;
    protected final PropertyDescriptorFactory propertyDescriptorFactory;

    private ClassMap classMap;
    private DozerField srcField;
    private DozerField destField;
    private HintContainer srcHintContainer;
    private HintContainer destHintContainer;
    private HintContainer srcDeepIndexHintContainer;
    private HintContainer destDeepIndexHintContainer;
    private MappingDirection type;
    private boolean copyByReference;
    private boolean copyByReferenceOveridden;
    private String mapId;
    private String customConverter;
    private String customConverterId;
    private String customConverterParam;
    private RelationshipType relationshipType;
    private boolean removeOrphans;

    private final ConcurrentMap<Class<?>, DozerPropertyDescriptor> srcPropertyDescriptorMap = new ConcurrentHashMap<>(); // For Caching Purposes
    private final ConcurrentMap<Class<?>, DozerPropertyDescriptor> destPropertyDescriptorMap = new ConcurrentHashMap<>();

    public FieldMap(ClassMap classMap, BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        this.classMap = classMap;
        this.beanContainer = beanContainer;
        this.destBeanCreator = destBeanCreator;
        this.propertyDescriptorFactory = propertyDescriptorFactory;
    }

    public ClassMap getClassMap() {
        return classMap;
    }

    public void setClassMap(ClassMap classMap) {
        this.classMap = classMap;
    }

    public Object getSrcFieldValue(Object runtimeSrcObj) {
        return getSrcPropertyDescriptor(runtimeSrcObj.getClass()).getPropertyValue(runtimeSrcObj);
    }

    public void writeDestValue(Object runtimeDestObj, Object destFieldValue) {
        if (log.isDebugEnabled()) {
            String className = MappingUtils.getClassNameWithoutPackage(runtimeDestObj.getClass());
            log.debug("Getting ready to invoke write method on the destination object. Dest Obj: {}, Dest value: {}",
                      className, destFieldValue);
        }
        DozerPropertyDescriptor propDescriptor = getDestPropertyDescriptor(BuilderUtil.unwrapDestClassFromBuilder(runtimeDestObj));
        propDescriptor.setPropertyValue(runtimeDestObj, destFieldValue, this);
    }

    public Class<?> getDestHintType(Class<?> runtimeSrcClass) {
        if (getDestHintContainer() != null) {
            if (getSrcHintContainer() != null) {
                return getDestHintContainer().getHint(runtimeSrcClass, getSrcHintContainer().getHints());
            } else {
                return getDestHintContainer().getHint();
            }
        } else {
            return runtimeSrcClass;
        }
    }

    public Class<?> getDestFieldType(Class<?> runtimeDestClass) {
        Class<?> result = null;
        if (isDestFieldIndexed()) {
            result = destHintContainer != null ? destHintContainer.getHint() : null;
        }
        if (result == null) {
            result = getDestPropertyDescriptor(runtimeDestClass).getPropertyType();
        }
        return result;
    }

    public Class<?> getSrcFieldType(Class<?> runtimeSrcClass) {
        return getSrcPropertyDescriptor(runtimeSrcClass).getPropertyType();
    }

    /**
     * Gets field param
     *
     * @param runtimeDestClass type
     * @return type
     * @deprecated As of 3.2 release
     */
    @Deprecated
    public Class<?> getDestFieldWriteMethodParameter(Class<?> runtimeDestClass) {
        // 4-07 mht: The getWriteMethod was removed from the prop descriptor interface. This was done as part of
        // refactoring effort to clean up the prop descriptor stuff. The underlying write method should not be exposed.
        // For now, just explicitly cast to the only prop descriptor(getter/setter) that could have been used in this
        // context. The other types of prop descriptors would have failed.
        DozerPropertyDescriptor dpd = getDestPropertyDescriptor(runtimeDestClass);
        return ((GetterSetterPropertyDescriptor)dpd).getWriteMethodPropertyType();
    }

    public Class<?> getGenericType(Class<?> runtimeDestClass) {
        DozerPropertyDescriptor propertyDescriptor = getDestPropertyDescriptor(runtimeDestClass);
        return propertyDescriptor.genericType();
    }

    public Object getDestValue(Object runtimeDestObj) {
        return getDestPropertyDescriptor(BuilderUtil.unwrapDestClassFromBuilder(runtimeDestObj)).getPropertyValue(runtimeDestObj);
    }

    public HintContainer getDestHintContainer() {
        return destHintContainer;
    }

    public void setDestHintContainer(HintContainer destHint) {
        this.destHintContainer = destHint;
    }

    public HintContainer getSrcHintContainer() {
        return srcHintContainer;
    }

    public void setSrcHintContainer(HintContainer sourceHint) {
        this.srcHintContainer = sourceHint;
    }

    public String getSrcFieldMapGetMethod() {
        return !MappingUtils.isBlankOrNull(srcField.getMapGetMethod()) ? srcField.getMapGetMethod() : classMap
                .getSrcClassMapGetMethod();
    }

    public String getSrcFieldMapSetMethod() {
        return !MappingUtils.isBlankOrNull(srcField.getMapSetMethod()) ? srcField.getMapSetMethod() : classMap
                .getSrcClassMapSetMethod();
    }

    public String getDestFieldMapGetMethod() {
        return !MappingUtils.isBlankOrNull(destField.getMapGetMethod()) ? destField.getMapGetMethod() : classMap
                .getDestClassMapGetMethod();
    }

    public String getDestFieldMapSetMethod() {
        return !MappingUtils.isBlankOrNull(destField.getMapSetMethod()) ? destField.getMapSetMethod() : classMap
                .getDestClassMapSetMethod();
    }

    public String getSrcFieldName() {
        return srcField.getName();
    }

    public String getDestFieldName() {
        return destField.getName();
    }

    public String getDestFieldType() {
        return destField.getType();
    }

    public String getSrcFieldType() {
        return srcField.getType();
    }

    public String getDateFormat() {
        if (!MappingUtils.isBlankOrNull(destField.getDateFormat())) {
            return destField.getDateFormat();
        } else if (!MappingUtils.isBlankOrNull(srcField.getDateFormat())) {
            return srcField.getDateFormat();
        } else {
            return classMap.getDateFormat();
        }
    }

    public String getDestFieldCreateMethod() {
        return destField.getCreateMethod();
    }

    public String getSrcFieldCreateMethod() {
        return srcField.getCreateMethod();
    }

    public boolean isDestFieldIndexed() {
        return destField.isIndexed();
    }

    public boolean isSrcFieldIndexed() {
        return srcField.isIndexed();
    }

    public int getSrcFieldIndex() {
        return srcField.getIndex();
    }

    public int getDestFieldIndex() {
        return destField.getIndex();
    }

    public String getSrcFieldTheGetMethod() {
        return srcField.getTheGetMethod();
    }

    public String getDestFieldTheGetMethod() {
        return destField.getTheGetMethod();
    }

    public String getSrcFieldTheSetMethod() {
        return srcField.getTheSetMethod();
    }

    public String getDestFieldTheSetMethod() {
        return destField.getTheSetMethod();
    }

    public String getSrcFieldKey() {
        return srcField.getKey();
    }

    public String getDestFieldKey() {
        return destField.getKey();
    }

    public boolean isDestFieldAccessible() {
        return determineAccess(destField, classMap.getDestClass());
    }

    public boolean isSrcFieldAccessible() {
        return determineAccess(srcField, classMap.getSrcClass());
    }

    private boolean determineAccess(DozerField field, DozerClass clazz) {
        Boolean fieldLevel = field.isAccessible();
        if (fieldLevel != null) {
            return fieldLevel;
        } else {
            return clazz.isAccessible();
        }
    }

    public void setSrcField(DozerField sourceField) {
        this.srcField = sourceField;
    }

    public void setDestField(DozerField destField) {
        this.destField = destField;
    }

    public HintContainer getDestDeepIndexHintContainer() {
        return destDeepIndexHintContainer;
    }

    public void setDestDeepIndexHintContainer(HintContainer destDeepIndexHintHint) {
        this.destDeepIndexHintContainer = destDeepIndexHintHint;
    }

    public HintContainer getSrcDeepIndexHintContainer() {
        return srcDeepIndexHintContainer;
    }

    public void setSrcDeepIndexHintContainer(HintContainer srcDeepIndexHint) {
        this.srcDeepIndexHintContainer = srcDeepIndexHint;
    }

    @Override
    public Object clone() {
        Object result = null;
        try {
            result = super.clone();
        } catch (CloneNotSupportedException e) {
            MappingUtils.throwMappingException(e);
        }
        return result;
    }

    public MappingDirection getType() {
        return type;
    }

    public void setType(MappingDirection type) {
        this.type = type;
    }

    public boolean isCopyByReference() {
        return copyByReference;
    }

    public void setCopyByReference(boolean copyByReference) {
        this.copyByReference = copyByReference;
        this.copyByReferenceOveridden = true;
    }

    /**
     * Return true if is self referencing. Is considered self referencing where no other sources are specified,
     * i.e., no source properties or #CDATA in the xml def.
     *
     * @return true if is self referencing
     */
    protected boolean isSrcSelfReferencing() {
        return getSrcFieldName().equals(DozerConstants.SELF_KEYWORD);
    }

    protected boolean isDestSelfReferencing() {
        return getDestFieldName().equals(DozerConstants.SELF_KEYWORD);
    }

    public boolean isCopyByReferenceOveridden() {
        return copyByReferenceOveridden;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getCustomConverter() {
        return customConverter;
    }

    public void setCustomConverter(String customConverter) {
        this.customConverter = customConverter;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType != null ? relationshipType : classMap.getRelationshipType();
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public void validate() {
        if (srcField == null) {
            MappingUtils.throwMappingException("src field must be specified");
        }
        if (destField == null) {
            MappingUtils.throwMappingException("dest field must be specified");
        }
    }

    protected DozerPropertyDescriptor getSrcPropertyDescriptor(Class<?> runtimeSrcClass) {
        DozerPropertyDescriptor result = this.srcPropertyDescriptorMap.get(runtimeSrcClass);
        if (result == null) {
            String srcFieldMapGetMethod = getSrcFieldMapGetMethod();
            String srcFieldMapSetMethod = getSrcFieldMapSetMethod();
            DozerPropertyDescriptor descriptor = propertyDescriptorFactory.getPropertyDescriptor(runtimeSrcClass,
                                                                                                 getSrcFieldTheGetMethod(), getSrcFieldTheSetMethod(),
                                                                                                 srcFieldMapGetMethod, srcFieldMapSetMethod, isSrcFieldAccessible(),
                                                                                                 isSrcFieldIndexed(), getSrcFieldIndex(),
                                                                                                 getSrcFieldName(), getSrcFieldKey(), isSrcSelfReferencing(), getDestFieldName(),
                                                                                                 getSrcDeepIndexHintContainer(),
                                                                                                 getDestDeepIndexHintContainer(), classMap.getSrcClassBeanFactory(), beanContainer,
                                                                                                 destBeanCreator);
            this.srcPropertyDescriptorMap.putIfAbsent(runtimeSrcClass, descriptor);
            result = descriptor;
        }
        return result;
    }

    protected DozerPropertyDescriptor getDestPropertyDescriptor(Class<?> runtimeDestClass) {
        if (BeanBuilder.class.isAssignableFrom(runtimeDestClass)) {
            MappingUtils.throwMappingException("getDestPropertyDescriptor received builder instead of concrete class - "
                                               + "it's a bug, please post stack trace at https://github.com/DozerMapper/dozer or directly to dmitry@spikhalskiy.com ");
            return null;
        }

        DozerPropertyDescriptor result = this.destPropertyDescriptorMap.get(runtimeDestClass);
        if (result == null) {
            DozerPropertyDescriptor descriptor = propertyDescriptorFactory.getPropertyDescriptor(runtimeDestClass,
                                                                                                 getDestFieldTheGetMethod(), getDestFieldTheSetMethod(), getDestFieldMapGetMethod(),
                                                                                                 getDestFieldMapSetMethod(), isDestFieldAccessible(), isDestFieldIndexed(),
                                                                                                 getDestFieldIndex(),
                                                                                                 getDestFieldName(), getDestFieldKey(), isDestSelfReferencing(), getSrcFieldName(),
                                                                                                 getSrcDeepIndexHintContainer(), getDestDeepIndexHintContainer(),
                                                                                                 classMap.getDestClassBeanFactory(),
                                                                                                 beanContainer, destBeanCreator);

            this.destPropertyDescriptorMap.putIfAbsent(runtimeDestClass, descriptor);
            result = descriptor;
        }
        return result;
    }

    public DozerField getSrcFieldCopy() {
        try {
            return (DozerField)srcField.clone();
        } catch (CloneNotSupportedException e) {
            throw new MappingException(e);
        }
    }

    public DozerField getDestFieldCopy() {
        try {
            return (DozerField)destField.clone();
        } catch (CloneNotSupportedException e) {
            throw new MappingException(e);
        }
    }

    protected DozerField getSrcField() {
        return srcField;
    }

    protected DozerField getDestField() {
        return destField;
    }

    public String getCustomConverterId() {
        return customConverterId;
    }

    public void setCustomConverterId(String customConverterId) {
        this.customConverterId = customConverterId;
    }

    public boolean isRemoveOrphans() {
        return removeOrphans;
    }

    public void setRemoveOrphans(boolean removeOrphans) {
        this.removeOrphans = removeOrphans;
    }

    public boolean isDestMapNull() {
        return classMap.isDestMapNull();
    }

    public boolean isDestMapEmptyString() {
        return classMap.isDestMapEmptyString();
    }

    public boolean isTrimStrings() {
        return classMap.isTrimStrings();
    }

    public boolean isStopOnErrors() {
        return classMap.isStopOnErrors();
    }

    public boolean isNonCumulativeRelationship() {
        return RelationshipType.NON_CUMULATIVE.equals(relationshipType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("source field", srcField).append("destination field",
                                                                                                                 destField).append("type", type)
                .append("customConverter", customConverter).append("relationshipType", relationshipType)
                .append("removeOrphans", removeOrphans).append("mapId", mapId).append("copyByReference", copyByReference).append(
                        "copyByReferenceOveridden", copyByReferenceOveridden).append("srcTypeHint", srcHintContainer).append("destTypeHint",
                                                                                                                             destHintContainer).toString();
    }

    public String getCustomConverterParam() {
        return customConverterParam;
    }

    public void setCustomConverterParam(String customConverterParam) {
        this.customConverterParam = customConverterParam;
    }

}
