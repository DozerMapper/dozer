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
 * ELEngine implementation that has no implementation logic.
 * Use when javax.el dependency is not on the class path.
 */
public class NoopELEngine implements ELEngine {

    /**
     * Note, this method does nothing
     *
     * @param key   key to store
     * @param value value to resolve
     * @param <T>   generic type of value
     */
    @Override
    public <T> void setVariable(String key, T value) {
        setVariable(key, value, value.getClass());
    }

    /**
     * Note, this method does nothing
     *
     * @param key   key to store
     * @param value value to resolve
     * @param type  type of value
     * @param <T>   generic type of value
     */
    @Override
    public <T> void setVariable(String key, T value, Class<? extends T> type) {
        //noop
    }

    /**
     * Note, this method does nothing
     *
     * @param prefix the prefix of the function
     * @param method method to resolve
     */
    @Override
    public void setFunction(String prefix, Method method) {
        //noop
    }

    /**
     * Note, this method does nothing
     *
     * @param prefix the prefix of the function
     * @param name   name of methhod
     * @param method method to resolve
     */
    @Override
    public void setFunction(String prefix, String name, Method method) {
        //noop
    }

    /**
     * Note, this method returns the expression passed in
     *
     * @param expression expression to resolve
     * @return the expression passed in
     */
    @Override
    public String resolve(String expression) {
        return expression;
    }
}
