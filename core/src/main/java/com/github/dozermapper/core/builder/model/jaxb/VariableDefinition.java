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
package com.github.dozermapper.core.builder.model.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "variable")
public class VariableDefinition {

    @XmlTransient
    private final VariablesDefinition parent;

    @XmlValue
    protected String clazz;

    @XmlAttribute(name = "name", required = true)
    protected String name;

    public VariableDefinition() {
        this(null);
    }

    public VariableDefinition(VariablesDefinition parent) {
        this.parent = parent;
    }

    // Fluent API
    //-------------------------------------------------------------------------
    public VariableDefinition withClazz(String clazz) {
        setClazz(clazz);

        return this;
    }

    public VariableDefinition withName(String value) {
        setName(value);

        return this;
    }

    public VariablesDefinition end() {
        return parent;
    }

    public void build() {
        //NOOP
    }

    public VariablesDefinition getParent() {
        return parent;
    }

    public String getClazz() {
        return clazz;
    }

    protected void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }
}
