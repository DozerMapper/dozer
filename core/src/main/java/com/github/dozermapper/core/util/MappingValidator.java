/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.github.dozermapper.core.config.BeanContainer;

/**
 * Internal class used to perform various validations. Validates mapping requests, field mappings, URL's, etc. Only
 * intended for internal use.
 */
public final class MappingValidator {

    private MappingValidator() {
    }

    public static void validateMappingRequest(Object srcObj) {
        if (srcObj == null) {
            MappingUtils.throwMappingException("Source object must not be null");
        }
    }

    public static void validateMappingRequest(Object srcObj, Object destObj) {
        validateMappingRequest(srcObj);
        if (destObj == null) {
            MappingUtils.throwMappingException("Destination object must not be null");
        }
    }

    public static void validateMappingRequest(Object srcObj, Class<?> destClass) {
        validateMappingRequest(srcObj);
        if (destClass == null) {
            MappingUtils.throwMappingException("Destination class must not be null");
        }
    }

    public static URL validateURL(String fileName, BeanContainer beanContainer) {
        DozerClassLoader classLoader = beanContainer.getClassLoader();
        if (fileName == null) {
            MappingUtils.throwMappingException("File name is null");
        }

        URL url = classLoader.loadResource(fileName);
        if (url == null) {
            DozerClassLoader tccl = beanContainer.getTCCL();
            url = tccl.loadResource(fileName);
            if (url == null) {
                MappingUtils.throwMappingException("Unable to locate dozer mapping file [" + fileName + "] in the classpath!");
            }
        }

        InputStream stream = null;
        try {
            stream = url.openStream();
        } catch (IOException e) {
            MappingUtils.throwMappingException("Unable to open URL input stream for dozer mapping file [" + url + "]");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    MappingUtils.throwMappingException("Unable to close input stream for dozer mapping file [" + url + "]");
                }
            }
        }

        return url;
    }

}
