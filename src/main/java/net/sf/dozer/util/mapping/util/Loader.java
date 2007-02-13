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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.dozer.util.mapping.MappingException;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class Loader {
  
  public URL getResource(String resource) {
    URL result = Thread.currentThread().getContextClassLoader().getResource(resource);
    
    // Could not find resource. Try with the classloader that loaded this class.
    if (result == null) {
      ClassLoader classLoader = Loader.class.getClassLoader(); 
      if(classLoader != null) {
        result = classLoader.getResource(resource);
      }
    }
    
    // Last ditch attempt searching classpath
    if (result == null) {
      result = ClassLoader.getSystemResource(resource);
    }
    
    //Patch to load mapping file from outside of classpath.
    if (resource.startsWith(MapperConstants.FILE_PREFIX)) {
      try {
        return new File(resource.substring(MapperConstants.FILE_PREFIX.length())).toURL();
      } catch (MalformedURLException e) {
        throw new MappingException(e);
      }
    }
    
    return result;
  }
  
}