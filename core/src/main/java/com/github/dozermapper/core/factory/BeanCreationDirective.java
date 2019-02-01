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

public class BeanCreationDirective {

    private Object srcObject;
    private Class<?> srcClass;
    private Class<?> targetClass;
    private Class<?> alternateClass;
    private String factoryName;
    private String factoryId;
    private String createMethod;
    private Boolean skipConstructor;
    private Object destObj;
    private String destFieldName;

    public BeanCreationDirective() {
        super();
    }

    public BeanCreationDirective(Object srcObject, Class<?> srcClass, Class<?> targetClass, Class<?> alternateClass, String factoryName, String factoryId, String createMethod,
                                 Boolean skipConstructor) {
        this(srcObject, srcClass, targetClass, alternateClass, factoryName, factoryId, createMethod, skipConstructor, null, null);
    }

    public BeanCreationDirective(Object srcObject, Class<?> srcClass, Class<?> targetClass, Class<?> alternateClass, String factoryName, String factoryId, String createMethod,
                                 Boolean skipConstructor, Object destObj, String destFieldName) {
        this.srcObject = srcObject;
        this.srcClass = srcClass;
        this.targetClass = targetClass;
        this.alternateClass = alternateClass;
        this.factoryName = factoryName;
        this.factoryId = factoryId;
        this.createMethod = createMethod;
        this.skipConstructor = skipConstructor != null ? skipConstructor : false;
        this.destObj = destObj;
        this.destFieldName = destFieldName;
    }

    public Object getSrcObject() {
        return srcObject;
    }

    public Class<?> getSrcClass() {
        return srcClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Class<?> getAlternateClass() {
        return alternateClass;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public String getCreateMethod() {
        return createMethod;
    }

    public void setSrcObject(Object srcObject) {
        this.srcObject = srcObject;
    }

    public void setSrcClass(Class<?> srcClass) {
        this.srcClass = srcClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public void setAlternateClass(Class<?> alternateClass) {
        this.alternateClass = alternateClass;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public void setCreateMethod(String createMethod) {
        this.createMethod = createMethod;
    }

    public Boolean isSkipConstructor() {
        return skipConstructor;
    }

    public void setSkipConstructor(Boolean skipConstructor) {
        this.skipConstructor = skipConstructor;
    }

    public Class<?> getActualClass() {
        if (targetClass != null) {
            return targetClass;
        } else {
            return alternateClass;
        }
    }

    public Object getDestObj() {
        return destObj;
    }

    public void setDestObj(Object destObj) {
        this.destObj = destObj;
    }

    public String getDestFieldName() {
        return destFieldName;
    }

    public void setDestFieldName(String destFieldName) {
        this.destFieldName = destFieldName;
    }

    public String getFieldName() {
        if (destFieldName != null) {
            return destFieldName;
        } else {
            return factoryName;
        }
    }
}
