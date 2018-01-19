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
package org.dozer.functional_tests.support;

import org.dozer.CustomFieldMapper;
import org.dozer.MappingException;
import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;
import org.dozer.vo.SimpleObj;
import org.dozer.vo.SimpleObjPrime;

/**
 * @author tierney.matt
 */
public class TestCustomFieldMapper implements CustomFieldMapper {

    public static String fieldValue = "This field was set on the dest object by the TestCustomFieldMapper";

    public boolean mapField(Object sourceObj, Object destObj, Object sourceFieldValue, ClassMap classMap, FieldMap fieldMapping) {
        boolean result = false;

        if (!(sourceObj instanceof SimpleObj)) {
            throw new MappingException("Unsupported source object type.  Should be of type: SimpleObj");
        }

        if (!(destObj instanceof SimpleObjPrime)) {
            throw new MappingException("Unsupported dest object type.  Should be of type: SimpleObjPrime");
        }

        if (fieldMapping.getSrcFieldName().equals("field1")) {
            ((SimpleObjPrime)destObj).setField1(fieldValue);
            result = true;
        }

        return result;
    }
}
