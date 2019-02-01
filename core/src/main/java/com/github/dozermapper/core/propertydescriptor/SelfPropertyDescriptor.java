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
package com.github.dozermapper.core.propertydescriptor;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.fieldmap.FieldMap;

/**
 * Internal class used for copy by reference mappings. Only intended for internal use.
 */
public class SelfPropertyDescriptor implements DozerPropertyDescriptor {

    private final Class<?> self;

    public SelfPropertyDescriptor(Class<?> self) {
        this.self = self;
    }

    public Class<?> getPropertyType() throws MappingException {
        return self;
    }

    public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) throws MappingException {
        // do nothing
    }

    public Class<?> genericType() {
        return null;
    }

    public Object getPropertyValue(Object bean) throws MappingException {
        return bean;
    }

}
