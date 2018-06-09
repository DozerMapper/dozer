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

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.builder.model.jaxb.AllowedExceptionsDefinition;
import com.github.dozermapper.core.builder.model.jaxb.ConfigurationDefinition;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.ELEngine;

/**
 * {@inheritDoc}
 */
public class ELAllowedExceptionsDefinition extends AllowedExceptionsDefinition {

    private final ELEngine elEngine;

    public ELAllowedExceptionsDefinition(ELEngine elEngine, AllowedExceptionsDefinition copy) {
        this(elEngine, (ConfigurationDefinition)null);

        if (copy != null) {
            this.exceptions = copy.getExceptions();
        }
    }

    public ELAllowedExceptionsDefinition(ELEngine elEngine, ConfigurationDefinition parent) {
        super(parent);

        this.elEngine = elEngine;
    }

    @Override
    public List<Class<RuntimeException>> build(BeanContainer beanContainer) {
        List<String> resolved = new ArrayList<>();
        if (exceptions != null) {
            for (String current : exceptions) {
                resolved.add(elEngine.resolve(current));
            }
        }

        setExceptions(resolved);

        return super.build(beanContainer);
    }
}

