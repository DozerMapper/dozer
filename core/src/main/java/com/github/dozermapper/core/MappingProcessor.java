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
package com.github.dozermapper.core;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.dozermapper.core.builder.BuilderUtil;
import com.github.dozermapper.core.builder.DestBeanBuilderCreator;
import com.github.dozermapper.core.cache.Cache;
import com.github.dozermapper.core.cache.CacheKeyFactory;
import com.github.dozermapper.core.cache.CacheManager;
import com.github.dozermapper.core.cache.DozerCacheType;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.ClassMapBuilder;
import com.github.dozermapper.core.classmap.ClassMappings;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.CopyByReferenceContainer;
import com.github.dozermapper.core.classmap.MappingDirection;
import com.github.dozermapper.core.classmap.RelationshipType;
import com.github.dozermapper.core.classmap.generator.BeanMappingGenerator;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.converters.DateFormatContainer;
import com.github.dozermapper.core.converters.PrimitiveOrWrapperConverter;
import com.github.dozermapper.core.events.DefaultEvent;
import com.github.dozermapper.core.events.EventManager;
import com.github.dozermapper.core.events.EventTypes;
import com.github.dozermapper.core.factory.BeanCreationDirective;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.CustomGetSetMethodFieldMap;
import com.github.dozermapper.core.fieldmap.ExcludeFieldMap;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.HintContainer;
import com.github.dozermapper.core.fieldmap.MapFieldMap;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.CollectionUtils;
import com.github.dozermapper.core.util.DozerConstants;
import com.github.dozermapper.core.util.IteratorUtils;
import com.github.dozermapper.core.util.LogMsgFactory;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.MappingValidator;
import com.github.dozermapper.core.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.dozermapper.core.util.DozerConstants.BASE_CLASS;
import static com.github.dozermapper.core.util.DozerConstants.ITERATE;

/**
 * Internal Mapping Engine. Not intended for direct use by Application code.
 * This class does most of the heavy lifting and is very recursive in nature.
 * <p>
 * This class is not threadsafe and is instantiated for each new mapping request.
 */
public class MappingProcessor implements Mapper {

    private static final Logger log = LoggerFactory.getLogger(MappingProcessor.class);

    private final ClassMappings classMappings;
    private final Configuration globalConfiguration;
    private final List<CustomConverter> customConverterObjects;
    private final Map<String, CustomConverter> customConverterObjectsWithId;
    private final EventManager eventManager;
    private final CustomFieldMapper customFieldMapper;

    private final MappedFieldsTracker mappedFields = new MappedFieldsTracker();

    private final Cache converterByDestTypeCache;
    private final Cache superTypeCache;
    private final PrimitiveOrWrapperConverter primitiveConverter;
    private final LogMsgFactory logMsgFactory = new LogMsgFactory();
    private final BeanContainer beanContainer;
    private final ClassMapBuilder classMapBuilder;
    private final DestBeanCreator destBeanCreator;
    private final DestBeanBuilderCreator destBeanBuilderCreator;

    protected MappingProcessor(ClassMappings classMappings, Configuration globalConfiguration, CacheManager cacheMgr,
                               List<CustomConverter> customConverterObjects,
                               EventManager eventManager, CustomFieldMapper customFieldMapper,
                               Map<String, CustomConverter> customConverterObjectsWithId, BeanContainer beanContainer,
                               DestBeanCreator destBeanCreator, DestBeanBuilderCreator destBeanBuilderCreator,
                               BeanMappingGenerator beanMappingGenerator, PropertyDescriptorFactory propertyDescriptorFactory) {
        this.classMappings = classMappings;
        this.globalConfiguration = globalConfiguration;
        this.customConverterObjects = customConverterObjects;
        this.eventManager = eventManager;
        this.customFieldMapper = customFieldMapper;
        this.converterByDestTypeCache = cacheMgr.getCache(DozerCacheType.CONVERTER_BY_DEST_TYPE.name());
        this.superTypeCache = cacheMgr.getCache(DozerCacheType.SUPER_TYPE_CHECK.name());
        this.customConverterObjectsWithId = customConverterObjectsWithId;
        this.beanContainer = beanContainer;
        this.destBeanBuilderCreator = destBeanBuilderCreator;
        this.classMapBuilder = new ClassMapBuilder(beanContainer, destBeanCreator, beanMappingGenerator, propertyDescriptorFactory);
        this.primitiveConverter = new PrimitiveOrWrapperConverter(beanContainer);
        this.destBeanCreator = destBeanCreator;
    }

    /* Mapper Interface Implementation */

    public <T> T map(final Object srcObj, final Class<T> destClass) {
        return map(srcObj, destClass, null);
    }

    public <T> T map(final Object srcObj, final Class<T> destClass, final String mapId) {
        MappingValidator.validateMappingRequest(srcObj, destClass);
        return mapGeneral(srcObj, destClass, null, mapId);
    }

    public void map(final Object srcObj, final Object destObj) {
        map(srcObj, destObj, null);
    }

    public void map(final Object srcObj, final Object destObj, final String mapId) {
        MappingValidator.validateMappingRequest(srcObj, destObj);
        mapGeneral(srcObj, null, destObj, mapId);
    }

    /**
     * Only required due to type coupling of MappingProcessor and Mapper - needs re-think
     * @return nothing
     */
    @Override
    public MapperModelContext getMapperModelContext() {
        throw new IllegalArgumentException("Can only be called on instance returned by DozerBeanMapperBuilder");
    }

    /* End of Mapper Interface Implementation */

