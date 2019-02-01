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
package com.github.dozermapper.core.classmap.generator;

import java.util.Objects;

/**
 * Internal class representing an wildcard mapping between a source field and a destination field.
 */
public class WildcardFieldMapping {
    private String srcFieldName;
    private String destFieldName;

    public WildcardFieldMapping(String srcFieldName, String destFieldName) {
        this.srcFieldName = srcFieldName;
        this.destFieldName = destFieldName;
    }

    public String getSrcFieldName() {
        return srcFieldName;
    }

    public String getDestFieldName() {
        return destFieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WildcardFieldMapping that = (WildcardFieldMapping)o;
        return Objects.equals(srcFieldName, that.srcFieldName)
               && Objects.equals(destFieldName, that.destFieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcFieldName, destFieldName);
    }
}
