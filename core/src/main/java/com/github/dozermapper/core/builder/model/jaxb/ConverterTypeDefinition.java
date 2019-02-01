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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.converters.CustomConverterDescription;
import com.github.dozermapper.core.util.MappingUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "converter-type")
public class ConverterTypeDefinition {

    @XmlTransient
    private final CustomConvertersDefinition parent;

    @XmlElement(name = "class-a", required = true)
    protected ClassDefinition classA;

    @XmlElement(name = "class-b", required = true)
    protected ClassDefinition classB;

    @XmlAttribute(name = "type", required = true)
    protected String type;

    public ConverterTypeDefinition() {
        this(null);
    }

    public ConverterTypeDefinition(CustomConvertersDefinition parent) {
        this.parent = parent;
    }

    // Fluent API
    //-------------------------------------------------------------------------
    public ClassDefinition withClassA() {
        ClassDefinition classA = new ClassDefinition(null, this);
        setClassA(classA);

        return classA;
    }

    public ClassDefinition withClassB() {
        ClassDefinition classB = new ClassDefinition(null, this);
        setClassB(classB);

        return classB;
    }

    public ConverterTypeDefinition withType(String type) {
        setType(type);

        return this;
    }

    public CustomConverterDescription build(BeanContainer beanContainer) {
        CustomConverterDescription converterDescription = new CustomConverterDescription();
        converterDescription.setType(MappingUtils.loadClass(type, beanContainer));
        converterDescription.setClassA(MappingUtils.loadClass(classA.build(beanContainer).getName(), beanContainer));
        converterDescription.setClassB(MappingUtils.loadClass(classB.build(beanContainer).getName(), beanContainer));

        return converterDescription;
    }

    public CustomConvertersDefinition end() {
        return parent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("classA", classA)
                .append("classB", classB)
                .append("type", type)
                .toString();
    }
}
