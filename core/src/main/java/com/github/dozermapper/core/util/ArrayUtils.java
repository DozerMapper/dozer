package com.github.dozermapper.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class ArrayUtils {

    private ArrayUtils() {
    }

    public static boolean isArray(Class<?> aClass) {
        return aClass.isArray();
    }

    public static boolean isPrimitiveArray(Class<?> aClass) {
        return aClass.isArray() && aClass.getComponentType().isPrimitive();
    }

    public static int getLengthOfArray(Object value) {
        return Array.getLength(value);
    }

    public static Object getValueFromArray(Object array, int index) {
        return Array.get(array, index);
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
}
