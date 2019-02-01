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

import java.util.Collection;

public class TestCustomConverterObject extends BaseTestObject {
    public CustomDoubleObjectIF attribute;

    public CustomDoubleObjectIF primitiveDoubleAttribute;

    public Collection names;

    public CustomDoubleObjectIF getAttribute() {
        return attribute;
    }

    public void setAttribute(CustomDoubleObjectIF attribute) {
        this.attribute = attribute;
    }

    public Collection getNames() {
        return names;
    }

    public void setNames(Collection names) {
        this.names = names;
    }

    public CustomDoubleObjectIF getPrimitiveDoubleAttribute() {
        return primitiveDoubleAttribute;
    }

    public void setPrimitiveDoubleAttribute(CustomDoubleObjectIF primitiveDoubleAttribute) {
        this.primitiveDoubleAttribute = primitiveDoubleAttribute;
    }

}
