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
package com.github.dozermapper.core.vo;

import com.github.dozermapper.core.vo.deep.House;

public class InsideTestObject extends BaseTestObject {

    private String label;
    private Integer wrapper;
    private int toWrapper;
    private String createdByFactoryName;
    private House house;
    private String testCreateMethod;

    public int getToWrapper() {
        return toWrapper;
    }

    public void setToWrapper(int toWrapper) {
        this.toWrapper = toWrapper;
    }

    public Integer getWrapper() {
        return wrapper;
    }

    public void setWrapper(Integer wrapper) {
        this.wrapper = wrapper;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCreatedByFactoryName() {
        return createdByFactoryName;
    }

    public void setCreatedByFactoryName(String createdByFactoryName) {
        this.createdByFactoryName = createdByFactoryName;
    }

    public static InsideTestObject createMethod() {
        InsideTestObject itop = new InsideTestObject();
        itop.setTestCreateMethod("testCreateMethod");
        return itop;
    }

    public String getTestCreateMethod() {
        return testCreateMethod;
    }

    public void setTestCreateMethod(String testCreateMethod) {
        this.testCreateMethod = testCreateMethod;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