    /**
     * Single point of entry for atomic mapping operations
     *
     * @param srcObj    source object
     * @param destClass destination class
     * @param destObj   destination object
     * @param mapId     mapping identifier
     * @param <T>       destination object type
     * @return new or updated destination object
     */
    private <T> T mapGeneral(Object srcObj, final Class<T> destClass, final T destObj, final String mapId) {
        srcObj = MappingUtils.deProxy(srcObj, beanContainer);

        Class<T> destType;
        T result;
        if (destClass == null) {
            destType = (Class<T>)destObj.getClass();
            result = destObj;
        } else {
            destType = destClass;
            result = null;
        }

        ClassMap classMap = null;
        try {
            classMap = getClassMap(srcObj.getClass(), destType, mapId);


            eventManager.on(new DefaultEvent(EventTypes.MAPPING_STARTED, classMap, null, srcObj, result, null));

            // TODO Check if any proxy issues are here
            // Check to see if custom converter has been specified for this mapping
            // combination. If so, just use it.
            Class<?> converterClass = MappingUtils.findCustomConverter(converterByDestTypeCache, classMap.getCustomConverters(), srcObj
                    .getClass(), destType);

            if (destObj == null) {
                // If this is a nested MapperAware conversion this mapping can be already processed
                // but we can do this optimization only in case of no destObject, instead we must copy to the dest object
                Object alreadyMappedValue = mappedFields.getMappedValue(srcObj, destType, mapId);
                if (alreadyMappedValue != null) {
                    return (T)alreadyMappedValue;
                }
            }

            if (converterClass != null) {
                return (T)mapUsingCustomConverter(converterClass, srcObj.getClass(), srcObj, destType, result, null, true);
            }

            BeanCreationDirective creationDirective =
                    new BeanCreationDirective(srcObj, classMap.getSrcClassToMap(), classMap.getDestClassToMap(), destType,
                                              classMap.getDestClassBeanFactory(), classMap.getDestClassBeanFactoryId(), classMap.getDestClassCreateMethod(),
                                              classMap.getDestClass().isSkipConstructor());

            result = createByCreationDirectiveAndMap(creationDirective, classMap, srcObj, result, false, null);
        } catch (Throwable e) {
            MappingUtils.throwMappingException(e);
        }
        eventManager.on(new DefaultEvent(EventTypes.MAPPING_FINISHED, classMap, null, srcObj, result, null));

        return result;
    }

    /**
     * Create builder or target object if needed and call
     * {@link MappingProcessor#mapToDestObject(com.github.dozermapper.core.classmap.ClassMap, Object, Object, boolean, String)} function with
     * arguments {@code classMap}, {@code srcObj}, {@code result}, {@code bypassSuperMappings}, {@code mapId}
     *
     * @param creationDirective   directive for concrete mapping (based mostly on {@code classMap})
     * @param classMap            class map information for concrete class
     * @param srcObj              source object
     * @param result              target entity for mapping
     * @param bypassSuperMappings //TODO
     * @param mapId               mapping identifier
     * @return result or created target entity for mapping
     */
    private <T> T createByCreationDirectiveAndMap(BeanCreationDirective creationDirective, ClassMap classMap, Object srcObj, T result, boolean bypassSuperMappings, String mapId) {
        if (result == null) {
            BeanBuilder beanBuilder = destBeanBuilderCreator.create(creationDirective);
            if (beanBuilder == null) {
                result = (T)destBeanCreator.create(creationDirective);
                mapToDestObject(classMap, srcObj, result, bypassSuperMappings, mapId);
            } else {
                mapToDestObject(classMap, srcObj, beanBuilder, bypassSuperMappings, mapId);
                result = (T)beanBuilder.build();
            }
        } else {
            mapToDestObject(classMap, srcObj, result, bypassSuperMappings, mapId);
        }
        return result;
    }

    /**
     * This function used to map into created instance of destination class
     *
     * @param classMap            object with mapping configuration
     * @param srcObj              source object
     * @param destObj             destination object
     * @param bypassSuperMappings //TODO
     * @param mapId               mapping identifier
     */
    private void mapToDestObject(ClassMap classMap, Object srcObj, Object destObj, boolean bypassSuperMappings, String mapId) {
        Object result = destObj;
        if (javax.xml.bind.JAXBElement.class.isAssignableFrom(destObj.getClass())) {
            classMap = getClassMap(srcObj.getClass(), javax.xml.bind.JAXBElement.class.cast(destObj).getDeclaredType(), mapId);
            result = javax.xml.bind.JAXBElement.class.cast(destObj).getValue();
        }

        map(classMap, srcObj, result, bypassSuperMappings, new ArrayList<>(), mapId);
    }

    private void map(ClassMap classMap, Object srcObj, Object destObj, boolean bypassSuperMappings, List<String> mappedParentFields, String mapId) {
        srcObj = MappingUtils.deProxy(srcObj, beanContainer);

        // 1596766 - Recursive object mapping issue. Prevent recursive mapping
        // infinite loop. Keep a record of mapped fields
        // by storing the id of the sourceObj and the destObj to be mapped. This can
        // be referred to later to avoid recursive mapping loops
        mappedFields.put(srcObj, destObj, mapId);

        // If class map hasn't already been determined, find the appropriate one for
        // the src/dest object combination
        if (classMap == null) {
            classMap = getClassMap(srcObj.getClass(), destObj.getClass(), mapId);
        }

        Class<?> srcClass = srcObj.getClass();
        Class<?> destClass = destObj.getClass();

        // Check to see if custom converter has been specified for this mapping
        // combination. If so, just use it.
        Class<?> converterClass = MappingUtils.findCustomConverter(converterByDestTypeCache, classMap.getCustomConverters(), srcClass,
                                                                   destClass);
        if (converterClass != null) {
            destObj = mapUsingCustomConverter(converterClass, srcClass, srcObj, destClass, destObj, null, true);
            return;
        }

        // Now check for super class mappings.  Process super class mappings first.
        if (!bypassSuperMappings) {
            Collection<ClassMap> superMappings = new ArrayList<>();

            Collection<ClassMap> superClasses = checkForSuperTypeMapping(srcClass, destClass);
            //List<ClassMap> interfaceMappings = classMappings.findInterfaceMappings(srcClass, destClass);

            superMappings.addAll(superClasses);
            //superMappings.addAll(interfaceMappings);
            if (!superMappings.isEmpty()) {
                processSuperTypeMapping(superMappings, srcObj, destObj, mappedParentFields, mapId);
            }
        }

        // Perform mappings for each field. Iterate through Fields Maps for this class mapping
        for (FieldMap fieldMapping : classMap.getFieldMaps()) {
            //Bypass field if it has already been mapped as part of super class mappings.
            String key = MappingUtils.getMappedParentFieldKey(destObj, fieldMapping);
            if (mappedParentFields != null && mappedParentFields.contains(key)) {
                continue;
            }
            mapField(fieldMapping, srcObj, destObj);
        }
    }

