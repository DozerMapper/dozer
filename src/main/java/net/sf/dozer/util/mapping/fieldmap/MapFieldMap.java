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

import java.util.HashMap;

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.propertydescriptor.DozerPropertyDescriptorIF;
import net.sf.dozer.util.mapping.propertydescriptor.JavaBeanPropertyDescriptor;
import net.sf.dozer.util.mapping.propertydescriptor.MapPropertyDescriptor;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

/**
 * Only intended for internal use.  Handles field mapping involving Map Backed properties.  Map backed property support
 * includes top level class Map data type, field level Map data type, and custom Map backed objects that provide custom map-get/set methods.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class MapFieldMap extends FieldMap {

  public MapFieldMap() {
  }

  public MapFieldMap(FieldMap fieldMap) {
    setCopyByReference(fieldMap.getCopyByReference());
    setCustomConverter(fieldMap.getCustomConverter());
    setDestField(fieldMap.getDestField());
    setDestinationTypeHint(fieldMap.getDestinationTypeHint());
    setMapId(fieldMap.getMapId());
    setRelationshipType(fieldMap.getRelationshipType());
    setSourceField(fieldMap.getSourceField());
    setSourceTypeHint(fieldMap.getSourceTypeHint());
    setType(fieldMap.getType());
  }

  public void writeDestinationValue(Object destObj, Object destFieldValue, ClassMap classMap) {
    DozerPropertyDescriptorIF propDescriptor;
    Object targetObject = destObj;

    if (getDestField().getName().equals(MapperConstants.SELF_KEYWORD) || (destFieldValue != null && MappingUtils.isSupportedMap(destFieldValue.getClass()))) {
      // Destination value is already a Map, so just use normal
      propDescriptor = super.getDestinationPropertyDescriptor(destObj.getClass());
    } else {

      if (getDestField().getMapGetMethod() != null || MappingUtils.isSupportedMap(determineActualPropertyType(getDestField(), destObj))) {
        // Need to dig out actual destination Map object and use map property descriptor to set the value on that target object....
        PrepareTargetObjectResult result = prepareTargetObject(destObj, classMap);
        targetObject = result.targetObject;
        propDescriptor = result.propDescriptor;
      } else {
        propDescriptor = super.getDestinationPropertyDescriptor(destObj.getClass());
      }
    }

    try {
      propDescriptor.setPropertyValue(targetObject, destFieldValue, getDestinationTypeHint(), classMap);
    } catch (NoSuchFieldException e) {
      MappingUtils.throwMappingException(e);
    } catch (NoSuchMethodException e) {
      MappingUtils.throwMappingException(e);
    }

  }

  public Object getSrcFieldValue(Object srcObj) {
    DozerPropertyDescriptorIF propDescriptor;
    Object targetObject = srcObj;

    if (getSourceField().getName().equals(MapperConstants.SELF_KEYWORD)) {
      propDescriptor = super.getSourcePropertyDescriptor(srcObj.getClass());
    } else {
      Class ac = determineActualPropertyType(getSourceField(), srcObj);
      if ((getSourceField().getMapGetMethod() != null)
          || (this.getMapId() == null && MappingUtils.isSupportedMap(ac) && getSourceTypeHint() == null)) {
        //Need to dig out actual map object by using getter on the field.  Use actual map object to get the field value
        DozerPropertyDescriptorIF d = new JavaBeanPropertyDescriptor(srcObj.getClass(), getSourceField().getName(),
            getSourceField().isIndexed(), getSourceField().getIndex());

        try {
          targetObject = d.getPropertyValue(srcObj);
        } catch (Exception e) {
          MappingUtils.throwMappingException(e);
        }

        propDescriptor = new MapPropertyDescriptor(ac, getSourceField().getName(), getSourceField().isIndexed(),
            getDestField().getIndex(), MappingUtils.isSupportedMap(ac) ? "put" : getSourceField().getMapSetMethod(),
            MappingUtils.isSupportedMap(ac) ? "get" : getSourceField().getMapGetMethod(),
            getSourceField().getKey() != null ? getSourceField().getKey() : getDestField().getName());
      } else {
        propDescriptor = super.getSourcePropertyDescriptor(srcObj.getClass());
      }
    }

    Object result = null;
    try {
      if (targetObject != null) {
        result = propDescriptor.getPropertyValue(targetObject);
      }
    } catch (Exception e) {
      MappingUtils.throwMappingException(e);
    }

    return result;

  }

  private PrepareTargetObjectResult prepareTargetObject(Object destObj,
      ClassMap classMap) {
    Object targetObject = destObj;
    DozerPropertyDescriptorIF d = new JavaBeanPropertyDescriptor(destObj.getClass(), getDestField().getName(),
        getDestField().isIndexed(), getDestField().getIndex());

    Class c = null;
    try {
      c = d.getPropertyType();
      targetObject = d.getPropertyValue(destObj);
      if (targetObject == null) {
        if (getDestinationTypeHint() != null) {
          if (MappingUtils.isSupportedMap(c)) {
            if (MappingUtils.isSupportedMap(getDestinationTypeHint().getHint())) {
              c = getDestinationTypeHint().getHint();
            }
          } else {
            c = getDestinationTypeHint().getHint();
          }

        }
        // TODO - call destbeancreator
        if (MappingUtils.isSupportedMap(c)) {
          targetObject = new HashMap();
        } else {
          targetObject = ReflectionUtils.newInstance(c);
        }
        d.setPropertyValue(destObj, targetObject, null, classMap);
      }

    } catch (Exception e) {
      MappingUtils.throwMappingException(e);
    }

    return new PrepareTargetObjectResult(targetObject, new MapPropertyDescriptor(c, getDestField().getName(),
        getDestField().isIndexed(), getDestField().getIndex(), MappingUtils.isSupportedMap(c) ? "put" : getDestField()
            .getMapSetMethod(), MappingUtils.isSupportedMap(c) ? "get" : getDestField().getMapGetMethod(),
        getDestField().getKey() != null ? getDestField().getKey() : getSourceField().getName()));

  }
  
  private Class determineActualPropertyType(DozerField field, Object targetObj) {
    // Dig out actual Map object by calling getter on top level object
    DozerPropertyDescriptorIF d = new JavaBeanPropertyDescriptor(targetObj.getClass(), field.getName(), field
        .isIndexed(), field.getIndex());
    Class ac = null;
    try {
      ac = d.getPropertyType();
    } catch (Throwable e1) {
      MappingUtils.throwMappingException(e1);
    }
    return ac;
  }

  private class PrepareTargetObjectResult {
    private Object targetObject;
    private MapPropertyDescriptor propDescriptor;
    public PrepareTargetObjectResult(Object targetObject, MapPropertyDescriptor propDescriptor) {
      this.targetObject = targetObject;
      this.propDescriptor = propDescriptor;
    }
  }

}
