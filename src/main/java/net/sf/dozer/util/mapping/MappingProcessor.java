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
import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.converters.CustomConverter;
import net.sf.dozer.util.mapping.converters.PrimitiveOrWrapperConverter;
import net.sf.dozer.util.mapping.event.DozerEvent;
import net.sf.dozer.util.mapping.event.DozerEventManager;
import net.sf.dozer.util.mapping.event.EventManagerIF;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.Configuration;
import net.sf.dozer.util.mapping.fieldmap.ExcludeFieldMap;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.GenericFieldMap;
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
 * Internal Mapping Engine.  Not intended for direct use by Application code.
 * This class does most of the heavy lifting and is very recursive in nature.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class MappingProcessor implements MapperIF {

  private static final Log log = LogFactory.getLog(MappingProcessor.class);

  private List superListOfFieldNames = null;
  private final transient Map customMappings;
  private final Configuration globalConfiguration;
  private final List customConverterObjects;// actual converter object instances
  private final StatisticsManagerIF statsMgr;
  private final EventManagerIF eventMgr;
  private final CustomFieldMapperIF customFieldMapper;
  private final Map mappedFields = new HashMap();
  private final Cache converterByDestTypeCache;
  private final Cache superTypeCache;
  private final PrimitiveOrWrapperConverter primitiveOrWrapperConverter = new PrimitiveOrWrapperConverter();
  
  //The stored factories don't belong in MappingUtils and need to be relocated
  private final DestBeanCreator destBeanCreator = new DestBeanCreator(MappingUtils.storedFactories);

  protected MappingProcessor(Map customMappings, Configuration globalConfiguration, CacheManagerIF cacheMgr,
      StatisticsManagerIF statsMgr, List customConverterObjects, List eventListeners, CustomFieldMapperIF customFieldMapper) {
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

  private void map(ClassMap classMap, Object sourceObj, Object destObj, ClassMap parentClassMap, FieldMap parentFieldMap)
      throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    MappingValidator.validateMappingRequest(sourceObj, destObj);

    // 1596766 - Recursive object mapping issue. Prevent recursive mapping
    // infinite loop
    // Keep a record of mapped fields by storing the id of the sourceObj and the
    // destObj to be mapped
    // This can be referred to later to avoid recursive mapping loops
    String key = MappingUtils.getMappedFieldKey(sourceObj);
    mappedFields.put(key, destObj);

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
      if (superClasses != null && superClasses.size() > 0) {
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
      mapField(fieldMapping, classMap, sourceObj, destObj, parentClassMap, parentFieldMap, parentFieldNames);
    }
  }

  private void mapField(FieldMap fieldMapping, ClassMap classMap, Object sourceObj, Object destObj,
      ClassMap parentClassMap, FieldMap parentFieldMap, List parentFieldNames) {

    // The field has been explicitly excluded from mapping. So just return, as
    // no further processing is needed for this field
    if (fieldMapping instanceof ExcludeFieldMap) {
      return;
    }

    Class sourceClass = sourceObj.getClass();
    Class destClass = destObj.getClass();
    Object sourceFieldValue = null;
    try {
      // check for super class names
      String parentSourceField = null;
      if (parentFieldMap != null) {
        parentSourceField = parentFieldMap.getSourceField().getName();
      }

      String key = MappingUtils.getParentFieldNameKey(parentSourceField, sourceObj, sourceClass.getName(),
          fieldMapping.getSourceField().getName(), fieldMapping.getDestField().getName());
      if (parentClassMap != null) {
        if (superListOfFieldNames.contains(key)) {
          return;
        } else {
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
        fieldMapped = customFieldMapper.mapField(sourceObj, destObj, sourceFieldValue, classMap, fieldMapping);
      }

      if (!fieldMapped) {
        if (fieldMapping instanceof GenericFieldMap && ((GenericFieldMap) fieldMapping).isMethodMap()
            && fieldMapping.getDestField().getType().equals(MapperConstants.ITERATE)) {
          // special logic for iterate feature
          mapFromIterateMethodFieldMap(sourceObj, destObj, sourceFieldValue, classMap, fieldMapping);
        } else {
          // either deep field map or generic map. The is the most likely scenario
          mapFromFieldMap(sourceObj, destObj, sourceFieldValue, classMap, fieldMapping);
        }
      }

      statsMgr.increment(StatisticTypeConstants.FIELD_MAPPING_SUCCESS_COUNT);

    } catch (Throwable e) {
      log.error(LogMsgFactory.createFieldMappingErrorMsg(sourceObj, fieldMapping, sourceFieldValue, destObj, e), e);
      statsMgr.increment(StatisticTypeConstants.FIELD_MAPPING_FAILURE_COUNT);

      // check error handling policy.
      if (classMap.getStopOnErrors()) {
        MappingUtils.throwMappingException(e);
      } else {
        // check if any Exceptions should be allowed to be thrown
        if (!classMap.getAllowedExceptions().isEmpty()) {
          if (e.getCause() instanceof InvocationTargetException) {
            Throwable thrownType = ((InvocationTargetException) e.getCause()).getTargetException();
            Class exceptionClass = thrownType.getClass();
            if (classMap.getAllowedExceptions().contains(exceptionClass)) {
              throw (RuntimeException) thrownType;
            }
          }
        }
        statsMgr.increment(StatisticTypeConstants.FIELD_MAPPING_FAILURE_IGNORED_COUNT);
      }
    }
  }

  private void mapFromFieldMap(Object sourceObj, Object destObj, Object sourceFieldValue, ClassMap classMap,
      FieldMap fieldMapping) throws IllegalAccessException, InvocationTargetException, InvocationTargetException,
      InstantiationException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, NoSuchFieldException {
    Class destFieldType = null;
    // methodmap logic should be encapsulated and figured out at the fieldmap level
    if (fieldMapping instanceof GenericFieldMap && ((GenericFieldMap) fieldMapping).isMethodMap()) {
      destFieldType = fieldMapping.getDestFieldWriteMethod(destObj.getClass()).getParameterTypes()[0];
    } else {
      destFieldType = fieldMapping.getDestFieldType(destObj.getClass());
    }

    // 1476780 - 12/2006 mht - Add support for field level custom converters
    // Use field level custom converter if one was specified. Otherwise, map or
    // recurse the object as normal
    Object destFieldValue = null;
    if (MappingUtils.isBlankOrNull(fieldMapping.getCustomConverter())) {
      destFieldValue = mapOrRecurseObject(sourceObj, sourceFieldValue, destFieldType, classMap, fieldMapping, destObj);
    } else {
      Class sourceFieldClass = sourceFieldValue != null ? sourceFieldValue.getClass() : fieldMapping
          .getSourceFieldType(sourceObj.getClass());
      destFieldValue = mapUsingCustomConverter(Class.forName(fieldMapping.getCustomConverter()), sourceFieldClass,
          sourceFieldValue, destFieldType, destObj, fieldMapping, false);
    }

    writeDestinationValue(destObj, destFieldValue, classMap, fieldMapping);

    if (log.isDebugEnabled()) {
      log.debug(LogMsgFactory.createFieldMappingSuccessMsg(sourceObj.getClass(), destObj.getClass(), fieldMapping
          .getSourceField().getName(), fieldMapping.getDestField().getName(), sourceFieldValue, destFieldValue));
    }
  }

  // TODO there has to be a much better way then ignoreClassMap flag
  // this is related to testPropertyClassLevelMapBack unit test. transforming
  // String to Integer from HashMap
  private Object mapOrRecurseObject(Object srcObj, Object sourceFieldValue, Class destFieldType, ClassMap classMap,
      FieldMap fieldMap, Object destObj) throws InvocationTargetException, InstantiationException,
      IllegalAccessException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    return mapOrRecurseObject(srcObj, sourceFieldValue, destFieldType, classMap, fieldMap, destObj, false);
  }

  private Object mapOrRecurseObject(Object srcObj, Object sourceFieldValue, Class destFieldType, ClassMap classMap,
      FieldMap fieldMap, Object destObj, boolean ignoreClassMap) throws InvocationTargetException,
      InstantiationException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException,
      NoSuchFieldException {
    Class sourceFieldClass = sourceFieldValue != null ? sourceFieldValue.getClass() : fieldMap
        .getSourceFieldType(srcObj.getClass());
    Class converterClass = MappingUtils.determineCustomConverter(fieldMap, converterByDestTypeCache, classMap.getCustomConverters(),
        sourceFieldClass, destFieldType);

    // 1-2007 mht: Invoke custom converter even if the src value is null. #1563795
    if (converterClass != null) {
      return mapUsingCustomConverter(converterClass, sourceFieldClass, sourceFieldValue, destFieldType, destObj,
          fieldMap, false);
    }

    boolean isDestFieldTypeSupportedMap = MappingUtils.isSupportedMap(destFieldType);
    if (sourceFieldValue == null && !isDestFieldTypeSupportedMap) {
      return null;
    }

    // this is so we don't null out the entire Map with a null source value
    if (sourceFieldValue == null && isDestFieldTypeSupportedMap) {
      return mapMapToProperty(srcObj, sourceFieldValue, null, fieldMap, destObj, destFieldType, classMap);
    }

    // 1596766 - Recursive object mapping issue. Prevent recursive mapping infinite loop
    String key = MappingUtils.getMappedFieldKey(sourceFieldValue);
    Object value = mappedFields.get(key);
    if (value != null) {
      if (value.getClass().equals(destFieldType)) {
        // Source value has already been mapped to the required destFieldType.
        return value;
      }
      
      // 1658168 - Recursive mapping issue for interfaces 
      if (destFieldType.isInterface() && destFieldType.isAssignableFrom(value.getClass())) {
        // Source value has already been mapped to the required destFieldType.
        return value;
      } 
    }

    if (fieldMap.getCopyByReference()) {
      // just get the src and return it, no transformation.
      return sourceFieldValue;
    }
    // if the mapId is not null then it means we are mapping a Class Level Map
    // Backed Property. In the future the mapId might be used for other things...
    if (fieldMap.getMapId() != null && MappingUtils.validateMap(sourceFieldClass, destFieldType, fieldMap)) {
      return mapCustomObject(fieldMap, destObj, destFieldType, sourceFieldValue);
    }
    if (fieldMap instanceof MapFieldMap && !ignoreClassMap) {
      // we just take the source and set it on the Map - if we are mapping in
      // reverse we need to get the value off of the map
      return mapClassLevelMap(srcObj, fieldMap, sourceFieldValue, sourceFieldClass, classMap, destFieldType, destObj);
    }
    boolean isSourceFieldClassSupportedMap = MappingUtils.isSupportedMap(sourceFieldClass);
    if (isSourceFieldClassSupportedMap && isDestFieldTypeSupportedMap) {
      return mapMap(srcObj, sourceFieldValue, classMap, fieldMap, destObj, destFieldType);
    }
    if (isSourceFieldClassSupportedMap || isDestFieldTypeSupportedMap) {
      return mapMapToProperty(srcObj, sourceFieldValue, sourceFieldClass, fieldMap, destObj, destFieldType, classMap);
    }
    if (MappingUtils.isCustomMapMethod(fieldMap)) {
      return mapCustomMapToProperty(sourceFieldValue, sourceFieldClass, fieldMap, destObj, destFieldType);
    }
    if (MappingUtils.isPrimitiveOrWrapper(sourceFieldClass) || MappingUtils.isPrimitiveOrWrapper(destFieldType)) {
      // Primitive or Wrapper conversion
      if (fieldMap.getDestinationTypeHint() != null) {
        destFieldType = fieldMap.getDestinationTypeHint().getHint();
      }
      return primitiveOrWrapperConverter.convert(sourceFieldValue, destFieldType, new DateFormatContainer(classMap,
          fieldMap));
    }
    if (MappingUtils.isSupportedCollection(sourceFieldClass) && (MappingUtils.isSupportedCollection(destFieldType))) {
      return mapCollection(srcObj, sourceFieldValue, classMap, fieldMap, destObj);
    }
    if(GlobalSettings.getInstance().isJava5()) {
      if ( ((Boolean)Jdk5Methods.getInstance().getClassIsEnumMethod().invoke(sourceFieldClass, null)).booleanValue() && 
          ((Boolean) Jdk5Methods.getInstance().getClassIsEnumMethod().invoke(destFieldType, null)).booleanValue()) {
        return mapEnum(sourceFieldValue, destFieldType);
      }
    }
    // Default: Map from one custom data object to another custom data object
    return mapCustomObject(fieldMap, destObj, destFieldType, sourceFieldValue);
  }

  private Object mapEnum(Object sourceFieldValue, Class destFieldType) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    String name = (String) Jdk5Methods.getInstance().getEnumNameMethod().invoke(sourceFieldValue, null);
    return Jdk5Methods.getInstance().getEnumValueOfMethod().invoke(destFieldType, new Object[] {destFieldType, name});
  }
  
  private Object mapClassLevelMap(Object srcObj, FieldMap fieldMap, Object sourceFieldValue, Class sourceFieldClass,
      ClassMap classMap, Class destType, Object destObj) throws InvocationTargetException, IllegalAccessException,
      NoSuchFieldException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
    // TODO This should be encapsulated in MapFieldMap?
    if (!MappingUtils.isSupportedMap(sourceFieldClass) && !classMap.getSourceClass().isCustomMap()) {
      return sourceFieldValue;
    } else {
      String key = null;
      if (StringUtils.isEmpty(fieldMap.getDestField().getKey())) {
        key = fieldMap.getDestField().getName();
      } else {
        key = fieldMap.getDestField().getKey();
      }
      Method resultMethod = ReflectionUtils.getMethod(sourceFieldValue, fieldMap.getSourceField().getMapGetMethod());
      Object result = resultMethod.invoke(sourceFieldValue, new Object[] { key });
      return mapOrRecurseObject(srcObj, result, destType, classMap, fieldMap, destObj, true);
    }
  }

  private Object mapCustomObject(FieldMap fieldMap, Object destObj, Class destFieldType, Object sourceFieldValue)
      throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException,
      ClassNotFoundException, NoSuchFieldException {
    // Custom java bean. Need to make sure that the destination object is not
    // already instantiated.
    Object field = MappingValidator.validateField(fieldMap, destObj, destFieldType);
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
      if (fieldMap != null) {
        mapId = fieldMap.getMapId();
      }
      classMap = getClassMap(sourceFieldValue, destFieldType, mapId, false);
      field = destBeanCreator.create(sourceFieldValue, classMap, fieldMap, null);
    }

    map(classMap, sourceFieldValue, field, null, fieldMap);

    return field;
  }

  private Object mapCollection(Object srcObj, Object sourceCollectionValue, ClassMap classMap, FieldMap fieldMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    // since we are mapping some sort of collection now is a good time to
    // decide if they provided hints
    // if no hint is provided then we will check to see if we can use JDK 1.5
    // generics to determine the mapping type
    // this will only happen once on the dest hint. the next mapping will
    // already have the hint
    if (fieldMap.getDestinationTypeHint() == null) {
      if (GlobalSettings.getInstance().isJava5()) {
        Object typeArgument = null;
        Object[] parameterTypes = null;
        try {
          Method method = fieldMap.getDestFieldWriteMethod(destObj.getClass());
          parameterTypes = (Object[]) Jdk5Methods.getInstance().getMethodGetGenericParameterTypesMethod().invoke(method, null);
        } catch (Exception e) {
          log.info("The destObj:" + destObj + " does not have a write method");
        }
        if (parameterTypes != null) {
          Class parameterTypesClass = Jdk5Methods.getInstance().getParameterizedTypeClass();

          if (parameterTypesClass.isAssignableFrom(parameterTypes[0].getClass())) {
            
            typeArgument = ((Object[])Jdk5Methods.getInstance().getParamaterizedTypeGetActualTypeArgsMethod().invoke(parameterTypes[0], null))[0];
            if (typeArgument != null) {
              Hint destHint = new Hint();
              Class argument = (Class) typeArgument;
              destHint.setHintName(argument.getName());
              fieldMap.setDestinationTypeHint(destHint);
            }
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
      result = mapArrayToArray(srcObj, sourceCollectionValue, classMap, fieldMap, destObj);
      // Array to List
    } else if (CollectionUtils.isArray(sourceFieldType) && (CollectionUtils.isList(destCollectionType))) {
      result = mapArrayToList(srcObj, sourceCollectionValue, classMap, fieldMap, destObj);
    }
    // List to Array
    else if (CollectionUtils.isList(sourceFieldType) && (CollectionUtils.isArray(destCollectionType))) {
      result = mapListToArray(srcObj, (List) sourceCollectionValue, classMap, fieldMap, destObj);
      // List to List
    } else if (CollectionUtils.isList(sourceFieldType) && (CollectionUtils.isList(destCollectionType))) {
      result = mapListToList(srcObj, (List) sourceCollectionValue, classMap, fieldMap, destObj);
    }
    // Set to Array
    else if (CollectionUtils.isSet(sourceFieldType) && CollectionUtils.isArray(destCollectionType)) {
      result = mapSetToArray(srcObj, (Set) sourceCollectionValue, classMap, fieldMap, destObj);
    }
    // Array to Set
    else if (CollectionUtils.isArray(sourceFieldType) && CollectionUtils.isSet(destCollectionType)) {
      result = addToSet(srcObj, fieldMap, Arrays.asList((Object[]) sourceCollectionValue), classMap, destObj);
    }
    // Set to List
    else if (CollectionUtils.isSet(sourceFieldType) && CollectionUtils.isList(destCollectionType)) {
      result = mapListToList(srcObj, (Set) sourceCollectionValue, classMap, fieldMap, destObj);
    }
    // Collection to Set
    else if (CollectionUtils.isCollection(sourceFieldType) && CollectionUtils.isSet(destCollectionType)) {
      result = addToSet(srcObj, fieldMap, (Collection) sourceCollectionValue, classMap, destObj);
    }
    return result;
  }

  private Object mapMap(Object srcObj, Object sourceMapValue, ClassMap classMap, FieldMap fieldMap, Object destObj,
      Class destFieldType) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    Map result = null;
    Object field = fieldMap.doesFieldExist(destObj, destFieldType);
    if (field == null) {
      // no destination map exists
      result = (Map) sourceMapValue.getClass().newInstance();
    } else {
      result = (Map) field;
    }
    Map sourceMap = (Map) sourceMapValue;
    Set sourceEntrySet = sourceMap.entrySet();
    Iterator iter = sourceEntrySet.iterator();
    while (iter.hasNext()) {
      Map.Entry sourceEntry = (Map.Entry) iter.next();
      Object sourceEntryValue = sourceEntry.getValue();
      Object destEntryValue = mapOrRecurseObject(srcObj, sourceEntryValue, sourceEntryValue.getClass(), classMap,
          fieldMap, destObj);
      Object obj = result.get(sourceEntry.getKey());
      if (obj != null && obj.equals(destEntryValue) && fieldMap.isGenericFieldMap()
          && MapperConstants.RELATIONSHIP_NON_CUMULATIVE.equals(((GenericFieldMap) fieldMap).getRelationshipType())) {
        if (!(obj instanceof String)) {
          map(null, sourceEntryValue, obj, null, fieldMap);
        }
      } else {
        result.put(sourceEntry.getKey(), destEntryValue);
      }

    }
    return result;
  }

  private Object mapMapToProperty(Object srcObj, Object sourceValue, Class sourceFieldClass, FieldMap fieldMap,
      Object destObj, Class destType, ClassMap classMap) throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    Object srcFieldValue = null;
    String key = null;

    // determine which field is the Map
    if (MappingUtils.isSupportedMap(destType)) {
      Object field = fieldMap.doesFieldExist(destObj, destType);
      // make sure we don't null out the whole Map
      if (sourceFieldClass == null) {
        return field;
      }
      if (field == null) {
        if (fieldMap.getDestinationTypeHint() != null) {
          // if we have a hint we can instantiate correct object
          destType = fieldMap.getDestinationTypeHint().getHint();
        } else if (destType.isInterface()) {
          // if there is no hint we assume it is a HashMap
          destType = HashMap.class;
        }
        // destType could also be a concrete implementation
        srcFieldValue = destType.newInstance();
      } else {
        srcFieldValue = field;
      }
      key = fieldMap.getDestKey();
      ((Map) srcFieldValue).put(key, sourceValue);
    } else {
      key = fieldMap.getSourceKey();
      srcFieldValue = ((Map) sourceValue).get(key);
    }
    return mapOrRecurseObject(srcObj, srcFieldValue, destType, classMap, fieldMap, destObj);
  }

  private Object mapCustomMapToProperty(Object sourceValue, Class sourceFieldClass, FieldMap fieldMap, Object destObj,
      Class destType) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    Object result = null;
    // determine which field is the Map
    if (fieldMap.getDestField().getMapGetMethod() != null && fieldMap.getDestField().getMapSetMethod() != null) {
      Object field = fieldMap.doesFieldExist(destObj, destType);
      // make sure we don't null out the whole Map
      if (sourceFieldClass == null) {
        return field;
      }
      if (field == null) {
        if (fieldMap.getDestinationTypeHint() != null) {
          // if we have a hint we can instantiate correct object
          destType = fieldMap.getDestinationTypeHint().getHint();
        }
        // destType could also be a concrete implementation
        result = destType.newInstance();
      } else {
        result = field;
      }
      String key = fieldMap.getDestKey();
      Method resultMethod = ReflectionUtils.getMethod(result, fieldMap.getDestField().getMapSetMethod());
      resultMethod.invoke(result, new Object[] { key, sourceValue });
    } else {
      String key = fieldMap.getSourceKey();
      Method resultMethod = ReflectionUtils.getMethod(sourceValue, fieldMap.getSourceField().getMapGetMethod());
      result = resultMethod.invoke(sourceValue, new Object[] { key });
    }
    return result;
  }

  private Object mapArrayToArray(Object srcObj, Object sourceCollectionValue, ClassMap classMap, FieldMap fieldMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    Class destEntryType = fieldMap.getDestFieldType(destObj.getClass()).getComponentType();
    int size = Array.getLength(sourceCollectionValue);
    if (CollectionUtils.isPrimitiveArray(sourceCollectionValue.getClass())) {
      return addToPrimitiveArray(srcObj, fieldMap, size, sourceCollectionValue, classMap, destObj, destEntryType);
    } else {
      List list = Arrays.asList((Object[]) sourceCollectionValue);
      List returnList = null;
      if (!destEntryType.getName().equals("java.lang.Object")) {
        returnList = addOrUpdateToList(srcObj, fieldMap, list, classMap, destObj, destEntryType);
      } else {
        returnList = addOrUpdateToList(srcObj, fieldMap, list, classMap, destObj, null);
      }
      return CollectionUtils.convertListToArray(returnList, destEntryType);
    }
  }

  private void mapFromIterateMethodFieldMap(Object sourceObj, Object destObj, Object sourceFieldValue,
      ClassMap classMap, FieldMap fieldMapping) throws IllegalAccessException, InvocationTargetException,
      InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchMethodException,
      NoSuchFieldException {
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
          throw new MappingException("<field type=\"iterate\"> must have a source or destination type hint");
        }
        // check for custom converters
        Class converterClass = MappingUtils.findCustomConverter(converterByDestTypeCache, classMap.getCustomConverters(), value
            .getClass(), Thread.currentThread().getContextClassLoader().loadClass(
            fieldMapping.getDestinationTypeHint().getHintName()));

        if (converterClass != null) {
          Class sourceFieldClass = sourceFieldValue != null ? sourceFieldValue.getClass() : fieldMapping
              .getSourceFieldType(sourceObj.getClass());
          value = mapUsingCustomConverter(converterClass, sourceFieldClass, value, fieldMapping
              .getDestinationTypeHint().getHint(), null, fieldMapping, false);
        } else {
          value = map(value, fieldMapping.getDestinationTypeHint().getHint());
        }
        if (value != null) {
          writeDestinationValue(destObj, value, classMap, fieldMapping);
        }
      }
    }
    if (log.isDebugEnabled()) {
      log.debug(LogMsgFactory.createFieldMappingSuccessMsg(sourceObj.getClass(), destObj.getClass(), fieldMapping
          .getSourceField().getName(), fieldMapping.getDestField().getName(), sourceFieldValue, null));
    }
  }

  private Object addToPrimitiveArray(Object srcObj, FieldMap fieldMap, int size, Object sourceCollectionValue,
      ClassMap classMap, Object destObj, Class destEntryType) throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {

    Object result = null;
    Object field = fieldMap.doesFieldExist(destObj, destEntryType);
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
      Object toValue = mapOrRecurseObject(srcObj, Array.get(sourceCollectionValue, i), destEntryType, classMap,
          fieldMap, destObj);
      Array.set(result, arraySize, toValue);
      arraySize++;
    }
    return result;
  }

  private Object mapListToArray(Object srcObj, Collection sourceCollectionValue, ClassMap classMap, FieldMap fieldMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    List list = null;
    Class destEntryType = fieldMap.getDestFieldType(destObj.getClass()).getComponentType();

    if (!destEntryType.getName().equals("java.lang.Object")) {
      list = addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, classMap, destObj, destEntryType);
    } else {
      list = addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, classMap, destObj);
    }
    return CollectionUtils.convertListToArray(list, destEntryType);
  }

  private List mapListToList(Object srcObj, Collection sourceCollectionValue, ClassMap classMap, FieldMap fieldMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    return addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, classMap, destObj);
  }

  private Set addToSet(Object srcObj, FieldMap fieldMap, Collection sourceCollectionValue, ClassMap classMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    Class destEntryType = null;
    ListOrderedSet result = new ListOrderedSet();
    // don't want to create the set if it already exists.
    Object field = fieldMap.doesFieldExist(destObj, null);
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
      destValue = mapOrRecurseObject(srcObj, sourceValue, destEntryType, classMap, fieldMap, destObj);
      if (fieldMap.isGenericFieldMap()) {
        GenericFieldMap gfm = (GenericFieldMap) fieldMap;
        if (gfm.getRelationshipType() == null
            || gfm.getRelationshipType().equals(MapperConstants.RELATIONSHIP_CUMULATIVE)) {
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

  private List addOrUpdateToList(Object srcObj, FieldMap fieldMap, Collection sourceCollectionValue, ClassMap classMap,
      Object destObj, Class destEntryType) throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {

    List result = null;
    // don't want to create the list if it already exists.
    // these maps are special cases which do not fall under what we are looking for
    Object field = fieldMap.doesFieldExist(destObj, destEntryType);
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
      destValue = mapOrRecurseObject(srcObj, sourceValue, destEntryType, classMap, fieldMap, destObj);
      prevDestEntryType = destEntryType;
      if (fieldMap.isGenericFieldMap()) {
        GenericFieldMap gfm = (GenericFieldMap) fieldMap;
        if (gfm.getRelationshipType() == null
            || gfm.getRelationshipType().equals(MapperConstants.RELATIONSHIP_CUMULATIVE)) {
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

  private List addOrUpdateToList(Object srcObj, FieldMap fieldMap, Collection sourceCollectionValue, ClassMap classMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    return addOrUpdateToList(srcObj, fieldMap, sourceCollectionValue, classMap, destObj, null);
  }

  private Object mapSetToArray(Object srcObj, Collection sourceCollectionValue, ClassMap classMap, FieldMap fieldMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
    return mapListToArray(srcObj, sourceCollectionValue, classMap, fieldMap, destObj);
  }

  private List mapArrayToList(Object srcObj, Object sourceCollectionValue, ClassMap classMap, FieldMap fieldMap,
      Object destObj) throws IllegalAccessException, InstantiationException, InvocationTargetException,
      NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
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
    return addOrUpdateToList(srcObj, fieldMap, srcValueList, classMap, destObj, destEntryType);
  }

  private void writeDestinationValue(Object destObj, Object destFieldValue, ClassMap classMap, FieldMap fieldMapping)
      throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException,
      ClassNotFoundException, NoSuchFieldException {
    boolean bypass = false;
    // don't map null to dest field if map-null="false"
    if (destFieldValue == null && !classMap.getDestClass().getMapNull().booleanValue()) {
      bypass = true;
    }

    // don't map "" to dest field if map-empty-string="false"
    if (destFieldValue != null && destFieldValue.getClass().equals(String.class)
        && StringUtils.isEmpty((String) destFieldValue) && !classMap.getDestClass().getMapEmptyString().booleanValue()) {
      bypass = true;
    }

    if (!bypass) {
      eventMgr.fireEvent(new DozerEvent(MapperConstants.MAPPING_PRE_WRITING_DEST_VALUE, classMap, fieldMapping, null,
          destObj, destFieldValue));

      fieldMapping.writeDestinationValue(destObj, destFieldValue, classMap);

      eventMgr.fireEvent(new DozerEvent(MapperConstants.MAPPING_POST_WRITING_DEST_VALUE, classMap, fieldMapping, null,
          destObj, destFieldValue));
    }
  }

  private Object mapUsingCustomConverter(Class customConverterClass, Class srcFieldClass, Object srcFieldValue,
      Class destFieldClass, Object existingDestFieldValue, FieldMap fieldMap, boolean topLevel) throws IllegalAccessException,
      InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException,
      NoSuchFieldException {
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
      converterInstance = customConverterClass.newInstance();
    }
    if (!(converterInstance instanceof CustomConverter)) {
      throw new MappingException("Custom Converter does not implement CustomConverter interface");
    }
    CustomConverter theConverter = (CustomConverter) converterInstance;
    // if this is a top level mapping the destObj is the highest level
    // mapping...not a recursive mapping
    if (topLevel) {
      return theConverter.convert(existingDestFieldValue, srcFieldValue, destFieldClass, srcFieldClass);
    }
    Object field = MappingValidator.validateField(fieldMap, existingDestFieldValue, destFieldClass);
    
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
    if(mapping.getSourceClass().getClassToMap() != sourceClass
       || mapping.getDestClass().getClassToMap() != destClass) {
    	// there are fields from the source object to dest class we might be missing. create a default mapping between the two.
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
      FieldMap parentFieldMap) throws NoSuchMethodException, NoSuchFieldException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException, InstantiationException {
    List fieldNamesList = new ArrayList();
    Object[] superClassArray = superClasses.toArray();
    for (int a = 0; a < superClassArray.length; a++) {
      ClassMap map = (ClassMap) superClassArray[a];
      map(map, sourceObj, destObj, map, parentFieldMap);
      List fieldMaps = map.getFieldMaps();
      Class destClassToMap = destObj.getClass();
      Class srcClassToMap = sourceObj.getClass();
      for (int i = 0; i < fieldMaps.size(); i++) {
        FieldMap fieldMapping = (FieldMap) fieldMaps.get(i);
        if (!fieldMapping.isGenericFieldMap() && !(fieldMapping instanceof ExcludeFieldMap)) {
          // do nothing
        } else {
          String parentSourceField = null;
          if (parentFieldMap != null) {
            parentSourceField = parentFieldMap.getSourceField().getName();
          }
          String key = MappingUtils.getParentFieldNameKey(parentSourceField, sourceObj, sourceClass.getName(),
              fieldMapping.getSourceField().getName(), fieldMapping.getDestField()
                  .getName());
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

  private ClassMap getClassMap(Object sourceObj, Class destClass, String mapId, boolean isInstance) {
    ClassMap mapping = ClassMapFinder.findClassMap(this.customMappings, sourceObj, destClass, mapId, isInstance);
    
    if (mapping == null) {
      //If mapId was specified and mapping was not found, then throw an exception
      if (!MappingUtils.isBlankOrNull(mapId)) {
        throw new MappingException("Class mapping not found for map-id: " + mapId);
      }

      // If mapping not found in exsting custom mapping collection, create default as an explicit mapping must not exist.
      // The create default class map method will also add all default mappings that it can determine.
      mapping = ClassMapBuilder.createDefaultClassMap(globalConfiguration, sourceObj.getClass(), destClass);
      customMappings.put(ClassMapKeyFactory.createKey(sourceObj.getClass(), destClass), mapping);
    }

    return mapping;
  }

}
