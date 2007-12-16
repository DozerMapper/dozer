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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

  // only making public temporarily while refactoring. This static data should be relocated.
  // The stored factories don't belong in MappingUtils and need to be relocated
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
    boolean result = false;
    if (CollectionUtils.isCollection(aClass)) {
      result = true;
    } else if (CollectionUtils.isArray(aClass)) {
      result = true;
    }
    return result;
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

  public static Throwable getRootCause(Throwable ex) {
    Throwable rootCause = ex;
    while (rootCause.getCause() != null) {
      rootCause = rootCause.getCause();
    }
    return rootCause;
  }

  public static String getMappedParentFieldKey(Object destObj, String destFieldName) {
    StringBuffer buf = new StringBuffer(150);
    buf.append(System.identityHashCode(destObj));
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
      } else if (destClass.isAssignableFrom(Collection.class) && fieldMap.getDestHintContainer() != null
          && !fieldMap.getDestHintContainer().hasMoreThanOneHint()) {
        // use hint when trying to find a custom converter
        destClass = fieldMap.getDestHintContainer().getHint();
      }
    }

    return findCustomConverter(converterByDestTypeCache, customConverterContainer, srcClass, destClass);
  }

  public static void reverseFields(FieldMap source, FieldMap destination) {
    // reverse the fields
    DozerField df = new DozerField(source.getSrcFieldName(), source.getSrcFieldType());
    df.setIndexed(source.isSrcFieldIndexed());
    df.setIndex(source.getSrcFieldIndex());
    df.setDateFormat(source.getSrcFieldDateFormat());
    df.setTheGetMethod(source.getSrcFieldTheGetMethod());
    df.setTheSetMethod(source.getSrcFieldTheSetMethod());
    df.setKey(source.getSrcFieldKey());
    df.setMapGetMethod(source.getSrcFieldMapGetMethod());
    df.setMapSetMethod(source.getSrcFieldMapSetMethod());
    df.setCreateMethod(source.getSrcFieldCreateMethod());
    df.setAccessible(source.isSrcFieldAccessible());

    DozerField sf = new DozerField(source.getDestFieldName(), source.getDestFieldType());
    sf.setIndexed(source.isDestFieldIndexed());
    sf.setIndex(source.getDestFieldIndex());
    sf.setDateFormat(source.getDestFieldDateFormat());
    sf.setTheGetMethod(source.getDestFieldTheGetMethod());
    sf.setTheSetMethod(source.getDestFieldTheSetMethod());
    sf.setKey(source.getDestFieldKey());
    sf.setMapGetMethod(source.getDestFieldMapGetMethod());
    sf.setMapSetMethod(source.getDestFieldMapSetMethod());
    sf.setCreateMethod(source.getDestFieldCreateMethod());
    sf.setAccessible(source.isDestFieldAccessible());

    destination.setDestField(df);
    destination.setSrcField(sf);
    destination.setCustomConverter(source.getCustomConverter());
    destination.setCustomConverterId(source.getCustomConverterId());
    destination.setMapId(source.getMapId());
    destination.setRelationshipType(source.getRelationshipType());
    destination.setRemoveOrphans(source.isRemoveOrphans());
    destination.setSrcHintContainer(source.getDestHintContainer());
    destination.setDestHintContainer(source.getSrcHintContainer());
    destination.setSrcDeepIndexHintContainer(source.getDestDeepIndexHintContainer());
    destination.setDestDeepIndexHintContainer(source.getSrcDeepIndexHintContainer());

  }

  public static void reverseFields(ClassMap source, ClassMap destination) {
    // reverse the fields
    destination.setSrcClass(new DozerClass(source.getDestClassName(), source.getDestClassToMap(), source.getDestClassBeanFactory(),
        source.getDestClassBeanFactoryId(), source.getDestClassMapGetMethod(), source.getDestClassMapSetMethod(), source
            .isDestMapNull(), source.isDestMapEmptyString()));
    destination.setDestClass(new DozerClass(source.getSrcClassName(), source.getSrcClassToMap(), source.getSrcClassBeanFactory(),
        source.getSrcClassBeanFactoryId(), source.getSrcClassMapGetMethod(), source.getSrcClassMapSetMethod(), source
            .isSrcMapNull(), source.isSrcMapEmptyString()));
    destination.setType(source.getType());
    destination.setWildcard(Boolean.valueOf(source.isWildcard()));
    destination.setTrimStrings(Boolean.valueOf(source.isTrimStrings()));
    destination.setDateFormat(source.getDateFormat());
    destination.setRelationshipType(source.getRelationshipType());
    destination.setStopOnErrors(Boolean.valueOf(source.isStopOnErrors()));
    destination.setAllowedExceptions(source.getAllowedExceptions());
    destination.setSrcClassCreateMethod(source.getDestClassCreateMethod());
    destination.setDestClassCreateMethod(source.getSrcClassCreateMethod());
    if (StringUtils.isNotEmpty(source.getMapId())) {
      destination.setMapId(source.getMapId());
    }
  }

  public static Object getIndexedValue(Object collection, int index) {
    Object result = null;
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
        result = iter.next();
      }
    }
    return result;
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

  public static boolean isProxy(Class clazz) {
    //todo: implement a better way of determining this that is more generic
    return clazz.getName().indexOf(MapperConstants.CGLIB_ID) >= 0;
  }

  public static Class getRealSuperclass(Class clazz) {
    return clazz.getName().indexOf(MapperConstants.CGLIB_ID) >= 0 ? clazz.getSuperclass().getSuperclass() : clazz.getSuperclass();
  }

  public static Class getProxyRealClass(Class clazz) {
    if (!isProxy(clazz)) {
      throw new IllegalArgumentException("specified class is not a proxy: " + clazz);
    }
    return clazz.getSuperclass();
  }

  public static Object prepareIndexedCollection(Class collectionType, Object existingCollection, Object collectionEntry, int index) {
    Object result = null;
    if (existingCollection == null) {

      if (collectionType.isArray()) {
        existingCollection = Array.newInstance(collectionType.getComponentType(), 0);
      } else if (CollectionUtils.isSet(collectionType)) {
        existingCollection = new HashSet();
      } else {
        existingCollection = new ArrayList();
      }
    }
    if (existingCollection instanceof Collection) {
      Collection newCollection;
      if (existingCollection instanceof Set) {
        newCollection = new HashSet();
      } else {
        newCollection = new ArrayList();
      }

      Collection c = (Collection) existingCollection;
      Iterator i = c.iterator();
      int x = 0;
      while (i.hasNext()) {
        if (x != index) {
          newCollection.add(i.next());
        } else {
          newCollection.add(collectionEntry);
        }
        x++;
      }
      if (newCollection.size() <= index) {
        while (newCollection.size() < index) {
          newCollection.add(null);
        }
        newCollection.add(collectionEntry);
      }
      result = newCollection;
    } else if (existingCollection.getClass().isArray()) {
      Object[] objs = (Object[]) existingCollection;
      Object[] x = (Object[]) Array.newInstance(objs.getClass().getComponentType(), objs.length > index ? objs.length + 1
          : index + 1);
      for (int i = 0; i < objs.length; i++) {
        x[i] = objs[i];
      }
      x[index] = collectionEntry;
      result = x;
    }
    return result;
  }

}