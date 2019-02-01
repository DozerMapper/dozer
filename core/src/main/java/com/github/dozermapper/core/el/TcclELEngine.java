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
import javax.el.ValueExpression;

/**
 * ELEngine extension of {@link DefaultELEngine} which uses ContextClassLoader when resolving expressions.
 * Use when running in a OSGi environment due to class loader requirements.
 */
public class TcclELEngine extends DefaultELEngine {

    private final ClassLoader classLoader;

    public TcclELEngine(ExpressionFactory expressionFactory, ClassLoader classLoader) {
        super(expressionFactory);

        this.classLoader = classLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolve(String expression) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader newTccl = classLoader == null ? ELExpressionFactory.class.getClassLoader() : classLoader;
            Thread.currentThread().setContextClassLoader(newTccl);

            ValueExpression expr = expressionFactory.createValueExpression(elContext, expression, String.class);

            return String.valueOf(expr.getValue(elContext));
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }
}
