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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.converters.CustomConverterDescription;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "custom-converters")
public class CustomConvertersDefinition {

    @XmlTransient
    private final ConfigurationDefinition parent;

    @XmlElement(name = "converter", required = true)
    protected List<ConverterTypeDefinition> converter;

    public CustomConvertersDefinition() {
        this(null);
    }

    public CustomConvertersDefinition(ConfigurationDefinition parent) {
        this.parent = parent;
    }

    // Fluent API
    //-------------------------------------------------------------------------
    public ConverterTypeDefinition withConverter() {
        if (getConverter() == null) {
            setConverter(new ArrayList<>());
        }

        ConverterTypeDefinition converter = new ConverterTypeDefinition(this);
        getConverter().add(converter);

        return converter;
    }

    public List<CustomConverterDescription> build(BeanContainer beanContainer) {
        List<CustomConverterDescription> answer = new ArrayList<>();
        if (converter != null && converter.size() > 0) {
            for (ConverterTypeDefinition current : converter) {
                answer.add(current.build(beanContainer));
            }
        }

        return answer;
    }

    public ConfigurationDefinition end() {
        return parent;
    }

    public ConfigurationDefinition getParent() {
        return parent;
    }

    public List<ConverterTypeDefinition> getConverter() {
        return converter;
    }

    protected void setConverter(List<ConverterTypeDefinition> converter) {
        this.converter = converter;
    }
}
