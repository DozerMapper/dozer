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
package com.github.dozermapper.core.config;

public final class SettingsKeys {

    private SettingsKeys() {
    }

    /**
     * i.e.: -Ddozer.configuration=somefile.properties
     */
    public static final String CONFIG_FILE_SYS_PROP = "dozer.configuration";

    public static final String CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE = "dozer.cache.converter-by-dest-type-maxsize";
    public static final String SUPER_TYPE_CHECK_CACHE_MAX_SIZE = "dozer.cache.super-type-maxsize";
    public static final String CLASS_LOADER_BEAN = "dozer.beans.class-loader-bean";
    public static final String PROXY_RESOLVER_BEAN = "dozer.beans.proxy-resolver-bean";
    public static final String USE_JAXB_MAPPING_ENGINE = "dozer.xml.use-jaxb-mapping-engine";

    @Deprecated
    public static final String DEPRECATED_EL_ENABLED = "dozer.el.enabled";

    @Deprecated
    public static final String DEPRECATED_CONVERTER_BY_DEST_TYPE_CACHE_MAX_SIZE = "dozer.cache.converter.by.dest.type.maxsize";

    @Deprecated
    public static final String DEPRECATED_SUPER_TYPE_CHECK_CACHE_MAX_SIZE = "dozer.cache.super.type.maxsize";

    @Deprecated
    public static final String DEPRECATED_CLASS_LOADER_BEAN = "com.github.dozermapper.core.util.DozerClassLoader";

    @Deprecated
    public static final String DEPRECATED_PROXY_RESOLVER_BEAN = "com.github.dozermapper.core.util.DozerProxyResolver";
}
