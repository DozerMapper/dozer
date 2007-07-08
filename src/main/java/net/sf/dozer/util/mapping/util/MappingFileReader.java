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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.sf.dozer.util.mapping.classmap.Mappings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal class that reads and parses a single custom mapping xml file into raw ClassMap objects.  Only intended for internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class MappingFileReader {
  private static final Log log = LogFactory.getLog(MappingFileReader.class);
  
  private final URL url;
  
  public MappingFileReader(URL url) {
    this.url = url;
  }
  
  public MappingFileReader(String fileName) {
    ResourceLoader loader = new ResourceLoader();
    url = loader.getResource(fileName);
  }
  
  public Mappings read() {
    Mappings result = null;
    InputStream stream = null;
    try {
      XMLParser parser = new XMLParser();
      stream = url.openStream();
      result = parser.parse(stream);
    } catch (Throwable e) {
      log.error("Error in loading dozer mapping file url: [" + url + "] : " + e);
      MappingUtils.throwMappingException(e);
    } finally {
      try {
        if (stream != null) {
          stream.close();
        }
      } catch (IOException e) {
        MappingUtils.throwMappingException(e);
      }
    }
    return result;
  }
  
}