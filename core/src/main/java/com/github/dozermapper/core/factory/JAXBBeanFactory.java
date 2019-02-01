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
package com.github.dozermapper.core.factory;

import java.lang.reflect.Method;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Public custom bean factory that can be used by application code when mapping JAXB data objects
 */
public class JAXBBeanFactory implements BeanFactory {

    private static final char INNER_CLASS_DELIMITER = '$';

    private final Logger log = LoggerFactory.getLogger(JAXBBeanFactory.class);

    /**
     * Create a bean implementation of a JAXB interface.
     *
     * @param srcObj      The source object
     * @param srcObjClass The source object class
     * @param beanId      the name of the destination interface class
     * @return A implementation of the destination interface
     */
    @Override
    public Object createBean(Object srcObj, Class<?> srcObjClass, String beanId, BeanContainer beanContainer) {
        log.debug("createBean(Object, Class, String) - start [{}]", beanId);

        int indexOf = beanId.indexOf(INNER_CLASS_DELIMITER);
        while (indexOf > 0) {
            beanId = beanId.substring(0, indexOf) + beanId.substring(indexOf + 1);
            log.debug("createBean(Object, Class, String) - HAS BEEN CHANGED TO  [{}]", beanId);
            indexOf = beanId.indexOf(INNER_CLASS_DELIMITER);
        }
        Object result;

        Class<?> objectFactory = MappingUtils.loadClass(beanId.substring(0, beanId.lastIndexOf(".")) + ".ObjectFactory", beanContainer);
        Object factory = ReflectionUtils.newInstance(objectFactory);
        Method method = null;
        try {
            method = ReflectionUtils.getMethod(objectFactory, "create" + beanId.substring(beanId.lastIndexOf(".") + 1), new Class[] {});
        } catch (NoSuchMethodException e) {
            MappingUtils.throwMappingException(e);
        }
        Object returnObject = ReflectionUtils.invoke(method, factory, new Object[] {});
        log.debug("createBean(Object, Class, String) - end [{}]", returnObject.getClass().getName());
        result = returnObject;

        return result;
    }

}
