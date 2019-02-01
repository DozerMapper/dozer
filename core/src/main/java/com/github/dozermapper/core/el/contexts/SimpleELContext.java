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
package com.github.dozermapper.core.el.contexts;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import com.github.dozermapper.core.el.mappers.FunctionsMapper;
import com.github.dozermapper.core.el.mappers.VariablesMapper;

/**
 * Provides a {@link ELContext} which can resolve
 * Functions via {@link FunctionsMapper} and Variables via {@link VariablesMapper}
 */
public class SimpleELContext extends ELContext {

    private final FunctionMapper functions = new FunctionsMapper();
    private final VariableMapper variables = new VariablesMapper();
    private final ELResolver resolver;

    public SimpleELContext(ELResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ELResolver getELResolver() {
        return resolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionMapper getFunctionMapper() {
        return functions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VariableMapper getVariableMapper() {
        return variables;
    }
}
