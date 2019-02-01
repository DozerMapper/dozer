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

/**
 * javax.el engine to resolve variables and functions
 * See: <a href="https://dozermapper.github.io/gitbook/documentation/expressionlanguage.html">expression language</a>
 */
public interface ELEngine {

    /**
     * Sets a variable to resolve
     *
     * @param key   key to store
     * @param value value to resolve
     * @param <T>   generic type of value
     */
    <T> void setVariable(String key, T value);

    /**
     * Sets a variable
     *
     * @param key   key to store
     * @param value value to resolve
     * @param type  type of value
     * @param <T>   generic type of value
     */
    <T> void setVariable(String key, T value, Class<? extends T> type);

    /**
     * Sets a function to resolve
     *
     * @param prefix the prefix of the function
     * @param method method to resolve
     */
    void setFunction(String prefix, Method method);

    /**
     * Sets a function to resolve
     *
     * @param prefix the prefix of the function
     * @param name   name of methhod
     * @param method method to resolve
     */
    void setFunction(String prefix, String name, Method method);

    /**
     * Resolves expression
     *
     * @param expression expression to resolve
     * @return resolved expression
     */
    String resolve(String expression);
}
