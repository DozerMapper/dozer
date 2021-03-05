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
package com.github.dozermapper.core.el;

import javax.el.ExpressionFactory;

import com.github.dozermapper.core.util.MappingUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves {@link ExpressionFactory}
 */
public final class ELExpressionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ELExpressionFactory.class);

    private static Boolean isJavaxEL;

    private ELExpressionFactory() {

    }

    /**
     * Checks whether javax.el is on the classpath and a {@link ExpressionFactory} can be constructed
     *
     * @return if javax.el can be used
     */
    public static Boolean isSupported() {
        return isSupported(ELExpressionFactory.class.getClassLoader());
    }

    /**
     * Checks whether javax.el is on the classpath and a {@link ExpressionFactory} can be constructed
     *
     * @param classLoader class loader to resolve {@link ExpressionFactory}
     * @return if javax.el can be used
     */
    public static Boolean isSupported(ClassLoader classLoader) {
        if (isJavaxEL == null) {
            try {
                isJavaxEL = newInstance(classLoader) != null;

                LOG.info("javax.el support is {}", isJavaxEL);
            } catch (IllegalStateException ex) {
                LOG.info("javax.el is not supported; {}", ex.getMessage());

                isJavaxEL = false;
            }
        }

        return isJavaxEL;
    }

    /**
     * Constructs a {@link ExpressionFactory} via {@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
     * or the provided {@link ClassLoader}
     *
     * @param classLoader class loader to resolve {@link ExpressionFactory}
     * @return {@link ExpressionFactory} instance
     * @throws IllegalStateException if javax.el is not on the class path or a {@link ExpressionFactory} instance could not be constructed
     */
    public static ExpressionFactory newInstance(ClassLoader classLoader) throws IllegalStateException {
        resolveClassForName();

        return resolveViaClassLoaderOrTCCL(classLoader);
    }

    private static void resolveClassForName() throws IllegalStateException {
        String expressionFactoryProperty = System.getProperty("javax.el.ExpressionFactory");
        String expressionFactoryClass = MappingUtils.isBlankOrNull(expressionFactoryProperty)
                ? "com.sun.el.ExpressionFactoryImpl"
                : expressionFactoryProperty;

        try {
            Class.forName(expressionFactoryClass);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to resolve ExpressionFactory, " + ex.getMessage(), ex);
        }
    }

    private static ExpressionFactory resolveViaClassLoaderOrTCCL(ClassLoader classLoader) {
        ExpressionFactory answer;

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader newTccl = classLoader == null ? tccl : classLoader;
            Thread.currentThread().setContextClassLoader(newTccl);

            answer = newInstance();
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Failed to resolve ExpressionFactory, " + ex.getMessage(), ex);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }

        return answer;
    }

    /**
     * Calls {@link ExpressionFactory#newInstance()} directly without any classpath checking
     *
     * @return {@link ExpressionFactory} instance
     */
    public static ExpressionFactory newInstance() {
        return ExpressionFactory.newInstance();
    }
}
