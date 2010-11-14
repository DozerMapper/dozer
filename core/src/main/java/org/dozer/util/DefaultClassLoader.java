/*
 * Copyright 2005-2009 the original author or authors.
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
package org.dozer.util;

import org.apache.commons.lang.ClassUtils;

import java.net.URL;

/**
 *
 *
 * @author dmitry.buzdin
 * @since 5.1
 */
public class DefaultClassLoader implements DozerClassLoader {

  private final ResourceLoader resourceLoader = new ResourceLoader();

  public Class<?> loadClass(String className)  {
    Class<?> result = null;
    try {
      //Class caller = Reflection.getCallerClass(3); TODO OSGi fix - Move to specific implementation
    	result = ClassUtils.getClass(className);
    } catch (ClassNotFoundException e) {
      MappingUtils.throwMappingException(e);
    }
    return result;
  }

  public URL loadResource(String uri) {
    return resourceLoader.getResource(uri);
  }

}
