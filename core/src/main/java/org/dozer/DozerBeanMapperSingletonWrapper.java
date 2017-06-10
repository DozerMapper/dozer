/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer;

import org.dozer.util.DozerConstants;

/**
 * Public Singleton wrapper for the DozerBeanMapper. Only supports a single mapping file named dozerBeanMapping.xml, so
 * configuration is very limited. Instead of using the DozerBeanMapperSingletonWrapper, it is recommended that the
 * DozerBeanMapper(MapperIF) instance is configured via an IOC framework, such as Spring, with singleton property set to
 * "true"
 *
 * @deprecated Will be removed in version 6.2. Please use {@link DozerBeanMapperBuilder#buildDefault()}.
 *
 * @author garsombke.franz
 */
@Deprecated
public final class DozerBeanMapperSingletonWrapper {

    private static Mapper instance;

    private DozerBeanMapperSingletonWrapper() {

    }

    /**
     * @deprecated Will be removed in version 6.2. Please use {@link DozerBeanMapperBuilder#buildDefault()}.
     */
    @Deprecated
    public static synchronized Mapper getInstance() {
        if (instance == null) {
            instance = DozerBeanMapperBuilder.buildDefault();
        }

        return instance;
    }
}
