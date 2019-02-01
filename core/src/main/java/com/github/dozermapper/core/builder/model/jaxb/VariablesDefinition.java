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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "variables")
public class VariablesDefinition {

    @XmlTransient
    private final ConfigurationDefinition parent;

    @XmlElement(name = "variable", required = true)
    protected List<VariableDefinition> variables;

    public VariablesDefinition() {
        this(null);
    }

    public VariablesDefinition(ConfigurationDefinition parent) {
        this.parent = parent;
    }

    // Fluent API
    //-------------------------------------------------------------------------
    public VariableDefinition withVariable() {
        if (getVariables() == null) {
            setVariables(new ArrayList<>());
        }

        VariableDefinition variable = new VariableDefinition(this);
        getVariables().add(variable);

        return variable;
    }

    public ConfigurationDefinition end() {
        return parent;
    }

    public void build() {
        if (variables != null && variables.size() > 0) {
            for (VariableDefinition current : variables) {
                current.build();
            }
        }
    }
}
