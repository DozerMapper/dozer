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
package com.github.dozermapper.core;

import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.fieldmap.FieldMap;

/**
 * Public custom field mapper interface. A custom field mapper should only be used in very rare and unusual cases
 * because it is invoked for ALL field mappings. For custom mappings of particular fields, using a CustomConverter is a
 * much better choice.
 * <p>
 * If a custom field mapper is specified, Dozer will invoke this class when performing all field mappings. If false is
 * returned from the call to mapField(), then the field will be subsequently mapped by Dozer as normal.
 */
public interface CustomFieldMapper {

    boolean mapField(Object source, Object destination, Object sourceFieldValue, ClassMap classMap, FieldMap fieldMapping);

}
