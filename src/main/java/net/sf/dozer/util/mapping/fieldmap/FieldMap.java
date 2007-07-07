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
package net.sf.dozer.util.mapping.fieldmap;

import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.propertydescriptor.DozerPropertyDescriptorIF;
import net.sf.dozer.util.mapping.propertydescriptor.GetterSetterPropertyDescriptor;
import net.sf.dozer.util.mapping.propertydescriptor.PropertyDescriptorFactory;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal class that represents a field mapping definition.  Holds all of the information about a single field mapping definition.
 * Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public abstract class FieldMap implements Cloneable {
  private static final Log log = LogFactory.getLog(FieldMap.class);

  private ClassMap classMap;
  private DozerField sourceField;
  private DozerField destField;
  private Hint sourceTypeHint;
  private Hint destinationTypeHint;
  private String type;
  private boolean copyByReference;
  private boolean copyByReferenceOveridden;
  private String mapId;
  private String customConverter;
  private String relationshipType;
  
  public FieldMap(ClassMap classMap) {
    this.classMap = classMap;
  }
  
  public ClassMap getClassMap() {
    return classMap;
  }
  
  public void setClassMap(ClassMap classMap) {
    this.classMap = classMap;
  }
  
  public Class getDestHintType(Class sourceClass) {
    if (getDestinationTypeHint() != null) {
      if (getSourceTypeHint() != null) {
        return getDestinationTypeHint().getHint(sourceClass, getSourceTypeHint().getHints());
      } else {
        return getDestinationTypeHint().getHint();
      }
    } else {
      return sourceClass;
    }
  }

  public Class getDestFieldType(Class destClass) {
    Class result = null;
    if (isDestFieldIndexed()) {
      result = destinationTypeHint != null ? destinationTypeHint.getHint() : null;
    } 
    if (result == null) {
      result = getDestinationPropertyDescriptor(destClass).getPropertyType();
    }
    return result;
  }
  
  public Class getSourceFieldType(Class srcClass) {
    return getSourcePropertyDescriptor(srcClass).getPropertyType();
  }

  /**
   * @deprecated  As of 3.2 release
   */
  public Method getDestFieldWriteMethod(Class destClass) {
    //4-07 mht:  The getWriteMethod was removed from the prop descriptor interface.  This was done as part of 
    //refactoring effort to clean up the prop descriptor stuff.  The underlying write method should not be exposed.
    //For now, just explicitly cast to the only prop descriptor(getter/setter) that could have been used in this context.
    //The other types of prop descriptors would have failed.
    //
    //TODO: remove this method FieldMap.getDestFieldWriteMethod()
    
    DozerPropertyDescriptorIF dpd = getDestinationPropertyDescriptor(destClass);
    Method result = null;
    try {
      result = ((GetterSetterPropertyDescriptor) dpd).getWriteMethod();
    } catch (Exception e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

  public Object getSrcFieldValue(Object srcObj) {
    // this is mainly for Maps...we cant use the selfdescriptor...so we have to cheat and use this
    if (isSourceSelfReferencing()) {
      return srcObj;
    }
    return getSourcePropertyDescriptor(srcObj.getClass()).getPropertyValue(srcObj);
  }

  public void writeDestinationValue(Object destObj, Object destFieldValue) {
    if (log.isDebugEnabled()) {
      log.debug("Getting ready to invoke write method on the destination object.  Dest Obj: "
          + MappingUtils.getClassNameWithoutPackage(destObj.getClass()) + ", Dest value: " + destFieldValue);
    }
    DozerPropertyDescriptorIF propDescriptor = getDestinationPropertyDescriptor(destObj.getClass()); 
    propDescriptor.setPropertyValue(destObj, destFieldValue, getDestinationTypeHint(), this);
  }

  public Object getDestinationValue(Object destObj) {
    return getDestinationPropertyDescriptor(destObj.getClass()).getPropertyValue(destObj);
  }
  
  public Hint getDestinationTypeHint() {
    return destinationTypeHint;
  }

  public void setDestinationTypeHint(Hint destHint) {
    this.destinationTypeHint = destHint;
  }

  public Hint getSourceTypeHint() {
    return sourceTypeHint;
  }

  public void setSourceTypeHint(Hint sourceHint) {
    this.sourceTypeHint = sourceHint;
  }
  
  public String getSrcFieldMapGetMethod() {
    return !MappingUtils.isBlankOrNull(sourceField.getMapGetMethod()) ? sourceField.getMapGetMethod() : classMap.getSrcClassMapGetMethod(); 
  }
  
  public String getSrcFieldMapSetMethod() {
    return !MappingUtils.isBlankOrNull(sourceField.getMapSetMethod()) ? sourceField.getMapSetMethod() : classMap.getSrcClassMapSetMethod(); 
  }
  
  public String getDestFieldMapGetMethod() {
    return !MappingUtils.isBlankOrNull(destField.getMapGetMethod()) ? destField.getMapGetMethod() : classMap.getDestClassMapGetMethod(); 
  }
  
  public String getDestFieldMapSetMethod() {
    return !MappingUtils.isBlankOrNull(destField.getMapSetMethod()) ? destField.getMapSetMethod() : classMap.getDestClassMapSetMethod(); 
  }
  
  public String getSrcFieldName() {
    return sourceField.getName();
  }
  
  public String getDestFieldName() {
    return destField.getName();
  }
  
  public String getDestFieldType() {
    return destField.getType();
  }
  
  public String getSrcFieldType() {
    return sourceField.getType();
  }
  
  
  public String getSrcFieldDateFormat() {
    return getDateFormat();
  }
  
  public String getDestFieldDateFormat() {
    return getDateFormat();
  }
  
  public String getDateFormat() {
    if (!MappingUtils.isBlankOrNull(destField.getDateFormat())) {
      return destField.getDateFormat();
    } else if (!MappingUtils.isBlankOrNull(sourceField.getDateFormat())) {
      return sourceField.getDateFormat();
    } else {
      return classMap.getDateFormat();
    }
  }
  
  public String getDestFieldCreateMethod() {
    return destField.getCreateMethod();
  }
  
  public String getSrcFieldCreateMethod() {
    return sourceField.getCreateMethod();
  }
  
  public boolean isDestFieldIndexed() {
    return destField.isIndexed();
  }
  
  public boolean isSrcFieldIndexed() {
    return sourceField.isIndexed();
  }
  
  public int getSrcFieldIndex() {
    return sourceField.getIndex();
  }
  
  public int getDestFieldIndex() {
    return destField.getIndex();
  }
  
  public void setDestFieldDateFormat(String dateFormat) {
    destField.setDateFormat(dateFormat);
  }
  
  public void setSrcFieldDateFormat(String dateFormat) {
    sourceField.setDateFormat(dateFormat);
  }
  
  public void setDestFieldTheGetMethod(String theGetMethod) {
    destField.setTheGetMethod(theGetMethod);
  }
  
  public void setSrcFieldTheGetMethod(String theGetMethod) {
    sourceField.setTheGetMethod(theGetMethod);
  }
  
  public void setDestFieldTheSetMethod(String theSetMethod) {
    destField.setTheSetMethod(theSetMethod);
  }
  
  public void setSrcFieldTheSetMethod(String theSetMethod) {
    sourceField.setTheSetMethod(theSetMethod);
  }
  
  public void setDestFieldMapSetMethod(String mapSetMethod) {
    destField.setMapSetMethod(mapSetMethod);
  }
  
  public void setSrcFieldMapSetMethod(String mapSetMethod) {
    sourceField.setMapSetMethod(mapSetMethod);
  }

  public void setDestFieldMapGetMethod(String mapGetMethod) {
    destField.setMapGetMethod(mapGetMethod);
  }
  
  public void setSrcFieldMapGetMethod(String mapGetMethod) {
    sourceField.setMapGetMethod(mapGetMethod);
  }

  public void setDestFieldCreateMethod(String createMethod) {
    destField.setCreateMethod(createMethod);
  }
  
  public void setSrcFieldCreateMethod(String createMethod) {
    sourceField.setCreateMethod(createMethod);
  }
  
  public void setDestFieldKey(String key) {
    destField.setKey(key);
  }
  
  public void setSrcFieldKey(String key) {
    sourceField.setKey(key);
  }
  
  public String getSrcFieldTheGetMethod() {
    return sourceField.getTheGetMethod();
  }
  
  public String getDestFieldTheGetMethod() {
    return destField.getTheGetMethod();
  }
  
  public String getSrcFieldTheSetMethod() {
    return sourceField.getTheSetMethod();
  }
  
  public String getDestFieldTheSetMethod() {
    return destField.getTheSetMethod();
  }
  
  public String getSrcFieldKey() {
    return sourceField.getKey();
  }
  
  public String getDestFieldKey() {
    return destField.getKey();
  }
  
  public boolean isDestFieldAccessible() {
    return destField.isAccessible();
  }
  
  public boolean isSrcFieldAccessible() {
    return sourceField.isAccessible();
  }
  
  public void setSrcFieldAccessible(boolean isAccessible) {
    sourceField.setAccessible(isAccessible);
  }
  
  public void setDestFieldAccessible(boolean isAccessible) {
    destField.setAccessible(isAccessible);
  }
  
  public void setSourceField(DozerField sourceField) {
    this.sourceField = sourceField;
  }
  
  public void setDestField(DozerField destField) {
    this.destField = destField;
  }

  public Object clone() {
    Object result = null;
    try {
      result = super.clone();
    } catch (CloneNotSupportedException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean getCopyByReference() {
    return copyByReference;
  }

  public void setCopyByReference(boolean copyByReference) {
    this.copyByReference = copyByReference;
    this.copyByReferenceOveridden = true;
  }

  /**
   * Return true if is self referencing. Is considered self referencing where no other sources are specified, i.e., no
   * source properties or #CDATA in the xml def.
   */
  protected boolean isSourceSelfReferencing() {
    return getSrcFieldName().equals(MapperConstants.SELF_KEYWORD);
  }
  
  protected boolean isDestSelfReferencing() {
    return getDestFieldName().equals(MapperConstants.SELF_KEYWORD);
  }

  public boolean getCopyByReferenceOveridden() {
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
  
  public String getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
  }
  
  public void validate() {
    if (sourceField == null) {
      MappingUtils.throwMappingException("src field must be specified");
    }
    if (destField == null) {
      MappingUtils.throwMappingException("dest field must be specified");
    }
  }
  
  protected DozerPropertyDescriptorIF getSourcePropertyDescriptor(Class sourceClass) {
    return PropertyDescriptorFactory.getPropertyDescriptor(sourceClass, getSrcFieldTheGetMethod(), getSrcFieldTheSetMethod(), getSrcFieldMapGetMethod(),
        getSrcFieldMapSetMethod(), isSrcFieldAccessible(), isSrcFieldIndexed(), getSrcFieldIndex(), getSrcFieldName(), 
        getSrcFieldKey(), isSourceSelfReferencing(), getDestFieldName());
  }
  
  protected DozerPropertyDescriptorIF getDestinationPropertyDescriptor(Class destClass) {
    return PropertyDescriptorFactory.getPropertyDescriptor(destClass, getDestFieldTheGetMethod(), getDestFieldTheSetMethod(), getDestFieldMapGetMethod(),
        getDestFieldMapSetMethod(), isDestFieldAccessible(), isDestFieldIndexed(), getDestFieldIndex(), getDestFieldName(), 
        getDestFieldKey(), isDestSelfReferencing(), getSrcFieldName());
  }
  
  protected DozerField getSourceField() {
    return sourceField;
  }

  protected DozerField getDestField() {
    return destField;
  }
  
}