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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Internal EntityResolver implementation for the dozer mappings DTD, to load the DTD from the dozer classpath resp. JAR file.
 * 
 * <p>
 * Fetches "dozerbeanmapping.dtd" from the classpath resource "/dozerbeanmapping.dtd", no matter if specified as some
 * local URL or as "http://dozer.sourceforge.net/dtd/dozerbeanmapping.dtd".
 * 
 * @author garsombke.franz
 */
public class DozerResolver implements EntityResolver {

  private static final Log log = LogFactory.getLog(DozerResolver.class);

  public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
    log.debug("Trying to resolve XML entity with public ID [" + publicId + "] and system ID [" + systemId + "]");
    if (systemId != null && systemId.indexOf(MapperConstants.DTD_NAME) > systemId.lastIndexOf("/")) {
      String dtdFile = systemId.substring(systemId.indexOf(MapperConstants.DTD_NAME));
      log.debug("Trying to locate [" + dtdFile + "] in classpath");
      try {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dtdFile);
        InputSource source = new InputSource(stream);
        source.setPublicId(publicId);
        source.setSystemId(systemId);
        log.debug("Found dozerbeanmapping DTD [" + systemId + "] in classpath");
        return source;
      } catch (Exception ex) {
        log.error("Could not resolve beans DTD [" + systemId + "]: not found in classpath", ex);
      }
    }
    // use the default behaviour -> download from website or wherever
    return null;
  }
}
