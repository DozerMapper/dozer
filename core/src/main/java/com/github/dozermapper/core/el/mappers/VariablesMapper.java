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
package com.github.dozermapper.core.el.mappers;

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * Maps between EL variables and the EL expressions
 */
public class VariablesMapper extends VariableMapper {

    private final Map<String, ValueExpression> map = new HashMap<>();

    /**
     * Resolves variables set via {@link #setVariable(String, ValueExpression)}
     *
     * @param variable variable name
     * @return the resolved {@link ValueExpression} assigned to the variable
     */
    @Override
    public ValueExpression resolveVariable(String variable) {
        return map.get(variable);
    }

    /**
     * Sets a variable which can be resolved
     *
     * @param variable   variable name
     * @param expression expression to assign
     * @return the previous resolved {@link ValueExpression} assigned to the variable, null if none
     */
    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        return map.put(variable, expression);
    }
}
