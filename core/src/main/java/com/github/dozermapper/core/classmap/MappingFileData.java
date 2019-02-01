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
package com.github.dozermapper.core.classmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal class that contains all of the custom mapping definitions, along with the global configuration instance.
 * Only intended for internal use.
 */
public class MappingFileData {
    private List<ClassMap> classMaps = new ArrayList<>();
    private Configuration configuration;

    public List<ClassMap> getClassMaps() {
        return classMaps;
    }

    public void addClassMap(ClassMap classMap) {
        this.classMaps.add(classMap);
    }

    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}
