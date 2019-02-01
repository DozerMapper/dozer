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

public class MethodFieldTestObject extends BaseTestObject {
    String integerStr;
    String priceItemStr;
    String fieldOne;

    public String getIntegerStr() {
        return integerStr;
    }

    public String getPriceItemStr() {
        return priceItemStr;
    }

    public void setPriceItem(String priceItem) {
        this.priceItemStr = priceItem;
    }

    public void setIntegerStr(String integerStr) {
        this.integerStr = integerStr;
    }

    public String getFieldOne() {
        return fieldOne;
    }

    public void setFieldOne(String fieldOne) {
        this.fieldOne = fieldOne;
    }

}
