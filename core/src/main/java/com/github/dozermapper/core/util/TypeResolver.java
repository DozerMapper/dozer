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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to map from a Java 5 Type to the Class that is represented by this Type at runtime.
 */
public final class TypeResolver {
    /**
     * empty type array
     */
    private static final Type[] EMPTY_TYPES = new Type[0];

    /**
     * container for the already resolved type mappings
     */
    private static Map<Class<?>, Map<TypeVariable<?>, Type>> typeMaps =
            new ConcurrentHashMap<>();

    /**
     * utility class constructor
     */
    private TypeResolver() {
    }

    /**
     * Try to resolve the property type for the supplied methods and target class. The supplied read and write methods
     * should resemble a Java bean property (setter and getter)
     *
     * @param targetClass the target class of the property
     * @param readMethod  the read method of the property
     * @param writeMethod the write method of the property
     * @return the actual class of the property type
     */
    public static Class<?> resolvePropertyType(final Class<?> targetClass, final Method readMethod, final Method writeMethod) {
        Class<?> type = null;

        if (readMethod != null) {
            type = resolveReturnType(targetClass, readMethod);
        }

        if ((type == null) && (writeMethod != null)) {
            type = resolveParameterType(targetClass, writeMethod);
        }
        return type;
    }

    /**
     * resolves the generic return type of the method
     */
    private static Class<?> resolveReturnType(final Class<?> targetClass, final Method method) {
        // get the generic return type
        Type type = method.getGenericReturnType();

        // get the actual type argument (expect the type to be a TypeVariable)
        return resolveType(targetClass, type);
    }

    /**
     * resolves the parameter return type of the method
     */
    private static Class<?> resolveParameterType(final Class<?> targetClass, final Method method) {
        // get the generic parametertypes of the method
        Type[] types = method.getGenericParameterTypes();

        // check for correct argument list length and type
        if ((types == null) || (types.length < 1)) {
            return null;
        }

        // get the actual type argument (expect the type to be a TypeVariable)
        return resolveType(targetClass, types[0]);
    }

    private static Class<?> resolveType(final Class<?> targetClass, final Type type) {
        if (type instanceof Class) {
            return (Class<?>)type;
        }

        if (!(type instanceof TypeVariable<?>)) {
            return null;
        }

        Type targetType = null;
        Map<TypeVariable<?>, Type> typeMap = getTypeMap(targetClass);
        for (Map.Entry<TypeVariable<?>, Type> typeVariableEntry : typeMap.entrySet()) {
            TypeVariable<?> typeVariable = typeVariableEntry.getKey();
            if (typeVariable.getName().equals(((TypeVariable)type).getName())) {
                targetType = typeVariableEntry.getValue();
                break;
            }
        }

        if (targetType instanceof Class) {
            return (Class<?>)targetType;
        }
        return null;
    }

    /**
     * returns an already cached [TypeVariable to Type] map for the targetClass or build a new one.
     */
    private static Map<TypeVariable<?>, Type> getTypeMap(final Class<?> targetClass) {
        // check if a TypeMap already exists for this class
        if (typeMaps.containsKey(targetClass)) {
            return typeMaps.get(targetClass);
        }

        // build a new TypeVariable -> Type map
        Map<TypeVariable<?>, Type> map = new HashMap<>();
        Class<?> clazz = targetClass;

        while (!Object.class.equals(clazz)) {
            Type genericSuperClass = clazz.getGenericSuperclass();
            if (genericSuperClass instanceof ParameterizedType) {
                collectActualTypeArguments((ParameterizedType)genericSuperClass, map);
            }
            collectActualTypeArguments(clazz.getGenericInterfaces(), map);
            clazz = clazz.getSuperclass();
        }

        typeMaps.put(targetClass, map);
        return map;
    }

    /**
     * collects all TypeVariable to Type mappings from the generic interfaces
     */
    private static void collectActualTypeArguments(final Type[] genericInterfaces,
                                                   final Map<TypeVariable<?>, Type> map) {
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
                collectActualTypeArguments(parameterizedType, map);
                Type rawType = parameterizedType.getRawType();
                collectActualTypeArguments(getGenericInterfaces(rawType), map);

            } else {
                collectActualTypeArguments(getGenericInterfaces(genericInterface), map);
            }
        }
    }

    /**
     * return the generic interfaces of the type if type is a class
     */
    private static Type[] getGenericInterfaces(final Type type) {
        if (type instanceof Class) {
            return Class.class.cast(type).getGenericInterfaces();
        }
        return EMPTY_TYPES;
    }

    /**
     * collects all TypeVariable to Type mappings from a parameterizedType
     */
    private static void collectActualTypeArguments(final ParameterizedType parameterizedType,
                                                   final Map<TypeVariable<?>, Type> map) {
        // get the raw type of the parameterized class and validate its a class
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class)) {
            return;
        }

        // get variables and types and store them into the map
        TypeVariable<?>[] variables = ((Class<?>)rawType).getTypeParameters();
        Type[] types = parameterizedType.getActualTypeArguments();

        for (int i = 0; i < variables.length; i++) {
            // found a valid parameter class
            if (types[i] instanceof Class) {
                map.put(variables[i], types[i]);
            }
        }
    }

}
