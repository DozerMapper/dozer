/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer.classmap.generator;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMapBuilder;
import org.dozer.classmap.Configuration;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.fieldmap.FieldMap;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.util.CollectionUtils;

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

    /** {@inheritDoc} */
    @Override
    public boolean accepts(ClassMap classMap) {
        return srcClassIsAccessible(classMap) || destClassIsAccessible(classMap);
    }

    /** {@inheritDoc} */
    @Override
    public boolean apply(ClassMap classMap, Configuration configuration) {
        BeanFieldsDetector beanFieldsDetector = new JavaBeanFieldsDetector();

        Set<String> destFieldNames = getDeclaredFieldNames(classMap.getDestClassToMap());
        Set<String> destWritablePropertyNames = beanFieldsDetector.getWritableFieldNames(classMap.getDestClassToMap());
        Set<String> srcFieldNames = getDeclaredFieldNames(classMap.getSrcClassToMap());
        Set<String> srcReadablePropertyNames = beanFieldsDetector.getReadableFieldNames(classMap.getSrcClassToMap());

        Set<String> matchingUnmappedFields = getMatchingUnmappedFieldNames(
                classMap.getFieldMaps(), srcFieldNames, destFieldNames);

        for (String mutualFieldName : matchingUnmappedFields) {
            mapFieldAppropriately(classMap, configuration, mutualFieldName, destWritablePropertyNames, srcReadablePropertyNames);
        }

        return false;
    }

    private void mapFieldAppropriately(ClassMap classMap, Configuration configuration,
                                       String mutualFieldName, Set<String> destWritablePropertyNames,
                                       Set<String> srcReadablePropertyNames) {
        MappingType mappingType;

        if (!destClassIsAccessible(classMap) && destWritablePropertyNames.contains(mutualFieldName)) {
            mappingType = MappingType.FIELD_TO_SETTER;
        } else if (!srcClassIsAccessible(classMap) && srcReadablePropertyNames.contains(mutualFieldName)) {
            mappingType = MappingType.GETTER_TO_FIELD;
        } else {
            mappingType = MappingType.FIELD_TO_FIELD;
        }

        GeneratorUtils.addGenericMapping(mappingType, classMap, configuration,
                mutualFieldName, mutualFieldName, beanContainer, destBeanCreator, propertyDescriptorFactory);
    }

    private Set<String> getDeclaredFieldNames(Class<?> srcType) {
        Set<String> declaredFieldNames = new HashSet<String>();

        do {
            for (Field field : srcType.getDeclaredFields()) {
                declaredFieldNames.add(field.getName());
            }

            srcType = srcType.getSuperclass();
        } while(srcType != null);

        return declaredFieldNames;
    }

    private Set<String> getMatchingUnmappedFieldNames(List<FieldMap> fieldMaps,
                                                      Set<String> srcFieldNames,
                                                      Set<String> destFieldNames) {

        for (FieldMap fieldMap : fieldMaps) {
            // Remove all already mapped
            srcFieldNames.remove(fieldMap.getSrcFieldName());
            destFieldNames.remove(fieldMap.getDestFieldName());
        }

        return new HashSet<String>(CollectionUtils.intersection(srcFieldNames, destFieldNames));
    }

    private boolean destClassIsAccessible(ClassMap classMap) {
        return classMap.getDestClass() != null && classMap.getDestClass().isAccessible();
    }

    private boolean srcClassIsAccessible(ClassMap classMap) {
        return classMap.getSrcClass() != null && classMap.getSrcClass().isAccessible();
    }
}
