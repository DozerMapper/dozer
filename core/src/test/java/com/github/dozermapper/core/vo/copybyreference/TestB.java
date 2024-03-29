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
package com.github.dozermapper.core.vo.copybyreference;

public class TestB {
    private String one;
    private String oneB;
    private Reference reference;

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getOneB() {
        return oneB;
    }

    public void setOneB(String oneA) {
        this.oneB = oneA;
    }

    public Reference getTestReference() {
        return reference;
    }

    public void setTestReference(Reference testC) {
        this.reference = testC;
    }
}
