/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.builder.model.elengine;

import com.github.dozermapper.core.builder.model.jaxb.VariableDefinition;
import com.github.dozermapper.core.builder.model.jaxb.VariablesDefinition;
import com.github.dozermapper.core.el.ELEngine;

import org.apache.commons.lang3.StringUtils;

/**
 * {@inheritDoc}
 */
public class ELVariableDefinition extends VariableDefinition {

    private final ELEngine elEngine;

    public ELVariableDefinition(ELEngine elEngine, VariableDefinition copy) {
        this(elEngine, (VariablesDefinition)null);

        if (copy != null) {
            this.name = copy.getName();
            this.clazz = copy.getClazz();
        }
    }

    public ELVariableDefinition(ELEngine elEngine, VariablesDefinition parent) {
        super(parent);

        this.elEngine = elEngine;
    }

    @Override
    public void build() {
        if (!StringUtils.isBlank(name) && !StringUtils.isBlank(clazz)) {
            elEngine.setVariable(name, clazz);
        }

        super.build();
    }
}

