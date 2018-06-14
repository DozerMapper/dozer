/*
 * Copyright 2005-2018 Dozer Project
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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.propertydescriptor.DeepHierarchyElement;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Internal class that provides a various reflection utilities(specific to Dozer requirements) used throughout the code
 * base. Not intended for direct use by application code.
 */
public final class ReflectionUtils {

    private static final String IAE_MESSAGE = "argument type mismatch";

    private ReflectionUtils() {
    }

    public static PropertyDescriptor findPropertyDescriptor(Class<?> objectClass, String fieldName,
                                                            HintContainer deepIndexHintContainer) {
        PropertyDescriptor result = null;
        if (MappingUtils.isDeepMapping(fieldName)) {
            DeepHierarchyElement[] hierarchy = getDeepFieldHierarchy(objectClass, fieldName, deepIndexHintContainer);
            result = hierarchy[hierarchy.length - 1].getPropDescriptor();
        } else {
            PropertyDescriptor[] descriptors = getPropertyDescriptors(objectClass);

            if (descriptors != null) {
                int size = descriptors.length;
                for (int i = 0; i < size; i++) {

          /*
            Bugfix #2826468.
            if object class has methods, f.e, getValue() and getValue(int index) in this case
            could happen that this field couldn't be mapped, because getValue(int index) becomes first
            and PropertyDescriptor.getReadMethod() returns null. We need to exclude IndexedPropertyDescriptor from
            search. At this time dozer dosen't support mappings from indexed fields from POJO.

            See KnownFailures.testIndexedGetFailure()
          */
                    // TODO Disables for now as it breaks indexed array mapping
                    //          if (descriptors[i] instanceof IndexedPropertyDescriptor) {
                    //            continue;
                    //          }

                    String propertyName = descriptors[i].getName();
                    Method readMethod = descriptors[i].getReadMethod();
                    if (fieldName.equals(propertyName)) {
                        return fixGenericDescriptor(objectClass, descriptors[i]);
                    }

                    if (fieldName.equalsIgnoreCase(propertyName)) {
                        result = descriptors[i];
                    }
                }
            }
        }

        return result;
    }

    /**
     * There are some nasty bugs for introspection with generics. This method addresses those nasty bugs and tries to find proper methods if available
     * http://bugs.sun.com/view_bug.do?bug_id=6788525
     * http://bugs.sun.com/view_bug.do?bug_id=6528714
     *
     * @param clazz      type to work on
     * @param descriptor property pair (get/set) information
     * @return descriptor
     */
    private static PropertyDescriptor fixGenericDescriptor(Class<?> clazz, PropertyDescriptor descriptor) {
        Method readMethod = descriptor.getReadMethod();

        if (readMethod != null && (readMethod.isBridge() || readMethod.isSynthetic())) {
            String propertyName = descriptor.getName();
            //capitalize the first letter of the string;
            String baseName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            String setMethodName = "set" + baseName;
            String getMethodName = "get" + baseName;
            Method getMethod = findPreferablyNonSyntheticMethod(getMethodName, clazz);
            Method setMethod = findPreferablyNonSyntheticMethod(setMethodName, clazz);
            try {
                return new PropertyDescriptor(propertyName, getMethod, setMethod);
            } catch (IntrospectionException e) {
                //move on
            }
        }
        return descriptor;
    }

