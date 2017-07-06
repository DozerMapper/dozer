/*
 * Copyright 2005-2017 Dozer Project
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

package org.dozer;

import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;

/**
 * Public custom field validator interface. A custom field validator is usually in conjunction with a CustomFieldMapper
 * when implementing the CustomFieldMapperAndValidator interface.
 * Use this when you want logic to prevent source field values being fetched
 * e.g. when checking lazy loaded relationships in an ORM
 *
 * <p>
 * If a custom field validator is specified, Dozer will invoke this class when performing all field mappings. If false is
 * returned from the call the source field value will never be fetched, and subsequently the field will never be mapped.
 *
 * @author Gilbert Grant
 */
public interface CustomFieldValidator {
    boolean canMapField(Object source, Object destination, ClassMap classMap, FieldMap fieldMapping);
}