    /**
     * Perform mapping of a field.
     * Uses {@link #mapFromFieldMap(Object, Object, Object, FieldMap)} to do the real work, unless
     * if iterate, where {@link #mapFromIterateMethodFieldMap(Object, Object, Object, FieldMap)} is used.
     *
     * @param fieldMapping Field mapping.
     * @param srcObj       Source object.
     * @param destObj      Destination object.
     */
    private void mapField(FieldMap fieldMapping, Object srcObj, Object destObj) {

        // The field has been explicitly excluded from mapping. So just return, as
        // no further processing is needed for this field
        if (fieldMapping instanceof ExcludeFieldMap) {
            return;
        }

        Object srcFieldValue = null;
        try {
            // If a custom field mapper was specified, then invoke it. If not, or the
            // custom field mapper returns false(indicating the
            // field was not actually mapped by the custom field mapper), proceed as
            // normal(use Dozer to map the field)
            srcFieldValue = fieldMapping.getSrcFieldValue(srcObj);
            boolean fieldMapped = false;
            if (customFieldMapper != null) {
                fieldMapped = customFieldMapper.mapField(srcObj, destObj, srcFieldValue, fieldMapping.getClassMap(), fieldMapping);
            }

            if (!fieldMapped) {
                if (fieldMapping.getDestFieldType() != null && ITERATE.equals(fieldMapping.getDestFieldType())) {
                    // special logic for iterate feature
                    mapFromIterateMethodFieldMap(srcObj, destObj, srcFieldValue, fieldMapping);
                } else {
                    // either deep field map or generic map. The is the most likely
                    // scenario
                    mapFromFieldMap(srcObj, destObj, srcFieldValue, fieldMapping);
                }
            }

        } catch (Throwable e) {
            log.error(logMsgFactory.createFieldMappingErrorMsg(srcObj, fieldMapping, srcFieldValue, destObj), e);

            // check error handling policy.
            if (fieldMapping.isStopOnErrors()) {
                MappingUtils.throwMappingException(e);
            } else {
                // check if any Exceptions should be allowed to be thrown
                if (!fieldMapping.getClassMap().getAllowedExceptions().isEmpty() && e.getCause() instanceof InvocationTargetException) {
                    Throwable thrownType = ((InvocationTargetException)e.getCause()).getTargetException();
                    Class<? extends Throwable> exceptionClass = thrownType.getClass();
                    if (fieldMapping.getClassMap().getAllowedExceptions().contains(exceptionClass)) {
                        throw (RuntimeException)thrownType;
                    }
                }
            }
        }
    }

    private void mapFromFieldMap(Object srcObj, Object destObj, Object srcFieldValue, FieldMap fieldMapping) {
        Class<?> destFieldType;
        if (fieldMapping instanceof CustomGetSetMethodFieldMap) {
            try {
                destFieldType = fieldMapping.getDestFieldWriteMethodParameter(destObj.getClass());
            } catch (Throwable e) {
                // try traditional way
                destFieldType = fieldMapping.getDestFieldType(BuilderUtil.unwrapDestClassFromBuilder(destObj));
            }
        } else {
            destFieldType = fieldMapping.getDestFieldType(BuilderUtil.unwrapDestClassFromBuilder(destObj));
        }

        // 1476780 - 12/2006 mht - Add support for field level custom converters
        // Use field level custom converter if one was specified. Otherwise, map or
        // recurse the object as normal
        // 1770440 - fdg - Using multiple instances of CustomConverter
        Object destFieldValue;
        if (!MappingUtils.isBlankOrNull(fieldMapping.getCustomConverterId())) {
            if (customConverterObjectsWithId != null && customConverterObjectsWithId.containsKey(fieldMapping.getCustomConverterId())) {
                Class<?> srcFieldClass = srcFieldValue != null ? srcFieldValue.getClass() : fieldMapping.getSrcFieldType(srcObj.getClass());
                destFieldValue = mapUsingCustomConverterInstance(customConverterObjectsWithId.get(fieldMapping.getCustomConverterId()),
                                                                 srcFieldClass, srcFieldValue, destFieldType, destObj, fieldMapping, false);
            } else {
                throw new MappingException("CustomConverter instance not found with id:" + fieldMapping.getCustomConverterId());
            }
        } else if (MappingUtils.isBlankOrNull(fieldMapping.getCustomConverter())) {
            destFieldValue = mapOrRecurseObject(srcObj, srcFieldValue, destFieldType, fieldMapping, destObj);
        } else {
            Class<?> srcFieldClass = srcFieldValue != null ? srcFieldValue.getClass() : fieldMapping.getSrcFieldType(srcObj.getClass());
            destFieldValue = mapUsingCustomConverter(MappingUtils.loadClass(fieldMapping.getCustomConverter(), beanContainer), srcFieldClass,
                                                     srcFieldValue, destFieldType, destObj, fieldMapping, false);
        }

        writeDestinationValue(destObj, destFieldValue, fieldMapping, srcObj);

        if (log.isDebugEnabled()) {
            log.debug(logMsgFactory.createFieldMappingSuccessMsg(srcObj.getClass(), destObj.getClass(), fieldMapping.getSrcFieldName(),
                                                                 fieldMapping.getDestFieldName(), srcFieldValue, destFieldValue, fieldMapping.getClassMap().getMapId()));
        }
    }

