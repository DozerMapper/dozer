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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import org.dozer.MappingException;
import org.dozer.config.BeanContainer;
import org.dozer.functional_tests.DataObjectInstantiator;
import org.dozer.util.MappingUtils;

public final class JavassistDataObjectInstantiator implements DataObjectInstantiator {

    public static final DataObjectInstantiator INSTANCE = new JavassistDataObjectInstantiator();

    private JavassistDataObjectInstantiator() {

    }

    @Override
    public String getName() {
        return "javassist";
    }

    public <T> T newInstance(Class<T> classToInstantiate) {
        Class newClass = proxiedClass(classToInstantiate);
        Object instance;
        try {
            instance = newClass.newInstance();
        } catch (Exception e) {
            throw new MappingException(e);
        }

        return (T)instance;
    }

    public <T> T newInstance(Class<T> classToInstantiate, Object[] args) {
        Class newClass = proxiedClass(classToInstantiate);
        Object instance;
        try {
            Class[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = MappingUtils.getRealClass(args[i].getClass(), new BeanContainer());
            }
            Constructor constructor = newClass.getDeclaredConstructor(argTypes);
            instance = constructor.newInstance(args);
        } catch (Exception e) {
            throw new MappingException(e);
        }

        return (T)instance;
    }

    public Object newInstance(Class<?>[] interfacesToProxy, Object target) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(interfacesToProxy);

        Object instance;
        try {
            instance = proxyFactory.create(new Class[0], new Object[0], new MethodHandler() {
                @Override
                public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                    return proceed.invoke(self, args);
                }
            });
        } catch (Exception e) {
            throw new MappingException(e);
        }

        return instance;
    }

    private <T> Class proxiedClass(Class<T> classToInstantiate) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(classToInstantiate);
        return proxyFactory.createClass();
    }
}
