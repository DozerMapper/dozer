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
package com.github.dozermapper.core.classmap;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.github.dozermapper.core.Mapping;
import com.github.dozermapper.core.OptionValue;
import com.github.dozermapper.core.classmap.generator.BeanMappingGenerator;
import com.github.dozermapper.core.classmap.generator.ClassLevelFieldMappingGenerator;
import com.github.dozermapper.core.classmap.generator.GeneratorUtils;
import com.github.dozermapper.core.classmap.generator.MappingType;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.DozerField;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.GenericFieldMap;
import com.github.dozermapper.core.fieldmap.MapFieldMap;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.DozerConstants;
import com.github.dozermapper.core.util.MappingOptions;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class for adding implicit field mappings to a ClassMap. Also, builds implicit ClassMap for class mappings
 * that don't have an explicit custom xml mapping. Only intended for internal use.
 */
public final class ClassMapBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClassMapBuilder.class);

    private final List<ClassMappingGenerator> buildTimeGenerators = new ArrayList<>();
    private final List<ClassMappingGenerator> runTimeGenerators = new ArrayList<>();
    private final BeanContainer beanContainer;

    public ClassMapBuilder(BeanContainer beanContainer, DestBeanCreator destBeanCreator, BeanMappingGenerator beanMappingGenerator,
                           PropertyDescriptorFactory propertyDescriptorFactory) {
        this.beanContainer = beanContainer;

        buildTimeGenerators.add(new ClassLevelFieldMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        buildTimeGenerators.add(new AnnotationPropertiesGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        buildTimeGenerators.add(new AnnotationFieldsGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        buildTimeGenerators.add(new AnnotationClassesGenerator());
        buildTimeGenerators.add(new MapMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        buildTimeGenerators.add(beanMappingGenerator);
        buildTimeGenerators.add(new CollectionMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));

        runTimeGenerators.add(new ClassLevelFieldMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        runTimeGenerators.add(new AnnotationPropertiesGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        runTimeGenerators.add(new AnnotationFieldsGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        runTimeGenerators.add(new AnnotationClassesGenerator());
        runTimeGenerators.add(new MapMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory));
        runTimeGenerators.add(beanMappingGenerator);
    }

    // TODO Cover with test cases
    // TODO Remove duplication
    // TODO Use Dozer Builder if possible ?
    // TODO Add Exclude Annotation process by separate generator
    // TODO Add Pluggable Buidlers
    // TODO Add field matcher based builder
    // TODO Add annotation based builder

    /**
     * Builds new default mapping on-the-fly for previously unknown mapped class pairs.
     *
     * @param globalConfiguration configuration of Dozer
     * @param srcClass            type to convert from
     * @param destClass           type to convert to
     * @return information about the classes being mapped
     */
    public ClassMap createDefaultClassMap(Configuration globalConfiguration, Class<?> srcClass, Class<?> destClass) {
        return createDefaultClassMap(globalConfiguration, srcClass, destClass, true);
    }

    public ClassMap createDefaultClassMap(Configuration globalConfiguration, Class<?> srcClass, Class<?> destClass, Boolean shouldGenerateMapping) {
        DozerClass srcDozerClass = new DozerClass(srcClass.getName(), srcClass, globalConfiguration.getBeanFactory(), null, null, null,
                                                  null, globalConfiguration.getMapNull(), globalConfiguration.getMapEmptyString(), false, null, beanContainer);

        DozerClass destDozerClass = new DozerClass(destClass.getName(), destClass, globalConfiguration.getBeanFactory(), null, null, null,
                                                   null, globalConfiguration.getMapNull(), globalConfiguration.getMapEmptyString(), false, null, beanContainer);

        ClassMap classMap = new ClassMap(globalConfiguration);
        classMap.setSrcClass(srcDozerClass);
        classMap.setDestClass(destDozerClass);

        if (shouldGenerateMapping) {
            generateMapping(classMap, globalConfiguration, buildTimeGenerators);
        }

        return classMap;
    }

    /**
     * Prepares default mappings based on provided mapping definition
     *
     * @param classMappings       information about the classes being mapped
     * @param globalConfiguration configuration of Dozer
     */
    public void addDefaultFieldMappings(ClassMappings classMappings, Configuration globalConfiguration) {
        Set<Entry<String, ClassMap>> entries = classMappings.getAll().entrySet();
        for (Entry<String, ClassMap> entry : entries) {
            ClassMap classMap = entry.getValue();
            generateMapping(classMap, globalConfiguration, runTimeGenerators);
        }
    }

    private void generateMapping(ClassMap classMap, Configuration configuration, List<ClassMappingGenerator> mappingGenerators) {
        if (!classMap.isWildcard()) {
            return;
        }

        for (ClassMappingGenerator generator : mappingGenerators) {
            if (generator.accepts(classMap)) {
                if (generator.apply(classMap, configuration)) {
                    return;
                }
            }
        }
    }

    public interface ClassMappingGenerator {

        boolean accepts(ClassMap classMap);

        /**
         * true if we should stop after applied
         *
         * @param classMap      information about the classes being mapped
         * @param configuration configuration of the mapping
         * @return true if we should stop after applied
         */
        boolean apply(ClassMap classMap, Configuration configuration);

    }

    public static class MapMappingGenerator implements ClassMappingGenerator {

        private final BeanContainer beanContainer;
        private final DestBeanCreator destBeanCreator;
        private final PropertyDescriptorFactory propertyDescriptorFactory;

        public MapMappingGenerator(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
            this.beanContainer = beanContainer;
            this.destBeanCreator = destBeanCreator;
            this.propertyDescriptorFactory = propertyDescriptorFactory;
        }

        public boolean accepts(ClassMap classMap) {
            Class<?> srcClass = classMap.getSrcClassToMap();
            Class<?> destClass = classMap.getDestClassToMap();

            return MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null
                   || MappingUtils.isSupportedMap(destClass) || classMap.getDestClassMapGetMethod() != null;
        }

        public boolean apply(ClassMap classMap, Configuration configuration) {
            Class<?> srcClass = classMap.getSrcClassToMap();
            Class<?> destClass = classMap.getDestClassToMap();
            PropertyDescriptor[] properties;
            boolean destinationIsMap = false;

            if (MappingUtils.isSupportedMap(srcClass) || classMap.getSrcClassMapGetMethod() != null) {
                properties = ReflectionUtils.getPropertyDescriptors(destClass);
            } else {
                properties = ReflectionUtils.getPropertyDescriptors(srcClass);
                destinationIsMap = true;
            }

            for (PropertyDescriptor property : properties) {
                String fieldName = property.getName();

                if (GeneratorUtils.shouldIgnoreField(fieldName, srcClass, destClass, beanContainer)) {
                    continue;
                }

                // already mapped
                if (destinationIsMap && classMap.getFieldMapUsingSrc(fieldName) != null) {
                    continue;
                }

                // already mapped
                if (!destinationIsMap && classMap.getFieldMapUsingDest(fieldName, true) != null) {
                    continue;
                }

                FieldMap fieldMap = new MapFieldMap(classMap, beanContainer, destBeanCreator, propertyDescriptorFactory);
                DozerField srcField = new DozerField(MappingUtils.isSupportedMap(srcClass) ? DozerConstants.SELF_KEYWORD : fieldName, null);
                srcField.setKey(fieldName);

                if (StringUtils.isNotEmpty(classMap.getSrcClassMapGetMethod())
                    || StringUtils.isNotEmpty(classMap.getSrcClassMapSetMethod())) {
                    srcField.setMapGetMethod(classMap.getSrcClassMapGetMethod());
                    srcField.setMapSetMethod(classMap.getSrcClassMapSetMethod());
                    srcField.setName(DozerConstants.SELF_KEYWORD);
                }

                DozerField destField = new DozerField(MappingUtils.isSupportedMap(destClass) ? DozerConstants.SELF_KEYWORD : fieldName,
                                                      null);
                srcField.setKey(fieldName);

                if (StringUtils.isNotEmpty(classMap.getDestClassMapGetMethod())
                    || StringUtils.isNotEmpty(classMap.getDestClassMapSetMethod())) {
                    destField.setMapGetMethod(classMap.getDestClassMapGetMethod());
                    destField.setMapSetMethod(classMap.getDestClassMapSetMethod());
                    destField.setName(DozerConstants.SELF_KEYWORD);
                }

                fieldMap.setSrcField(srcField);
                fieldMap.setDestField(destField);

                classMap.addFieldMapping(fieldMap);
            }
            return true;
        }
    }

    public static class CollectionMappingGenerator implements ClassMappingGenerator {

        private final BeanContainer beanContainer;
        private final DestBeanCreator destBeanCreator;
        private final PropertyDescriptorFactory propertyDescriptorFactory;

        public CollectionMappingGenerator(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
            this.beanContainer = beanContainer;
            this.destBeanCreator = destBeanCreator;
            this.propertyDescriptorFactory = propertyDescriptorFactory;
        }

        public boolean accepts(ClassMap classMap) {
            Class<?> srcClass = classMap.getSrcClassToMap();
            Class<?> destClass = classMap.getDestClassToMap();
            return MappingUtils.isSupportedCollection(srcClass) && MappingUtils.isSupportedCollection(destClass);
        }

        public boolean apply(ClassMap classMap, Configuration configuration) {
            FieldMap fieldMap = new GenericFieldMap(classMap, beanContainer, destBeanCreator, propertyDescriptorFactory);
            DozerField selfReference = new DozerField(DozerConstants.SELF_KEYWORD, null);
            fieldMap.setSrcField(selfReference);
            fieldMap.setDestField(selfReference);
            classMap.addFieldMapping(fieldMap);
            return true;
        }
    }

    public static class AnnotationClassesGenerator implements ClassMappingGenerator {

        public boolean accepts(ClassMap classMap) {
            return true;
        }

        public boolean apply(ClassMap classMap, Configuration configuration) {
            Class<?> srcType = classMap.getSrcClassToMap();
            Class<?> dstType = classMap.getDestClassToMap();
            applyClassMappingOptions(classMap, reconcileOptions(srcType, dstType));

            return false;
        }

        private static MappingOptions reconcileOptions(final Class<?> srcClass, final Class<?> dstClass) {
            final MappingOptions srcOpts = srcClass.getAnnotation(MappingOptions.class);
            final MappingOptions dstOpts = dstClass.getAnnotation(MappingOptions.class);
            if (srcOpts == null) {
                return dstOpts;
            }
            if (dstOpts == null) {
                return srcOpts;
            }

            return new MappingOptions() {

                private OptionValue reconcile(String fieldName, OptionValue srcOption, OptionValue dstOption) {
                    if (srcOption == dstOption) {
                        return srcOption;
                    }
                    if (srcOption == OptionValue.INHERITED) {
                        return dstOption;
                    }
                    if (dstOption == OptionValue.INHERITED) {
                        return srcOption;
                    }
                    log.info("Conflicting class annotations for " + fieldName + " on src class " + srcClass.getCanonicalName() + " and dst class " + dstClass.getCanonicalName());
                    return dstOption;
                }

                private String reconcile(String fieldName, String srcOption, String dstOption) {
                    if (srcOption.equals(dstOption)) {
                        return srcOption;
                    }
                    if (srcOption.isEmpty()) {
                        return dstOption;
                    }
                    if (dstOption.isEmpty()) {
                        return srcOption;
                    }
                    log.info("Conflicting class annotations for " + fieldName + " on src class " + srcClass.getCanonicalName() + " and dst class " + dstClass.getCanonicalName());
                    return dstOption;
                }

                @Override
                public OptionValue wildCard() {
                    return reconcile("wildCard", srcOpts.wildCard(), dstOpts.wildCard());
                }

                @Override
                public OptionValue wildCardCaseInsensitive() {
                    return reconcile("wildCardCaseInsensitive", srcOpts.wildCardCaseInsensitive(), dstOpts.wildCardCaseInsensitive());
                }

                @Override
                public OptionValue stopOnErrors() {
                    return reconcile("stopOnErrors", srcOpts.stopOnErrors(), dstOpts.stopOnErrors());
                }

                @Override
                public OptionValue mapNull() {
                    return reconcile("mapNull", srcOpts.mapNull(), dstOpts.mapNull());
                }

                @Override
                public OptionValue mapEmptyString() {
                    return reconcile("mapEmptyString", srcOpts.mapEmptyString(), dstOpts.mapEmptyString());
                }

                @Override
                public String dateFormat() {
                    return reconcile("dateFormat", srcOpts.dateFormat(), dstOpts.dateFormat());
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return MappingOptions.class;
                }
            };
        }

        private static void applyClassMappingOptions(ClassMap classMap, MappingOptions mappingOptions) {
            if (mappingOptions != null) {
                classMap.setWildcard(mappingOptions.wildCard().toBoolean());
                classMap.setWildcardCaseInsensitive(mappingOptions.wildCardCaseInsensitive().toBoolean());
                classMap.setStopOnErrors(mappingOptions.stopOnErrors().toBoolean());

                Boolean mapNull = mappingOptions.mapNull().toBoolean();
                classMap.getDestClass().setMapNull(mapNull);
                classMap.getSrcClass().setMapNull(mapNull);

                Boolean mapEmptyString = mappingOptions.mapEmptyString().toBoolean();
                classMap.getDestClass().setMapEmptyString(mapEmptyString);
                classMap.getSrcClass().setMapEmptyString(mapEmptyString);

                String dateFormat = mappingOptions.dateFormat();
                if (!dateFormat.isEmpty()) {
                    classMap.setDateFormat(dateFormat);
                }
            }
        }
    }

    public static class AnnotationPropertiesGenerator implements ClassMappingGenerator {

        private final BeanContainer beanContainer;
        private final DestBeanCreator destBeanCreator;
        private final PropertyDescriptorFactory propertyDescriptorFactory;

        public AnnotationPropertiesGenerator(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
            this.beanContainer = beanContainer;
            this.destBeanCreator = destBeanCreator;
            this.propertyDescriptorFactory = propertyDescriptorFactory;
        }

        public boolean accepts(ClassMap classMap) {
            return true;
        }

        public boolean apply(ClassMap classMap, Configuration configuration) {
            Class<?> srcType = classMap.getSrcClassToMap();

            PropertyDescriptor[] srcProperties = ReflectionUtils.getPropertyDescriptors(srcType);
            for (PropertyDescriptor property : srcProperties) {
                Method readMethod = property.getReadMethod();
                if (readMethod != null) {
                    Mapping mapping = readMethod.getAnnotation(Mapping.class);
                    if (mapping != null) {
                        String propertyName = property.getName();
                        String pairName = mapping.value().trim();
                        if (requireMapping(mapping, classMap.getDestClassToMap(), propertyName, pairName)
                                && classMap.getFieldMapUsingSrc(propertyName) == null) {
                            GeneratorUtils.addGenericMapping(MappingType.GETTER_TO_SETTER, classMap, configuration,
                                                             propertyName, pairName.isEmpty() ? propertyName : pairName, beanContainer, destBeanCreator, propertyDescriptorFactory);
                        }
                    }
                }
            }

            Class<?> destType = classMap.getDestClassToMap();

            PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(destType);
            for (PropertyDescriptor property : destProperties) {
                Method readMethod = property.getReadMethod();
                if (readMethod != null) {
                    Mapping mapping = readMethod.getAnnotation(Mapping.class);
                    if (mapping != null) {
                        String propertyName = property.getName();
                        String pairName = mapping.value().trim();
                        if (requireMapping(mapping, classMap.getSrcClassToMap(), propertyName, pairName)) {
                            GeneratorUtils.addGenericMapping(MappingType.GETTER_TO_SETTER, classMap, configuration,
                                                             pairName.isEmpty() ? propertyName : pairName, propertyName, beanContainer, destBeanCreator, propertyDescriptorFactory);
                        }
                    }
                }
            }

            return false;
        }
    }

    public static class AnnotationFieldsGenerator implements ClassMappingGenerator {

        private final BeanContainer beanContainer;
        private final DestBeanCreator destBeanCreator;
        private final PropertyDescriptorFactory propertyDescriptorFactory;

        public AnnotationFieldsGenerator(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
            this.beanContainer = beanContainer;
            this.destBeanCreator = destBeanCreator;
            this.propertyDescriptorFactory = propertyDescriptorFactory;
        }

        public boolean accepts(ClassMap classMap) {
            return true;
        }

        public boolean apply(ClassMap classMap, Configuration configuration) {
            Class<?> srcType = classMap.getSrcClassToMap();
            do {
                for (Field field : srcType.getDeclaredFields()) {
                    Mapping mapping = field.getAnnotation(Mapping.class);
                    String fieldName = field.getName();
                    if (mapping != null) {
                        String pairName = mapping.value().trim();
                        if (requireMapping(mapping, classMap.getDestClassToMap(), fieldName, pairName)) {
                            GeneratorUtils.addGenericMapping(MappingType.FIELD_TO_FIELD, classMap, configuration,
                                                             fieldName, pairName.isEmpty() ? fieldName : pairName, beanContainer, destBeanCreator, propertyDescriptorFactory);
                        }
                    }
                }
                srcType = srcType.getSuperclass();
            }
            while (srcType != null);

            Class<?> destType = classMap.getDestClassToMap();
            do {
                for (Field field : destType.getDeclaredFields()) {
                    Mapping mapping = field.getAnnotation(Mapping.class);
                    String fieldName = field.getName();
                    if (mapping != null) {
                        String pairName = mapping.value().trim();
                        if (requireMapping(mapping, classMap.getSrcClassToMap(), fieldName, pairName)) {
                            GeneratorUtils.addGenericMapping(MappingType.FIELD_TO_FIELD, classMap, configuration,
                                                             pairName.isEmpty() ? fieldName : pairName, fieldName, beanContainer, destBeanCreator, propertyDescriptorFactory);
                        }
                    }
                }
                destType = destType.getSuperclass();
            }
            while (destType != null);

            return false;
        }
    }

    private static boolean requireMapping(Mapping mapping, Class<?> clazz, String fieldName, String pairName) {
        try {
            return !mapping.optional()
                   || (mapping.optional() && clazz.getDeclaredField(pairName.isEmpty() ? fieldName : pairName) != null);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
