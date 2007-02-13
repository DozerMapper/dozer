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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Iterator;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.CopyByReference;
import net.sf.dozer.util.mapping.fieldmap.Field;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class MappingValidator {

  private final MappingUtils mappingUtils = new MappingUtils();
  private final CollectionUtils collectionUtils = new CollectionUtils();

  public void validateMappingRequest(Object srcObj) {
    if (srcObj == null) {
      throw new MappingException("source object must not be null");
    }
  }

  public void validateMappingRequest(Object srcObj, Object destObj) {
    if (srcObj == null) {
      throw new MappingException("source object must not be null");
    }
    if (destObj == null) {
      throw new MappingException("destination object must not be null");
    }
  }

  public void validateMappingRequest(Object srcObj, Class destClass) {
    if (srcObj == null) {
      throw new MappingException("source object must not be null");
    }
    if (destClass == null) {
      throw new MappingException("destination class must not be null");
    }
  }

  public void validateCopyByReference(FieldMap fieldMap, ClassMap classMap) throws NoSuchMethodException,
      ClassNotFoundException, NoSuchFieldException {
    String destFieldTypeName = null;
    if (classMap.getConfiguration().getCopyByReferences() != null) {
      Iterator copyIterator = classMap.getConfiguration().getCopyByReferences().getCopyByReferences().iterator();
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

  public void validateFieldMapping(FieldMap fm, ClassMap classMap) {
    Field srcField = fm.getSourceField();
    Field destField = fm.getDestField();
    if (srcField == null) {
      throw new MappingException("src field must be specified");
    }
    if (destField == null) {
      throw new MappingException("dest field must be specified");
    }
  }

  public Object validateField(FieldMap fieldMap, Object destObj, Class destFieldType) throws InvocationTargetException,
      IllegalAccessException, InstantiationException, NoSuchMethodException, ClassNotFoundException,
      NoSuchFieldException {
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
      if (collectionUtils.isList(field.getClass()) || collectionUtils.isArray(field.getClass())
          || collectionUtils.isSet(field.getClass()) || mappingUtils.isSupportedMap(field.getClass())) {
        if (collectionUtils.isList(destFieldType) || collectionUtils.isArray(destFieldType)
            || collectionUtils.isSet(destFieldType) || mappingUtils.isSupportedMap(destFieldType)) {
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

  public URL validateURL(String fileName) {
    Loader loader = new Loader();
    URL url = loader.getResource(fileName);
    if (url == null) {
      throw new MappingException("Unable to locate dozer mapping file [" + fileName + "] in the classpath!!!");
    }
    
    InputStream stream = null;
    try {
      stream = url.openStream();
    } catch (IOException e) {
      throw new MappingException("Unable to open URL input stream for dozer mapping file [" + url + "]");
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          throw new MappingException("Unable to close input stream for dozer mapping file [" + url + "]");
        }
      }
    }
    
    return url;
  }
}
