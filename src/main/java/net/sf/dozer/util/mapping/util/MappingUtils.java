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
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.cache.Cache;
import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.classmap.Configuration;
import net.sf.dozer.util.mapping.classmap.CopyByReference;
import net.sf.dozer.util.mapping.classmap.CopyByReferenceContainer;
import net.sf.dozer.util.mapping.classmap.DozerClass;
import net.sf.dozer.util.mapping.config.GlobalSettings;
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
public final class MappingUtils {

  private MappingUtils() {
  }

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
    return CollectionUtils.isCollection(aClass) || CollectionUtils.isArray(aClass);
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
    return value == null || value.trim().length() < 1;
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

  public static void reverseFields(FieldMap source, FieldMap reversed) {
    DozerField destField = source.getSrcFieldCopy();
    DozerField sourceField = source.getDestFieldCopy();

    reversed.setDestField(destField);
    reversed.setSrcField(sourceField);

    reversed.setCustomConverter(source.getCustomConverter());
    reversed.setCustomConverterId(source.getCustomConverterId());
    reversed.setMapId(source.getMapId());
    reversed.setRelationshipType(source.getRelationshipType());
    reversed.setRemoveOrphans(source.isRemoveOrphans());
    reversed.setSrcHintContainer(source.getDestHintContainer());
    reversed.setDestHintContainer(source.getSrcHintContainer());
    reversed.setSrcDeepIndexHintContainer(source.getDestDeepIndexHintContainer());
    reversed.setDestDeepIndexHintContainer(source.getSrcDeepIndexHintContainer());
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
    CopyByReferenceContainer copyByReferenceContainer = globalConfig.getCopyByReferences();
    if (copyByReferenceContainer != null) {
      String destFieldTypeName = null;
      Iterator copyIterator = copyByReferenceContainer.getCopyByReferences().iterator();
      Class clazz = fieldMap.getDestFieldType(classMap.getDestClassToMap());
      if (clazz != null) {
        destFieldTypeName = clazz.getName();
      }
      while (copyIterator.hasNext()) {
        CopyByReference copyByReference = (CopyByReference) copyIterator.next();
        if (copyByReference.matches(destFieldTypeName) && !fieldMap.isCopyByReferenceOveridden()) {
          fieldMap.setCopyByReference(true);
        }
      }
    }
  }

  public static Class loadClass(String name) {
    Class result = null;
    try {
      result = Class.forName(name);
    } catch (ClassNotFoundException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

  static boolean isProxy(Class clazz) {
    //todo: implement a better way of determining this that is more generic
    return clazz.getName().indexOf(MapperConstants.CGLIB_ID) >= 0 || clazz.getName().indexOf(MapperConstants.JAVASSIST_ID) >= 0;
  }

  public static Class getRealSuperclass(Class clazz) {
    if (isProxy(clazz)) {
      return clazz.getSuperclass().getSuperclass();
    }
    return clazz.getSuperclass();
  }

  public static Class getRealClass(Class clazz) {
    if (isProxy(clazz)) {
      return clazz.getSuperclass();
    }
    return clazz;
  }

  public static Object prepareIndexedCollection(Class collectionType, Object existingCollection, Object collectionEntry, int index) {
    Object result = null;
    if (collectionType.isArray()) {
      result = prepareIndexedArray(collectionType, existingCollection, collectionEntry, index);
    } else if (Collection.class.isAssignableFrom(collectionType)) {
      result = prepareIndexedCollectionType(collectionType, existingCollection, collectionEntry, index);
    } else {
      throwMappingException("Only types java.lang.Object[] and java.util.Collection are supported for indexed properties.");
    }

    return result;
  }
  
  public static boolean isDeepMapping(String mapping) {
    return mapping != null && mapping.indexOf(MapperConstants.DEEP_FIELD_DELIMITOR) >= 0;
  }
  
  private static Object[] prepareIndexedArray(Class collectionType, Object existingCollection, Object collectionEntry, int index) {
    Object[] result;

    if (existingCollection == null) {
      result = (Object[]) Array.newInstance(collectionType.getComponentType(), index + 1);
    } else {
      int originalLenth = ((Object[]) existingCollection).length;
      result = (Object[]) Array.newInstance(collectionType.getComponentType(), Math.max(index + 1, originalLenth));

      for (int i = 0; i < originalLenth; i++) {
        result[i] = ((Object[]) existingCollection)[i];
      }
    }
    result[index] = collectionEntry;
    return result;
  }
  
  private static Collection prepareIndexedCollectionType(Class collectionType, Object existingCollection, Object collectionEntry, int index) {
    Collection result = null;
    //Instantiation of the new Collection: can be interface or implementation class
    if (collectionType.isInterface()) {
      if (collectionType.equals(Set.class)) {
        result = new HashSet();
      }
      else if (collectionType.equals(List.class)) {
        result = new ArrayList();
      } else {
        throwMappingException("Only interface types java.util.Set and java.util.List are supported for java.util.Collection type indexed properties.");
      }
    } else {
      //It is an implementation class of Collection
      try {
        result = (Collection) collectionType.newInstance();
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    //Fill in old values in new Collection
    if (existingCollection != null) {
      result.addAll((Collection) existingCollection);
    }

    //Add the new value:
    //For an ordered Collection
    if (result instanceof List) {
      while (result.size() < index + 1) {
        result.add(null);
      }
      ((List) result).set(index, collectionEntry);
    }
    //for an unordered Collection (index has no use here)
    else {
      result.add(collectionEntry);
    }
    return result;
  }
  
  /**
   * Used to test if both {@code srcFieldClass} and {@code destFieldType} are enum.
   * @param srcFieldClass the source field to be tested.
   * @param destFieldType the destination field to be tested.
   * @return {@code true} if and only if current running JRE is 1.5 or above, and both 
   * {@code srcFieldClass} and {@code destFieldType} are enum; otherwise return {@code false}.
   */
  public static boolean isEnumType(Class srcFieldClass, Class destFieldType){
    if (GlobalSettings.getInstance().isJava5()){//Verify if running JRE is 1.5 or above
      if (srcFieldClass.isAnonymousClass()){
        //If srcFieldClass is anonymous class, replace srcFieldClass with its enclosing class.
        //This is used to ensure Dozer can get correct Enum type.
        srcFieldClass = srcFieldClass.getEnclosingClass();
      }
      if (destFieldType.isAnonymousClass()){
        //Just like srcFieldClass, if destFieldType is anonymous class, replace destFieldType with 
        //its enclosing class. This is used to ensure Dozer can get correct Enum type.
        destFieldType = destFieldType.getEnclosingClass();
      }
      return 
        ((Boolean) ReflectionUtils
          .invoke(Jdk5Methods.getInstance().getClassIsEnumMethod(), srcFieldClass, null))
          .booleanValue()
        && 
        ((Boolean) ReflectionUtils
          .invoke(Jdk5Methods.getInstance().getClassIsEnumMethod(), destFieldType, null))
          .booleanValue();
    }
    return false;
  }

}