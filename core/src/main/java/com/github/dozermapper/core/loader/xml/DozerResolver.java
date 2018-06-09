/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.loader.xml;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.DozerClassLoader;
import com.github.dozermapper.core.util.DozerConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal EntityResolver implementation to load Xml Schema from the dozer classpath resp. JAR file.
 * <p>
 * Fetches "bean-mapping.xsd" from the classpath resource "/bean-mapping.xsd", no matter if specified as some
 * local URL or as "http://dozermapper.github.io/schema/bean-mapping.xsd".
 */
@Deprecated
public class DozerResolver implements EntityResolver {

    private final Logger log = LoggerFactory.getLogger(DozerResolver.class);
    private static final String VERSION_5_XSD = "http://dozer.sourceforge.net/schema/beanmapping.xsd";
    private static final String VERSION_6_XSD = "https://dozermapper.github.io/schema/bean-mapping.xsd";

    private final BeanContainer beanContainer;

    public DozerResolver(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public InputSource resolveEntity(String publicId, String systemId) {
        InputSource source = null;

        log.debug("Trying to resolve XML entity with public ID [{}] and system ID [{}]", publicId, systemId);

        if (VERSION_5_XSD.equalsIgnoreCase(systemId)) {
            throw new MappingException("Dozer >= v6.0.0 uses a new XSD location. Your current config needs to be upgraded. "
                                       + "Found v5 XSD: '"
                                       + VERSION_5_XSD
                                       + "'. Expected v6 XSD: '"
                                       + VERSION_6_XSD
                                       + "'. Please see migration guide @ https://dozermapper.github.io/gitbook");
        }

        try {
            source = resolveFromClassPath(publicId, systemId);
        } catch (IOException ex1) {
            log.error("Could not resolve bean-mapping XML Schema [" + systemId + "]: not found in classpath", ex1);

            try {
                source = resolveFromURL(systemId);
            } catch (IOException ex2) {
                log.error("Could not resolve bean-mapping XML Schema [" + systemId + "]", ex2);
            }
        }

        return source;
    }

    /**
     * 1. Checks if DozerClassloader (i.e.: maybe the user is using a local XSD for some reason)
     * 2. Trys classloader for dozer-schema.jar
     * 3. Trys bundle context for dozer-schema.jar
     *
     * @param publicId publicId used by XSD
     * @param systemId systemId used by XSD
     * @return stream to XSD
     * @throws IOException if fails to find XSD
     */
    private InputSource resolveFromClassPath(String publicId, String systemId) throws IOException {
        InputSource source = null;
        if (systemId != null && systemId.indexOf(DozerConstants.XSD_NAME) > systemId.lastIndexOf("/")) {
            String fileName = String.join("/", "schema", systemId.substring(systemId.indexOf(DozerConstants.XSD_NAME)));

            log.debug("Trying to locate [{}] in classpath", fileName);

            //Attempt to find via user defined class loader
            DozerClassLoader classLoader = beanContainer.getClassLoader();
            URL url = classLoader.loadResource(fileName);

            try {
                if (url == null) {
                    throw new IOException("URL is null. Failed to find '" + fileName + "' via DozerClassLoader.loadResource");
                }

                source = new InputSource(url.openStream());
                source.setPublicId(publicId);
                source.setSystemId(systemId);
            } catch (IOException ex) {
                throw new IOException("Could not resolve bean-mapping XML Schema [" + systemId + "]: not found in classpath; " + fileName, ex);
            }

            log.debug("Found bean-mapping XML Schema [{}] in classpath", systemId);
        }

        return source;
    }

    /**
     * NOTE: GitHub.io returns 301 so we need to handle redirection here, else SAX fails.
     *
     * @param systemId systemId used by XSD
     * @return stream to XSD
     * @throws IOException if fails to find XSD
     */
    private InputSource resolveFromURL(String systemId) throws IOException {
        log.debug("Trying to download [{}]", systemId);

        URL obj = new URL(systemId);
        HttpURLConnection conn = (HttpURLConnection)obj.openConnection();

        int status = conn.getResponseCode();
        if ((status != HttpURLConnection.HTTP_OK)
            && (status == HttpURLConnection.HTTP_MOVED_TEMP
             || status == HttpURLConnection.HTTP_MOVED_PERM
             || status == HttpURLConnection.HTTP_SEE_OTHER)) {

            log.debug("Received status of {}, attempting to follow Location header", status);

            String newUrl = conn.getHeaderField("Location");
            conn = (HttpURLConnection)new URL(newUrl).openConnection();
        }

        return new InputSource(conn.getInputStream());
    }
}
