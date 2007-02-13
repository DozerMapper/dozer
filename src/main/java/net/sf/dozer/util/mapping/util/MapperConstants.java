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
package net.sf.dozer.util.mapping.util;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 *
 */
public abstract class MapperConstants {

  public static final String CURRENT_VERSION = "3.0";
  public static final boolean DEFAULT_WILDCARD_POLICY = true;
  public static final boolean DEFAULT_ERROR_POLICY = true;
  public static final boolean DEFAULT_MAP_NULL_POLICY = true;
  public static final boolean DEFAULT_MAP_EMPTY_STRING_POLICY = true;
  public static final String DTD_NAME = "dozerbeanmapping.dtd";
  public static final String DEFAULT_CONFIG_FILE = "dozer.properties";
  public static final String DEFAULT_MAPPING_FILE = "dozerBeanMapping.xml";
  public static final String DEFAULT_CONTEXT_FILE = "dozerContext.xml";
  public static final String DEFAULT_PATH_ROOT = "";
  public static final String FILE_PREFIX = "file:";
  public static final String DEFAULT_BEAN_FACTORY = "beanfactory";
  public static final boolean DEFAULT_STATISTICS_ENABLED = false;
  public static final String CONFIG_FILE_SYS_PROP = "dozer.configuration"; //i.e)-Ddozer.configuration=somefile.properties
  public static final String DEBUG_SYS_PROP="dozer.debug";//i.e)-Ddozer.debug=true
  public static final String DEBUG_FALSE="false";
  public static final String WILDCARD_POLICY_TRUE = "true";
  public static final String WILDCARD_POLICY_FALSE = "false";
  public static final String ITERATE = "iterate";  
  public static final String ONE_WAY = "one-way";
  public static final String DEEP_FIELD_DELIMITOR = ".";
  public static final String INDEXED_FIELD_DELIMITOR = "[";
  public static final String RELATIONSHIP_CUMULATIVE = "cumulative";
  public static final String RELATIONSHIP_NON_CUMULATIVE = "non-cumulative";
  public static final String GLOBAL_CONFIGURATION = "globalConfiguration";
  public static final String SELF_KEYWORD = "this";
    
  // Parsing Elements
  public static final String CONFIGURATION_ELEMENT = "configuration";
  public static final String STOP_ON_ERRORS_ELEMENT = "stop-on-errors";
  public static final String DATE_FORMAT_ELEMENT = "date-format";
  public static final String WILDCARD_ELEMENT = "wildcard";
  public static final String CUSTOM_CONVERTERS_ELEMENT = "custom-converters";
  public static final String COPY_BY_REFERENCES_ELEMENT = "copy-by-references";
  public static final String COPY_BY_REFERENCE_ELEMENT = "copy-by-reference";
  public static final String CONVERTER_ELEMENT = "converter";
  public static final String CLASS_A_ELEMENT = "class-a";
  public static final String CLASS_B_ELEMENT = "class-b";
  public static final String MAPPING_ELEMENT = "mapping";
  public static final String FIELD_ELEMENT = "field";
  public static final String FIELD_EXCLUDE_ELEMENT = "field-exclude";
  public static final String A_ELEMENT = "a";
  public static final String B_ELEMENT = "b";
  public static final String SOURCE_TYPE_HINT_ELEMENT = "a-hint";
  public static final String DESTINATION_TYPE_HINT_ELEMENT = "b-hint";
  public static final String BEAN_FACTORY_ELEMENT = "bean-factory";
  public static final String IS_ACCESSIBLE_ELEMENT = "is-accessible";
  public static final String ALLOWED_EXCEPTIONS_ELEMENT = "allowed-exceptions";
  public static final String ALLOWED_EXCEPTION_ELEMENT = "exception";
  
  // Parsing Attributes
  public static final String TYPE_ATTRIBUTE = "type";
  public static final String WILDCARD_ATTRIBUTE = "wildcard";
  public static final String DATE_FORMAT_ATTRIBUTE = "date-format";
  public static final String RELATIONSHIP_TYPE_ATTRIBUTE = "relationship-type";
  public static final String COPY_BY_REFERENCE_ATTRIBUTE = "copy-by-reference";
  public static final String THE_SET_METHOD_ATTRIBUTE = "set-method";
  public static final String THE_GET_METHOD_ATTRIBUTE = "get-method";
  public static final String STOP_ON_ERRORS_ATTRIBUTE = "stop-on-errors";
  public static final String MAPID_ATTRIBUTE = "map-id";
  public static final String MAP_SET_METHOD_ATTRIBUTE = "map-set-method";
  public static final String MAP_GET_METHOD_ATTRIBUTE = "map-get-method";
  public static final String KEY_ATTRIBUTE = "key";
  public static final String BEAN_FACTORY_ATTRIBUTE = "bean-factory";
  public static final String FACTORY_BEANID_ATTRIBUTE = "factory-bean-id";
  public static final String IS_ACCESSIBLE_ATTRIBUTE = "is-accessible";
  public static final String CREATE_METHOD_ATTRIBUTE = "create-method";
  public static final String MAP_NULL_ATTRIBUTE = "map-null";
  public static final String MAP_EMPTY_STRING_ATTRIBUTE = "map-empty-string";
  public static final String CUSTOM_CONVERTER_ATTRIBUTE = "custom-converter";

  //DozerBeanMapper instance caches
  public static final String CONVERTER_BY_DEST_TYPE_CACHE = "Converter By Destination Type Dozer Cache";
  public static final String SUPER_TYPE_CHECK_CACHE = "Super Type Mapping Dozer Cache";
  public static final int DEFAULT_CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE = 5000;
  public static final int DEFAULT_SUPER_TYPE_CHECK_CACHE_MAX_SIZE = 5000;

  //Supported Events
  public static final String MAPPING_STARTED_EVENT = "MAPPING_STARTED";
  public static final String MAPPING_PRE_WRITING_DEST_VALUE = "MAPPING_PRE_WRITING_DEST_VALUE";
  public static final String MAPPING_POST_WRITING_DEST_VALUE = "MAPPING_POST_WRITING_DEST_VALUE";
  public static final String MAPPING_FINISHED_EVENT = "MAPPING_FINISHED";
  
}
