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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.cache.Cache;
import net.sf.dozer.util.mapping.converters.CustomConverterContainer;
import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.GenericFieldMap;

import org.apache.commons.lang.StringUtils;

/**
 * Internal class that provides various mapping utilities used throughout the code base.  Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public abstract class MappingUtils {

  //only making public temporarily while refactoring.  This static data should be relocated
  public static Map storedFactories = Collections.synchronizedMap(new HashMap());
  
  public static String getClassNameWithoutPackage(Class clazz) {
    Package pckage = clazz.getPackage();
    int pckageIndex = 0;
    if (pckage != null) {
      pckageIndex = pckage.getName().length() + 1;
    }
    return clazz.getName().substring(pckageIndex);
  }

  public static boolean isSupportedCollection(Class aClass) {
    boolean collection = false;
    if (CollectionUtils.isCollection(aClass)) {
      collection = true;
    } else if (CollectionUtils.isArray(aClass)) {
      collection = true;
    }
    return collection;
  }

  public static boolean isSupportedMap(Class aClass) {
    return Map.class.isAssignableFrom(aClass);
  }

  public static boolean isCustomMapMethod(FieldMap fieldMap) {
    return (fieldMap instanceof GenericFieldMap && ((GenericFieldMap) fieldMap).isCustomMap()) ? true : false;
  }

  public static  boolean isPrimitiveOrWrapper(Class aClass) {
    return (aClass.isPrimitive() || Number.class.isAssignableFrom(aClass) || aClass.equals(String.class)
        || aClass.equals(Character.class) || aClass.equals(Boolean.class)
        || java.util.Date.class.isAssignableFrom(aClass) || java.util.Calendar.class.isAssignableFrom(aClass));
  }

  public static void throwMappingException(Throwable e) throws MappingException {
    if (e instanceof MappingException) {
      // in this case we do not want to re-wrap an existing mapping exception
      throw (MappingException) e;
    } else if (e instanceof RuntimeException){
      //feature request 1561837.  Dont wrap any runtime exceptions in a MappingException
      throw (RuntimeException) e;
    } else {
      throw new MappingException(e);
    }
  }

  public static boolean isBlankOrNull(String value) {
    return value == null || value.trim().length() < 1 ? true : false;
  }

  public static void addFactories(Map factories) {
    storedFactories.putAll(factories);
  }

  public static void isMethodMap(FieldMap fieldMap) {
    boolean methodMap = false;
    if (fieldMap.getSourceField().getTheGetMethod() != null || fieldMap.getSourceField().getTheSetMethod() != null
        || fieldMap.getDestField().getTheGetMethod() != null || fieldMap.getDestField().getTheSetMethod() != null) {
      methodMap = true;
    }
    if (methodMap && fieldMap instanceof GenericFieldMap) {
      ((GenericFieldMap) fieldMap).setMethodMap(true);
    }
  }

  public static void isCustomMap(FieldMap fieldMap) {
    boolean customMap = false;
    if (fieldMap.getSourceField().getMapGetMethod() != null || fieldMap.getSourceField().getMapSetMethod() != null
        || fieldMap.getDestField().getMapGetMethod() != null || fieldMap.getDestField().getMapSetMethod() != null) {
      customMap = true;
    }
    if (customMap && fieldMap instanceof GenericFieldMap) {
      ((GenericFieldMap) fieldMap).setCustomMap(true);
    }
  }

  public static Throwable getRootCause(Throwable ex) {
    Throwable rootCause = ex;
    while (rootCause.getCause() != null) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }

  public static String getParentFieldNameKey(String parentSourceField, Object srcObj, String sourceClassName, String srcFieldName, String destFieldName) {
    StringBuffer buf = new StringBuffer(150);
    buf.append(parentSourceField);
    buf.append(System.identityHashCode(srcObj));
    buf.append(sourceClassName);
    buf.append(srcFieldName);
    buf.append(destFieldName);
    return buf.toString();
  }
  
  public static Class findCustomConverter(Cache converterByDestTypeCache, CustomConverterContainer customConverterContainer, Class sourceClass, Class destClass) throws ClassNotFoundException {
    if (customConverterContainer == null || customConverterContainer.getConverters() == null || customConverterContainer.getConverters().size() < 1) {
      return null;
    }

    return customConverterContainer.getCustomConverter(sourceClass, destClass, converterByDestTypeCache);
  }
 
  public static Class determineCustomConverter(FieldMap fieldMap, Cache converterByDestTypeCache, CustomConverterContainer customConverterContainer, 
      Class sourceClass, Class destClass) throws ClassNotFoundException {
    if (customConverterContainer == null || customConverterContainer.getConverters() == null || customConverterContainer.getConverters().size() < 1) {
      return null;
    }
    
    //This method is messy.  Just trying to isolate the junk into this one method instead of spread across the mapping processor until a better solution can be put into place
    //For indexed mapping, need to use the actual class at index to determine the custom converter.
    if (fieldMap != null && fieldMap.getDestField().isIndexed()) {
      if (destClass.isArray()) {
        destClass = destClass.getComponentType();
      } else if (destClass.isAssignableFrom(Collection.class)) {
        //use hint when trying to find a custom converter
        if (fieldMap.getDestinationTypeHint() != null && fieldMap.getDestinationTypeHint().getHints().size() < 2) {
          destClass = Thread.currentThread().getContextClassLoader().loadClass(fieldMap.getDestinationTypeHint().getHintName());
        }
      }
    }

    return findCustomConverter(converterByDestTypeCache, customConverterContainer, sourceClass, destClass);
  }
  
  public static void reverseFields(FieldMap source, FieldMap destination) {
    // reverse the fields
    DozerField df = new DozerField(source.getSourceField().getName(), source.getSourceField().getType());
    df.setIndexed(source.getSourceField().isIndexed());
    df.setIndex(source.getSourceField().getIndex());

    DozerField sf = new DozerField(source.getDestField().getName(), source.getDestField().getType());
    sf.setIndexed(source.getDestField().isIndexed());
    sf.setIndex(source.getDestField().getIndex());

    destination.setDestField(df);
    destination.setSourceField(sf);
    destination.setCustomConverter(source.getCustomConverter());

    destination.getDestField().setDateFormat(source.getSourceField().getDateFormat());
    destination.getSourceField().setDateFormat(source.getDestField().getDateFormat());

    destination.getDestField().setTheGetMethod(source.getSourceField().getTheGetMethod());
    destination.getDestField().setTheSetMethod(source.getSourceField().getTheSetMethod());
    destination.getSourceField().setTheGetMethod(source.getDestField().getTheGetMethod());
    destination.getSourceField().setTheSetMethod(source.getDestField().getTheSetMethod());
    destination.getDestField().setKey(source.getSourceField().getKey());
    destination.getSourceField().setKey(source.getDestField().getKey());
    destination.getDestField().setMapGetMethod(source.getSourceField().getMapGetMethod());
    destination.getDestField().setMapSetMethod(source.getSourceField().getMapSetMethod());
    destination.getSourceField().setMapGetMethod(source.getDestField().getMapGetMethod());
    destination.getSourceField().setMapSetMethod(source.getDestField().getMapSetMethod());
    destination.getSourceField().setAccessible(source.getDestField().isAccessible());
    destination.getDestField().setAccessible(source.getSourceField().isAccessible());    
    if (StringUtils.isNotEmpty(destination.getMapId())) {
      destination.setMapId(source.getMapId());
    }
    destination.getDestField().setCreateMethod(source.getSourceField().getCreateMethod());
    destination.getSourceField().setCreateMethod(source.getDestField().getCreateMethod());
  }
  
  public static boolean validateMap(Class sourceClass, Class destClass, FieldMap fieldMap) {
    if (Map.class.isAssignableFrom(sourceClass) || fieldMap.getSourceField().getMapGetMethod() != null ||
        Map.class.isAssignableFrom(destClass) || fieldMap.getDestField().getMapGetMethod() != null) {
      return true;
    } else {
      return false;
    }
  }
  
  public static Object getIndexedValue(Object collection, int index) {
    Object r = null;
    if (collection instanceof Object[]) {
      Object[] x = (Object[]) collection;
      if (index < x.length) {
        return x[index];
      }
    } else if (collection instanceof Collection) {
      Collection x = (Collection) collection;
      if (index < x.size()) {
        Iterator iter = x.iterator();
        for (int i = 0; i < index; i++) {
          iter.next();
        }
        r = iter.next();
      }
    }
    return r;
  }
  
}