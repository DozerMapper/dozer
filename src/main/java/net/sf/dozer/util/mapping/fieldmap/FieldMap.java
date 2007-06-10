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

import java.lang.reflect.InvocationTargetException;
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
 * Internal interface that represents a field mapping definition.  Holds all of the information about a single field mapping definition.
 * Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public interface FieldMap {
  public Class getDestHintType(Class sourceClass);
  public Class getDestFieldType(Class destClass);
  public Class getSourceFieldType(Class srcClass);
  /**
   * @deprecated  As of 3.2 release
   */
  public Method getDestFieldWriteMethod(Class destClass);
  public Object getSrcFieldValue(Object srcObj);
  public void writeDestinationValue(Object destObj, Object destFieldValue, ClassMap classMap);
  public Object getDestinationObject(Object destObj);
  public Object doesFieldExist(Object destObj, Class destClass);
  public String getDestKey();
  public String getSourceKey();
  public Hint getDestinationTypeHint();
  public void setDestinationTypeHint(Hint destHint);
  public Hint getSourceTypeHint();
  public void setSourceTypeHint(Hint sourceHint);
  public DozerField getDestField();
  public void setDestField(DozerField destField);
  public DozerField getSourceField();
  public void setSourceField(DozerField sourceField);
  public Object clone();
  public String getType();
  public void setType(String type);
  public boolean getCopyByReference();
  public void setCopyByReference(boolean copyByReference);
  public boolean getCopyByReferenceOveridden();
  public String getMapId();
  public void setMapId(String mapId);
  public boolean isGenericFieldMap();
  public String getCustomConverter();
  public void setCustomConverter(String customConverter);
  public void setRelationshipType(String relationshipType);
  public String getRelationshipType();
}