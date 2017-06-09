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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dozer.builder.DestBeanBuilderCreator;
import org.dozer.classmap.ClassMapBuilder;
import org.dozer.classmap.generator.BeanMappingGenerator;
import org.dozer.config.BeanContainer;
import org.dozer.config.GlobalSettings;
import org.dozer.factory.DestBeanCreator;
import org.dozer.loader.CustomMappingsLoader;
import org.dozer.loader.MappingsParser;
import org.dozer.loader.xml.XMLParser;
import org.dozer.loader.xml.XMLParserFactory;
import org.dozer.osgi.Activator;
import org.dozer.osgi.OSGiClassLoader;
import org.dozer.stats.StatisticsManager;
import org.dozer.stats.StatisticsManagerImpl;
import org.dozer.util.DefaultClassLoader;
import org.dozer.util.DozerClassLoader;
import org.dozer.util.RuntimeUtils;

/**
 * Builds an instance of {@link DozerBeanMapper}.
 * Provides fluent interface to configure every aspect of the mapper. Everything which is not explicitly specified
 * will receive its default value. Please refer to class methods for possible configuration options.
 */
public final class DozerBeanMapperBuilder {

    private List<String> mappingFiles = new ArrayList<>(1);
    private DozerClassLoader classLoader;

    private DozerBeanMapperBuilder() {
    }

    /**
     * Creates new builder. All the configuration has its default values.
     *
     * @return new instance of the builder.
     */
    public static DozerBeanMapperBuilder create() {
        return new DozerBeanMapperBuilder();
    }

    /**
     * Creates an instance of {@link DozerBeanMapper}, with all the configuration set to its default values.
     * <p>
     * Is a shortcut for {@code DozerBeanMapperBuilder.create().build()}.
     *
     * @return new instance of {@link DozerBeanMapper} with default configuration.
     */
    public static DozerBeanMapper buildDefaultImplicit() {
        return create().build();
    }

    /**
     * Adds {@code mappingFiles} to the list of files to be used as mapping configuration.
     * Multiple calls of this method will result in all the files being added to the list
     * of mappings in the order methods were called.
     * <p>
     * If not called, no files will be added to the mapping configuration, and mapper will use implicit mode.
     *
     * @param mappingFiles mapping files to be added.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withMappingFiles(String... mappingFiles) {
        this.mappingFiles.addAll(Arrays.asList(mappingFiles));
        return this;
    }

    /**
     * Sets {@link DozerClassLoader} to be used whenever Dozer needs to load a class or resource.
     * <p>
     * By default, if Dozer is executed in OSGi environment, {@link org.dozer.osgi.OSGiClassLoader} will be
     * used (i.e. delegate loading to Dozer bundle classloader). If Dozer is executed in non-OSGi environment,
     * classloader of {@link DozerBeanMapperBuilder} will be used (wrapped into {@link DefaultClassLoader}).
     *
     * @param classLoader custom classloader to be used by Dozer.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withClassLoader(DozerClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    /**
     * Sets classloader to be used whenever Dozer needs to load a class or resource.
     * <p>
     * By default, if Dozer is executed in OSGi environment, {@link org.dozer.osgi.OSGiClassLoader} will be
     * used (i.e. delegate loading to Dozer bundle classloader). If Dozer is executed in non-OSGi environment,
     * classloader of {@link DozerBeanMapperBuilder} will be used (wrapped into {@link DefaultClassLoader}).
     *
     * @param classLoader custom classloader to be used by Dozer. Will be wrapped into {@link DefaultClassLoader}.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withClassLoader(ClassLoader classLoader) {
        this.classLoader = new DefaultClassLoader(classLoader);
        return this;
    }

    /**
     * Creates an instance of {@link DozerBeanMapper}. Mapper is configured according to the current builder state.
     * <p>
     * Subsequent calls of this method will return new instances.
     *
     * @return new instance of {@link DozerBeanMapper}.
     */
    public DozerBeanMapper build() {
        DozerClassLoader classLoader = getClassLoader();
        GlobalSettings globalSettings = new GlobalSettings(classLoader);
        BeanContainer beanContainer = new BeanContainer();
        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        BeanMappingGenerator beanMappingGenerator = new BeanMappingGenerator(beanContainer, destBeanCreator);
        ClassMapBuilder classMapBuilder = new ClassMapBuilder(beanContainer, destBeanCreator, beanMappingGenerator);
        CustomMappingsLoader customMappingsLoader = new CustomMappingsLoader(new MappingsParser(beanContainer, destBeanCreator), classMapBuilder, beanContainer);
        XMLParserFactory xmlParserFactory = new XMLParserFactory(beanContainer);
        StatisticsManager statisticsManager = new StatisticsManagerImpl(globalSettings);
        DozerInitializer dozerInitializer = new DozerInitializer();
        XMLParser xmlParser = new XMLParser(beanContainer, destBeanCreator);
        DestBeanBuilderCreator destBeanBuilderCreator = new DestBeanBuilderCreator();

        return new DozerBeanMapper(mappingFiles,
                globalSettings,
                customMappingsLoader,
                xmlParserFactory,
                statisticsManager,
                dozerInitializer,
                beanContainer,
                xmlParser,
                destBeanCreator,
                destBeanBuilderCreator,
                beanMappingGenerator);
    }

    private DozerClassLoader getClassLoader() {
        if (classLoader == null) {
            if (RuntimeUtils.isOSGi()) {
                return new OSGiClassLoader(Activator.getBundle().getBundleContext());

            } else {
                return new DefaultClassLoader(DozerBeanMapperBuilder.class.getClassLoader());
            }

        } else {
            return classLoader;
        }
    }

}
