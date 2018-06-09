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

import com.github.dozermapper.core.builder.model.jaxb.ConfigurationDefinition;
import com.github.dozermapper.core.builder.model.jaxb.CopyByReferencesDefinition;
import com.github.dozermapper.core.classmap.CopyByReference;
import com.github.dozermapper.core.el.ELEngine;

/**
 * {@inheritDoc}
 */
public class ELCopyByReferencesDefinition extends CopyByReferencesDefinition {

    private final ELEngine elEngine;

    public ELCopyByReferencesDefinition(ELEngine elEngine, CopyByReferencesDefinition copy) {
        this(elEngine, (ConfigurationDefinition)null);

        if (copy != null) {
            this.copyByReference = copy.getCopyByReference();
        }
    }

    public ELCopyByReferencesDefinition(ELEngine elEngine, ConfigurationDefinition parent) {
        super(parent);

        this.elEngine = elEngine;
    }

    @Override
    public List<CopyByReference> build() {
        List<String> resolved = new ArrayList<>();
        if (copyByReference != null) {
            for (String current : copyByReference) {
                resolved.add(elEngine.resolve(current));
            }
        }

        setCopyByReference(resolved);

        return super.build();
    }
}

