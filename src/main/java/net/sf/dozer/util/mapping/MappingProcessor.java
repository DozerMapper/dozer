/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.dozer.util.mapping.cache.Cache;
import net.sf.dozer.util.mapping.cache.CacheEntry;
import net.sf.dozer.util.mapping.cache.CacheKeyFactory;
import net.sf.dozer.util.mapping.cache.CacheManagerIF;
import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.classmap.Configuration;
import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.converters.CustomConverter;
import net.sf.dozer.util.mapping.converters.PrimitiveOrWrapperConverter;
import net.sf.dozer.util.mapping.event.DozerEvent;
import net.sf.dozer.util.mapping.event.DozerEventManager;
import net.sf.dozer.util.mapping.event.EventManagerIF;
import net.sf.dozer.util.mapping.fieldmap.CustomGetSetMethodFieldMap;
import net.sf.dozer.util.mapping.fieldmap.ExcludeFieldMap;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.Hint;
import net.sf.dozer.util.mapping.fieldmap.MapFieldMap;
import net.sf.dozer.util.mapping.stats.StatisticTypeConstants;
import net.sf.dozer.util.mapping.stats.StatisticsManagerIF;
import net.sf.dozer.util.mapping.util.ClassMapBuilder;
import net.sf.dozer.util.mapping.util.ClassMapFinder;
import net.sf.dozer.util.mapping.util.ClassMapKeyFactory;
import net.sf.dozer.util.mapping.util.CollectionUtils;
import net.sf.dozer.util.mapping.util.DateFormatContainer;
import net.sf.dozer.util.mapping.util.DestBeanCreator;
import net.sf.dozer.util.mapping.util.Jdk5Methods;
import net.sf.dozer.util.mapping.util.LogMsgFactory;
import net.sf.dozer.util.mapping.util.MapperConstants;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.MappingValidator;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal Mapping Engine. Not intended for direct use by Application code. This class does most of the heavy lifting
 * and is very recursive in nature.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class MappingProcessor implements MapperIF {

  private static final Log log = LogFactory.getLog(MappingProcessor.class);

  private List superListOfFieldNames = null;
  private final Map customMappings;
  private final Configuration globalConfiguration;
  private final List customConverterObjects;// actual converter object instances
  private final StatisticsManagerIF statsMgr;
  private final EventManagerIF eventMgr;
  private final CustomFieldMapperIF customFieldMapper;
  private final Map mappedFields = new HashMap();
  private final Cache converterByDestTypeCache;
  private final Cache superTypeCache;
  private final PrimitiveOrWrapperConverter primitiveOrWrapperConverter = new PrimitiveOrWrapperConverter();

  // The stored factories don't belong in MappingUtils and need to be relocated
  private final DestBeanCreator destBeanCreator = new DestBeanCreator(MappingUtils.storedFactories);

  protected MappingProcessor(Map customMappings, Configuration globalConfiguration, CacheManagerIF cacheMgr,
      StatisticsManagerIF statsMgr, List customConverterObjects, List eventListeners,
      CustomFieldMapperIF customFieldMapper) {
    this.customMappings = customMappings;
    this.globalConfiguration = globalConfiguration;
    this.statsMgr = statsMgr;
    this.customConverterObjects = customConverterObjects;
    this.eventMgr = new DozerEventManager(eventListeners);
    this.customFieldMapper = customFieldMapper;
    this.converterByDestTypeCache = cacheMgr.getCache(MapperConstants.CONVERTER_BY_DEST_TYPE_CACHE);
    this.superTypeCache = cacheMgr.getCache(MapperConstants.SUPER_TYPE_CHECK_CACHE);
  }

  public Object map(Object sourceObj, Class destClass) {
    // map-id is optional, so just pass in null
    return map(sourceObj, destClass, (String) null);
  }

  public Object map(Object sourceObj, Class destClass, String mapId) {
    MappingValidator.validateMappingRequest(sourceObj, destClass);

    Object destObj = null;
    ClassMap classMap = null;
    try {
      classMap = getClassMap(sourceObj, destClass, mapId, false);

      // Check to see if custom converter has been specified for this mapping
      // combination. If so, just use it.
      Class converterClass = MappingUtils.findCustomConverter(converterByDestTypeCache, classMap.getCustomConverters(),
          sourceObj.getClass(), destClass);
      if (converterClass != null) {
        eventMgr.fireEvent(new DozerEvent(MapperConstants.MAPPING_STARTED_EVENT, classMap, null, sourceObj, destObj,
            null));
        return mapUsingCustomConverter(converterClass, sourceObj.getClass(), sourceObj, destClass, destObj, null, true);
      }

      // Create destination object. It will be populated in the call to map()
      destObj = destBeanCreator.create(sourceObj, classMap, destClass);

      // Map src values to dest object
      map(classMap, sourceObj, destObj, null, null);
    } catch (Throwable e) {
      MappingUtils.throwMappingException(e);
    }
    eventMgr
        .fireEvent(new DozerEvent(MapperConstants.MAPPING_FINISHED_EVENT, classMap, null, sourceObj, destObj, null));
    return destObj;
  }

  public void map(Object sourceObj, Object destObj) {
    map(sourceObj, destObj, null);
  }

  public void map(Object sourceObj, Object destObj, String mapId) {
    MappingValidator.validateMappingRequest(sourceObj, destObj);

    ClassMap classMap = null;
    try {
      classMap = getClassMap(sourceObj, destObj.getClass(), mapId, true);

      // Check to see if custom converter has been specified for this mapping
      // combination. If so, just use it.
      Class converterClass = MappingUtils.findCustomConverter(converterByDestTypeCache, classMap.getCustomConverters(),
          sourceObj.getClass(), destObj.getClass());
      eventMgr
          .fireEvent(new DozerEvent(MapperConstants.MAPPING_STARTED_EVENT, classMap, null, sourceObj, destObj, null));

      if (converterClass != null) {
        mapUsingCustomConverter(converterClass, sourceObj.getClass(), sourceObj, destObj.getClass(), destObj, null,
            true);
        return;
      }

      // Map src values to dest object
      map(classMap, sourceObj, destObj, null, null);
    } catch (Throwable e) {
      MappingUtils.throwMappingException(e);
    }
    eventMgr
        .fireEvent(new DozerEvent(MapperConstants.MAPPING_FINISHED_EVENT, classMap, null, sourceObj, destObj, null));
  }

  private void map(ClassMap classMap, Object sourceObj, Object destObj, ClassMap parentClassMap, FieldMap parentFieldMap) {
    MappingValidator.validateMappingRequest(sourceObj, destObj);

    // 1596766 - Recursive object mapping issue. Prevent recursive mapping
    // infinite loop. Keep a record of mapped fields by storing the id of the sourceObj and the
    // destObj to be mapped. This can be referred to later to avoid recursive mapping loops
    mappedFields.put(sourceObj, destObj);

    // see if we need to pull a referenced mapId
    String mapId = null;
    if (parentFieldMap != null) {
      mapId = parentFieldMap.getMapId();
    }

    // If class map hasnt already been determined, find the appropriate one for
    // the src/dest object combination
    if (classMap == null) {
      classMap = getClassMap(sourceObj, destObj.getClass(), mapId, true);
    }

    Class sourceClass = sourceObj.getClass();
    Class destClass = destObj.getClass();

    // Check to see if custom converter has been specified for this mapping
    // combination. If so, just use it.
    Class converterClass = MappingUtils.findCustomConverter(converterByDestTypeCache, classMap.getCustomConverters(),
        sourceObj.getClass(), destClass);
    if (converterClass != null) {
      Object rvalue = mapUsingCustomConverter(converterClass, sourceObj.getClass(), sourceObj, destClass, destObj,
          null, true);
      if (rvalue != null) {
        destObj = rvalue;
      }
      return;
    }

    // Now check for super class mappings as a convenience -assuming for now
    // that super class mappings are at the same level
    List parentFieldNames = null;
    if (parentClassMap == null) {
      // check for super classes
      Set superClasses = checkForSuperTypeMapping(sourceClass, destClass, classMap);
      // check for interfaces
      superClasses.addAll(ClassMapFinder.findInterfaceMappings(this.customMappings, sourceClass, destClass));
      if (superClasses.size() > 0) {
        superListOfFieldNames = new ArrayList();
        parentFieldNames = processSuperTypeMapping(superClasses, sourceObj, destObj, sourceClass, parentFieldMap);
        superListOfFieldNames = null;
      }
    }

    // Perform mappings for each field. Iterate through Fields Maps for this class mapping
    List fieldMaps = classMap.getFieldMaps();
    int size = fieldMaps.size();
    for (int i = 0; i < size; i++) {
      FieldMap fieldMapping = (FieldMap) fieldMaps.get(i);
      mapField(fieldMapping, sourceObj, destObj, parentClassMap, parentFieldMap, parentFieldNames);
    }
  }

  private void mapField(FieldMap fieldMapping, Object sourceObj, Object destObj, ClassMap parentClassMap,
      FieldMap parentFieldMap, List parentFieldNames) {

    // The field has been explicitly excluded from mapping. So just return, as
    // no further processing is needed for this field
    if (fieldMapping instanceof ExcludeFieldMap) {
      return;
    }

    Class sourceClass = sourceObj.getClass();
    Object sourceFieldValue = null;
    try {
      // check for super class names
      String parentSourceField = null;
      if (parentFieldMap != null) {
        parentSourceField = parentFieldMap.getSrcFieldName();
      }

      String key = MappingUtils.getParentFieldNameKey(parentSourceField, sourceObj, sourceClass.getName(), fieldMapping
          .getSrcFieldName(), fieldMapping.getDestFieldName());
      if (parentClassMap != null) {
        if (superListOfFieldNames != null) {
          if (superListOfFieldNames.contains(key)) {
            return;
          } else {
            superListOfFieldNames.add(key);
          }
        } else {
          superListOfFieldNames = new ArrayList();
          superListOfFieldNames.add(key);
        }
      }
      // check for parent field names
      if (parentFieldNames != null && parentFieldNames.size() > 0) {
        if (parentFieldNames.contains(key)) {
          return;
        } else {
          parentFieldNames.add(key);
        }
      }

      // If a custom field mapper was specified, then invoke it. If not, or the
      // custom field mapper returns false(indicating the field was not actually mapped
      // by the custom field mapper), proceed as normal(use Dozer to map the field)
      sourceFieldValue = fieldMapping.getSrcFieldValue(sourceObj);
      boolean fieldMapped = false;
      if (customFieldMapper != null) {
        fieldMapped = customFieldMapper.mapField(sourceObj, destObj, sourceFieldValue, fieldMapping.getClassMap(),
            fieldMapping);
      }

      if (!fieldMapped) {
        if (fieldMapping.getDestFieldType() != null
            && fieldMapping.getDestFieldType().equals(MapperConstants.ITERATE)) {
          // special logic for iterate feature
          mapFromIterateMethodFieldMap(sourceObj, destObj, sourceFieldValue, fieldMapping);
        } else {
          // either deep field map or generic map. The is the most likely scenario
          mapFromFieldMap(sourceObj, destObj, sourceFieldValue, fieldMapping);
        }
      }

      statsMgr.increment(StatisticTypeConstants.FIELD_MAPPING_SUCCESS_COUNT);

    } catch (Throwable e) {
      log.error(LogMsgFactory.createFieldMappingErrorMsg(sourceObj, fieldMapping, sourceFieldValue, destObj, e), e);
      statsMgr.increment(StatisticTypeConstants.FIELD_MAPPING_FAILURE_COUNT);

      // check error handling policy.
      if (fieldMapping.getClassMap().getStopOnErrors()) {
        MappingUtils.throwMappingException(e);
      } else {
        // check if any Exceptions should be allowed to be thrown
        if (!fieldMapping.getClassMap().getAllowedExceptions().isEmpty()
            && e.getCause() instanceof InvocationTargetException) {
          Throwable thrownType = ((InvocationTargetException) e.getCause()).getTargetException();
          Class exceptionClass = thrownType.getClass();
          if (fieldMapping.getClassMap().getAllowedExceptions().contains(exceptionClass)) {
            throw (RuntimeException) thrownType;
          }
        }
        statsMgr.increment(StatisticTypeConstants.FIELD_MAPPING_FAILURE_IGNORED_COUNT);
      }
    }
  }

  private void mapFromFieldMap(Object sourceObj, Object destObj, Object sourceFieldValue, FieldMap fieldMapping) {
    Class destFieldType = null;
    // methodmap logic should be encapsulated and figured out at the fieldmap level
    if (fieldMapping instanceof CustomGetSetMethodFieldMap) {
      destFieldType = fieldMapping.getDestFieldWriteMethod(destObj.getClass()).getParameterTypes()[0];
    } else {
      destFieldType = fieldMapping.getDestFieldType(destObj.getClass());
    }

    // 1476780 - 12/2006 mht - Add support for field level custom converters
    // Use field level custom converter if one was specified. Otherwise, map or
    // recurse the object as normal
    Object destFieldValue = null;
    if (MappingUtils.isBlankOrNull(fieldMapping.getCustomConverter())) {
      destFieldValue = mapOrRecurseObject(sourceObj, sourceFieldValue, destFieldType, fieldMapping, destObj);
    } else {
      Class sourceFieldClass = sourceFieldValue != null ? sourceFieldValue.getClass() : fieldMapping
          .getSourceFieldType(sourceObj.getClass());
      destFieldValue = mapUsingCustomConverter(MappingUtils.loadClass(fieldMapping.getCustomConverter()),
          sourceFieldClass, sourceFieldValue, destFieldType, destObj, fieldMapping, false);
    }

    writeDestinationValue(destObj, destFieldValue, fieldMapping);

    if (log.isDebugEnabled()) {
      log.debug(LogMsgFactory.createFieldMappingSuccessMsg(sourceObj.getClass(), destObj.getClass(), fieldMapping
          .getSrcFieldName(), fieldMapping.getDestFieldName(), sourceFieldValue, destFieldValue));
    }
  }

  private Object mapOrRecurseObject(Object srcObj, Object sourceFieldValue, Class destFieldType, FieldMap fieldMap,
      Object destObj) {
    Class sourceFieldClass = sourceFieldValue != null ? sourceFieldValue.getClass() : fieldMap
        .getSourceFieldType(srcObj.getClass());
    Class converterClass = MappingUtils.determineCustomConverter(fieldMap, converterByDestTypeCache, fieldMap
        .getClassMap().getCustomConverters(), sourceFieldClass, destFieldType);

    // 1-2007 mht: Invoke custom converter even if the src value is null. #1563795
    if (converterClass != null) {
      return mapUsingCustomConverter(converterClass, sourceFieldClass, sourceFieldValue, destFieldType, destObj,
          fieldMap, false);
    }

    if (sourceFieldValue == null) {
      return null;
    }

    // 1596766 - Recursive object mapping issue. Prevent recursive mapping infinite loop
    Object alreadyMappedValue = null;
    if (sourceFieldValue != null) {
      alreadyMappedValue = mappedFields.get(sourceFieldValue);
    }
    if (alreadyMappedValue != null) {
      // 1664984 - bi-directionnal mapping with sets & subclasses
      if (destFieldType.isAssignableFrom(alreadyMappedValue.getClass())) {
        // Source value has already been mapped to the required destFieldType.
        return alreadyMappedValue;
      }

      // 1658168 - Recursive mapping issue for interfaces
      if (destFieldType.isInterface() && destFieldType.isAssignableFrom(alreadyMappedValue.getClass())) {
        // Source value has already been mapped to the required destFieldType.
        return alreadyMappedValue;
      }
    }

    if (fieldMap.getCopyByReference()) {
      // just get the src and return it, no transformation.
      return sourceFieldValue;
    }

    boolean isSourceFieldClassSupportedMap = MappingUtils.isSupportedMap(sourceFieldClass);
    boolean isDestFieldTypeSupportedMap = MappingUtils.isSupportedMap(destFieldType);
    if (isSourceFieldClassSupportedMap && isDestFieldTypeSupportedMap) {
      return mapMap(srcObj, sourceFieldValue, fieldMap, destObj, destFieldType);
    }
    if (fieldMap instanceof MapFieldMap && destFieldType.equals(Object.class)) {
      // TODO: find better place for this logic. try to encapsulate in FieldMap?
      destFieldType = fieldMap.getDestinationTypeHint() != null ? fieldMap.getDestinationTypeHint().getHint()
          : sourceFieldClass;
    }
    if (MappingUtils.isPrimitiveOrWrapper(sourceFieldClass) || MappingUtils.isPrimitiveOrWrapper(destFieldType)) {
      // Primitive or Wrapper conversion
      if (fieldMap.getDestinationTypeHint() != null) {
        destFieldType = fieldMap.getDestinationTypeHint().getHint();
      }
      return primitiveOrWrapperConverter.convert(sourceFieldValue, destFieldType, new DateFormatContainer(fieldMap));
    }
    if (MappingUtils.isSupportedCollection(sourceFieldClass) && (MappingUtils.isSupportedCollection(destFieldType))) {
      return mapCollection(srcObj, sourceFieldValue, fieldMap, destObj);
    }
    if (GlobalSettings.getInstance().isJava5()
        && ((Boolean) ReflectionUtils.invoke(Jdk5Methods.getInstance().getClassIsEnumMethod(), sourceFieldClass, null))
            .booleanValue()
        && ((Boolean) ReflectionUtils.invoke(Jdk5Methods.getInstance().getClassIsEnumMethod(), destFieldType, null))
            .booleanValue()) {
      return mapEnum(sourceFieldValue, destFieldType);
    }

    // Default: Map from one custom data object to another custom data object
    return mapCustomObject(fieldMap, destObj, destFieldType, sourceFieldValue);
  }

  private Object mapEnum(Object sourceFieldValue, Class destFieldType) {
    String name = (String) ReflectionUtils
        .invoke(Jdk5Methods.getInstance().getEnumNameMethod(), sourceFieldValue, null);
    return ReflectionUtils.invoke(Jdk5Methods.getInstance().getEnumValueOfMethod(), destFieldType, new Object[] {
        destFieldType, name });
  }

  private Object mapCustomObject(FieldMap fieldMap, Object destObj, Class destFieldType, Object sourceFieldValue) {
    // Custom java bean. Need to make sure that the destination object is not
    // already instantiated.
    Object field = determineExistingValue(fieldMap, destObj, destFieldType);
    ClassMap classMap = null;
    // if the field is not null than we don't want a new instance
    if (field == null) {
      // first check to see if this plain old field map has hints to the actual type.
      if (fieldMap.getDestinationTypeHint() != null) {
        Class destType = fieldMap.getDestinationTypeHint().getHint();
        // if the destType is null this means that there was more than one hint.
        // we must have already set the destType then.
        if (destType != null) {
          destFieldType = destType;
        }
      }
      // Check to see if explicit map-id has been specified for the field mapping
      String mapId = null;
      mapId = fieldMap.getMapId();
      classMap = getClassMap(sourceFieldValue, destFieldType, mapId, false);
      field = destBeanCreator.create(sourceFieldValue, classMap, fieldMap, null);
    }

    map(classMap, sourceFieldValue, field, null, fieldMap);

    return field;
  }

  private Object mapCollection(Object srcObj, Object sourceCollectionValue, FieldMap fieldMap, Object destObj) {
    // since we are mapping some sort of collection now is a good time to
    // decide if they provided hints
    // if no hint is provided then we will check to see if we can use JDK 1.5
    // generics to determine the mapping type
    // this will only happen once on the dest hint. the next mapping will
    // already have the hint
    if (fieldMap.getDestinationTypeHint() == null && GlobalSettings.getInstance().isJava5()) {
      Object typeArgument = null;
      Object[] parameterTypes = null;
      try {
        Method method = fieldMap.getDestFieldWriteMethod(destObj.getClass());
        parameterTypes = (Object[]) ReflectionUtils.invoke(Jdk5Methods.getInstance()
            .getMethodGetGenericParameterTypesMethod(), method, null);
      } catch (Throwable e) {
        log.info("The destObj:" + destObj + " does not have a write method");
      }
      if (parameterTypes != null) {
        Class parameterTypesClass = Jdk5Methods.getInstance().getParameterizedTypeClass();

        if (parameterTypesClass.isAssignableFrom(parameterTypes[0].getClass())) {

          typeArgument = ((Object[]) ReflectionUtils.invoke(Jdk5Methods.getInstance()
              .getParamaterizedTypeGetActualTypeArgsMethod(), parameterTypes[0], null))[0];
          if (typeArgument != null) {
            Hint destHint = new Hint();
            Class argument = (Class) typeArgument;
            destHint.setHintName(argument.getName());
            fieldMap.setDestinationTypeHint(destHint);
          }
        }
      }
    }

    // if it is an iterator object turn it into a List
    if (sourceCollectionValue instanceof Iterator) {
      sourceCollectionValue = IteratorUtils.toList((Iterator) sourceCollectionValue);
    }

    Class destCollectionType = fieldMap.getDestFieldType(destObj.getClass());
    Class sourceFieldType = sourceCollectionValue.getClass();
    Object result = null;

    // if they use a standard Collection we have to assume it is a List...better
    // way to do this?
    if (destCollectionType.getName().equals(Collection.class.getName())) {
      destCollectionType = List.class;
    }
    // Array to Array
    if (CollectionUtils.isArray(sourceFieldType) && (CollectionUtils.isArray(destCollectionType))) {
      result = mapArrayToArray(srcObj, sourceCollectionValue, fieldMap, destObj);
      // Array to List
    } else if (CollectionUtils.isArray(sourceFieldType) && (CollectionUtils.isList(destCollectionType))) {
      result = mapArrayToList(srcObj, sourceCollectionValue, fieldMap, destObj);
    }
    // List to Array
    else if (CollectionUtils.isList(sourceFieldType) && (CollectionUtils.isArray(destCollectionType))) {
      result = mapListToArray(srcObj, (List) sourceCollectionValue, fieldMap, destObj);
      // List to List
    } else if (CollectionUtils.isList(sourceFieldType) && (CollectionUtils.isList(destCollectionType))) {
      result = mapListToList(srcObj, (List) sourceCollectionValue, fieldMap, destObj);
    }
    // Set to Array
    else if (CollectionUtils.isSet(sourceFieldType) && CollectionUtils.isArray(destCollectionType)) {
      result = mapSetToArray(srcObj, (Set) sourceCollectionValue, fieldMap, destObj);
    }
    // Array to Set
    else if (CollectionUtils.isArray(sourceFieldType) && CollectionUtils.isSet(destCollectionType)) {
      result = addToSet(srcObj, fieldMap, Arrays.asList((Object[]) sourceCollectionValue), destObj);
    }
    // Set to List
    else if (CollectionUtils.isSet(sourceFieldType) && CollectionUtils.isList(destCollectionType)) {
      result = mapListToList(srcObj, (Set) sourceCollectionValue, fieldMap, destObj);
    }
    // Collection to Set
    else if (CollectionUtils.isCollection(sourceFieldType) && CollectionUtils.isSet(destCollectionType)) {
      result = addToSet(srcObj, fieldMap, (Collection) sourceCollectionValue, destObj);
    }
    return result;
  }

  private Object mapMap(Object srcObj, Object sourceMapValue, FieldMap fieldMap, Object destObj, Class destFieldType) {
    Map result = null;
    Object field = fieldMap.getDestinationValue(destObj);
    if (field == null) {
      // no destination map exists
      result = (Map) ReflectionUtils.newInstance(sourceMapValue.getClass());
    } else {
      result = (Map) field;
    }
    Map sourceMap = (Map) sourceMapValue;
    Set sourceEntrySet = sourceMap.entrySet();
    Iterator iter = sourceEntrySet.iterator();
    while (iter.hasNext()) {
      Map.Entry sourceEntry = (Map.Entry) iter.next();
      Object sourceEntryValue = sourceEntry.getValue();
      Object destEntryValue = mapOrRecurseObject(srcObj, sourceEntryValue, sourceEntryValue.getClass(), fieldMap,
          destObj);
      Object obj = result.get(sourceEntry.getKey());
      if (obj != null && obj.equals(destEntryValue) && !(fieldMap instanceof MapFieldMap)
          && MapperConstants.RELATIONSHIP_NON_CUMULATIVE.equals(fieldMap.getRelationshipType())) {
        if (!(obj instanceof String)) {
          map(null, sourceEntryValue, obj, null, fieldMap);
        }
      } else {
        result.put(sourceEntry.getKey(), destEntryValue);
      }

    }
    return result;
  }

  private Object mapArrayToArray(Object srcObj, Object sourceCollectionValue, FieldMap fieldMap, Object destObj) {
    Class destEntryType = fieldMap.getDestFieldType(destObj.getClass()).getComponentType();
    int size = Array.getLength(sourceCollectionValue);
    if (CollectionUtils.isPrimitiveArray(sourceCollectionValue.getClass())) {
      return addToPrimitiveArray(srcObj, fieldMap, size, sourceCollectionValue, destObj, destEntryType);
    } else {
      List list = Arrays.asList((Object[]) sourceCollectionValue);
      List returnList = null;
      if (!destEntryType.getName().equals("java.lang.Object")) {
        returnList = addOrUpdateToList(srcObj, fieldMap, list, destObj, destEntryType);
      } else {
        returnList = addOrUpdateToList(srcObj, fieldMap, list, destObj, null);
      }
      return CollectionUtils.convertListToArray(returnList, destEntryType);
    }
  }

  private void mapFromIterateMethodFieldMap(Object sourceObj, Object destObj, Object sourceFieldValue,
      FieldMap fieldMapping) {
    // Iterate over the destFieldValue - iterating is fine unless we are mapping
    // in the other direction.
    // Verify that it is truly a collection if it is an iterator object turn it
    // into a List
    if (sourceFieldValue instanceof Iterator) {
      sourceFieldValue = IteratorUtils.toList((Iterator) sourceFieldValue);
    }
    if (sourceFieldValue != null) {
      for (int i = 0; i < CollectionUtils.getLengthOfCollection(sourceFieldValue); i++) {
        Object value = CollectionUtils.getValueFromCollection(sourceFieldValue, i);
        // map this value
        if (fieldMapping.getDestinationTypeHint() == null) {
          MappingUtils.throwMappingException("<field type=\"iterate\"> must have a source or destination type hint");
        }
        // check for custom converters
        Class converterClass = MappingUtils.findCustomConverter(converterByDestTypeCache, fieldMapping.getClassMap()
            .getCustomConverters(), value.getClass(), MappingUtils.loadClass(fieldMapping.getDestinationTypeHint()
            .getHintName()));

        if (converterClass != null) {
          Class sourceFieldClass = sourceFieldValue.getClass();
          value = mapUsingCustomConverter(converterClass, sourceFieldClass, value, fieldMapping
              .getDestinationTypeHint().getHint(), null, fieldMapping, false);
        } else {
          value = map(value, fieldMapping.getDestinationTypeHint().getHint());
        }
        if (value != null) {
          writeDestinationValue(destObj, value, fieldMapping);
        }
      }
    }
    if (log.isDebugEnabled()) {
      log.debug(LogMsgFactory.createFieldMappingSuccessMsg(sourceObj.getClass(), destObj.getClass(), fieldMapping
          .getSrcFieldName(), fieldMapping.getDestFieldName(), sourceFieldValue, null));
    }
  }

  private Object addToPrimitiveArray(Object srcObj, FieldMap fieldMap, int size, Object sourceCollectionValue,
      Object destObj, Class destEntryType) {

    Object result = null;
    Object field = fieldMap.getDestinationValue(destObj);
    int arraySize = 0;
    if (field == null) {
      result = Array.newInstance(destEntryType, size);
    } else {
      result = Array.newInstance(destEntryType, size + Array.getLength(field));
      arraySize = Array.getLength(field);
      for (int i = 0; i < Array.getLength(field); i++) {
        Array.set(result, i, Array.get(field, i));
      }
    }
    // primitive arrays are ALWAYS cumulative
    for (int i = 0; i < size; i++) {
      Object toValue = mapOrRecurseObject(srcObj, Array.get(sourceCollectionValue, i), destEntryType, fieldMap, destObj);
      Array.set(result, arraySize, toValue);
      arraySize++;
    }
    return result;
  }

  private Object mapListToArray(Object srcObj, Collection sourceCollectionValue, FieldMap fieldMap, Object destObj) {
    List list = null;
    Class destEntryType = fieldMap.getDestFieldType(destObj.getClass()).getComponentType();

    if (!destEntryType.getName().equals("java.lang.Object")) {
      list = addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, destObj, destEntryType);
    } else {
      list = addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, destObj);
    }
    return CollectionUtils.convertListToArray(list, destEntryType);
  }

  private List mapListToList(Object srcObj, Collection sourceCollectionValue, FieldMap fieldMap, Object destObj) {
    return addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, destObj);
  }

  private Set addToSet(Object srcObj, FieldMap fieldMap, Collection sourceCollectionValue, Object destObj) {
    Class destEntryType = null;
    ListOrderedSet result = new ListOrderedSet();
    // don't want to create the set if it already exists.
    Object field = fieldMap.getDestinationValue(destObj);
    if (field != null) {
      result.addAll((Collection) field);
    }
    Object destValue = null;
    Iterator iter = sourceCollectionValue.iterator();
    Object sourceValue = null;
    while (iter.hasNext()) {
      sourceValue = iter.next();
      if (destEntryType == null
          || (fieldMap.getDestinationTypeHint() != null && fieldMap.getDestinationTypeHint().hasMoreThanOneHint())) {
        destEntryType = fieldMap.getDestHintType(sourceValue.getClass());
      }
      destValue = mapOrRecurseObject(srcObj, sourceValue, destEntryType, fieldMap, destObj);
      if (!(fieldMap instanceof MapFieldMap)) {
        if (fieldMap.getRelationshipType() == null
            || fieldMap.getRelationshipType().equals(MapperConstants.RELATIONSHIP_CUMULATIVE)) {
          result.add(destValue);
        } else {
          if (result.contains(destValue)) {
            int index = result.indexOf(destValue);
            // perform an update if complex type - can't map strings
            Object obj = result.get(index);
            // make sure it is not a String
            if (!obj.getClass().isAssignableFrom(String.class)) {
              map(null, sourceValue, obj, null, fieldMap);
            }
          } else {
            result.add(destValue);
          }
        }
      } else {
        result.add(destValue);
      }
    }
    if (field == null) {
      Class destSetType = fieldMap.getDestFieldType(destObj.getClass());
      return CollectionUtils.createNewSet(destSetType, result);
    } else {
      ((Set) field).addAll(result);
      return (Set) field;
    }
  }

  private List addOrUpdateToList(Object srcObj, FieldMap fieldMap, Collection sourceCollectionValue, Object destObj,
      Class destEntryType) {

    List result = null;
    // don't want to create the list if it already exists.
    // these maps are special cases which do not fall under what we are looking for
    Object field = fieldMap.getDestinationValue(destObj);
    if (field == null) {
      result = new ArrayList(sourceCollectionValue.size());
    } else {
      if (CollectionUtils.isList(field.getClass())) {
        result = (List) field;
      } else if (CollectionUtils.isArray(field.getClass())) {// must be array
        result = new ArrayList(Arrays.asList((Object[]) field));
      } else { // assume it is neither - safest way is to create new
        // List
        result = new ArrayList(sourceCollectionValue.size());
      }
    }
    Object destValue = null;
    Iterator iter = sourceCollectionValue.iterator();
    Object sourceValue = null;
    Class prevDestEntryType = null;
    while (iter.hasNext()) {
      sourceValue = iter.next();
      if (destEntryType == null
          || (fieldMap.getDestinationTypeHint() != null && fieldMap.getDestinationTypeHint().hasMoreThanOneHint())) {
        if (sourceValue == null) {
          destEntryType = prevDestEntryType;
        } else {
          destEntryType = fieldMap.getDestHintType(sourceValue.getClass());
        }
      }
      destValue = mapOrRecurseObject(srcObj, sourceValue, destEntryType, fieldMap, destObj);
      prevDestEntryType = destEntryType;
      if (!(fieldMap instanceof MapFieldMap)) {
        if (fieldMap.getRelationshipType() == null
            || fieldMap.getRelationshipType().equals(MapperConstants.RELATIONSHIP_CUMULATIVE)) {
          result.add(destValue);
        } else {
          if (result.contains(destValue)) {
            int index = result.indexOf(destValue);
            // perform an update if complex type - can't map strings
            Object obj = result.get(index);
            // make sure it is not a String
            if (!obj.getClass().isAssignableFrom(String.class)) {
              map(null, sourceValue, obj, null, fieldMap);
            }
          } else {
            result.add(destValue);
          }
        }
      } else {
        result.add(destValue);
      }
    }
    return result;
  }

  private List addOrUpdateToList(Object srcObj, FieldMap fieldMap, Collection sourceCollectionValue, Object destObj) {
    return addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, destObj, null);
  }

  private Object mapSetToArray(Object srcObj, Collection sourceCollectionValue, FieldMap fieldMap, Object destObj) {
    return mapListToArray(srcObj, sourceCollectionValue, fieldMap, destObj);
  }

  private List mapArrayToList(Object srcObj, Object sourceCollectionValue, FieldMap fieldMap, Object destObj) {
    Class destEntryType = null;
    if (fieldMap.getDestinationTypeHint() != null) {
      destEntryType = fieldMap.getDestinationTypeHint().getHint();
    } else {
      destEntryType = sourceCollectionValue.getClass().getComponentType();
    }
    List srcValueList = null;
    if (CollectionUtils.isPrimitiveArray(sourceCollectionValue.getClass())) {
      srcValueList = CollectionUtils.convertPrimitiveArrayToList(sourceCollectionValue);
    } else {
      srcValueList = Arrays.asList((Object[]) sourceCollectionValue);
    }
    return addOrUpdateToList(srcObj, fieldMap, srcValueList, destObj, destEntryType);
  }

  private void writeDestinationValue(Object destObj, Object destFieldValue, FieldMap fieldMap) {
    boolean bypass = false;
    // don't map null to dest field if map-null="false"
    if (destFieldValue == null && !fieldMap.getClassMap().getDestClassMapNull()) {
      bypass = true;
    }

    // don't map "" to dest field if map-empty-string="false"
    if (destFieldValue != null && destFieldValue.getClass().equals(String.class)
        && StringUtils.isEmpty((String) destFieldValue)
        && !fieldMap.getClassMap().getDestClassMapEmptyString()) {
      bypass = true;
    }

    if (!bypass) {
      eventMgr.fireEvent(new DozerEvent(MapperConstants.MAPPING_PRE_WRITING_DEST_VALUE, fieldMap.getClassMap(),
          fieldMap, null, destObj, destFieldValue));

      fieldMap.writeDestinationValue(destObj, destFieldValue);

      eventMgr.fireEvent(new DozerEvent(MapperConstants.MAPPING_POST_WRITING_DEST_VALUE, fieldMap.getClassMap(),
          fieldMap, null, destObj, destFieldValue));
    }
  }

  // TODO: possibly extract this to a seperate class
  private Object mapUsingCustomConverter(Class customConverterClass, Class srcFieldClass, Object srcFieldValue,
      Class destFieldClass, Object existingDestFieldValue, FieldMap fieldMap, boolean topLevel) {
    Object converterInstance = null;
    // search our injected customconverters for a match
    if (customConverterObjects != null) {
      for (int i = 0; i < customConverterObjects.size(); i++) {
        Object customConverter = (Object) customConverterObjects.get(i);
        if (customConverter.getClass().isAssignableFrom(customConverterClass)) {
          // we have a match
          converterInstance = customConverter;
        }
      }
    }
    // if converter object instances were not injected, then create new instance
    // of the converter for each conversion
    if (converterInstance == null) {
      converterInstance = ReflectionUtils.newInstance(customConverterClass);
    }
    if (!(converterInstance instanceof CustomConverter)) {
      MappingUtils.throwMappingException("Custom Converter does not implement CustomConverter interface");
    }
    CustomConverter theConverter = (CustomConverter) converterInstance;
    // if this is a top level mapping the destObj is the highest level
    // mapping...not a recursive mapping
    if (topLevel) {
      return theConverter.convert(existingDestFieldValue, srcFieldValue, destFieldClass, srcFieldClass);
    }
    Object field = determineExistingValue(fieldMap, existingDestFieldValue, destFieldClass);

    long start = System.currentTimeMillis();
    Object result = theConverter.convert(field, srcFieldValue, destFieldClass, srcFieldClass);
    long stop = System.currentTimeMillis();

    statsMgr.increment(StatisticTypeConstants.CUSTOM_CONVERTER_SUCCESS_COUNT);
    statsMgr.increment(StatisticTypeConstants.CUSTOM_CONVERTER_TIME, stop - start);

    return result;
  }

  private Set checkForSuperTypeMapping(Class sourceClass, Class destClass, ClassMap mapping) {
    // Check cache first
    Object cacheKey = CacheKeyFactory.createKey(new Object[] { destClass, sourceClass });
    CacheEntry cacheEntry = superTypeCache.get(cacheKey);
    if (cacheEntry != null) {
      return (Set) cacheEntry.getValue();
    }

    Set superClasses = new HashSet();

    // Fix for bug # [ 1486105 ] Inheritance mapping not working correctly
    // We were not creating a default configuration if we found a parent level class map
    if (mapping.getSrcClassToMap() != sourceClass || mapping.getDestClassToMap() != destClass) {
      // there are fields from the source object to dest class we might be missing. create a default mapping between the
      // two.
      mapping = ClassMapBuilder.createDefaultClassMap(globalConfiguration, sourceClass, destClass);
      superClasses.add(mapping);
    }

    // If no existing cache entry is found, determine super type mapping and
    // store in cache
    Class superSourceClass = sourceClass.getSuperclass();
    Class superDestClass = destClass.getSuperclass();
    boolean stillHasSuperClasses = true;
    while (stillHasSuperClasses) {
      if ((superSourceClass != null && !superSourceClass.getName().equals("java.lang.Object"))
          || (superDestClass != null && !superDestClass.getName().equals("java.lang.Object"))) {
        // see if the source super class is mapped to the dest class
        ClassMap superClassMap = (ClassMap) customMappings.get(ClassMapKeyFactory
            .createKey(superSourceClass, destClass));
        if (superClassMap != null) {
          superClasses.add(superClassMap);
        }
        // now walk up the dest classes super classes with our super
        // source class and our source class
        superDestClass = destClass.getSuperclass();
        boolean stillHasDestSuperClasses = true;
        while (stillHasDestSuperClasses) {
          if (superDestClass != null && !superDestClass.getName().equals("java.lang.Object")) {
            ClassMap superDestClassMap = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(superSourceClass,
                superDestClass));
            if (superDestClassMap != null) {
              superClasses.add(superDestClassMap);
            }
            ClassMap sourceClassMap = (ClassMap) customMappings.get(ClassMapKeyFactory.createKey(sourceClass,
                superDestClass));
            if (sourceClassMap != null) {
              superClasses.add(sourceClassMap);
            }
            superDestClass = superDestClass.getSuperclass();
          } else {
            break;
          }
        }
        superSourceClass = superSourceClass.getSuperclass();
      } else {
        break;
      }
    }// while

    // Do we still need to reverse? I have no idea...
    // We reversed because of this bug:
    // http://sourceforge.net/tracker/index.php?func=detail&aid=1346370&group_id=133517&atid=727368
    // [ 1346370 ] multiple levels of custom mapping processed in wrong order
    // Collections.reverse(superClasses);
    // All the test cases pass...
    // Add to cache
    cacheEntry = new CacheEntry(cacheKey, (Set) superClasses);
    superTypeCache.put(cacheEntry);

    return superClasses;
  }

  private List processSuperTypeMapping(Set superClasses, Object sourceObj, Object destObj, Class sourceClass,
      FieldMap parentFieldMap) {
    List fieldNamesList = new ArrayList();
    Object[] superClassArray = superClasses.toArray();
    for (int a = 0; a < superClassArray.length; a++) {
      ClassMap map = (ClassMap) superClassArray[a];
      map(map, sourceObj, destObj, map, parentFieldMap);
      List fieldMaps = map.getFieldMaps();
      for (int i = 0; i < fieldMaps.size(); i++) {
        FieldMap fieldMapping = (FieldMap) fieldMaps.get(i);
        if (!(fieldMapping instanceof MapFieldMap)) {
          String parentSourceField = null;
          if (parentFieldMap != null) {
            parentSourceField = parentFieldMap.getSrcFieldName();
          }
          String key = MappingUtils.getParentFieldNameKey(parentSourceField, sourceObj, sourceClass.getName(),
              fieldMapping.getSrcFieldName(), fieldMapping.getDestFieldName());
          if (fieldNamesList.contains(key)) {
            continue;
          } else {
            fieldNamesList.add(key);
          }
        }
      }
    }
    return fieldNamesList;
  }
  
  public static Object determineExistingValue(FieldMap fieldMap, Object destObj, Class destFieldType) {
    Object field = null;
    // verify that the dest obj is not null
    if (destObj == null) {
      return null;
    }
    if (!(fieldMap instanceof MapFieldMap)) {
      // call the getXX method to see if the field is already
      // instantiated
      field = fieldMap.getDestinationValue(destObj);
    }
    // When we are recursing through a list we need to make sure
    // that we are not in the list
    // by checking the destFieldType
    if (field != null) {
      if (CollectionUtils.isList(field.getClass()) || CollectionUtils.isArray(field.getClass())
          || CollectionUtils.isSet(field.getClass()) || MappingUtils.isSupportedMap(field.getClass())) {
        if (!CollectionUtils.isList(destFieldType) && !CollectionUtils.isArray(destFieldType)
            && !CollectionUtils.isSet(destFieldType) && !MappingUtils.isSupportedMap(destFieldType)) {
          // this means the getXX field is a List but we
          // are actually trying to map one of its
          // elements
          field = null;
        }
      }
    }
    return field;
  }

  private ClassMap getClassMap(Object sourceObj, Class destClass, String mapId, boolean isInstance) {
    ClassMap mapping = ClassMapFinder.findClassMap(this.customMappings, sourceObj, destClass, mapId, isInstance);

    if (mapping == null) {
      // If mapId was specified and mapping was not found, then throw an exception
      if (!MappingUtils.isBlankOrNull(mapId)) {
        MappingUtils.throwMappingException("Class mapping not found for map-id: " + mapId);
      }

      // If mapping not found in exsting custom mapping collection, create default as an explicit mapping must not
      // exist.
      // The create default class map method will also add all default mappings that it can determine.
      mapping = ClassMapBuilder.createDefaultClassMap(globalConfiguration, sourceObj.getClass(), destClass);
      customMappings.put(ClassMapKeyFactory.createKey(sourceObj.getClass(), destClass), mapping);
    }

    return mapping;
  }
  
  

}
