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
package com.github.dozermapper.core.builder.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.NoopELEngine;

import org.junit.Test;

public class BeanMappingXMLBuilderTest {

    @Test
    public void canLoadFiles() throws IOException {
        File folder = new File(new File(".").getCanonicalPath() + "/src/test/resources/mappings");
        Stream<File> listOfFiles = Arrays.stream(folder.listFiles());

        BeanMappingXMLBuilder builder = new BeanMappingXMLBuilder(new BeanContainer(), new NoopELEngine());
        builder.loadFiles(listOfFiles.map(file -> "file:" + file.getAbsolutePath()).collect(Collectors.toList()));
    }

    @Test
    public void canLoadStreams() throws IOException {
        File folder = new File(new File(".").getCanonicalPath() + "/src/test/resources/mappings");
        Stream<File> listOfFiles = Arrays.stream(folder.listFiles());

        BeanMappingXMLBuilder builder = new BeanMappingXMLBuilder(new BeanContainer(), new NoopELEngine());
        builder.loadInputStreams(listOfFiles.map(file -> {
            Supplier<InputStream> stream = () -> {
                try {
                    return new BufferedInputStream(new URL("file:" + file.getAbsolutePath()).openStream());
                } catch (IOException e) {
                    return null;
                }
            };
            return stream;
        }).collect(Collectors.toList()));
    }
}
