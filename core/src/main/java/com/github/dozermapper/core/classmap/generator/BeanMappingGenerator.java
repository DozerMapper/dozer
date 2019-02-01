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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.ClassMapBuilder;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.CollectionUtils;

public class BeanMappingGenerator implements ClassMapBuilder.ClassMappingGenerator {

    final List<BeanFieldsDetector> pluggedFieldDetectors = new ArrayList<>();

    final List<BeanFieldsDetector> availableFieldDetectors = new ArrayList<BeanFieldsDetector>() {{
            add(new JavaBeanFieldsDetector());
        }
    };

    private final BeanContainer beanContainer;
    private final DestBeanCreator destBeanCreator;
    private final PropertyDescriptorFactory propertyDescriptorFactory;

    public BeanMappingGenerator(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        this.beanContainer = beanContainer;
        this.destBeanCreator = destBeanCreator;
        this.propertyDescriptorFactory = propertyDescriptorFactory;
    }

    public boolean accepts(ClassMap classMap) {
        return true;
    }

    public boolean apply(ClassMap classMap, Configuration configuration) {
        Class<?> srcClass = classMap.getSrcClassToMap();
        Class<?> destClass = classMap.getDestClassToMap();

        Set<String> destFieldNames = getAcceptsFieldsDetector(destClass).getWritableFieldNames(destClass);
        Set<String> srcFieldNames = getAcceptsFieldsDetector(srcClass).getReadableFieldNames(srcClass);
        Set<WildcardFieldMapping> wildcardFieldMappings = (classMap.isWildcardCaseInsensitive()) ?
                getMatchingFieldsCaseInsensitive(srcFieldNames, destFieldNames) : getMatchingFields(srcFieldNames, destFieldNames);

        for (WildcardFieldMapping wildcardFieldMapping : wildcardFieldMappings) {
            if (GeneratorUtils.shouldIgnoreField(wildcardFieldMapping.getSrcFieldName(), srcClass, destClass, beanContainer)
                || GeneratorUtils.shouldIgnoreField(wildcardFieldMapping.getDestFieldName(), srcClass, destClass, beanContainer)) {
                continue;
            }

            // If field has already been accounted for, then skip
            if (classMap.getFieldMapUsingDest(wildcardFieldMapping.getDestFieldName()) != null ||
                classMap.getFieldMapUsingSrc(wildcardFieldMapping.getSrcFieldName()) != null) {
                continue;
            }

            GeneratorUtils.addGenericMapping(MappingType.GETTER_TO_SETTER, classMap, configuration,
                                             wildcardFieldMapping.getSrcFieldName(), wildcardFieldMapping.getDestFieldName(),
                                             beanContainer, destBeanCreator, propertyDescriptorFactory);
        }
        return false;
    }

    private Set<WildcardFieldMapping> getMatchingFields(Set<String> srcFieldNames, Set<String> destFieldNames) {
        return CollectionUtils.intersection(srcFieldNames, destFieldNames).stream().map(matchingFieldName ->
                                                                                                new WildcardFieldMapping(matchingFieldName, matchingFieldName))
                .collect(Collectors.toSet());
    }

    private Set<WildcardFieldMapping> getMatchingFieldsCaseInsensitive(Set<String> srcFieldNames, Set<String> destFieldNames) {
        Map<String, String> srcFieldNamesLookup = new HashMap<>(srcFieldNames.size(), 1);
        Map<String, String> destFieldNamesLookup = new HashMap<>(destFieldNames.size(), 1);

        for (String srcFieldName : srcFieldNames) {
            srcFieldNamesLookup.put(srcFieldName.toLowerCase(), srcFieldName);
        }
        for (String destFieldName : destFieldNames) {
            destFieldNamesLookup.put(destFieldName.toLowerCase(), destFieldName);
        }

        Set<WildcardFieldMapping> wildcardFields = new HashSet<>();

        for (Map.Entry<String, String> lowercaseToActualFieldName : srcFieldNamesLookup.entrySet()) {
            if (destFieldNamesLookup.containsKey(lowercaseToActualFieldName.getKey())) {
                wildcardFields.add(new WildcardFieldMapping(lowercaseToActualFieldName.getValue(), destFieldNamesLookup.get(lowercaseToActualFieldName.getKey())));
            }
        }

        return wildcardFields;
    }

    private BeanFieldsDetector getAcceptsFieldsDetector(Class<?> clazz) {
        BeanFieldsDetector detector = getAcceptsFieldDetector(clazz, pluggedFieldDetectors);
        if (detector == null) {
            detector = getAcceptsFieldDetector(clazz, availableFieldDetectors);
        }

        return detector;
    }

    private BeanFieldsDetector getAcceptsFieldDetector(Class<?> clazz, List<BeanFieldsDetector> detectors) {
        for (BeanFieldsDetector detector : new CopyOnWriteArrayList<>(detectors)) {
            if (detector.accepts(clazz)) {
                return detector;
            }
        }

        return null;
    }

    public void addPluggedFieldDetectors(Collection<BeanFieldsDetector> beanFieldsDetectors) {
        pluggedFieldDetectors.addAll(beanFieldsDetectors);
    }
}