    private static Method findPreferablyNonSyntheticMethod(String methodName, Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Method syntheticMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (!method.isBridge() && !method.isSynthetic()) {
                    return method;
                } else {
                    syntheticMethod = method;
                }
            }
        }
        return syntheticMethod;
    }

    public static DeepHierarchyElement[] getDeepFieldHierarchy(Class<?> parentClass, String field,
                                                               HintContainer deepIndexHintContainer) {
        if (!MappingUtils.isDeepMapping(field)) {
            MappingUtils.throwMappingException("Field does not contain deep field delimitor");
        }

        StringTokenizer toks = new StringTokenizer(field, DozerConstants.DEEP_FIELD_DELIMITER);
        Class<?> latestClass = parentClass;
        DeepHierarchyElement[] hierarchy = new DeepHierarchyElement[toks.countTokens()];
        int index = 0;
        int hintIndex = 0;
        while (toks.hasMoreTokens()) {
            String aFieldName = toks.nextToken();
            String theFieldName = aFieldName;
            int collectionIndex = -1;

            if (aFieldName.contains("[")) {
                theFieldName = aFieldName.substring(0, aFieldName.indexOf("["));
                collectionIndex = Integer.parseInt(aFieldName.substring(aFieldName.indexOf("[") + 1, aFieldName.indexOf("]")));
            }

            PropertyDescriptor propDescriptor = findPropertyDescriptor(latestClass, theFieldName, deepIndexHintContainer);
            DeepHierarchyElement r = new DeepHierarchyElement(propDescriptor, collectionIndex);

            if (propDescriptor == null) {
                MappingUtils.throwMappingException("Exception occurred determining deep field hierarchy for Class --> "
                                                   + parentClass.getName() + ", Field --> " + field + ".  Unable to determine property descriptor for Class --> "
                                                   + latestClass.getName() + ", Field Name: " + aFieldName);
            }

            latestClass = propDescriptor.getPropertyType();
            if (toks.hasMoreTokens()) {
                if (latestClass.isArray()) {
                    latestClass = latestClass.getComponentType();
                } else if (Collection.class.isAssignableFrom(latestClass)) {
                    Class<?> genericType = determineGenericsType(parentClass.getClass(), propDescriptor);

                    if (genericType == null && deepIndexHintContainer == null) {
                        MappingUtils
                                .throwMappingException("Hint(s) or Generics not specified.  Hint(s) or Generics must be specified for deep mapping with indexed field(s). "
                                                       + "Exception occurred determining deep field hierarchy for Class --> "
                                                       + parentClass.getName()
                                                       + ", Field --> "
                                                       + field
                                                       + ".  Unable to determine property descriptor for Class --> "
                                                       + latestClass.getName() + ", Field Name: " + aFieldName);
                    }

                    if (genericType != null) {
                        latestClass = genericType;
                    } else {
                        latestClass = deepIndexHintContainer.getHint(hintIndex);
                        hintIndex += 1;
                    }
                }
            }
            hierarchy[index++] = r;
        }

        return hierarchy;
    }

    public static Method getMethod(Object obj, String methodName) {
        return getMethod(obj.getClass(), methodName);
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        Method result = findMethod(clazz, methodName);
        if (result == null) {
            MappingUtils.throwMappingException("No method found for class:" + clazz + " and method name:" + methodName);
        }
        return result;
    }

    private static Method findMethod(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        Method result = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                result = method;
            }
        }
        return result;
    }

    /**
     * Find a method with concrete string representation of it's parameters
     *
     * @param clazz         clazz to search
     * @param methodName    name of method with representation of it's parameters
     * @param beanContainer beanContainer instance
     * @return found method
     * @throws NoSuchMethodException if no method found
     */
    public static Method findAMethod(Class<?> clazz, String methodName, BeanContainer beanContainer) throws NoSuchMethodException {
        StringTokenizer tokenizer = new StringTokenizer(methodName, "(");
        String m = tokenizer.nextToken();
        Method result;
        // If tokenizer has more elements, it mean that parameters may have been specified
        if (tokenizer.hasMoreElements()) {
            StringTokenizer tokens = new StringTokenizer(tokenizer.nextToken(), ")");
            String params = tokens.hasMoreTokens() ? tokens.nextToken() : null;
            result = findMethodWithParam(clazz, m, params, beanContainer);
        } else {
            result = findMethod(clazz, methodName);
        }
        if (result == null) {
            throw new NoSuchMethodException(clazz.getName() + "." + methodName);
        }
        return result;
    }

    private static Method findMethodWithParam(Class<?> parentDestClass, String methodName, String params, BeanContainer beanContainer)
            throws NoSuchMethodException {
        List<Class<?>> list = new ArrayList<>();
        if (params != null) {
            StringTokenizer tokenizer = new StringTokenizer(params, ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                list.add(MappingUtils.loadClass(token, beanContainer));
            }
        }
        return getMethod(parentDestClass, methodName, list.toArray(new Class[list.size()]));
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> objectClass) {
        // If the class is an interface, use custom method to get all prop descriptors in the inheritance hierarchy.
        // PropertyUtils.getPropertyDescriptors() does not work correctly for interface inheritance. It finds props in the
        // actual interface ok, but does not find props in the inheritance hierarchy.
        if (objectClass.isInterface()) {
            return getInterfacePropertyDescriptors(objectClass);
        } else {
            return PropertyUtils.getPropertyDescriptors(objectClass);
        }
    }

    static PropertyDescriptor[] getInterfacePropertyDescriptors(Class<?> interfaceClass) {
        List<PropertyDescriptor> propDescriptors = new ArrayList<>();
        // Add prop descriptors for interface passed in
        propDescriptors.addAll(Arrays.asList(PropertyUtils.getPropertyDescriptors(interfaceClass)));

        // Look for interface inheritance. If super interfaces are found, recurse up the hierarchy tree and add prop
        // descriptors for each interface found.
        // PropertyUtils.getPropertyDescriptors() does not correctly walk the inheritance hierarchy for interfaces.
        Class<?>[] interfaces = interfaceClass.getInterfaces();
        if (interfaces != null) {
            for (Class<?> superInterfaceClass : interfaces) {
                List<PropertyDescriptor> superInterfacePropertyDescriptors = Arrays
                        .asList(getInterfacePropertyDescriptors(superInterfaceClass));
                /*
                 * #1814758
                 * Check for existing descriptor with the same name to prevent 2 property descriptors with the same name being added
                 * to the result list.  This caused issues when getter and setter of an attribute on different interfaces in
                 * an inheritance hierarchy
                 */
                for (PropertyDescriptor superPropDescriptor : superInterfacePropertyDescriptors) {
                    PropertyDescriptor existingPropDescriptor = findPropDescriptorByName(propDescriptors, superPropDescriptor.getName());
                    if (existingPropDescriptor == null) {
                        propDescriptors.add(superPropDescriptor);
                    } else {
                        try {
                            if (existingPropDescriptor.getReadMethod() == null) {
                                existingPropDescriptor.setReadMethod(superPropDescriptor.getReadMethod());
                            }
                            if (existingPropDescriptor.getWriteMethod() == null) {
                                existingPropDescriptor.setWriteMethod(superPropDescriptor.getWriteMethod());
                            }
                        } catch (IntrospectionException e) {
                            throw new MappingException(e);
                        }

                    }
                }
            }
        }
        return propDescriptors.toArray(new PropertyDescriptor[propDescriptors.size()]);
    }

    private static PropertyDescriptor findPropDescriptorByName(List<PropertyDescriptor> propDescriptors, String name) {
        PropertyDescriptor result = null;
        for (PropertyDescriptor pd : propDescriptors) {
            if (pd.getName().equals(name)) {
                result = pd;
                break;
            }
        }
        return result;
    }

    public static Field getFieldFromBean(Class<?> clazz, String fieldName) {
        return getFieldFromBean(clazz, fieldName, clazz);
    }

    private static Field getFieldFromBean(Class<?> clazz, String fieldName, final Class<?> originalClass) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            // Allow access to private instance var's that dont have public setter.
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getFieldFromBean(clazz.getSuperclass(), fieldName, originalClass);
            }
            throw new MappingException("No such field found " + originalClass.getName() + "." + fieldName, e);
        }
    }

    public static Object invoke(Method method, Object obj, Object[] args) {
        Object result = null;
        try {
            method.setAccessible(true);
            result = method.invoke(obj, args);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals(IAE_MESSAGE)) {
                MappingUtils.throwMappingException(prepareExceptionMessage(method, args), e);
            }
            MappingUtils.throwMappingException(e);
        } catch (IllegalAccessException e) {
            MappingUtils.throwMappingException(e);
        } catch (InvocationTargetException e) {
            MappingUtils.throwMappingException(e);
        }
        return result;
    }

    private static String prepareExceptionMessage(Method method, Object[] args) {
        StringBuffer message = new StringBuffer("Illegal object type for the method '" + method.getName() + "'. \n ");
        message.append("Expected types: \n");
        for (Class<?> type : method.getParameterTypes()) {
            message.append(type.getName());
        }
        message.append("\n Actual types: \n");
        for (Object param : args) {
            message.append(param.getClass().getName());
        }
        return message.toString();
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
        return clazz.getMethod(name, parameterTypes);
    }

    public static <T> T newInstance(Class<T> clazz) {
        T result = null;
        try {
            result = clazz.newInstance();
        } catch (InstantiationException e) {
            MappingUtils.throwMappingException(e);
        } catch (IllegalAccessException e) {
            MappingUtils.throwMappingException(e);
        }
        return result;
    }

    public static Class<?> determineGenericsType(Class<?> parentClazz, PropertyDescriptor propDescriptor) {
        Class<?> result = null;
        //Try getter and setter to determine the Generics type in case one does not exist
        if (propDescriptor.getWriteMethod() != null) {
            result = determineGenericsType(parentClazz, propDescriptor.getWriteMethod(), false);
        }

        if (result == null && propDescriptor.getReadMethod() != null) {
            result = determineGenericsType(parentClazz, propDescriptor.getReadMethod(), true);
        }

        return result;
    }

    public static Class<?> determineGenericsType(Class<?> clazz, Method method, boolean isReadMethod) {
        Class<?> result = null;
        if (isReadMethod) {
            Type parameterType = method.getGenericReturnType();
            result = determineGenericsType(parameterType);
        } else {
            Type[] parameterTypes = method.getGenericParameterTypes();
            if (parameterTypes != null) {
                result = determineGenericsType(parameterTypes[0]);
            }
        }

        if (result == null) {
            // Have a look at generic superclass
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass != null) {
                result = determineGenericsType(genericSuperclass);
            }
        }

        return result;
    }

    public static Class<?> determineGenericsType(Type type) {
        Class<?> result = null;
        if (type != null && ParameterizedType.class.isAssignableFrom(type.getClass())) {
            Type genericType = ((ParameterizedType)type).getActualTypeArguments()[0];
            if (genericType != null) {
                if (genericType instanceof Class) {
                    result = (Class<?>)genericType;
                } else if (genericType instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType)genericType).getRawType();
                    result = (Class<?>)rawType;
                }
            }
        }
        return result;
    }

    /**
     * Finds non-standard setters {@link PropertyUtils#getPropertyDescriptors} does not find.
     * The non-standard setters include:
     * <ul>
     * <li> Setters that return something instead of {@code void}</li>
     * <li> Setters that take a wrapper argument (e.g. Boolean) when the field is of primitive type (e.g. boolean) - or the other way around.
     * </ul>
     *
     * @param clazz     The class to find non-standard setters from
     * @param fieldName The field to find a non-standard setter for
     * @return The non-standard setter or {@code null}
     */
    public static Method getNonStandardSetter(Class<?> clazz, String fieldName) {
        Field field;

        try {
            field = getFieldFromBean(clazz, fieldName);
        } catch (MappingException me) {
            return null;
        }

        String methodName = "set" + StringUtils.capitalize(fieldName);

        for (Method method : clazz.getMethods()) {
            if (isNonVoidSetter(method, methodName) || isAutoboxingSetter(method, methodName, field)) {
                return method;
            }
        }

        return null;
    }

    private static boolean isNonVoidSetter(Method method, String setterMethodName) {
        return method.getName().equals(setterMethodName) && method.getParameterTypes().length == 1
               && method.getReturnType() != Void.TYPE;
    }

    private static boolean isAutoboxingSetter(Method method, String setterMethodName, Field field) {
        return method.getName().equals(setterMethodName) && method.getParameterTypes().length == 1
               && canBeAutoboxed(method.getParameterTypes()[0], field.getType());
    }

    /**
     * Checks if classA or classB can be auto boxed by the JVM
     *
     * @return {@code true}, if both classes are either primitive or wrapper classes and
     * autoboxing is possible between {@code classA} and {@code classB}
     */
    private static boolean canBeAutoboxed(Class<?> classA, Class<?> classB) {
        return ClassUtils.isPrimitiveOrWrapper(classA)
               && ClassUtils.isPrimitiveOrWrapper(classB)
               && (classB.equals(classA) // Same types
               || ClassUtils.primitiveToWrapper(classB).equals(classA) // Matching primitive-wrapper pair, e.g. double - Double
               || ClassUtils.primitiveToWrapper(classA).equals(classB)); // Matching wrapper-primitive pair, e.g. Long - long
    }

}
