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

/**
 * Utility class to find methods that are bridged by other methods (bridge methods)
 */
public final class BridgedMethodFinder {

    /**
     * utility class constructor
     */
    private BridgedMethodFinder() {
    }

    /**
     * Find the original method for the Java5 bridge Method. If the supplied method
     * is not a bridge method then the supplied method is returned.
     * If the supplied method is a bridge method, the algorithm tries to find a more
     * specific method with parameters and return types that can be assigned to the
     * the supplied method parameters and return type.
     * <p>
     * Informally, method A is more specific than method B if
     * any invocation handled by method A can also be handled by method B.
     *
     * @param bridgeMethod the bridge method (Java 5 specific)
     * @param targetClass  the class the method belongs to
     * @return the original method or a more specific method if available
     */
    public static Method findMethod(final Method bridgeMethod, final Class<?> targetClass) {
        if (bridgeMethod == null || targetClass == null) {
            return bridgeMethod;
        }

        if (!bridgeMethod.isBridge()) {
            return bridgeMethod;
        }

        Method[] methods = bridgeMethod.getDeclaringClass().getDeclaredMethods();
        for (Method method : methods) {
            // don't care about methods that are bridge methods
            if (method.isBridge()) {
                continue;
            }

            // check if the method is more specific
            if (isAssignable(method, bridgeMethod)) {
                return method;
            }
        }
        return bridgeMethod;
    }

    /**
     * returns true if the supplied method can handle the invocation
     * of the candidate Method.
     *
     * @param candidate the method to inspect
     * @param method    the proposed bridge method
     * @return true if the supplied method can handle the invocation
     * of the candidate Method.
     */
    private static boolean isAssignable(final Method candidate, final Method method) {
        if (!method.getName().equals(candidate.getName())) {
            return false;
        }

        if (method.getParameterTypes().length != candidate.getParameterTypes().length) {
            return false;
        }

        if (!isAssignable(method.getReturnType(), candidate.getReturnType())) {
            return false;
        }

        return isAssignable(method.getParameterTypes(), candidate.getParameterTypes());
    }

    /**
     * Returns true if all the classes supplied as a's can be assigned
     * to the classes supplied as b's
     */
    private static boolean isAssignable(final Class<?>[] as, final Class<?>[] bs) {
        if (as == null && bs == null) {
            return true;
        }

        if (as == null || bs == null) {
            return false;
        }

        if (as.length != bs.length) {
            return false;
        }

        for (int i = 0; i < as.length; i++) {
            if (!isAssignable(as[i], bs[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if all the class supplied as a can be assigned
     * from b
     */
    private static boolean isAssignable(final Class<?> a, final Class<?> b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.isAssignableFrom(b);
    }

}
