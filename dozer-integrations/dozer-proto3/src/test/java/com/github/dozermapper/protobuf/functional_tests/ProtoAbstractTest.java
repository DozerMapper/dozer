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
package com.github.dozermapper.protobuf.functional_tests;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.util.DozerConstants;

public abstract class ProtoAbstractTest {

    protected Mapper getMapper(String fileName) {
        System.setProperty("log4j.debug", "true");
        System.setProperty(DozerConstants.DEBUG_SYS_PROP, "true");

        return DozerBeanMapperBuilder.create()
                .withMappingFiles(fileName)
                .build();
    }
}
