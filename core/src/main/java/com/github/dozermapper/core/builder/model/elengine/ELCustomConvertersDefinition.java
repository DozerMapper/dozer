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
import java.util.stream.Collectors;

import com.github.dozermapper.core.builder.model.jaxb.ConfigurationDefinition;
import com.github.dozermapper.core.builder.model.jaxb.ConverterTypeDefinition;
import com.github.dozermapper.core.builder.model.jaxb.CustomConvertersDefinition;
import com.github.dozermapper.core.el.ELEngine;

/**
 * {@inheritDoc}
 */
public class ELCustomConvertersDefinition extends CustomConvertersDefinition {

    private final ELEngine elEngine;

    public ELCustomConvertersDefinition(ELEngine elEngine, CustomConvertersDefinition copy) {
        this(elEngine, (ConfigurationDefinition)null);

        if (copy != null) {
            if (copy.getConverter() != null && copy.getConverter().size() > 0) {
                this.converter = copy.getConverter()
                        .stream()
                        .map(c -> new ELConverterTypeDefinition(elEngine, c))
                        .collect(Collectors.toList());
            }
        }
    }

    public ELCustomConvertersDefinition(ELEngine elEngine, ConfigurationDefinition parent) {
        super(parent);

        this.elEngine = elEngine;
    }

    @Override
    public ConverterTypeDefinition withConverter() {
        if (getConverter() == null) {
            setConverter(new ArrayList<>());
        }

        ELConverterTypeDefinition converter = new ELConverterTypeDefinition(elEngine, this);
        getConverter().add(converter);

        return converter;
    }
}