    private Object mapOrRecurseObject(Object srcObj, Object srcFieldValue, Class<?> destFieldType, FieldMap fieldMap, Object destObj) {
        Class<?> srcFieldClass = srcFieldValue != null ? srcFieldValue.getClass() : fieldMap.getSrcFieldType(srcObj.getClass());
        Class<?> converterClass = MappingUtils.determineCustomConverter(fieldMap, converterByDestTypeCache, fieldMap.getClassMap()
                .getCustomConverters(), srcFieldClass, destFieldType);

        // 1-2007 mht: Invoke custom converter even if the src value is null.
        // #1563795
        if (converterClass != null) {
            return mapUsingCustomConverter(converterClass, srcFieldClass, srcFieldValue, destFieldType, destObj, fieldMap, false);
        }

        if (srcFieldValue == null) {
            return null;
        }

        String srcFieldName = fieldMap.getSrcFieldName();
        String destFieldName = fieldMap.getDestFieldName();

        // 1596766 - Recursive object mapping issue. Prevent recursive mapping
        // infinite loop
        // In case of "this->this" mapping this rule should be omitted as processing is done on objects, which has been
        // just marked as mapped.
        if (!(DozerConstants.SELF_KEYWORD.equals(srcFieldName) && DozerConstants.SELF_KEYWORD.equals(destFieldName))) {
            Object alreadyMappedValue = mappedFields.getMappedValue(srcFieldValue, destFieldType, fieldMap.getMapId());
            if (alreadyMappedValue != null) {
                return alreadyMappedValue;
            }
        }

        if (fieldMap.isCopyByReference()) {
            // just get the src and return it, no transformation.
            return srcFieldValue;
        }

        boolean isSrcFieldClassSupportedMap = MappingUtils.isSupportedMap(srcFieldClass);
        boolean isDestFieldTypeSupportedMap = MappingUtils.isSupportedMap(destFieldType);
        if (isSrcFieldClassSupportedMap && isDestFieldTypeSupportedMap) {
            return mapMap(srcObj, (Map<?, ?>)srcFieldValue, fieldMap, destObj);
        }
        if (fieldMap instanceof MapFieldMap && destFieldType.equals(Object.class)) {
            // TODO: find better place for this logic. try to encapsulate in FieldMap?
            destFieldType = fieldMap.getDestHintContainer() != null ? fieldMap.getDestHintContainer().getHint() : srcFieldClass;
        }

        if (primitiveConverter.accepts(srcFieldClass) || primitiveConverter.accepts(destFieldType)) {
            // Primitive or Wrapper conversion
            if (fieldMap.getDestHintContainer() != null) {
                Class<?> destHintType = fieldMap.getDestHintType(srcFieldValue.getClass());
                // if the destType is null this means that there was more than one hint.
                // we must have already set the destType then.
                if (destHintType != null) {
                    destFieldType = destHintType;
                }
            }

            //#1841448 - if trim-strings=true, then use a trimmed src string value when converting to dest value
            Object convertSrcFieldValue = srcFieldValue;
            if (fieldMap.isTrimStrings() && srcFieldValue.getClass().equals(String.class)) {
                convertSrcFieldValue = ((String)srcFieldValue).trim();
            }

            DateFormatContainer dfContainer = new DateFormatContainer(fieldMap.getDateFormat());

            if (fieldMap instanceof MapFieldMap && !primitiveConverter.accepts(destFieldType)) {
                // This handles a very special/rare use case(see indexMapping.xml + unit
                // test
                // testStringToIndexedSet_UsingMapSetMethod). If the destFieldType is a
                // custom object AND has a String param
                // constructor, we don't want to construct the custom object with the
                // src value because the map backed property
                // logic at lower layers handles setting the value on the custom object.
                // Without this special logic, the
                // destination map backed custom object would contain a value that is
                // the custom object dest type instead of the
                // desired src value.
                return primitiveConverter.convert(convertSrcFieldValue, convertSrcFieldValue.getClass(), dfContainer);
            } else {
                return primitiveConverter.convert(convertSrcFieldValue, destFieldType, dfContainer, destFieldName, destObj);
            }
        }
        if (MappingUtils.isSupportedCollection(srcFieldClass) && (MappingUtils.isSupportedCollection(destFieldType))) {
            return mapCollection(srcObj, srcFieldValue, fieldMap, destObj);
        }

        if (MappingUtils.isEnumType(srcFieldClass, destFieldType)) {
            return mapEnum((Enum)srcFieldValue, (Class<Enum>)destFieldType);
        }

        if (fieldMap.getDestDeepIndexHintContainer() != null) {
            destFieldType = fieldMap.getDestDeepIndexHintContainer().getHint();
        }

        // Default: Map from one custom data object to another custom data object
        return mapCustomObject(fieldMap, destObj, destFieldType, destFieldName, srcFieldValue);
    }

    private <T extends Enum<T>> T mapEnum(Enum<T> srcFieldValue, Class<T> destFieldType) {
        String name = srcFieldValue.name();
        return Enum.valueOf(getSerializableEnumClass(destFieldType), name);
    }

    private <T extends Enum<T>> Class<T> getSerializableEnumClass(Class<T> enumClass) {
        if (checkIfOverriddenEnum(enumClass)) {
            /* This cast is case because of the check performed by the checkIfOverridenEnum function. */
            @SuppressWarnings("unchecked")
            Class<T> castedSuperclass = (Class<T>)enumClass.getSuperclass();
            return castedSuperclass;
        }
        return enumClass;
    }

    private <T extends Enum<T>> boolean checkIfOverriddenEnum(Class<T> enumClass) {
        return !Enum.class.equals(enumClass.getSuperclass());
    }

    private Object mapCustomObject(FieldMap fieldMap, Object destObj, Class<?> destFieldType, String destFieldName, Object srcFieldValue) {
        srcFieldValue = MappingUtils.deProxy(srcFieldValue, beanContainer);

        // Custom java bean. Need to make sure that the destination object is not
        // already instantiated.
        Object result = null;
        // in case of iterate feature new objects are created in any case
        if (!DozerConstants.ITERATE.equals(fieldMap.getDestFieldType())) {
            result = getExistingValue(fieldMap, destObj, destFieldType);
        }

        // if the field is not null than we don't want a new instance
        if (result == null) {
            // first check to see if this plain old field map has hints to the actual
            // type.
            if (fieldMap.getDestHintContainer() != null) {
                Class<?> destHintType = fieldMap.getDestHintType(srcFieldValue.getClass());
                // if the destType is null this means that there was more than one hint.
                // we must have already set the destType then.
                if (destHintType != null) {
                    destFieldType = destHintType;
                }
            }
            // Check to see if explicit map-id has been specified for the field
            // mapping
            String mapId = fieldMap.getMapId();

            Class<?> targetClass;
            if (fieldMap.getDestHintContainer() != null && fieldMap.getDestHintContainer().getHint() != null) {
                targetClass = fieldMap.getDestHintContainer().getHint();
            } else {
                targetClass = destFieldType;
            }
            ClassMap classMap = getClassMap(srcFieldValue.getClass(), targetClass, mapId);

            BeanCreationDirective creationDirective = new BeanCreationDirective(srcFieldValue, classMap.getSrcClassToMap(), classMap.getDestClassToMap(),
                                                                                destFieldType, classMap.getDestClassBeanFactory(), classMap.getDestClassBeanFactoryId(),
                                                                                fieldMap.getDestFieldCreateMethod() != null ? fieldMap.getDestFieldCreateMethod() :
                                                                                        classMap.getDestClassCreateMethod(),
                                                                                classMap.getDestClass().isSkipConstructor(), destObj, destFieldName);

            result = createByCreationDirectiveAndMap(creationDirective, classMap, srcFieldValue, null, false, fieldMap.getMapId());
        } else {
            mapToDestObject(null, srcFieldValue, result, false, fieldMap.getMapId());
        }

        return result;
    }

