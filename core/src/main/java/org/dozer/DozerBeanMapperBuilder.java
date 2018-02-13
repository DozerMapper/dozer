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
package org.dozer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.dozer.builder.DestBeanBuilderCreator;
import org.dozer.classmap.ClassMapBuilder;
import org.dozer.classmap.ClassMappings;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingFileData;
import org.dozer.classmap.generator.BeanMappingGenerator;
import org.dozer.config.BeanContainer;
import org.dozer.config.Settings;
import org.dozer.config.processors.DefaultSettingsProcessor;
import org.dozer.config.processors.SettingsProcessor;
import org.dozer.el.DefaultELEngine;
import org.dozer.el.ELEngine;
import org.dozer.el.ELExpressionFactory;
import org.dozer.el.NoopELEngine;
import org.dozer.el.TcclELEngine;
import org.dozer.factory.DestBeanCreator;
import org.dozer.loader.CustomMappingsLoader;
import org.dozer.loader.LoadMappingsResult;
import org.dozer.loader.MappingsParser;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.xml.ElementReader;
import org.dozer.loader.xml.ExpressionElementReader;
import org.dozer.loader.xml.MappingFileReader;
import org.dozer.loader.xml.MappingStreamReader;
import org.dozer.loader.xml.SimpleElementReader;
import org.dozer.loader.xml.XMLParser;
import org.dozer.loader.xml.XMLParserFactory;
import org.dozer.osgi.Activator;
import org.dozer.osgi.OSGiClassLoader;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.util.DefaultClassLoader;
import org.dozer.util.DozerClassLoader;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingValidator;
import org.dozer.util.RuntimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds an instance of {@link Mapper}.
 * Provides fluent interface to configure every aspect of the mapper. Everything which is not explicitly specified
 * will receive its default value. Please refer to class methods for possible configuration options.
 */
