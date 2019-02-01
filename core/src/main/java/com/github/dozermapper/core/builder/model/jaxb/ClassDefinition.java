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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.github.dozermapper.core.classmap.DozerClass;
import com.github.dozermapper.core.config.BeanContainer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Specifies one of the classes in the mapping definition. All Mapping definitions are bi-directional by default.
 * Global configuration and Mapping element values are inherited
 * <p>
 * Required Attributes:
 * <p>
 * Optional Attributes:
 * <p>
 * bean-factory The factory class to create data objects. This typically will not be specified.
 * By default Dozer constructs new instances of data objects by invoking the no-arg constructor
 * <p>
 * factory-bean-id The id passed to the specified bean factory
 * <p>
 * map-set-method For Map backed objects, this indicates which setter method should be used to retrieve field
 * values. This should only be used of Map backed objects.
 * <p>
 * map-get-method For Map backed objects, this indicates which getter method should be used to retrieve field values.
 * This should only be used of Map backed objects.
 * <p>
 * create-method Which method to invoke to create a new instance of the class. This is typically not specified.
 * By default, the no arg constructor(public or private) is used
 * <p>
 * map-null Indicates whether null values are mapped. The default value is "true"
 * <p>
 * map-empty-string Indicates whether empty string values are mapped. The default value is "true"
 * <p>
 * is-accessible Indicates whether Dozer bypasses getter/setter methods and accesses the field directly. This will typically be set to "false". The default value is
 * "false". If set to "true", the getter/setter methods will NOT be invoked. You would want to set this to "true" if the field is lacking a getter or setter method.
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "class")
public class ClassDefinition {

    @XmlTransient
    private final MappingDefinition parentMappingDefinition;

    @XmlTransient
    private final ConverterTypeDefinition parentConverterTypeDefinition;

    @XmlValue
    protected String clazz;

    @XmlAttribute(name = "bean-factory")
    protected String beanFactory;

    @XmlAttribute(name = "factory-bean-id")
    protected String factoryBeanId;

    @XmlAttribute(name = "map-set-method")
    protected String mapSetMethod;

    @XmlAttribute(name = "map-get-method")
    protected String mapGetMethod;

    @XmlAttribute(name = "create-method")
    protected String createMethod;

    @XmlAttribute(name = "map-null")
    protected Boolean mapNull;

    @XmlAttribute(name = "map-empty-string")
    protected Boolean mapEmptyString;

    @XmlAttribute(name = "is-accessible")
    protected Boolean isAccessible;

    @XmlAttribute(name = "skip-constructor")
    protected Boolean skipConstructor;

    public ClassDefinition() {
        this(null, null);
    }

    public ClassDefinition(MappingDefinition parentMappingDefinition, ConverterTypeDefinition parentConverterTypeDefinition) {
        this.parentMappingDefinition = parentMappingDefinition;
        this.parentConverterTypeDefinition = parentConverterTypeDefinition;
    }

    // Fluent API
    //-------------------------------------------------------------------------
    public ClassDefinition withClazz(String clazz) {
        setClazz(clazz);

        return this;
    }

    public ClassDefinition withBeanFactory(String beanFactory) {
        setBeanFactory(beanFactory);

        return this;
    }

    public ClassDefinition withFactoryBeanId(String factoryBeanId) {
        setFactoryBeanId(factoryBeanId);

        return this;
    }

    public ClassDefinition withMapSetMethod(String mapSetMethod) {
        setMapSetMethod(mapSetMethod);

        return this;
    }

    public ClassDefinition withMapGetMethod(String mapGetMethod) {
        setMapGetMethod(mapGetMethod);

        return this;
    }

    public ClassDefinition withCreateMethod(String createMethod) {
        setCreateMethod(createMethod);

        return this;
    }

    public ClassDefinition withMapNull(Boolean mapNull) {
        setMapNull(mapNull);

        return this;
    }

    public ClassDefinition withMapEmptyString(Boolean mapEmptyString) {
        setMapEmptyString(mapEmptyString);

        return this;
    }

    public ClassDefinition withAccessible(Boolean accessible) {
        setIsAccessible(accessible);

        return this;
    }

    public ClassDefinition withSkipConstructor(Boolean skipConstructor) {
        setSkipConstructor(skipConstructor);

        return this;
    }

    public MappingDefinition end() {
        return parentMappingDefinition;
    }

    public ConverterTypeDefinition endType() {
        return parentConverterTypeDefinition;
    }

    public DozerClass build(BeanContainer beanContainer) {
        DozerClass dozerClass = new DozerClass(beanContainer);
        dozerClass.setName(clazz);
        dozerClass.setBeanFactory(beanFactory);
        dozerClass.setFactoryBeanId(factoryBeanId);
        dozerClass.setMapGetMethod(mapGetMethod);
        dozerClass.setMapSetMethod(mapSetMethod);
        dozerClass.setCreateMethod(createMethod);
        dozerClass.setMapNull(mapNull);
        dozerClass.setMapEmptyString(mapEmptyString);
        dozerClass.setAccessible(isAccessible);
        dozerClass.setSkipConstructor(skipConstructor);

        return dozerClass;
    }
}
