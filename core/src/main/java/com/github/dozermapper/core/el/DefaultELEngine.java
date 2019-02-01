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

import java.lang.reflect.Method;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import com.github.dozermapper.core.el.contexts.SimpleELContext;

/**
 * ELEngine implementation that uses an {@link ExpressionFactory} and {@link SimpleELContext}
 * to resolve variables and functions
 */
public class DefaultELEngine implements ELEngine {

    protected ELContext elContext;
    protected ExpressionFactory expressionFactory;

    public DefaultELEngine(ExpressionFactory expressionFactory) {
        ELResolver resolver = new CompositeELResolver() {
            {
                add(new ArrayELResolver());
                add(new ListELResolver());
                add(new MapELResolver());
                add(new BeanELResolver());
                add(new ResourceBundleELResolver());
            }
        };

        this.elContext = new SimpleELContext(resolver);
        this.expressionFactory = expressionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void setVariable(String key, T value) {
        setVariable(key, value, value.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void setVariable(String key, T value, Class<? extends T> type) {
        ValueExpression valueExpression = expressionFactory.createValueExpression(value, type);

        VariableMapper variableMapper = elContext.getVariableMapper();
        variableMapper.setVariable(key, valueExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFunction(String prefix, Method method) {
        FunctionMapper functions = elContext.getFunctionMapper();
        functions.mapFunction(prefix, method.getName(), method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFunction(String prefix, String name, Method method) {
        FunctionMapper functions = elContext.getFunctionMapper();
        functions.mapFunction(prefix, name, method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolve(String expression) {
        ValueExpression expr = expressionFactory.createValueExpression(elContext, expression, String.class);

        return String.valueOf(expr.getValue(elContext));
    }
}
