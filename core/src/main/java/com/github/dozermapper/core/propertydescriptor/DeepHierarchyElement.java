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
package com.github.dozermapper.core.propertydescriptor;

import java.beans.PropertyDescriptor;

public class DeepHierarchyElement {
    private PropertyDescriptor propDescriptor;
    private int index;

    public DeepHierarchyElement(PropertyDescriptor propDescriptor, int index) {
        this.propDescriptor = propDescriptor;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public PropertyDescriptor getPropDescriptor() {
        return propDescriptor;
    }
}
