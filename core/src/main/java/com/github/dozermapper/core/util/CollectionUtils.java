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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Internal class that contains various Collection utilities specific to Dozer requirements. Not intended for direct use
 * by application code.
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static boolean isArray(Class<?> aClass) {
        return aClass.isArray();
    }

    public static boolean isCollection(Class<?> aClass) {
        return Collection.class.isAssignableFrom(aClass);
    }

    public static boolean isList(Class<?> aClass) {
        return List.class.isAssignableFrom(aClass);
    }

    public static boolean isSet(Class<?> aClass) {
        return Set.class.isAssignableFrom(aClass);
    }

    public static boolean isPrimitiveArray(Class<?> aClass) {
        return aClass.isArray() && aClass.getComponentType().isPrimitive();
    }

    public static int getLengthOfCollection(Object value) {
        if (isArray(value.getClass())) {
            return Array.getLength(value);
        } else {
            return ((Collection<?>)value).size();
        }
    }

    public static Object getValueFromCollection(Object collection, int index) {
        if (isArray(collection.getClass())) {
            return Array.get(collection, index);
        } else {
            return ((Collection<?>)collection).toArray()[index];
        }
    }

    public static <T extends Set<?>> Set<?> createNewSet(Class<T> destType) {
        Set<Object> result;
        if (SortedSet.class.isAssignableFrom(destType)) {
            result = new TreeSet<>();
        } else {
            result = new HashSet<>();
        }
        return result;
    }

    public static <T extends Set<?>> Set<?> createNewSet(Class<T> destSetType, Collection<?> srcValue) {
        Set<Object> result = (Set<Object>)createNewSet(destSetType);
        if (srcValue != null) {
            result.addAll(srcValue);
        }
        return result;
    }

    public static <T> Object convertListToArray(List<T> list, Class<T> destEntryType) {

        Object outArray = Array.newInstance(destEntryType, list.size());
        int count = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Object element = list.get(i);
            Array.set(outArray, count, element);
            count++;
        }

        if (destEntryType.isPrimitive()) {
            return outArray;
        } else {
            return outArray;
        }
    }

    public static List<Object> convertPrimitiveArrayToList(Object primitiveArray) {
        int length = Array.getLength(primitiveArray);
        List<Object> result = new ArrayList<>(length);

        // wrap and copy elements
        for (int i = 0; i < length; i++) {
            result.add(Array.get(primitiveArray, i));
        }
        return result;
    }

    public static <E> Set<E> intersection(final Set<E> set1, final Set<?> set2) {
        Set<E> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }
}
