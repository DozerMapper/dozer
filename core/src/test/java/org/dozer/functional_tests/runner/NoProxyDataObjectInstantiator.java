/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer.functional_tests.runner;

import org.apache.commons.beanutils.ConstructorUtils;
import org.dozer.functional_tests.DataObjectInstantiator;

public final class NoProxyDataObjectInstantiator implements DataObjectInstantiator {

    public static final NoProxyDataObjectInstantiator INSTANCE = new NoProxyDataObjectInstantiator();

    private NoProxyDataObjectInstantiator() {

    }

    @Override
    public String getName() {
        return "no-proxy";
    }

    public <T> T newInstance(Class<T> classToInstantiate) {
        try {
            return classToInstantiate.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T newInstance(Class<T> classToInstantiate, Object[] args) {
        try {
            return (T)ConstructorUtils.invokeConstructor(classToInstantiate, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object newInstance(Class<?>[] interfacesToProxy, Object target) {
        return target;
    }
}