public final class DozerBeanMapperBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DozerBeanMapperBuilder.class);

    private List<String> mappingFiles = new ArrayList<>(1);
    private DozerClassLoader classLoader;
    private List<CustomConverter> customConverters = new ArrayList<>(0);
    private List<Supplier<InputStream>> xmlMappingSuppliers = new ArrayList<>(0);
    private List<BeanMappingBuilder> mappingBuilders = new ArrayList<>(0);
    private List<DozerEventListener> eventListeners = new ArrayList<>(0);
    private CustomFieldMapper customFieldMapper;
    private Map<String, CustomConverter> customConvertersWithId = new HashMap<>(0);
    private Map<String, BeanFactory> beanFactories = new HashMap<>(0);
    private SettingsProcessor settingsProcessor;
    private ELEngine elEngine;
    private ElementReader elementReader;
    private ClassMappings customMappings;
    private Configuration globalConfiguration;

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
     * Creates an instance of {@link Mapper}, with all the configuration set to its default values.
     * <p>
     * The only special handling is for mapping file. If there is a file with name {@code dozerBeanMapping.xml}
     * available on classpath, this file will be used by created mapper. Otherwise the mapper is implicit.
     *
     * @return new instance of {@link Mapper} with default configuration and optionally initiated mapping file.
     */
    public static Mapper buildDefault() {
        DozerBeanMapperBuilder builder = create();
        DozerClassLoader classLoader = builder.getClassLoader();
        URL defaultMappingFile = classLoader.loadResource(DozerConstants.DEFAULT_MAPPING_FILE);
        if (defaultMappingFile != null) {
            builder.withMappingFiles(DozerConstants.DEFAULT_MAPPING_FILE);
        }
        return builder.withClassLoader(classLoader).build();
    }

    /**
     * Adds {@code mappingFiles} to the list of URLs to be used as mapping configuration. It is possible to load files from
     * file system via {@code file:} prefix. If no prefix is given, loaded from classpath.
     * <p>
     * Multiple calls of this method will result in all the files being added to the list
     * of mappings in the order methods were called.
     * <p>
     * If not called, no files will be added to the mapping configuration, and mapper will use implicit mode.
     *
     * @param mappingFiles URLs to mapping files to be added.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withMappingFiles(String... mappingFiles) {
        return withMappingFiles(Arrays.asList(mappingFiles));
    }

    /**
     * Adds {@code mappingFiles} to the list of URLs to be used as mapping configuration. It is possible to load files from
     * file system via {@code file:} prefix. If no prefix is given, loaded from classpath.
     * <p>
     * Multiple calls of this method will result in all the files being added to the list
     * of mappings in the order methods were called.
     * <p>
     * If not called, no files will be added to the mapping configuration, and mapper will use implicit mode.
     *
     * @param mappingFiles URLs to mapping files to be added.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withMappingFiles(List<String> mappingFiles) {
        if (mappingFiles != null) {
            this.mappingFiles.addAll(mappingFiles);
        }
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
     * Registers a {@link CustomConverter} for the mapper. Multiple calls of this method will register converters in the order of calling.
     * <p>
     * By default, no custom converters are used by generated mapper.
     *
     * @param customConverter converter to be registered.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withCustomConverter(CustomConverter customConverter) {
        return withCustomConverters(customConverter);
    }

    /**
     * Registers a {@link CustomConverter} for the mapper. Multiple calls of this method will register converters in the order of calling.
     * <p>
     * By default, no custom converters are used by generated mapper.
     *
     * @param customConverters converters to be registered.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withCustomConverters(CustomConverter... customConverters) {
        return withCustomConverters(Arrays.asList(customConverters));
    }

    /**
     * Registers a {@link CustomConverter} for the mapper. Multiple calls of this method will register converters in the order of calling.
     * <p>
     * By default, no custom converters are used by generated mapper.
     *
     * @param customConverters converters to be registered.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withCustomConverters(List<CustomConverter> customConverters) {
        if (customConverters != null) {
            this.customConverters.addAll(customConverters);
        }
        return this;
    }

    /**
     * Registers a supplier of {@link InputStream} which is expected to contain data of XML mapping file.
     * At the moment of {@link #create()} method call, suppliers will be called in the order they were registered,
     * the data of each stream will be read and processed, stream will be immediately closed.
     * <p>
     * Please note, XML mappings are processed before fluent builder mappings. Although it is not recommended to mix the approaches.
     * <p>
     * By default, no XML mappings are registered.
     *
     * @param xmlMappingSupplier supplier of a Dozer mapping XML InputStream.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withXmlMapping(Supplier<InputStream> xmlMappingSupplier) {
        this.xmlMappingSuppliers.add(xmlMappingSupplier);
        return this;
    }

    /**
     * Registers a {@link BeanMappingBuilder} for the mapper. Multiple calls of this method will register builders in the order of calling.
     * <p>
     * Builders are executed at the moment of {@link #create()} method call.
     * <p>
     * Please note, XML mappings are processed before Java builder mappings. Although it is not recommended to mix the approaches.
     * <p>
     * By default, no API builders are registered.
     *
     * @param mappingBuilder mapping builder to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withMappingBuilder(BeanMappingBuilder mappingBuilder) {
        return withMappingBuilders(mappingBuilder);
    }

    /**
     * Registers a {@link BeanMappingBuilder} for the mapper. Multiple calls of this method will register builders in the order of calling.
     * <p>
     * Builders are executed at the moment of {@link #create()} method call.
     * <p>
     * Please note, XML mappings are processed before Java builder mappings. Although it is not recommended to mix the approaches.
     * <p>
     * By default, no API builders are registered.
     *
     * @param mappingBuilders mapping builder to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withMappingBuilders(BeanMappingBuilder... mappingBuilders) {
        return withMappingBuilders(Arrays.asList(mappingBuilders));
    }

    /**
     * Registers a {@link BeanMappingBuilder} for the mapper. Multiple calls of this method will register builders in the order of calling.
     * <p>
     * Builders are executed at the moment of {@link #create()} method call.
     * <p>
     * Please note, XML mappings are processed before Java builder mappings. Although it is not recommended to mix the approaches.
     * <p>
     * By default, no API builders are registered.
     *
     * @param mappingBuilders mapping builder to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withMappingBuilders(List<BeanMappingBuilder> mappingBuilders) {
        if (mappingBuilders != null) {
            this.mappingBuilders.addAll(mappingBuilders);
        }
        return this;
    }

    /**
     * Registers a {@link DozerEventListener} for the mapper. Multiple calls of this method will register listeners in the order of calling.
     * <p>
     * By default, no listeners are registered.
     *
     * @param eventListener listener to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withEventListener(DozerEventListener eventListener) {
        return withEventListeners(eventListener);
    }

    /**
     * Registers a {@link DozerEventListener} for the mapper. Multiple calls of this method will register listeners in the order of calling.
     * <p>
     * By default, no listeners are registered.
     *
     * @param eventListeners listeners to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withEventListeners(DozerEventListener... eventListeners) {
        return withEventListeners(Arrays.asList(eventListeners));
    }

    /**
     * Registers a {@link DozerEventListener} for the mapper. Multiple calls of this method will register listeners in the order of calling.
     * <p>
     * By default, no listeners are registered.
     *
     * @param eventListeners listeners to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withEventListeners(List<DozerEventListener> eventListeners) {
        if (eventListeners != null) {
            this.eventListeners.addAll(eventListeners);
        }
        return this;
    }

    /**
     * Registers a {@link CustomFieldMapper} for the mapper. Mapper has only one custom field mapper,
     * and thus consecutive calls of this method will override previously specified value.
     * <p>
     * By default, no custom field mapper is registered.
     *
     * @param customFieldMapper custom field mapper to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withCustomFieldMapper(CustomFieldMapper customFieldMapper) {
        this.customFieldMapper = customFieldMapper;
        return this;
    }

    /**
     * Registers a {@link CustomConverter} which can be referenced in mapping by provided ID.
     * Consecutive calls of this method with the same ID will override previously provided value.
     * <p>
     * Converter instances provided this way are considered stateful and will not be initialized for each mapping.
     * <p>
     * By default, no converters with IDs are registered.
     *
     * @param converterId unique ID of the converter, used as reference in mappings.
     * @param converter   converter to be used for provided ID.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withCustomConverterWithId(String converterId, CustomConverter converter) {
        this.customConvertersWithId.put(converterId, converter);
        return this;
    }

    /**
     * Registers a {@link CustomConverter} which can be referenced in mapping by provided ID.
     * Consecutive calls of this method with the same ID will override previously provided value.
     * <p>
     * Converter instances provided this way are considered stateful and will not be initialized for each mapping.
     * <p>
     * By default, no converters with IDs are registered.
     *
     * @param customConvertersWithId   converters to be used by mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withCustomConvertersWithIds(Map<String, CustomConverter> customConvertersWithId) {
        if (customConvertersWithId != null) {
            this.customConvertersWithId.putAll(customConvertersWithId);
        }
        return this;
    }

    /**
     * Registers a {@link BeanFactory} for the mapper.
     * Consecutive calls of this method with the same factory name will override previously provided value.
     * <p>
     * By default, no custom bean factories are registered.
     *
     * @param factoryName unique name of the factory.
     * @param beanFactory factory to be used by mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withBeanFactory(String factoryName, BeanFactory beanFactory) {
        this.beanFactories.put(factoryName, beanFactory);
        return this;
    }

    /**
     * Registers a {@link BeanFactory} for the mapper.
     * Consecutive calls of this method with the same factory name will override previously provided value.
     * <p>
     * By default, no custom bean factories are registered.
     *
     * @param beanFactories factory's to be used by mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withBeanFactorys(Map<String, BeanFactory> beanFactories) {
        if (beanFactories != null) {
            this.beanFactories.putAll(beanFactories);
        }
        return this;
    }

    /**
     * Registers a {@link SettingsProcessor} for the mapper. Which can be used to resolve a settings instance.
     * <p>
     * By default, {@link DefaultSettingsProcessor} is registered.
     *
     * @param processor processor to use
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withSettingsProcessor(SettingsProcessor processor) {
        this.settingsProcessor = settingsProcessor;
        return this;
    }

    /**
     * Registers a {@link ELEngine} for the mapper.
     * Which can be used to resolve expressions within the defined mappings.
     * <p>
     * By default, {@link NoopELEngine} is registered,
     * unless {@link com.sun.el.ExpressionFactoryImpl} is detected on classpath, then {@link DefaultELEngine}
     *
     * @param elEngine elEngine to use
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withELEngine(ELEngine elEngine) {
        this.elEngine = elEngine;
        return this;
    }

    /**
     * Registers a {@link ElementReader} for the mapper.
     * Which can be used to resolve expressions within the defined XML mappings.
     * <p>
     * By default, {@link SimpleElementReader} are registered,
     * unless {@link com.sun.el.ExpressionFactoryImpl} is detected on classpath, then {@link ExpressionElementReader}
     *
     * @param elementReader elementReader to use
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withElementReader(ElementReader elementReader) {
        this.elementReader = elementReader;
        return this;
    }

    /**
     * Creates an instance of {@link Mapper}. Mapper is configured according to the current builder state.
     * <p>
     * Subsequent calls of this method will return new instances.
     *
     * @return new instance of {@link Mapper}.
     */
    public Mapper build() {
        LOG.info("Initializing a new instance of dozer bean mapper.");

        DozerClassLoader classLoader = getClassLoader();
        Settings settings = getSettings(classLoader);
        ELEngine elEngine = getELEngine();
        ElementReader elementReader = getElementReader(elEngine);

        BeanContainer beanContainer = new BeanContainer();
        beanContainer.setElEngine(elEngine);
        beanContainer.setElementReader(elementReader);
        beanContainer.setClassLoader(classLoader);

        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        destBeanCreator.setStoredFactories(beanFactories);

        PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();
        BeanMappingGenerator beanMappingGenerator = new BeanMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory);
        DestBeanBuilderCreator destBeanBuilderCreator = new DestBeanBuilderCreator();

        XMLParser xmlParser = new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
        XMLParserFactory xmlParserFactory = new XMLParserFactory(beanContainer);

        DozerInitializer dozerInitializer = new DozerInitializer();
        dozerInitializer.init(settings, beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);

        List<MappingFileData> mappingsFileData = new ArrayList<>();
        mappingsFileData.addAll(readXmlMappings(xmlParserFactory, xmlParser));
        mappingsFileData.addAll(createMappingsWithBuilders(beanContainer, destBeanCreator, propertyDescriptorFactory));

        loadCustomMappings(mappingsFileData, xmlParserFactory, xmlParser,
                           beanContainer, propertyDescriptorFactory, beanMappingGenerator, destBeanCreator);

        return new DozerBeanMapper(mappingFiles,
                                   settings,
                                   dozerInitializer,
                                   beanContainer,
                                   destBeanCreator,
                                   destBeanBuilderCreator,
                                   beanMappingGenerator,
                                   propertyDescriptorFactory,
                                   customConverters,
                                   mappingsFileData,
                                   eventListeners,
                                   customFieldMapper,
                                   customConvertersWithId,
                                   customMappings,
                                   globalConfiguration);
    }

    private List<MappingFileData> createMappingsWithBuilders(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        return this.mappingBuilders.stream()
                .map(fluentBuilder -> fluentBuilder.build(beanContainer, destBeanCreator, propertyDescriptorFactory))
                .collect(Collectors.toList());
    }

    private List<MappingFileData> readXmlMappings(XMLParserFactory xmlParserFactory, XMLParser xmlParser) {
        return this.xmlMappingSuppliers.stream()
                .map(xmlMappingSupplier -> {
                    try (InputStream xmlMappingStream = xmlMappingSupplier.get()) {
                        MappingStreamReader fileReader = new MappingStreamReader(xmlParserFactory, xmlParser);
                        return fileReader.read(xmlMappingStream);
                    } catch (IOException e) {
                        throw new MappingException(e);
                    }
                })
                .collect(Collectors.toList());
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

    private Settings getSettings(DozerClassLoader classLoader) {
        if (settingsProcessor == null) {
            settingsProcessor = new DefaultSettingsProcessor(classLoader);
            return settingsProcessor.process();
        } else {
            return settingsProcessor.process();
        }
    }

    private ELEngine getELEngine() {
        if (elEngine == null) {
            if (ELExpressionFactory.isSupported()) {
                if (RuntimeUtils.isOSGi()) {
                    ClassLoader classLoader = getClass().getClassLoader();
                    return new TcclELEngine(ELExpressionFactory.newInstance(classLoader), classLoader);
                } else {
                    return new DefaultELEngine(ELExpressionFactory.newInstance());
                }
            } else {
                return new NoopELEngine();
            }
        } else {
            return elEngine;
        }
    }

    private ElementReader getElementReader(ELEngine elEngine) {
        if (elementReader == null) {
            return new ExpressionElementReader(elEngine);
        } else {
            return elementReader;
        }
    }

    private void loadCustomMappings(List<MappingFileData> mappingsFileData, XMLParserFactory xmlParserFactory, XMLParser xmlParser,
                                    BeanContainer beanContainer, PropertyDescriptorFactory propertyDescriptorFactory,
                                    BeanMappingGenerator beanMappingGenerator, DestBeanCreator destBeanCreator) {

        List<MappingFileData> xmlMappings = loadFromFiles(mappingFiles, xmlParserFactory, xmlParser, beanContainer);
        ArrayList<MappingFileData> allMappings = new ArrayList<>();
        allMappings.addAll(xmlMappings);
        allMappings.addAll(mappingsFileData);

        MappingsParser mappingsParser = new MappingsParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
        ClassMapBuilder classMapBuilder = new ClassMapBuilder(beanContainer, destBeanCreator, beanMappingGenerator, propertyDescriptorFactory);
        CustomMappingsLoader customMappingsLoader = new CustomMappingsLoader(mappingsParser, classMapBuilder, beanContainer);

        LoadMappingsResult loadMappingsResult = customMappingsLoader.load(allMappings);

        this.customMappings = loadMappingsResult.getCustomMappings();
        this.globalConfiguration = loadMappingsResult.getGlobalConfiguration();
    }

    private List<MappingFileData> loadFromFiles(List<String> mappingFiles, XMLParserFactory xmlParserFactory, XMLParser xmlParser,
                                                BeanContainer beanContainer) {
        MappingFileReader mappingFileReader = new MappingFileReader(xmlParserFactory, xmlParser, beanContainer);
        List<MappingFileData> mappingFileDataList = new ArrayList<>();
        if (mappingFiles != null && mappingFiles.size() > 0) {
            LOG.info("Using the following xml files to load custom mappings for the bean mapper instance: {}", mappingFiles);

            for (String mappingFileName : mappingFiles) {
                LOG.info("Trying to find xml mapping file: {}", mappingFileName);

                URL url = MappingValidator.validateURL(mappingFileName, beanContainer);

                LOG.info("Using URL [" + url + "] to load custom xml mappings");

                MappingFileData mappingFileData = mappingFileReader.read(url);

                LOG.info("Successfully loaded custom xml mappings from URL: [{}]", url);

                mappingFileDataList.add(mappingFileData);
            }
        }

        return mappingFileDataList;
    }
}
