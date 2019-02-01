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
package com.github.dozermapper.core.util;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 * Hibernate specific implementation. Checks for HibernateProxy interface and uses Hibernate internal API
 * to unwrap proxies.
 */
public class HibernateProxyResolver extends DefaultProxyResolver {

    @Override
    public boolean isProxy(Class<?> clazz) {
        return HibernateProxy.class.isAssignableFrom(clazz);
    }

    @Override
    public <T> T unenhanceObject(T object) {
        if (object instanceof HibernateProxy) {
            HibernateProxy hibernateProxy = (HibernateProxy)object;
            LazyInitializer lazyInitializer = hibernateProxy.getHibernateLazyInitializer();

            return (T)lazyInitializer.getImplementation();
        }
        return object;
    }

}
