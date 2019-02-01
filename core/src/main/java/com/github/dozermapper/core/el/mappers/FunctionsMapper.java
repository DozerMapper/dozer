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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;

/**
 * Maps between EL function names and methods.
 */
public class FunctionsMapper extends FunctionMapper {

    private final Map<String, Method> map = new HashMap<>();

    /**
     * Resolves functions set via {@link #mapFunction(String, String, Method)}
     *
     * @param prefix    the prefix of the function
     * @param localName the short name of the function
     * @return the resolved {@link Method} assigned to the function
     */
    @Override
    public Method resolveFunction(String prefix, String localName) {
        return map.get(prefix + ":" + localName);
    }

    /**
     * Sets a function which can be resolved
     *
     * @param prefix    the prefix of the function
     * @param localName the short name of the function
     * @param meth      method to invoke
     */
    @Override
    public void mapFunction(String prefix, String localName, Method meth) {
        map.put(prefix + ":" + localName, meth);
    }
}