    private Object mapCollection(Object srcObj, Object srcCollectionValue, FieldMap fieldMap, Object destObj) {
        // since we are mapping some sort of collection now is a good time to decide
        // if they provided hints
        // if no hint is provided then we will use generics to determine the mapping type
        if (fieldMap.getDestHintContainer() == null) {
            Class<?> genericType = fieldMap.getGenericType(BuilderUtil.unwrapDestClassFromBuilder(destObj));
            if (genericType != null) {
                HintContainer destHintContainer = new HintContainer(beanContainer);
                destHintContainer.setHintName(genericType.getName());
                FieldMap cloneFieldMap = (FieldMap)fieldMap.clone();
                cloneFieldMap.setDestHintContainer(destHintContainer); // should affect only this time as fieldMap is cloned
                fieldMap = cloneFieldMap;
            }
        }

        // if it is an iterator object turn it into a List
        if (srcCollectionValue instanceof Iterator) {
            srcCollectionValue = IteratorUtils.toList((Iterator<?>)srcCollectionValue);
        }

        Class<?> destCollectionType = fieldMap.getDestFieldType(BuilderUtil.unwrapDestClassFromBuilder(destObj));
        Class<?> srcFieldType = srcCollectionValue.getClass();
        Object result = null;

        // if they use a standard Collection we have to assume it is a List...better
        // way to handle this?
        if (destCollectionType.getName().equals(Collection.class.getName())) {
            // dont!!
            destCollectionType = List.class;
            if (Set.class.isAssignableFrom(srcFieldType)) {
                destCollectionType = Set.class;
            }
        }
        // Array to Array
        if (CollectionUtils.isArray(srcFieldType) && (CollectionUtils.isArray(destCollectionType))) {
            result = mapArrayToArray(srcObj, srcCollectionValue, fieldMap, destObj);
        } else if (CollectionUtils.isArray(srcFieldType) && (CollectionUtils.isList(destCollectionType))) {
            // Array to List
            result = mapArrayToList(srcObj, srcCollectionValue, fieldMap, destObj);
        } else if (CollectionUtils.isList(srcFieldType) && (CollectionUtils.isArray(destCollectionType))) {
            // List to Array
            result = mapListToArray(srcObj, (List<?>)srcCollectionValue, fieldMap, destObj);
        } else if (CollectionUtils.isSet(srcFieldType) && CollectionUtils.isArray(destCollectionType)) {
            // Set to Array
            result = mapSetToArray(srcObj, (Set<?>)srcCollectionValue, fieldMap, destObj);
        } else if (CollectionUtils.isArray(srcFieldType) && CollectionUtils.isSet(destCollectionType)) {
            // Array to Set
            result = addToSet(srcObj, fieldMap, Arrays.asList((Object[])srcCollectionValue), destObj);
        } else if (CollectionUtils.isCollection(srcFieldType) && CollectionUtils.isSet(destCollectionType)) {
            // Collection to Set
            result = addToSet(srcObj, fieldMap, (Collection<?>)srcCollectionValue, destObj);
        } else if (CollectionUtils.isCollection(srcFieldType) && MappingUtils.isSupportedMap(destCollectionType)) {
            // List to Map value
            result = mapListToList(srcObj, (List<?>)srcCollectionValue, fieldMap, destObj);
        } else if (CollectionUtils.isCollection(srcFieldType) && CollectionUtils.isList(destCollectionType)) {
            // List to List
            // Set to List
            // Collection to List. Fix for 3378952, http://sourceforge.net/tracker/index.php?func=detail&aid=3378952&group_id=133517&atid=727368
            result = mapListToList(srcObj, (Collection<?>)srcCollectionValue, fieldMap, destObj);
        }
        return result;
    }

    private Object mapMap(Object srcObj, Map srcMapValue, FieldMap fieldMap, Object destObj) {
        Map result;
        Map destinationMap = (Map)fieldMap.getDestValue(destObj);
        if (destinationMap == null) {
            result = destBeanCreator.create(srcMapValue.getClass());
        } else {
            result = destinationMap;
            if (fieldMap.isRemoveOrphans()) {
                result.clear();
            }
        }

        for (Entry<?, Object> srcEntry : ((Map<?, Object>)srcMapValue).entrySet()) {
            Object srcEntryValue = srcEntry.getValue();

            if (srcEntryValue == null) { // overwrites with null in any case
                result.put(srcEntry.getKey(), null);
                continue;
            }

            Object destEntryValue = mapOrRecurseObject(srcObj, srcEntryValue, srcEntryValue.getClass(), fieldMap, destObj);
            Object obj = result.get(srcEntry.getKey());
            if (obj != null && obj.equals(destEntryValue) && fieldMap.isNonCumulativeRelationship()) {
                mapToDestObject(null, srcEntryValue, obj, false, null);
            } else {
                result.put(srcEntry.getKey(), destEntryValue);
            }
        }
        return result;
    }

    private Object mapArrayToArray(Object srcObj, Object srcCollectionValue, FieldMap fieldMap, Object destObj) {
        Class destEntryType = fieldMap.getDestFieldType(destObj.getClass()).getComponentType();
        Class srcEntryType = srcCollectionValue.getClass().getComponentType();
        int size = Array.getLength(srcCollectionValue);

        CopyByReferenceContainer copyByReferences = globalConfiguration.getCopyByReferences();
        boolean isPrimitiveArray = CollectionUtils.isPrimitiveArray(srcCollectionValue.getClass());
        boolean isFinal = Modifier.isFinal(srcEntryType.getModifiers());
        boolean isCopyByReference = copyByReferences.contains(srcEntryType);

        if (destEntryType.isAssignableFrom(srcEntryType) && isFinal && (isPrimitiveArray || isCopyByReference)) {
            return addArrayContentCopy(fieldMap, size, srcCollectionValue, destObj, destEntryType);
        } else if (isPrimitiveArray) {
            return addToPrimitiveArray(srcObj, fieldMap, size, srcCollectionValue, destObj, destEntryType);
        } else {
            List<?> list = Arrays.asList((Object[])srcCollectionValue);
            List<?> returnList;
            if (!destEntryType.getName().equals(BASE_CLASS)) {
                returnList = addOrUpdateToList(srcObj, fieldMap, list, destObj, destEntryType);
            } else {
                returnList = addOrUpdateToList(srcObj, fieldMap, list, destObj, null);
            }
            return CollectionUtils.convertListToArray(returnList, destEntryType);
        }
    }

