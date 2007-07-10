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
import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.classmap.Configuration;
import net.sf.dozer.util.mapping.classmap.CopyByReference;
import net.sf.dozer.util.mapping.classmap.DozerClass;
import net.sf.dozer.util.mapping.converters.CustomConverterContainer;
import net.sf.dozer.util.mapping.fieldmap.DozerField;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;

import org.apache.commons.lang.StringUtils;

/**
 * Internal class that provides various mapping utilities used throughout the code base. Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public abstract class MappingUtils {

  // only making public temporarily while refactoring. This static data should be relocated
  public static final Map storedFactories = Collections.synchronizedMap(new HashMap());

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

  public static boolean isPrimitiveOrWrapper(Class aClass) {
    return (aClass.isPrimitive() || Number.class.isAssignableFrom(aClass) || aClass.equals(String.class)
        || aClass.equals(Character.class) || aClass.equals(Boolean.class) || java.util.Date.class.isAssignableFrom(aClass) || java.util.Calendar.class
        .isAssignableFrom(aClass));
  }

  public static void throwMappingException(Throwable e) throws MappingException {
    if (e instanceof MappingException) {
      // in this case we do not want to re-wrap an existing mapping exception
      throw (MappingException) e;
    } else if (e instanceof RuntimeException) {
      // feature request 1561837. Dont wrap any runtime exceptions in a MappingException
      throw (RuntimeException) e;
    } else {
      throw new MappingException(e);
    }
  }

  public static void throwMappingException(String msg) throws MappingException {
    throw new MappingException(msg);
  }

  public static void throwMappingException(String msg, Throwable cause) throws MappingException {
    throw new MappingException(msg, cause);
  }

  public static boolean isBlankOrNull(String value) {
    return value == null || value.trim().length() < 1 ? true : false;
  }

  public static void addFactories(Map factories) {
    storedFactories.putAll(factories);
  }

  public static Throwable getRootCause(Throwable ex) {
    Throwable rootCause = ex;
    while (rootCause.getCause() != null) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }

  public static String getParentFieldNameKey(String parentSrcField, Object srcObj, String srcClassName, String srcFieldName,
      String destFieldName) {
    StringBuffer buf = new StringBuffer(150);
    buf.append(parentSrcField);
    buf.append(System.identityHashCode(srcObj));
    buf.append(srcClassName);
    buf.append(srcFieldName);
    buf.append(destFieldName);
    return buf.toString();
  }

  public static Class findCustomConverter(Cache converterByDestTypeCache, CustomConverterContainer customConverterContainer,
      Class srcClass, Class destClass) {
    if (customConverterContainer == null || customConverterContainer.getConverters() == null
        || customConverterContainer.getConverters().size() < 1) {
      return null;
    }

    return customConverterContainer.getCustomConverter(srcClass, destClass, converterByDestTypeCache);
  }

  public static Class determineCustomConverter(FieldMap fieldMap, Cache converterByDestTypeCache,
      CustomConverterContainer customConverterContainer, Class srcClass, Class destClass) {
    if (customConverterContainer == null || customConverterContainer.getConverters() == null
        || customConverterContainer.getConverters().size() < 1) {
      return null;
    }

    // This method is messy. Just trying to isolate the junk into this one method instead of spread across the mapping
    // processor until a better solution can be put into place
    // For indexed mapping, need to use the actual class at index to determine the custom converter.
    if (fieldMap != null && fieldMap.isDestFieldIndexed()) {
      if (destClass.isArray()) {
        destClass = destClass.getComponentType();
      } else if (destClass.isAssignableFrom(Collection.class) && fieldMap.getDestTypeHint() != null
          && fieldMap.getDestTypeHint().getHints().size() < 2) {
        // use hint when trying to find a custom converter
        destClass = MappingUtils.loadClass(fieldMap.getDestTypeHint().getHintName());
      }
    }

    return findCustomConverter(converterByDestTypeCache, customConverterContainer, srcClass, destClass);
  }

  public static void reverseFields(FieldMap source, FieldMap destination) {
    // reverse the fields
    DozerField df = new DozerField(source.getSrcFieldName(), source.getSrcFieldType());
    df.setIndexed(source.isSrcFieldIndexed());
    df.setIndex(source.getSrcFieldIndex());

    DozerField sf = new DozerField(source.getDestFieldName(), source.getDestFieldType());
    sf.setIndexed(source.isDestFieldIndexed());
    sf.setIndex(source.getDestFieldIndex());

    destination.setDestField(df);
    destination.setSrcField(sf);
    destination.setCustomConverter(source.getCustomConverter());

    destination.setDestFieldDateFormat(source.getSrcFieldDateFormat());
    destination.setSrcFieldDateFormat(source.getDestFieldDateFormat());
    destination.setDestFieldTheGetMethod(source.getSrcFieldTheGetMethod());
    destination.setDestFieldTheSetMethod(source.getSrcFieldTheSetMethod());
    destination.setSrcFieldTheGetMethod(source.getDestFieldTheGetMethod());
    destination.setSrcFieldTheSetMethod(source.getDestFieldTheSetMethod());
    destination.setDestFieldKey(source.getSrcFieldKey());
    destination.setSrcFieldKey(source.getDestFieldKey());
    destination.setDestFieldMapGetMethod(source.getSrcFieldMapGetMethod());
    destination.setDestFieldMapSetMethod(source.getSrcFieldMapSetMethod());
    destination.setSrcFieldMapGetMethod(source.getDestFieldMapGetMethod());
    destination.setSrcFieldMapSetMethod(source.getDestFieldMapSetMethod());
    destination.setSrcFieldAccessible(source.isDestFieldAccessible());
    destination.setDestFieldAccessible(source.isSrcFieldAccessible());
    destination.setMapId(source.getMapId());
    destination.setDestFieldCreateMethod(source.getSrcFieldCreateMethod());
    destination.setSrcFieldCreateMethod(source.getDestFieldCreateMethod());
    destination.setRelationshipType(source.getRelationshipType());
    destination.setSrcTypeHint(source.getDestTypeHint());
    destination.setDestTypeHint(source.getSrcTypeHint());
  }

  public static void reverseFields(ClassMap source, ClassMap destination) {
    // reverse the fields
    destination.setSrcClass(new DozerClass(source.getDestClassName(), source.getDestClassToMap(), source.getDestClassBeanFactory(),
        source.getDestClassBeanFactoryId(), source.getDestClassMapGetMethod(), source.getDestClassMapSetMethod(), source
            .isDestClassMapNull(), source.isDestClassMapEmptyString()));
    destination.setDestClass(new DozerClass(source.getSrcClassName(), source.getSrcClassToMap(), source.getSrcClassBeanFactory(),
        source.getSrcClassBeanFactoryId(), source.getSrcClassMapGetMethod(), source.getSrcClassMapSetMethod(), source
            .isSrcClassMapNull(), source.isSrcClassMapEmptyString()));
    destination.setType(source.getType());
    destination.setWildcard(source.isWildcard());
    destination.setTrimStrings(source.isTrimStrings());
    destination.setDateFormat(source.getDateFormat());
    destination.setStopOnErrors(source.isStopOnErrors());
    destination.setAllowedExceptions(source.getAllowedExceptions());
    destination.setSrcClassCreateMethod(source.getDestClassCreateMethod());
    destination.setDestClassCreateMethod(source.getSrcClassCreateMethod());
    if (StringUtils.isNotEmpty(source.getMapId())) {
      destination.setMapId(source.getMapId());
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

  public static void applyGlobalCopyByReference(Configuration globalConfig, FieldMap fieldMap, ClassMap classMap) {
    String destFieldTypeName = null;
    if (globalConfig.getCopyByReferences() != null) {
      Iterator copyIterator = globalConfig.getCopyByReferences().getCopyByReferences().iterator();
      Class clazz = fieldMap.getDestFieldType(classMap.getDestClassToMap());
      if (clazz != null) {
        destFieldTypeName = clazz.getName();
      }
      while (copyIterator.hasNext()) {
        CopyByReference copyByReference = (CopyByReference) copyIterator.next();
        if (copyByReference.getReferenceName().equals(destFieldTypeName) && !fieldMap.isCopyByReferenceOveridden()) {
          fieldMap.setCopyByReference(true);
        }
      }
    }
  }

  public static Class loadClass(String name) {
    Class result = null;
    try {
      result = Thread.currentThread().getContextClassLoader().loadClass(name);
    } catch (ClassNotFoundException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

}