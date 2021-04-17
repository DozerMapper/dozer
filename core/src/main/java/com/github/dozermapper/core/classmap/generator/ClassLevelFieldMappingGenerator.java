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
package com.github.dozermapper.core.classmap.generator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.ClassMapBuilder;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.CollectionUtils;

/**
 * Provides default field mappings when either the source class, destination class or both
 * classes have been declared field accessible e.g. with {@code is-accessible="true"}.
 */
public class ClassLevelFieldMappingGenerator implements ClassMapBuilder.ClassMappingGenerator {

    private final BeanContainer beanContainer;
    private final DestBeanCreator destBeanCreator;
    private final PropertyDescriptorFactory propertyDescriptorFactory;

    public ClassLevelFieldMappingGenerator(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        this.beanContainer = beanContainer;
        this.destBeanCreator = destBeanCreator;
        this.propertyDescriptorFactory = propertyDescriptorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(ClassMap classMap) {
        return srcClassIsAccessible(classMap) || destClassIsAccessible(classMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(ClassMap classMap, Configuration configuration) {
        BeanFieldsDetector beanFieldsDetector = new JavaBeanFieldsDetector();

        Set<String> destFieldNames = getDeclaredFieldNames(classMap.getDestClassToMap());
        Set<String> destWritablePropertyNames = beanFieldsDetector.getWritableFieldNames(classMap.getDestClassToMap());
        Set<String> srcFieldNames = getDeclaredFieldNames(classMap.getSrcClassToMap());
        Set<String> srcReadablePropertyNames = beanFieldsDetector.getReadableFieldNames(classMap.getSrcClassToMap());

        Set<WildcardFieldMapping> matchingUnmappedFields = (classMap.isWildcardCaseInsensitive()) ? getMatchingUnmappedFieldNamesCaseInsensitive(
                classMap.getFieldMaps(), srcFieldNames, destFieldNames) : getMatchingUnmappedFieldNames(
                classMap.getFieldMaps(), srcFieldNames, destFieldNames);

        for (WildcardFieldMapping matchingFields : matchingUnmappedFields) {
            mapFieldAppropriately(classMap, configuration, matchingFields, destWritablePropertyNames, srcReadablePropertyNames);
        }

        return false;
    }

    private void mapFieldAppropriately(ClassMap classMap, Configuration configuration,
                                       WildcardFieldMapping matchingFields, Set<String> destWritablePropertyNames,
                                       Set<String> srcReadablePropertyNames) {
        MappingType mappingType;

        if (!destClassIsAccessible(classMap) && destWritablePropertyNames.contains(matchingFields.getSrcFieldName())) {
            mappingType = MappingType.FIELD_TO_SETTER;
        } else if (!srcClassIsAccessible(classMap) && srcReadablePropertyNames.contains(matchingFields.getDestFieldName())) {
            mappingType = MappingType.GETTER_TO_FIELD;
        } else {
            mappingType = MappingType.FIELD_TO_FIELD;
        }

        GeneratorUtils.addGenericMapping(mappingType, classMap, configuration,
                                         matchingFields.getSrcFieldName(), matchingFields.getDestFieldName(), beanContainer, destBeanCreator, propertyDescriptorFactory);
    }

    private Set<String> getDeclaredFieldNames(Class<?> srcType) {
        Set<String> declaredFieldNames = new HashSet<>();

        do {
            for (Field field : srcType.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    declaredFieldNames.add(field.getName());
                }
            }

            srcType = srcType.getSuperclass();
        }
        while (srcType != null);

        return declaredFieldNames;
    }

    private Set<WildcardFieldMapping> getMatchingUnmappedFieldNames(List<FieldMap> fieldMaps,
                                                                    Set<String> srcFieldNames,
                                                                    Set<String> destFieldNames) {

        for (FieldMap fieldMap : fieldMaps) {
            // Remove all already mapped
            srcFieldNames.remove(fieldMap.getSrcFieldName());
            destFieldNames.remove(fieldMap.getDestFieldName());
        }

        return CollectionUtils.intersection(srcFieldNames, destFieldNames).stream().map(mutualFieldName ->
                                                                                                new WildcardFieldMapping(mutualFieldName, mutualFieldName))
                .collect(Collectors.toSet());
    }

    private Set<WildcardFieldMapping> getMatchingUnmappedFieldNamesCaseInsensitive(List<FieldMap> fieldMaps,
                                                                                   Set<String> srcFieldNames,
                                                                                   Set<String> destFieldNames) {
        Map<String, String> srcFieldNamesLookup = new HashMap<>(srcFieldNames.size(), 1);
        Map<String, String> destFieldNamesLookup = new HashMap<>(destFieldNames.size(), 1);

        for (String srcFieldName : srcFieldNames) {
            srcFieldNamesLookup.put(srcFieldName.toLowerCase(), srcFieldName);
        }
        for (String destFieldName : destFieldNames) {
            destFieldNamesLookup.put(destFieldName.toLowerCase(), destFieldName);
        }

        for (FieldMap fieldMap : fieldMaps) {
            // Remove all already mapped
            srcFieldNamesLookup.remove(fieldMap.getSrcFieldName().toLowerCase());
            destFieldNamesLookup.remove(fieldMap.getDestFieldName().toLowerCase());
        }

        Set<WildcardFieldMapping> wildcardFields = new HashSet<>();

        for (Map.Entry<String, String> lowercaseToActualFieldName : srcFieldNamesLookup.entrySet()) {
            if (destFieldNamesLookup.containsKey(lowercaseToActualFieldName.getKey())) {
                wildcardFields.add(new WildcardFieldMapping(lowercaseToActualFieldName.getValue(), destFieldNamesLookup.get(lowercaseToActualFieldName.getKey())));
            }
        }

        return wildcardFields;
    }

    private boolean destClassIsAccessible(ClassMap classMap) {
        return classMap.getDestClass() != null && classMap.getDestClass().isAccessible();
    }

    private boolean srcClassIsAccessible(ClassMap classMap) {
        return classMap.getSrcClass() != null && classMap.getSrcClass().isAccessible();
    }

}
