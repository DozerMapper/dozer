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
package com.github.dozermapper.core.loader.api;

import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.loader.DozerBuilder;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.DozerConstants;

/**
 * Programmatic Builder of Dozer mappings.
 */
public abstract class BeanMappingBuilder {

    private DozerBuilder dozerBuilder;

    public BeanMappingBuilder() {
    }

    /**
     * For internal use
     *
     * @param beanContainer             bean container instance
     * @param destBeanCreator           bean creator instance
     * @param propertyDescriptorFactory property descriptor instance
     * @return mappings created with given builder
     */
    public MappingFileData build(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        dozerBuilder = new DozerBuilder(beanContainer, destBeanCreator, propertyDescriptorFactory);
        configure();
        return dozerBuilder.build();
    }

    public TypeMappingBuilder mapping(String typeA, String typeB, TypeMappingOption... typeMappingOption) {
        return mapping(new TypeDefinition(typeA), new TypeDefinition(typeB), typeMappingOption);
    }

    public TypeMappingBuilder mapping(TypeDefinition typeA, String typeB, TypeMappingOption... typeMappingOption) {
        return mapping(typeA, new TypeDefinition(typeB), typeMappingOption);
    }

    public TypeMappingBuilder mapping(String typeA, TypeDefinition typeB, TypeMappingOption... typeMappingOption) {
        return mapping(new TypeDefinition(typeA), typeB, typeMappingOption);
    }

    public TypeMappingBuilder mapping(Class<?> typeA, Class<?> typeB, TypeMappingOption... typeMappingOption) {
        return mapping(new TypeDefinition(typeA), new TypeDefinition(typeB), typeMappingOption);
    }

    public TypeMappingBuilder mapping(TypeDefinition typeA, Class<?> typeB, TypeMappingOption... typeMappingOption) {
        return mapping(typeA, new TypeDefinition(typeB), typeMappingOption);
    }

    public TypeMappingBuilder mapping(Class<?> typeA, TypeDefinition typeB, TypeMappingOption... typeMappingOption) {
        return mapping(new TypeDefinition(typeA), typeB, typeMappingOption);
    }

    public TypeMappingBuilder mapping(TypeDefinition typeA, TypeDefinition typeB, TypeMappingOption... typeMappingOption) {
        DozerBuilder.MappingBuilder mappingBuilder = dozerBuilder.mapping();
        DozerBuilder.ClassDefinitionBuilder typeBuilderA = mappingBuilder.classA(typeA.getName());
        DozerBuilder.ClassDefinitionBuilder typeBuilderB = mappingBuilder.classB(typeB.getName());

        typeA.build(typeBuilderA);
        typeB.build(typeBuilderB);

        for (TypeMappingOption option : typeMappingOption) {
            option.apply(mappingBuilder);
        }

        return new TypeMappingBuilder(mappingBuilder);
    }

    public TypeDefinition type(String name) {
        return new TypeDefinition(name);
    }

    public TypeDefinition type(Class<?> type) {
        return new TypeDefinition(type);
    }

    public FieldDefinition field(String name) {
        return new FieldDefinition(name);
    }

    /**
     * References current object in mapping process.
     *
     * @return field definition
     */
    public FieldDefinition this_() {
        return new FieldDefinition(DozerConstants.SELF_KEYWORD);
    }

    protected abstract void configure();

}
