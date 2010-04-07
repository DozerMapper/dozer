/*
 * Copyright 2005-2010 the original author or authors.
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
package org.dozer.config;

/**
 * Internal constants file that holds all the keys to configurable properties in the dozer.properties file
 * 
 * @author tierney.matt
 */
public final class PropertyConstants {
  
  private PropertyConstants() {}

  public static final String STATISTICS_ENABLED = "dozer.statistics.enabled";
  public static final String CONVERTER_CACHE_MAX_SIZE = "dozer.cache.converter.by.dest.type.maxsize";
  public static final String SUPERTYPE_CACHE_MAX_SIZE = "dozer.cache.super.type.maxsize";
  public static final String AUTOREGISTER_JMX_BEANS = "dozer.autoregister.jmx.beans";
  public static final String EL_ENABLED = "dozer.el.enabled";

  // Bean Implementations
  public static final String CLASS_LOADER_BEAN = "org.dozer.util.DozerClassLoader";
  public static final String PROXY_RESOLVER_BEAN = "org.dozer.util.DozerProxyResolver";

}
