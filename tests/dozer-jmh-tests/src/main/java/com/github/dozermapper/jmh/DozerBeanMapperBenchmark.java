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
package com.github.dozermapper.jmh;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.jmh.model.Person;

import org.openjdk.jmh.annotations.Benchmark;

public class DozerBeanMapperBenchmark {

    @Benchmark
    public Person mapSimpleSameTypes() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/simple-sametypes.xml")
                .build();

        return mapper.map(new Person("bob"), Person.class);
    }
}
