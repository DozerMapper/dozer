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

public final class RelationshipType {

    public static final RelationshipType CUMULATIVE = new RelationshipType();
    public static final RelationshipType NON_CUMULATIVE = new RelationshipType();

    private static final String CUMULATIVE_VALUE = "cumulative";
    private static final String NON_CUMULATIVE_VALUE = "non-cumulative";

    private RelationshipType() {
    }

    public static RelationshipType valueOf(String relationshipType) {
        if (CUMULATIVE_VALUE.equals(relationshipType)) {
            return CUMULATIVE;
        } else if (NON_CUMULATIVE_VALUE.equals(relationshipType)) {
            return NON_CUMULATIVE;
        } else if (StringUtils.isEmpty(relationshipType)) {
            return null;
        }
        throw new IllegalStateException("relationship-type should be cumulative or non-cumulative. " + relationshipType);
    }

}