    private void mapFromIterateMethodFieldMap(Object srcObj, Object destObj, Object srcFieldValue, FieldMap fieldMapping) {
        // Iterate over the destFieldValue - iterating is fine unless we are mapping
        // in the other direction.
        // Verify that it is truly a collection if it is an iterator object turn it
        // into a List
        if (srcFieldValue instanceof Iterator) {
            srcFieldValue = IteratorUtils.toList((Iterator<?>)srcFieldValue);
        }
        if (srcFieldValue != null) {
            for (int i = 0; i < CollectionUtils.getLengthOfCollection(srcFieldValue); i++) {
                final Object value = CollectionUtils.getValueFromCollection(srcFieldValue, i);

                // map this value
                if (fieldMapping.getDestHintContainer() == null) {
                    MappingUtils.throwMappingException("<field type=\"iterate\"> must have a source or destination type hint");
                }

                Class<?> destinationHint = fieldMapping.getDestHintType(value.getClass());

                Object result = mapOrRecurseObject(srcObj, value, destinationHint, fieldMapping, destObj);

                if (value != null) {
                    writeDestinationValue(destObj, result, fieldMapping, srcObj);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(logMsgFactory.createFieldMappingSuccessMsg(srcObj.getClass(), destObj.getClass(), fieldMapping.getSrcFieldName(),
                                                                 fieldMapping.getDestFieldName(), srcFieldValue, null, fieldMapping.getClassMap().getMapId()));
        }
    }

    private Object addArrayContentCopy(FieldMap fieldMap, int size, Object srcCollectionValue, Object destObj,
                                       Class destEntryType) {
        Object result;
        Object field = fieldMap.getDestValue(destObj);
        int arraySize = 0;
        if (field == null) {
            result = Array.newInstance(destEntryType, size);
        } else {
            result = Array.newInstance(destEntryType, size + Array.getLength(field));
            arraySize = Array.getLength(field);
            System.arraycopy(field, 0, result, 0, arraySize);
        }
        System.arraycopy(srcCollectionValue, 0, result, arraySize, size);
        return result;
    }

    private Object addToPrimitiveArray(Object srcObj, FieldMap fieldMap, int size, Object srcCollectionValue, Object destObj,
                                       Class<?> destEntryType) {

        Object result;
        Object field = fieldMap.getDestValue(destObj);
        int arraySize = 0;
        if (field == null) {
            result = Array.newInstance(destEntryType, size);
        } else {
            result = Array.newInstance(destEntryType, size + Array.getLength(field));
            arraySize = Array.getLength(field);
            System.arraycopy(field, 0, result, 0, arraySize);
        }
        // primitive arrays are ALWAYS cumulative
        for (int i = 0; i < size; i++) {
            CopyByReferenceContainer copyByReferences = globalConfiguration.getCopyByReferences();
            Object toValue;
            if (srcCollectionValue != null && copyByReferences.contains(srcCollectionValue.getClass())) {
                toValue = srcCollectionValue;
            } else {
                toValue = mapOrRecurseObject(srcObj, Array.get(srcCollectionValue, i), destEntryType, fieldMap, destObj);
            }
            Array.set(result, arraySize, toValue);
            arraySize++;
        }
        return result;
    }

    private Object mapListToArray(Object srcObj, Collection<?> srcCollectionValue, FieldMap fieldMap, Object destObj) {
        Class destEntryType = fieldMap.getDestFieldType(destObj.getClass()).getComponentType();
        List list;
        if (!destEntryType.getName().equals(BASE_CLASS)) {
            list = addOrUpdateToList(srcObj, fieldMap, srcCollectionValue, destObj, destEntryType);
        } else {
            list = addOrUpdateToList(srcObj, fieldMap, srcCollectionValue, destObj);
        }
        return CollectionUtils.convertListToArray(list, destEntryType);
    }

    private List<?> mapListToList(Object srcObj, Collection<?> srcCollectionValue, FieldMap fieldMap, Object destObj) {
        return addOrUpdateToList(srcObj, fieldMap, srcCollectionValue, destObj);
    }

    private Set<?> addToSet(Object srcObj, FieldMap fieldMap, Collection<?> srcCollectionValue, Object destObj) {
        // create a list here so we can keep track of which elements we have mapped, and remove all others if removeOrphans = true
        Set<Object> mappedElements = new HashSet<>();

        LinkedHashSet<Object> result = new LinkedHashSet<>();
        // don't want to create the set if it already exists.
        Object field = fieldMap.getDestValue(destObj);
        if (field != null) {
            result.addAll((Collection<?>)field);
        }
        Object destValue;

        Class<?> destEntryType = null;
        Class<?> prevDestEntryType = null;
        for (Object srcValue : srcCollectionValue) {
            if (destEntryType == null
                || (fieldMap.getDestHintContainer() != null && fieldMap.getDestHintContainer().hasMoreThanOneHint())) {
                destEntryType = determineCollectionItemType(fieldMap, destObj, srcValue, prevDestEntryType);
            }

            Integer tx = mappedFields.startTransaction();
            CopyByReferenceContainer copyByReferences = globalConfiguration.getCopyByReferences();
            if (srcValue != null && copyByReferences.contains(srcValue.getClass())) {
                destValue = srcValue;
            } else {
                destValue = mapOrRecurseObject(srcObj, srcValue, destEntryType, fieldMap, destObj);
            }
            prevDestEntryType = destEntryType;

            if (RelationshipType.NON_CUMULATIVE.equals(fieldMap.getRelationshipType())
                && result.contains(destValue)) {
                mappedFields.rollbackTransaction(tx); // rollback side effects of dry-run
                List<Object> resultAsList = new ArrayList<>(result);
                int index = resultAsList.indexOf(destValue);
                // perform an update if complex type - can't map strings
                Object obj = resultAsList.get(index);
                // make sure it is not a String
                if (!obj.getClass().isAssignableFrom(String.class)) {
                    mapToDestObject(null, srcValue, obj, false, fieldMap.getMapId());
                    mappedElements.add(obj);
                }
            } else {
                mappedFields.commitTransaction(tx);
                if (destValue != null || fieldMap.isDestMapNull()) {
                    result.add(destValue);
                }
                mappedElements.add(destValue);
            }
        }

        // If remove orphans - we only want to keep the objects we've mapped from the src collection
        // so we'll clear result and replace all entries with the ones in mappedElements
        if (fieldMap.isRemoveOrphans()) {
            result.clear();
            result.addAll(mappedElements);
        }

        if (field == null) {
            Class<? extends Set<?>> destSetType = (Class<? extends Set<?>>)fieldMap.getDestFieldType(destObj.getClass());
            return CollectionUtils.createNewSet(destSetType, result);
        } else {
            // Bug #1822421 - Clear first so we don't end up with the removed orphans again
            ((Set)field).clear();
            ((Set)field).addAll(result);
            return (Set<?>)field;
        }
    }

    private List<?> addOrUpdateToList(Object srcObj, FieldMap fieldMap, Collection<?> srcCollectionValue, Object destObj,
                                      Class<?> destEntryType) {
        // create a Set here so we can keep track of which elements we have mapped, and remove all others if removeOrphans = true
        List<Object> mappedElements = new ArrayList<>();
        List result;
        // don't want to create the list if it already exists.
        // these maps are special cases which do not fall under what we are looking for
        Object field = fieldMap.getDestValue(destObj);
        result = prepareDestinationList(srcCollectionValue, field);

        Object destValue;
        Class<?> prevDestEntryType = null;
        for (Object srcValue : srcCollectionValue) {
            if (destEntryType == null
                || (fieldMap.getDestHintContainer() != null && fieldMap.getDestHintContainer().hasMoreThanOneHint())) {
                destEntryType = determineCollectionItemType(fieldMap, destObj, srcValue, prevDestEntryType);
            }

            Integer tx = mappedFields.startTransaction();
            CopyByReferenceContainer copyByReferences = globalConfiguration.getCopyByReferences();
            if (srcValue != null && copyByReferences.contains(srcValue.getClass())) {
                destValue = srcValue;
            } else {
                destValue = mapOrRecurseObject(srcObj, srcValue, destEntryType, fieldMap, destObj);
            }
            prevDestEntryType = destEntryType;

            if (RelationshipType.NON_CUMULATIVE.equals(fieldMap.getRelationshipType())
                && result.contains(destValue)) {
                mappedFields.rollbackTransaction(tx); // rollback side effects of dry-run
                int index = result.indexOf(destValue);
                // perform an update if complex type - can't map strings
                Object obj = result.get(index);
                // make sure it is not a String
                if (obj != null && !obj.getClass().isAssignableFrom(String.class)) {
                    mapToDestObject(null, srcValue, obj, false, fieldMap.getMapId());
                    mappedElements.add(obj);
                }
            } else {
                mappedFields.commitTransaction(tx);
                // respect null mappings
                if (destValue != null || fieldMap.isDestMapNull()) {
                    result.add(destValue);
                }
                mappedElements.add(destValue);
            }
        }

        // If remove orphans - we only want to keep the objects we've mapped from the src collection
        if (fieldMap.isRemoveOrphans()) {
            removeOrphans(mappedElements, result);
        }

        return result;
    }

    private Class<?> determineCollectionItemType(FieldMap fieldMap, Object destObj, Object srcValue, Class<?> prevDestEntryType) {
        if (srcValue == null && fieldMap.getDestHintType(destObj.getClass()) != null) {
            // try to get a possible configured dest hint for the dest obj
            return fieldMap.getDestHintType(destObj.getClass());
        } else if (srcValue == null && prevDestEntryType != null) {
            // if we already evaluated the dest type, use it
            return prevDestEntryType;
        } else if (srcValue != null) {
            // if there's no dest hint for the dest obj, take the src hint
            return fieldMap.getDestHintType(srcValue.getClass());
        }
        throw new MappingException("Unable to determine type for value '" + srcValue + "'. Use hints or generic collections.");
    }

    static void removeOrphans(Collection<?> mappedElements, List<Object> result) {
        for (Iterator<?> iterator = result.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (!mappedElements.contains(object)) {
                iterator.remove();
            }
        }
        for (Object object : mappedElements) {
            if (!result.contains(object)) {
                result.add(object);
            }
        }
    }

    static List<?> prepareDestinationList(Collection<?> srcCollectionValue, Object field) {
        if (field == null) {
            return new ArrayList<>(srcCollectionValue.size());
        } else {
            if (CollectionUtils.isList(field.getClass())) {
                return (List<?>)field;
            } else if (CollectionUtils.isArray(field.getClass())) {
                return new ArrayList<>(Arrays.asList((Object[])field));
            } else { // assume it is neither - safest way is to create new List
                return new ArrayList<>(srcCollectionValue.size());
            }
        }
    }

    private List<?> addOrUpdateToList(Object srcObj, FieldMap fieldMap, Collection<?> srcCollectionValue, Object destObj) {
        return addOrUpdateToList(srcObj, fieldMap, srcCollectionValue, destObj, null);
    }

    private Object mapSetToArray(Object srcObj, Collection<?> srcCollectionValue, FieldMap fieldMap, Object destObj) {
        return mapListToArray(srcObj, srcCollectionValue, fieldMap, destObj);
    }

    private List<?> mapArrayToList(Object srcObj, Object srcCollectionValue, FieldMap fieldMap, Object destObj) {
        Class<?> destEntryType;
        if (fieldMap.getDestHintContainer() != null) {
            destEntryType = fieldMap.getDestHintContainer().getHint();
        } else {
            destEntryType = srcCollectionValue.getClass().getComponentType();
        }
        List<?> srcValueList;
        if (CollectionUtils.isPrimitiveArray(srcCollectionValue.getClass())) {
            srcValueList = CollectionUtils.convertPrimitiveArrayToList(srcCollectionValue);
        } else {
            srcValueList = Arrays.asList((Object[])srcCollectionValue);
        }
        return addOrUpdateToList(srcObj, fieldMap, srcValueList, destObj, destEntryType);
    }

    private void writeDestinationValue(Object destObj, Object destFieldValue, FieldMap fieldMap, Object srcObj) {
        boolean bypass = false;
        // don't map null to dest field if map-null="false"
        if (destFieldValue == null && !fieldMap.isDestMapNull()) {
            bypass = true;
        }

        // don't map "" to dest field if map-empty-string="false"
        if (destFieldValue != null && !fieldMap.isDestMapEmptyString() && destFieldValue.getClass().equals(String.class)
            && StringUtils.isEmpty((String)destFieldValue)) {
            bypass = true;
        }

        // trim string value if trim-strings="true"
        if (destFieldValue != null && fieldMap.isTrimStrings() && destFieldValue.getClass().equals(String.class)) {
            destFieldValue = ((String)destFieldValue).trim();
        }

        if (!bypass) {
            eventManager.on(new DefaultEvent(EventTypes.MAPPING_PRE_WRITING_DEST_VALUE, fieldMap.getClassMap(), fieldMap, srcObj,
                                             destObj, destFieldValue));

            fieldMap.writeDestValue(destObj, destFieldValue);

            eventManager.on(new DefaultEvent(EventTypes.MAPPING_POST_WRITING_DEST_VALUE, fieldMap.getClassMap(), fieldMap, srcObj,
                                             destObj, destFieldValue));
        }
    }

    private Object mapUsingCustomConverterInstance(CustomConverter converterInstance, Class<?> srcFieldClass, Object srcFieldValue,
                                                   Class<?> destFieldClass, Object existingDestFieldValue, FieldMap fieldMap, boolean topLevel) {

        //1792048 - If map-null = "false" and src value is null, then don't even invoke custom converter
        if (srcFieldValue == null && !fieldMap.isDestMapNull()) {
            return null;
        }

        if (converterInstance instanceof MapperAware) {
            ((MapperAware)converterInstance).setMapper(this);
        }

        Object result;
        if (converterInstance instanceof ConfigurableCustomConverter) {
            ConfigurableCustomConverter theConverter = (ConfigurableCustomConverter) converterInstance;

            // Converter could be not configured for this particular case
            if (fieldMap != null) {
                String param = fieldMap.getCustomConverterParam();
                theConverter.setParameter(param);
            }
        }
        // if this is a top level mapping the destObj is the highest level
        // mapping...not a recursive mapping
        if (topLevel) {
            result = converterInstance.convert(existingDestFieldValue, srcFieldValue, destFieldClass, srcFieldClass);
        } else {
            Object existingValue = getExistingValue(fieldMap, existingDestFieldValue, destFieldClass);
            result = converterInstance.convert(existingValue, srcFieldValue, destFieldClass, srcFieldClass);
        }

        return result;
    }

    // TODO: possibly extract this to a separate class

    private Object mapUsingCustomConverter(Class<?> customConverterClass, Class<?> srcFieldClass, Object srcFieldValue,
                                           Class<?> destFieldClass, Object existingDestFieldValue, FieldMap fieldMap, boolean topLevel) {
        CustomConverter converterInstance = null;
        // search our injected customconverters for a match
        if (customConverterObjects != null) {
            for (CustomConverter customConverterObject : customConverterObjects) {
                if (customConverterClass.isInstance(customConverterObject)) {
                    // we have a match
                    converterInstance = customConverterObject;
                }
            }
        }
        // if converter object instances were not injected, then create new instance
        // of the converter for each conversion
        // TODO : Should we really create it each time?
        if (converterInstance == null) {
            converterInstance = (CustomConverter)ReflectionUtils.newInstance(customConverterClass);
        }
        return mapUsingCustomConverterInstance(converterInstance, srcFieldClass, srcFieldValue, destFieldClass, existingDestFieldValue,
                                               fieldMap, topLevel);
    }

    private Collection<ClassMap> checkForSuperTypeMapping(Class<?> srcClass, Class<?> destClass) {
        // Check cache first
        Object cacheKey = CacheKeyFactory.createKey(destClass, srcClass);
        Collection<ClassMap> cachedResult = (Collection<ClassMap>)superTypeCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        // If no existing cache entry is found, determine super type mappings.
        // Recursively walk the inheritance hierarchy.
        List<ClassMap> superClasses = new ArrayList<>();
        // Need to call getRealSuperclass because proxied data objects will not return correct
        // superclass when using basic reflection

        List<Class<?>> superSrcClasses = MappingUtils.getSuperClassesAndInterfaces(srcClass, beanContainer);
        List<Class<?>> superDestClasses = MappingUtils.getSuperClassesAndInterfaces(destClass, beanContainer);

        // add the actual classes to check for mappings between the original and the opposite
        // super classes
        superSrcClasses.add(0, srcClass);
        superDestClasses.add(0, destClass);

        for (Class<?> superSrcClass : superSrcClasses) {
            for (Class<?> superDestClass : superDestClasses) {
                if (!(superSrcClass.equals(srcClass) && superDestClass.equals(destClass))) {
                    checkForClassMapping(superSrcClass, superClasses, superDestClass);
                }
            }
        }

        Collections.reverse(superClasses); // Done so base classes are processed first

        superTypeCache.put(cacheKey, superClasses);

        return superClasses;
    }

    private void checkForClassMapping(Class<?> srcClass, List<ClassMap> superClasses, Class<?> superDestClass) {
        ClassMap srcClassMap = classMappings.find(srcClass, superDestClass);
        if (srcClassMap != null) {
            superClasses.add(srcClassMap);
        }
    }

    private void processSuperTypeMapping(Collection<ClassMap> superClasses, Object srcObj, Object destObj, List<String> mappedParentFields, String mapId) {
        for (ClassMap map : superClasses) {
            map(map, srcObj, destObj, true, mappedParentFields, mapId);
            for (FieldMap fieldMapping : map.getFieldMaps()) {
                String key = MappingUtils.getMappedParentFieldKey(destObj, fieldMapping);
                mappedParentFields.add(key);
            }
        }
    }

    private static Object getExistingValue(FieldMap fieldMap, Object destObj, Class<?> destFieldType) {
        // verify that the dest obj is not null
        if (destObj == null) {
            return null;
        }
        // call the getXX method to see if the field is already instantiated
        Object result = fieldMap.getDestValue(destObj);

        // When we are recursing through a list we need to make sure that we are not
        // in the list
        // by checking the destFieldType
        if (result != null) {
            if (CollectionUtils.isList(result.getClass()) || CollectionUtils.isArray(result.getClass())
                || CollectionUtils.isSet(result.getClass()) || MappingUtils.isSupportedMap(result.getClass())) {
                if (!CollectionUtils.isList(destFieldType) && !CollectionUtils.isArray(destFieldType)
                    && !CollectionUtils.isSet(destFieldType) && !MappingUtils.isSupportedMap(destFieldType)) {
                    // this means the getXX field is a List but we are actually trying to
                    // map one of its elements
                    result = null;
                }
            }
        }
        return result;
    }

    private ClassMap getClassMap(Class<?> srcClass, Class<?> destClass, String mapId) {
        ClassMap mapping = classMappings.find(srcClass, destClass, mapId);

        if (mapping == null) {
            mapping = classMappings.find(destClass, srcClass, null);
            if (mapping != null && MappingDirection.ONE_WAY == mapping.getType()) {
                //Does the opposite mapping exist, but its only a ONE_WAY?
                //Then create an empty mapping, i.e.: an object instance will be returned by the mapper, but no fields will be set
                mapping = classMapBuilder.createDefaultClassMap(globalConfiguration, srcClass, destClass, false);
                classMappings.addDefault(srcClass, destClass, mapping);
            } else {
                // If mapping not found in existing custom mapping collection,
                // create default as an explicit mapping must not exist.
                // The create default class map method will also add all default
                // mappings that it can determine.
                mapping = classMapBuilder.createDefaultClassMap(globalConfiguration, srcClass, destClass);
                classMappings.addDefault(srcClass, destClass, mapping);
            }
        }

        return mapping;
    }

}
