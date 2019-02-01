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
package com.github.dozermapper.core.loader;

import com.github.dozermapper.core.classmap.ClassMappings;
import com.github.dozermapper.core.classmap.Configuration;

/**
 * Internal class that contains the results of the loadMappings operation. Only intended for internal use.
 */
public class LoadMappingsResult {

    private ClassMappings customMappings;
    private Configuration globalConfiguration;

    public LoadMappingsResult(ClassMappings customMappings, Configuration globalConfiguration) {
        this.customMappings = customMappings;
        this.globalConfiguration = globalConfiguration;
    }

    public ClassMappings getCustomMappings() {
        return customMappings;
    }

    public Configuration getGlobalConfiguration() {
        return globalConfiguration;
    }

}
