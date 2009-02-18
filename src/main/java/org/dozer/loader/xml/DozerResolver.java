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
package org.dozer.loader.xml;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.util.DozerConstants;
import org.dozer.util.ResourceLoader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Internal EntityResolver implementation to load Xml Schema from the dozer classpath resp. JAR file.
 * 
 * <p>
 * Fetches "beanmapping.xsd" from the classpath resource "/beanmapping.xsd", no matter if specified as some
 * local URL or as "http://dozer.sourceforge.net/schema/beanmapping.xsd".
 * 
 * @author garsombke.franz
 */
public class DozerResolver implements EntityResolver {

  private static final Log log = LogFactory.getLog(DozerResolver.class);

  public InputSource resolveEntity(String publicId, String systemId) {
    log.debug("Trying to resolve XML entity with public ID [" + publicId + "] and system ID [" + systemId + "]");
    if (systemId != null && systemId.indexOf(DozerConstants.XSD_NAME) > systemId.lastIndexOf("/")) {
      String fileName = systemId.substring(systemId.indexOf(DozerConstants.XSD_NAME));
      log.debug("Trying to locate [" + fileName + "] in classpath");
      try {
        ResourceLoader resourceLoader = new ResourceLoader();
        InputStream stream = resourceLoader.getResource(fileName).openStream();
        InputSource source = new InputSource(stream);
        source.setPublicId(publicId);
        source.setSystemId(systemId);
        log.debug("Found beanmapping XML Schema [" + systemId + "] in classpath");
        return source;
      } catch (Exception ex) {
        log.error("Could not resolve beansmapping XML Schema [" + systemId + "]: not found in classpath", ex);
      }
    }
    // use the default behaviour -> download from website or wherever
    return null;
  }
}
