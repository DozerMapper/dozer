/*
 * Copyright 2005-2024 Dozer Project
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

import com.github.dozermapper.core.ConfigurableCustomConverter;

public class CustomConverterParamConverter implements ConfigurableCustomConverter {

    private String param;

    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass,
                          Class<?> sourceClass) {
        String source = null;
        Object dest = null;
        if (String.class.isAssignableFrom(sourceClass)) {
            source = (String)sourceFieldValue;
        }

        if (String.class.isAssignableFrom(destinationClass)) {
            dest = source + "-" + param;
        }

        return dest;
    }

    public void setParameter(String parameter) {
        param = parameter;
    }

}
