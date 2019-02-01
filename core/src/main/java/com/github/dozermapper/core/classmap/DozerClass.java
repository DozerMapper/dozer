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
package com.github.dozermapper.core.classmap;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Only intended for internal use.
 */
public class DozerClass {

    private String name;
    private Class<?> classToMap;
    private String beanFactory;
    private String factoryBeanId;
    private String mapGetMethod;
    private String mapSetMethod;
    private String createMethod;
    private Boolean mapNull;
    private Boolean mapEmptyString;
    private Boolean accessible;
    private Boolean skipConstructor;
    private final BeanContainer beanContainer;

    public DozerClass(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public DozerClass(String name, Class<?> classToMap, String beanFactory, String factoryBeanId, String mapGetMethod,
                      String mapSetMethod, String createMethod, Boolean mapNull, Boolean mapEmptyString, Boolean accessible, Boolean skipConstructor, BeanContainer beanContainer) {
        this.name = name;
        this.classToMap = classToMap;
        this.beanFactory = beanFactory;
        this.factoryBeanId = factoryBeanId;
        this.mapGetMethod = mapGetMethod;
        this.mapSetMethod = mapSetMethod;
        this.createMethod = createMethod;
        this.mapNull = mapNull;
        this.mapEmptyString = mapEmptyString;
        this.accessible = accessible;
        this.skipConstructor = skipConstructor;
        this.beanContainer = beanContainer;
    }

    public String getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(String beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Class<?> getClassToMap() {
        return classToMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        classToMap = MappingUtils.loadClass(name, beanContainer);
    }

    public String getFactoryBeanId() {
        return factoryBeanId;
    }

    public void setFactoryBeanId(String factoryBeanId) {
        this.factoryBeanId = factoryBeanId;
    }

    public String getMapGetMethod() {
        return mapGetMethod;
    }

    public void setMapGetMethod(String mapGetMethod) {
        this.mapGetMethod = mapGetMethod;
    }

    public String getMapSetMethod() {
        return mapSetMethod;
    }

    public void setMapSetMethod(String mapSetMethod) {
        this.mapSetMethod = mapSetMethod;
    }

    public String getCreateMethod() {
        return createMethod;
    }

    public void setCreateMethod(String createMethod) {
        this.createMethod = createMethod;
    }

    public Boolean getMapNull() {
        return mapNull;
    }

    public void setMapNull(Boolean mapNull) {
        this.mapNull = mapNull;
    }

    public Boolean getMapEmptyString() {
        return mapEmptyString;
    }

    public void setMapEmptyString(Boolean mapEmptyString) {
        this.mapEmptyString = mapEmptyString;
    }

    public boolean isMapTypeCustomGetterSetterClass() {
        return getMapGetMethod() != null || getMapSetMethod() != null;
    }

    public boolean isAccessible() {
        return accessible != null && accessible;
    }

    public void setAccessible(Boolean accessible) {
        this.accessible = accessible;
    }

    public boolean isSkipConstructor() {
        return skipConstructor != null && skipConstructor;
    }

    public void setSkipConstructor(Boolean skipConstructor) {
        this.skipConstructor = skipConstructor;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
