/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.cache.Cache;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.CopyByReferenceContainer;
import com.github.dozermapper.core.classmap.DozerClass;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.converters.CustomConverterContainer;
import com.github.dozermapper.core.fieldmap.DozerField;
import com.github.dozermapper.core.fieldmap.FieldMap;

import org.apache.commons.lang3.StringUtils;

import static com.github.dozermapper.core.util.DozerConstants.BASE_CLASS;

/**
 * Internal class that provides various mapping utilities used throughout the code base. Only intended for internal use.
 */
public final class MappingUtils {

    private MappingUtils() {
    }

    public static String getClassNameWithoutPackage(Class<?> clazz) { // TODO Replace with Apache implementation
        Package pckage = clazz.getPackage();
        int pckageIndex = 0;
        if (pckage != null) {
            pckageIndex = pckage.getName().length() + 1;
        }
        return clazz.getName().substring(pckageIndex);
    }

    public static boolean isSupportedCollection(Class<?> aClass) {
        return CollectionUtils.isCollection(aClass) || CollectionUtils.isArray(aClass);
    }

    public static boolean isSupportedMap(Class<?> aClass) {
        return Map.class.isAssignableFrom(aClass);
    }

    public static void throwMappingException(Throwable e) throws MappingException {
        if (e instanceof MappingException) {
            // in this case we do not want to re-wrap an existing mapping exception
            throw (MappingException)e;
        } else if (e instanceof RuntimeException) {
            // feature request 1561837. Dont wrap any runtime exceptions in a MappingException
            throw (RuntimeException)e;
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

    public static String getMappedParentFieldKey(Object destObj, FieldMap destFieldMap) {
        StringBuilder buf = new StringBuilder(100); // TODO Use IdentityHashMap instead of String concatenation
        buf.append(System.identityHashCode(destObj));
        buf.append(destFieldMap.getDestFieldName());
        if (destFieldMap.getDestFieldKey() != null) {
            buf.append("[").append(destFieldMap.getDestFieldKey()).append("]");
        }
        return buf.toString();
    }

    public static Class<?> findCustomConverter(Cache converterByDestTypeCache, CustomConverterContainer customConverterContainer,
                                               Class<?> srcClass, Class<?> destClass) {
        if (customConverterContainer == null) {
            return null;
        }

        return customConverterContainer.getCustomConverter(srcClass, destClass, converterByDestTypeCache);
    }

    public static Class<?> determineCustomConverter(FieldMap fieldMap, Cache converterByDestTypeCache,
                                                    CustomConverterContainer customConverterContainer, Class<?> srcClass, Class<?> destClass) {
        if (customConverterContainer == null) {
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

    public static void reverseFields(ClassMap source, ClassMap destination, BeanContainer beanContainer) {
        // reverse the fields
        destination.setSrcClass(new DozerClass(source.getDestClassName(), source.getDestClassToMap(), source.getDestClassBeanFactory(),
                                               source.getDestClassBeanFactoryId(), source.getDestClassMapGetMethod(), source.getDestClassMapSetMethod(),
                                               source.getDestClass().getCreateMethod(),
                                               source.isDestMapNull(), source.isDestMapEmptyString(), source.getDestClass().isAccessible(),
                                               source.getDestClass().isSkipConstructor(), beanContainer));
        destination.setDestClass(new DozerClass(source.getSrcClassName(), source.getSrcClassToMap(), source.getSrcClassBeanFactory(),
                                                source.getSrcClassBeanFactoryId(), source.getSrcClassMapGetMethod(), source.getSrcClassMapSetMethod(),
                                                source.getSrcClass().getCreateMethod(),
                                                source.isSrcMapNull(), source.isSrcMapEmptyString(), source.getSrcClass().isAccessible(), source.getSrcClass().isSkipConstructor(),
                                                beanContainer));
        destination.setType(source.getType());
        destination.setWildcard(source.isWildcard());
        destination.setWildcardCaseInsensitive(source.isWildcardCaseInsensitive());
        destination.setTrimStrings(source.isTrimStrings());
        destination.setDateFormat(source.getDateFormat());
        destination.setRelationshipType(source.getRelationshipType());
        destination.setStopOnErrors(source.isStopOnErrors());
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
            Object[] x = (Object[])collection;
            if (index < x.length) {
                return x[index];
            }
        } else if (collection instanceof Collection) {
            Collection<?> x = (Collection<?>)collection;
            if (index < x.size()) {
                Iterator<?> iter = x.iterator();
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
        String destFieldTypeName = null;
        Class<?> clazz = fieldMap.getDestFieldType(classMap.getDestClassToMap());
        if (clazz != null) {
            destFieldTypeName = clazz.getName();
        }
        if (copyByReferenceContainer.contains(destFieldTypeName) && !fieldMap.isCopyByReferenceOveridden()) {
            fieldMap.setCopyByReference(true);
        }
    }

    public static Class<?> loadClass(String name, BeanContainer beanContainer) {
        DozerClassLoader loader = beanContainer.getClassLoader();
        return loader.loadClass(name);
    }

    public static Class<?> getRealClass(Class<?> clazz, BeanContainer beanContainer) {
        DozerProxyResolver proxyResolver = beanContainer.getProxyResolver();
        return proxyResolver.getRealClass(clazz);
    }

    public static <T> T deProxy(T object, BeanContainer beanContainer) {
        DozerProxyResolver proxyResolver = beanContainer.getProxyResolver();
        return proxyResolver.unenhanceObject(object);
    }

    public static boolean isProxy(Class<?> clazz, BeanContainer beanContainer) {
        DozerProxyResolver proxyResolver = beanContainer.getProxyResolver();
        return proxyResolver.isProxy(clazz);
    }

    public static Object prepareIndexedCollection(Class<?> collectionType, Object existingCollection, Object collectionEntry,
                                                  int index) {
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
        return mapping != null && mapping.contains(DozerConstants.DEEP_FIELD_DELIMITER);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] prepareIndexedArray(Class<T> collectionType, Object existingCollection, Object collectionEntry, int index) {
        T[] result;

        if (existingCollection == null) {
            result = (T[])Array.newInstance(collectionType.getComponentType(), index + 1);
        } else {
            int originalLenth = ((Object[])existingCollection).length;
            result = (T[])Array.newInstance(collectionType.getComponentType(), Math.max(index + 1, originalLenth));
            System.arraycopy(existingCollection, 0, result, 0, originalLenth);
        }
        result[index] = (T)collectionEntry;
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Collection<?> prepareIndexedCollectionType(Class<?> collectionType, Object existingCollection,
                                                              Object collectionEntry, int index) {
        Collection result = null;
        //Instantiation of the new Collection: can be interface or implementation class
        if (collectionType.isInterface()) {
            if (collectionType.equals(Set.class)) {
                result = new HashSet();
            } else if (collectionType.equals(List.class)) {
                result = new ArrayList();
            } else {
                throwMappingException("Only interface types java.util.Set and java.util.List are supported for java.util.Collection type indexed properties.");
            }
        } else {
            //It is an implementation class of Collection
            try {
                result = (Collection)collectionType.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        //Fill in old values in new Collection
        if (existingCollection != null) {
            result.addAll((Collection)existingCollection);
        }

        //Add the new value:
        //For an ordered Collection
        if (result instanceof List) {
            while (result.size() < index + 1) {
                result.add(null);
            }
            ((List)result).set(index, collectionEntry);
        } else {
            //for an unordered Collection (index has no use here)
            result.add(collectionEntry);
        }
        return result;
    }

    /**
     * Used to test if {@code srcFieldClass} is enum.
     *
     * @param srcFieldClass the source field to be tested.
     * @return {@code true} if and only if current running JRE is 1.5 or above, and
     * {@code srcFieldClass} is enum; otherwise return {@code false}.
     */
    public static boolean isEnumType(Class<?> srcFieldClass) {
        if (srcFieldClass.isAnonymousClass()) {
            //If srcFieldClass is anonymous class, replace srcFieldClass with its enclosing class.
            //This is used to ensure Dozer can get correct Enum type.
            srcFieldClass = srcFieldClass.getEnclosingClass();
        }
        return srcFieldClass.isEnum();
    }

    /**
     * Used to test if both {@code srcFieldClass} and {@code destFieldType} are enum.
     *
     * @param srcFieldClass the source field to be tested.
     * @param destFieldType the destination field to be tested.
     * @return {@code true} if and only if current running JRE is 1.5 or above, and both
     * {@code srcFieldClass} and {@code destFieldType} are enum; otherwise return {@code false}.
     */
    public static boolean isEnumType(Class<?> srcFieldClass, Class<?> destFieldType) {
        return isEnumType(srcFieldClass) && isEnumType(destFieldType);
    }

    public static List<Class<?>> getSuperClassesAndInterfaces(Class<?> srcClass, BeanContainer beanContainer) {
        List<Class<?>> superClasses = new ArrayList<>();
        Class<?> realClass = getRealClass(srcClass, beanContainer);

        // Add all super classes first
        Class<?> superClass = getRealClass(realClass, beanContainer).getSuperclass();
        while (!isBaseClass(superClass)) {
            superClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }

        // Now add all interfaces of the passed in class and all it's super classes

        // Linked hash set so duplicated are not added but insertion order is kept
        LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<>();

        interfaces.addAll(getInterfaceHierarchy(realClass, beanContainer));
        for (Class<?> clazz : superClasses) {
            interfaces.addAll(getInterfaceHierarchy(clazz, beanContainer));
        }

        superClasses.addAll(interfaces);
        return superClasses;
    }

    @SuppressWarnings("unchecked")
    public static List<Class<?>> getInterfaceHierarchy(Class<?> srcClass, BeanContainer beanContainer) {
        final List<Class<?>> result = new LinkedList<>();
        Class<?> realClass = getRealClass(srcClass, beanContainer);

        final LinkedList<Class> interfacesToProcess = new LinkedList<>();

        Class[] interfaces = realClass.getInterfaces();

        interfacesToProcess.addAll(Arrays.asList(interfaces));

        while (!interfacesToProcess.isEmpty()) {
            Class<?> iface = interfacesToProcess.remove();
            if (!result.contains(iface)) {
                result.add(iface);
                for (Class subiface : iface.getInterfaces()) {
                    // if we haven't processed this interface yet then add it to be processed
                    if (!result.contains(subiface)) {
                        interfacesToProcess.add(subiface);
                    }
                }
            }
        }

        return result;

    }

    private static boolean isBaseClass(Class<?> clazz) {
        return clazz == null || BASE_CLASS.equals(clazz.getName());
    }

}
