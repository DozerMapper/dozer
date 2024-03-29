/*
 * Copyright 2005-2024 Dozer Project
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

public class SuperClassPrime extends SuperSuperClassPrime {
    private String superAttr;
    private String[] superArray;
    private String superFieldToExcludePrime;

    public String[] getSuperArray() {
        return superArray;
    }

    public void setSuperArray(String[] superArray) {
        this.superArray = superArray;
    }

    public String getSuperAttr() {
        return superAttr;
    }

    public void setSuperAttr(String superAttr) {
        this.superAttr = superAttr;
    }

    public String getSuperFieldToExcludePrime() {
        return superFieldToExcludePrime;
    }

    public void setSuperFieldToExcludePrime(String superFieldToExcludePrime) {
        this.superFieldToExcludePrime = superFieldToExcludePrime;
    }
}
