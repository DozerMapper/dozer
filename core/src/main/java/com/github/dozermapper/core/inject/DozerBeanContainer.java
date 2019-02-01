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
package com.github.dozermapper.core.inject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.util.ReflectionUtils;

public class DozerBeanContainer implements BeanRegistry {

    private Map<Class<?>, Object[]> beans = new ConcurrentHashMap<>();

    public void register(Class<?> type) {
        beans.put(type, new Object[1]);
    }

    public <T> T getBean(Class<T> type) {
        Collection<T> result = getBeans(type);
        if (result.isEmpty()) {
            throw new IllegalStateException("Bean is not registered : " + type.getName());
        }
        if (result.size() > 1) {
            throw new IllegalStateException("More than one bean found of type : " + type.getName());
        }

        return result.iterator().next();
    }

    public <T> Collection<T> getBeans(Class<T> type) {
        HashSet<T> result = new HashSet<>();
        for (Map.Entry<Class<?>, Object[]> entry : beans.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                Object[] value = entry.getValue();
                if (value[0] == null) {
                    value[0] = wireBean(entry.getKey());
                }
                result.add((T)value[0]);
            }
        }
        return result;
    }

    private <T> T wireBean(Class<T> type) {
        final T bean = ReflectionUtils.newInstance(type);

        Field[] destFields = type.getDeclaredFields();
        for (Field field : destFields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                Class<?> fieldType = field.getType();
                try {
                    if (field.get(bean) == null) {
                        Object dependency = wireBean(fieldType);
                        field.set(bean, dependency);
                    }
                } catch (IllegalAccessException e) {
                    throw new MappingException("Field annotated with @Inject is not accessible : " + field.getName(), e);
                }
            }
        }

        Object[] instance = beans.get(type);
        if (instance == null) {
            instance = new Object[1];
            beans.put(type, instance);
        }
        instance[0] = bean;
        return bean;
    }

}
