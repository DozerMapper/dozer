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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.dozer.config.BeanContainer;
import org.dozer.functional_tests.DataObjectInstantiator;
import org.dozer.util.MappingUtils;

/**
 * Quick and dirty class used to create test data objects that are wrapped with a proxy.  Right now use CGLIB to proxy
 * the data objects since this is probably the most typical usage of proxied data objects(Hibernate lazy loading).
 *
 * @author Matt Tierney
 */
public final class ProxyDataObjectInstantiator implements DataObjectInstantiator {

    public static final ProxyDataObjectInstantiator INSTANCE = new ProxyDataObjectInstantiator();

    private ProxyDataObjectInstantiator() {

    }

    @Override
    public String getName() {
        return "cglib";
    }

    public <T> T newInstance(Class<T> classToInstantiate) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(classToInstantiate);
        enhancer.setCallback(NoOpInterceptor.INSTANCE);

        return (T)enhancer.create();
    }

    public <T> T newInstance(Class<T> classToInstantiate, Object[] args) {
        List<Class<?>> argTypes = new ArrayList<Class<?>>();
        for (Object arg : args) {
            argTypes.add(MappingUtils.getRealClass(arg.getClass(), new BeanContainer()));
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(classToInstantiate);
        enhancer.setCallback(NoOpInterceptor.INSTANCE);

        return (T)enhancer.create(argTypes.toArray(new Class[0]), args);
    }

    public Object newInstance(Class<?>[] interfacesToProxy, final Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(interfacesToProxy);
        enhancer.setCallback(new Dispatcher() {
            public Object loadObject() throws Exception {
                return target;
            }
        });

        return enhancer.create();
    }

    private static class NoOpInterceptor implements MethodInterceptor, Serializable {
        private static final NoOpInterceptor INSTANCE = new NoOpInterceptor();

        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return methodProxy.invokeSuper(object, args);
        }
    }
}
