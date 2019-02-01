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
package com.github.dozermapper.core.builder.model.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

@ToString
@XmlType(name = "field-type")
@XmlEnum
public enum FieldType {

    @XmlEnumValue("iterate")
    ITERATE("iterate"),

    @XmlEnumValue("generic")
    GENERIC("generic");

    private final String value;

    FieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FieldType fromValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        for (FieldType c : FieldType.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }

        throw new IllegalArgumentException("field-type should be iterate or generic. Found: " + value);
    }
}
