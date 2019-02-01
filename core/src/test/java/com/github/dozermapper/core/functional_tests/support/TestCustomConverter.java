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
package com.github.dozermapper.core.functional_tests.support;

import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.vo.CustomDoubleObject;
import com.github.dozermapper.core.vo.CustomDoubleObjectIF;

public class TestCustomConverter implements CustomConverter {

    public Object convert(Object destination, Object source, Class<?> destClass, Class<?> sourceClass) {

        if (source == null) {
            return null;
        }

        CustomDoubleObjectIF dest = null;
        if (source instanceof Double) {
            // check to see if the object already exists
            if (destination == null) {
                dest = new CustomDoubleObject();
            } else {
                dest = (CustomDoubleObjectIF)destination;
            }
            dest.setTheDouble(((Double)source).doubleValue());
            return dest;
        } else if (source instanceof CustomDoubleObject) {
            double sourceObj = ((CustomDoubleObjectIF)source).getTheDouble();
            return new Double(sourceObj);
        } else {
            throw new MappingException("Converter TestCustomConverter used incorrectly. Arguments passed in were:" + destination
                                       + " and " + source);
        }
    }
}
