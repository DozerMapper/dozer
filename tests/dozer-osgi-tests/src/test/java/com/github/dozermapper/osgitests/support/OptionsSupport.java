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
package com.github.dozermapper.osgitests.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ops4j.pax.exam.options.UrlProvisionOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.url;

public final class OptionsSupport {

    private OptionsSupport() {
    }

    /**
     * Provision in format 'url("link:classpath:...' doesn't work with karaf
     *
     * @param link - link-file name
     * @return - provision option which can be loaded by Apache Karaf
     */
    public static UrlProvisionOption localBundle(String link) {
        try (InputStream resourceAsStream = OptionsSupport.class.getClassLoader().getResourceAsStream(link)) {
            assertNotNull("Invalid link file was provided: " + link, resourceAsStream);

            List<String> urls = IOUtils.readLines(resourceAsStream, Charset.forName("UTF-8"));

            assertEquals("Invalid link file was provided: " + link, 1, urls.size());

            return url(urls.get(0));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
