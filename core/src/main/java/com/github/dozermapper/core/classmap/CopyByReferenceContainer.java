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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Only intended for internal use.
 */
public class CopyByReferenceContainer {

    private List<CopyByReference> copyByReferences = new ArrayList<>();

    public void add(CopyByReference copyByReference) {
        copyByReferences.add(copyByReference);
    }

    public boolean contains(Class type) {
        return contains(type.getName());
    }

    public boolean contains(String typeName) {
        for (CopyByReference reference : copyByReferences) {
            if (reference.matches(typeName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
