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

import com.github.dozermapper.core.fieldmap.FieldMap;

/**
 * Internal property descriptor interface. Only intended for internal use.  Dozer property descriptors are used
 * to read and write the actual field mapping values on the target objects.
 */
public interface DozerPropertyDescriptor {

    Class<?> getPropertyType();

    Object getPropertyValue(Object bean);

    void setPropertyValue(Object bean, Object value, FieldMap fieldMap);

    /**
     * Determines generic parameter type
     *
     * @return For Type returns Parameter class, should return null if can't determine type
     */
    Class<?> genericType();

}
