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

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;
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
@XmlRootElement(name = "allowed-exceptions")
public class AllowedExceptionsDefinition {

    @XmlTransient
    private final ConfigurationDefinition parent;

    @XmlElement(name = "exception", required = true)
    protected List<String> exceptions;

    public AllowedExceptionsDefinition() {
        this(null);
    }

    public AllowedExceptionsDefinition(ConfigurationDefinition parent) {
        this.parent = parent;
    }

    // Fluent API
    //-------------------------------------------------------------------------
    public AllowedExceptionsDefinition addException(String exception) {
        if (getExceptions() == null) {
            setExceptions(new ArrayList<>());
        }

        getExceptions().add(exception);

        return this;
    }

    @SuppressWarnings("unchecked")
    public List<Class<RuntimeException>> build(BeanContainer beanContainer) {
        List<Class<RuntimeException>> answer = new ArrayList<>();
        for (String current : exceptions) {
            Class runtimeException = MappingUtils.loadClass(current, beanContainer);

            if (!RuntimeException.class.isAssignableFrom(runtimeException)) {
                MappingUtils.throwMappingException("allowed-exceptions must extend java.lang.RuntimeException. Found: "
                                                   + runtimeException.getName());
            }

            answer.add(runtimeException);
        }

        return answer;
    }

    public ConfigurationDefinition end() {
        return parent;
    }
}
