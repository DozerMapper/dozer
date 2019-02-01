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
package com.github.dozermapper.core.vo.generics.parameterized;

public class GenericType<AType, BType, CType> {

    private AType a;
    private BType b;
    private CType c;

    public AType getA() {
        return a;
    }

    public void setA(final AType a) {
        this.a = a;
    }

    public BType getB() {
        return b;
    }

    public void setB(final BType b) {
        this.b = b;
    }

    public CType getC() {
        return c;
    }

    public void setC(final CType c) {
        this.c = c;
    }
}
