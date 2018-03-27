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
package org.dozer.builder.model.elengine;

import org.apache.commons.lang3.StringUtils;
import org.dozer.builder.model.jaxb.AllowedExceptionsDefinition;
import org.dozer.builder.model.jaxb.ConfigurationDefinition;
import org.dozer.builder.model.jaxb.CopyByReferencesDefinition;
import org.dozer.builder.model.jaxb.CustomConvertersDefinition;
import org.dozer.builder.model.jaxb.MappingsDefinition;
import org.dozer.builder.model.jaxb.VariablesDefinition;
import org.dozer.classmap.Configuration;
import org.dozer.config.BeanContainer;
import org.dozer.el.ELEngine;

public class ELConfigurationDefinition extends ConfigurationDefinition {

    private final ELEngine elEngine;

    public ELConfigurationDefinition(ELEngine elEngine, ConfigurationDefinition copy) {
        this(elEngine, (MappingsDefinition)null);

        if (copy != null) {
            this.stopOnErrors = copy.getStopOnErrors();
            this.dateFormat = copy.getDateFormat();
            this.wildcard = copy.getWildcard();
            this.wildcardCaseInsensitive = copy.getWildcardCaseInsensitive();
            this.trimStrings = copy.getTrimStrings();
            this.mapNull = copy.getMapNull();
            this.mapEmptyString = copy.getMapEmptyString();
            this.beanFactory = copy.getBeanFactory();
            this.relationshipType = copy.getRelationshipType();
            this.customConverters = new ELCustomConvertersDefinition(elEngine, copy.getCustomConverters());
            this.copyByReferences = new ELCopyByReferencesDefinition(elEngine, copy.getCopyByReferences());
            this.allowedExceptions = new ELAllowedExceptionsDefinition(elEngine, copy.getAllowedExceptions());
            this.variables = new ELVariablesDefinition(elEngine, copy.getVariables());
        }
    }

    public ELConfigurationDefinition(ELEngine elEngine, MappingsDefinition parent) {
        super(parent);

        this.elEngine = elEngine;
    }

    @Override
    public CustomConvertersDefinition withCustomConverters() {
        ELCustomConvertersDefinition customConverters = new ELCustomConvertersDefinition(elEngine, this);
        setCustomConverters(customConverters);

        return customConverters;
    }

    @Override
    public CopyByReferencesDefinition withCopyByReferences() {
        ELCopyByReferencesDefinition copyByReferences = new ELCopyByReferencesDefinition(elEngine, this);
        setCopyByReferences(copyByReferences);

        return copyByReferences;
    }

    @Override
    public AllowedExceptionsDefinition withAllowedExceptions() {
        ELAllowedExceptionsDefinition allowedExceptions = new ELAllowedExceptionsDefinition(elEngine, this);
        setAllowedExceptions(allowedExceptions);

        return allowedExceptions;
    }

    @Override
    public VariablesDefinition withVariables() {
        ELVariablesDefinition variables = new ELVariablesDefinition(elEngine, this);
        setVariables(variables);

        return variables;
    }

    @Override
    public Configuration build(BeanContainer beanContainer) {
        if (!StringUtils.isBlank(beanFactory)) {
            setBeanFactory(elEngine.resolve(beanFactory));
        }

        return super.build(beanContainer);
    }
}

