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

import org.apache.commons.lang3.StringUtils;

public final class MappingDirection {

    private static final String BI_DIRECTIONAL_VALUE = "bi-directional";
    private static final String ONE_WAY_VALUE = "one-way";

    /**
     * Default mapping approach when a to b to a' then a == a'
     */
    public static final MappingDirection BI_DIRECTIONAL = new MappingDirection();

    /**
     * Unidirectional mapping when a to b to a' then a != a'
     */
    public static final MappingDirection ONE_WAY = new MappingDirection();

    private MappingDirection() {

    }

    public static MappingDirection valueOf(String mappingDirection) {
        if (BI_DIRECTIONAL_VALUE.equals(mappingDirection)) {
            return BI_DIRECTIONAL;
        } else if (ONE_WAY_VALUE.equals(mappingDirection)) {
            return ONE_WAY;
        } else if (StringUtils.isEmpty(mappingDirection)) {
            return null;
        }

        throw new IllegalStateException("type should be bi-directional or one-way. " + mappingDirection);
    }
}
