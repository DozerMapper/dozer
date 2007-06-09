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
package net.sf.dozer.util.mapping.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Configuration;
import net.sf.dozer.util.mapping.fieldmap.CopyByReference;
import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;

/**
 * Internal class used to perform various validations.  Validates mapping requests, field mappings, URL's, etc.  
 * Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public abstract class MappingValidator {

  public static void validateMappingRequest(Object srcObj) {
    if (srcObj == null) {
      MappingUtils.throwMappingException("source object must not be null");
    }
  }

  public static void validateMappingRequest(Object srcObj, Object destObj) {
    if (srcObj == null) {
      MappingUtils.throwMappingException("source object must not be null");
    }
    if (destObj == null) {
      MappingUtils.throwMappingException("destination object must not be null");
    }
  }

  public static void validateMappingRequest(Object srcObj, Class destClass) {
    if (srcObj == null) {
      MappingUtils.throwMappingException("source object must not be null");
    }
    if (destClass == null) {
      MappingUtils.throwMappingException("destination class must not be null");
    }
  }

  public static void validateCopyByReference(Configuration globalConfig, FieldMap fieldMap, ClassMap classMap) {
    String destFieldTypeName = null;
    if (globalConfig.getCopyByReferences() != null) {
      Iterator copyIterator = globalConfig.getCopyByReferences().getCopyByReferences().iterator();
      Class clazz = fieldMap.getDestFieldType(classMap.getDestClass().getClassToMap());
      if (clazz != null) {
        destFieldTypeName = clazz.getName();
      }
      while (copyIterator.hasNext()) {
        CopyByReference copyByReference = (CopyByReference) copyIterator.next();
        if (copyByReference.getReferenceName().equals(destFieldTypeName) && !fieldMap.getCopyByReferenceOveridden()) {
          fieldMap.setCopyByReference(true);
        }
      }
    }
  }

  public static void validateFieldMapping(FieldMap fm, ClassMap classMap) {
    DozerField srcField = fm.getSourceField();
    DozerField destField = fm.getDestField();
    if (srcField == null) {
      MappingUtils.throwMappingException("src field must be specified");
    }
    if (destField == null) {
      MappingUtils.throwMappingException("dest field must be specified");
    }
  }

  public static Object validateField(FieldMap fieldMap, Object destObj, Class destFieldType) {
    Object field = null;
    // verify that the dest obj is not null
    if (destObj == null) {
      return null;
    }
    if (!fieldMap.isGenericFieldMap()) {
    } else {
      // call the getXX method to see if the field is already
      // instantiated
      field = fieldMap.getDestinationObject(destObj);
    }
    // When we are recursing through a list we need to make sure
    // that we are not in the list
    // by checking the destFieldType
    if (field != null) {
      if (CollectionUtils.isList(field.getClass()) || CollectionUtils.isArray(field.getClass())
          || CollectionUtils.isSet(field.getClass()) || MappingUtils.isSupportedMap(field.getClass())) {
        if (CollectionUtils.isList(destFieldType) || CollectionUtils.isArray(destFieldType)
            || CollectionUtils.isSet(destFieldType) || MappingUtils.isSupportedMap(destFieldType)) {
          // do nothing
        } else {
          // this means the getXX field is a List but we
          // are actually trying to map one of its
          // elements
          field = null;
        }
      }
    }
    return field;
  }

  public static URL validateURL(String fileName) {
    Loader loader = new Loader();
    URL url = loader.getResource(fileName);
    if (url == null) {
      MappingUtils.throwMappingException("Unable to locate dozer mapping file [" + fileName + "] in the classpath!!!");
    }
    
    InputStream stream = null;
    try {
      stream = url.openStream();
    } catch (IOException e) {
      MappingUtils.throwMappingException("Unable to open URL input stream for dozer mapping file [" + url + "]");
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          MappingUtils.throwMappingException("Unable to close input stream for dozer mapping file [" + url + "]");
        }
      }
    }
    
    return url;
  }
}
