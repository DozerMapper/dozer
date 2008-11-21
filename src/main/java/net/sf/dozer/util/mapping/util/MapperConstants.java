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
 * Internal constants file containing a variety of constants used throughout the code base. Only intended for internal
 * use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public final class MapperConstants {

  private MapperConstants() {
  }

  public static final String CURRENT_VERSION = "4.2.1";
  public static final boolean DEFAULT_WILDCARD_POLICY = true;
  public static final boolean DEFAULT_ERROR_POLICY = true;
  public static final boolean DEFAULT_MAP_NULL_POLICY = true;
  public static final boolean DEFAULT_MAP_EMPTY_STRING_POLICY = true;
  public static final boolean DEFAULT_TRIM_STRINGS_POLICY = false;
  public static final String RELATIONSHIP_CUMULATIVE = "cumulative";
  public static final String RELATIONSHIP_NON_CUMULATIVE = "non-cumulative";
  public static final String DEFAULT_RELATIONSHIP_TYPE_POLICY = RELATIONSHIP_CUMULATIVE;
  public static final String DTD_NAME = "dozerbeanmapping.dtd";
  public static final String DEFAULT_CONFIG_FILE = "dozer.properties";
  public static final String DEFAULT_MAPPING_FILE = "dozerBeanMapping.xml";
  public static final String DEFAULT_PATH_ROOT = "";
  public static final String FILE_PREFIX = "file:";
  public static final boolean DEFAULT_STATISTICS_ENABLED = false;
  public static final String CONFIG_FILE_SYS_PROP = "dozer.configuration"; // i.e)-Ddozer.configuration=somefile.properties
  public static final String DEBUG_SYS_PROP = "dozer.debug";// i.e)-Ddozer.debug=true
  public static final String ITERATE = "iterate";
  public static final String ONE_WAY = "one-way";
  public static final String DEEP_FIELD_DELIMITOR = ".";
  public static final String SELF_KEYWORD = "this";
  public static final String CGLIB_ID = "$$EnhancerByCGLIB$$";
  public static final String JAVASSIST_ID = "$$_javassist";
  
  // DozerBeanMapper instance caches
  public static final String CONVERTER_BY_DEST_TYPE_CACHE = "Converter By Destination Type Dozer Cache";
  public static final String SUPER_TYPE_CHECK_CACHE = "Super Type Mapping Dozer Cache";
  public static final int DEFAULT_CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE = 5000;
  public static final int DEFAULT_SUPER_TYPE_CHECK_CACHE_MAX_SIZE = 5000;

  // Supported Events
  public static final String MAPPING_STARTED_EVENT = "MAPPING_STARTED";
  public static final String MAPPING_PRE_WRITING_DEST_VALUE = "MAPPING_PRE_WRITING_DEST_VALUE";
  public static final String MAPPING_POST_WRITING_DEST_VALUE = "MAPPING_POST_WRITING_DEST_VALUE";
  public static final String MAPPING_FINISHED_EVENT = "MAPPING_FINISHED";

  public static final String XML_BEAN_FACTORY = "net.sf.dozer.util.mapping.factory.XMLBeanFactory";

  // JMX
  public static final boolean DEFAULT_AUTOREGISTER_JMX_BEANS = true;

}
