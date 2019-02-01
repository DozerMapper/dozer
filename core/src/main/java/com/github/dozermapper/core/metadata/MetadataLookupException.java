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
package com.github.dozermapper.core.metadata;

import com.github.dozermapper.core.MappingException;

/**
 * Used whenever a lookup in the meta model fails, e.g. when a class cannot be found in the
 * mapping definitions.
 */
public class MetadataLookupException extends MappingException {

    public MetadataLookupException(String description) {
        super(description);
    }

}
