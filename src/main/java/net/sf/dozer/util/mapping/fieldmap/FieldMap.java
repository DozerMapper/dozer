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
    if (destField.isIndexed()) {
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

  public DozerField getDestField() {
    return destField;
  }

  public void setDestField(DozerField destField) {
    this.destField = destField;
  }

  public DozerField getSourceField() {
    return sourceField;
  }

  public void setSourceField(DozerField sourceField) {
    this.sourceField = sourceField;
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
    return sourceField.getName().equals(MapperConstants.SELF_KEYWORD);
  }
  
  protected boolean isDestSelfReferencing() {
    return destField.getName().equals(MapperConstants.SELF_KEYWORD);
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
  
  public ClassMap getClassMap() {
    return classMap;
  }

  protected DozerPropertyDescriptorIF getSourcePropertyDescriptor(Class sourceClass) {
    return PropertyDescriptorFactory.getPropertyDescriptor(sourceField, sourceClass, isSourceSelfReferencing(), destField.getName());
  }
  
  protected DozerPropertyDescriptorIF getDestinationPropertyDescriptor(Class destClass) {
    return PropertyDescriptorFactory.getPropertyDescriptor(destField, destClass, isDestSelfReferencing(), sourceField.getName());
  }
  
}