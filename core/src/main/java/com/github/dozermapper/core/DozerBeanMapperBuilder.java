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
package com.github.dozermapper.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.dozermapper.core.builder.BeanMappingsBuilder;
import com.github.dozermapper.core.builder.DestBeanBuilderCreator;
import com.github.dozermapper.core.builder.xml.BeanMappingXMLBuilder;
import com.github.dozermapper.core.cache.CacheManager;
import com.github.dozermapper.core.cache.DefaultCacheManager;
import com.github.dozermapper.core.cache.DozerCacheType;
import com.github.dozermapper.core.classmap.ClassMapBuilder;
import com.github.dozermapper.core.classmap.ClassMappings;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.classmap.generator.BeanMappingGenerator;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.config.Settings;
import com.github.dozermapper.core.config.SettingsDefaults;
import com.github.dozermapper.core.config.processors.DefaultSettingsProcessor;
import com.github.dozermapper.core.config.processors.SettingsProcessor;
import com.github.dozermapper.core.el.DefaultELEngine;
import com.github.dozermapper.core.el.ELEngine;
import com.github.dozermapper.core.el.ELExpressionFactory;
import com.github.dozermapper.core.el.NoopELEngine;
import com.github.dozermapper.core.el.TcclELEngine;
import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.loader.CustomMappingsLoader;
import com.github.dozermapper.core.loader.LoadMappingsResult;
import com.github.dozermapper.core.loader.MappingsParser;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.xml.ElementReader;
import com.github.dozermapper.core.loader.xml.ExpressionElementReader;
import com.github.dozermapper.core.loader.xml.MappingFileReader;
import com.github.dozermapper.core.loader.xml.MappingStreamReader;
import com.github.dozermapper.core.loader.xml.SimpleElementReader;
import com.github.dozermapper.core.loader.xml.XMLParser;
import com.github.dozermapper.core.loader.xml.XMLParserFactory;
import com.github.dozermapper.core.osgi.Activator;
import com.github.dozermapper.core.osgi.OSGiClassLoader;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.DefaultClassLoader;
import com.github.dozermapper.core.util.DefaultProxyResolver;
import com.github.dozermapper.core.util.DozerClassLoader;
import com.github.dozermapper.core.util.DozerConstants;
import com.github.dozermapper.core.util.DozerProxyResolver;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.MappingValidator;
import com.github.dozermapper.core.util.ReflectionUtils;
import com.github.dozermapper.core.util.RuntimeUtils;

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
    private DozerClassLoader fluentDefinedClassLoader;
    private List<CustomConverter> customConverters = new ArrayList<>(0);
    private List<Supplier<InputStream>> xmlMappingSuppliers = new ArrayList<>(0);
    private List<BeanMappingBuilder> mappingBuilders = new ArrayList<>(0);
    private List<BeanMappingsBuilder> beanMappingsBuilders = new ArrayList<>(0);
    private List<EventListener> eventListeners = new ArrayList<>(0);
    private CustomFieldMapper customFieldMapper;
    private Map<String, CustomConverter> customConvertersWithId = new HashMap<>(0);
    private Map<String, BeanFactory> beanFactories = new HashMap<>(0);
    private SettingsProcessor settingsProcessor;
    private ELEngine elEngine;
    private ElementReader elementReader;
    private ClassMappings customMappings;
    private Configuration globalConfiguration;
    private CacheManager cacheManager;

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
     * By default, if Dozer is executed in OSGi environment, {@link com.github.dozermapper.core.osgi.OSGiClassLoader} will be
     * used (i.e. delegate loading to Dozer bundle classloader). If Dozer is executed in non-OSGi environment,
     * classloader of {@link DozerBeanMapperBuilder} will be used (wrapped into {@link DefaultClassLoader}).
     *
     * @param classLoader custom classloader to be used by Dozer.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withClassLoader(DozerClassLoader classLoader) {
        this.fluentDefinedClassLoader = classLoader;
        return this;
    }

    /**
     * Sets classloader to be used whenever Dozer needs to load a class or resource.
     * <p>
     * By default, if Dozer is executed in OSGi environment, {@link com.github.dozermapper.core.osgi.OSGiClassLoader} will be
     * used (i.e. delegate loading to Dozer bundle classloader). If Dozer is executed in non-OSGi environment,
     * classloader of {@link DozerBeanMapperBuilder} will be used (wrapped into {@link DefaultClassLoader}).
     *
     * @param classLoader custom classloader to be used by Dozer. Will be wrapped into {@link DefaultClassLoader}.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withClassLoader(ClassLoader classLoader) {
        this.fluentDefinedClassLoader = new DefaultClassLoader(classLoader);
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
     * Registers a {@link BeanMappingsBuilder} for the mapper. Multiple calls of this method will register builders in the order of calling.
     * <p>
     * Builders are executed at the moment of {@link #create()} method call.
     * <p>
     * Current implementations include; {@link BeanMappingXMLBuilder}
     * <p>
     * By default, no API builders are registered.
     *
     * @param beanMappingsBuilder mapping builder to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withBeanMappingsBuilders(BeanMappingsBuilder beanMappingsBuilder) {
        return withBeanMappingsBuilders(Arrays.asList(beanMappingsBuilder));
    }

    /**
     * Registers a {@link BeanMappingsBuilder} for the mapper. Multiple calls of this method will register builders in the order of calling.
     * <p>
     * Builders are executed at the moment of {@link #create()} method call.
     * <p>
     * Current implementations include; {@link BeanMappingXMLBuilder}
     * <p>
     * By default, no API builders are registered.
     *
     * @param beanMappingsBuilder mapping builder to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withBeanMappingsBuilders(BeanMappingsBuilder... beanMappingsBuilder) {
        return withBeanMappingsBuilders(Arrays.asList(beanMappingsBuilder));
    }

    /**
     * Registers a {@link BeanMappingsBuilder} for the mapper. Multiple calls of this method will register builders in the order of calling.
     * <p>
     * Builders are executed at the moment of {@link #create()} method call.
     * <p>
     * Current implementations include; {@link BeanMappingXMLBuilder}
     * <p>
     * By default, no API builders are registered.
     *
     * @param beanMappingsBuilder mapping builder to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withBeanMappingsBuilders(List<BeanMappingsBuilder> beanMappingsBuilder) {
        if (beanMappingsBuilder != null) {
            this.beanMappingsBuilders.addAll(beanMappingsBuilder);
        }
        return this;
    }

    /**
     * Registers a {@link EventListener} for the mapper. Multiple calls of this method will register listeners in the order of calling.
     * <p>
     * By default, no listeners are registered.
     *
     * @param eventListener listener to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withEventListener(EventListener eventListener) {
        return withEventListeners(eventListener);
    }

    /**
     * Registers a {@link EventListener} for the mapper. Multiple calls of this method will register listeners in the order of calling.
     * <p>
     * By default, no listeners are registered.
     *
     * @param eventListeners listeners to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withEventListeners(EventListener... eventListeners) {
        return withEventListeners(Arrays.asList(eventListeners));
    }

    /**
     * Registers a {@link EventListener} for the mapper. Multiple calls of this method will register listeners in the order of calling.
     * <p>
     * By default, no listeners are registered.
     *
     * @param eventListeners listeners to be registered for the mapper.
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withEventListeners(List<EventListener> eventListeners) {
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
     * @param customConvertersWithId converters to be used by mapper.
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
        this.settingsProcessor = processor;
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
     * Registers a {@link CacheManager} for the mapper.
     * Which can be used to control the caching behaviour
     * <p>
     * By default, {@link DefaultCacheManager} are registered
     *
     * @param cacheManager cacheManager to use
     * @return modified builder to be further configured.
     */
    public DozerBeanMapperBuilder withCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
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
        LOG.info("Initializing Dozer. Version: {}, Thread Name: {}",
                 DozerConstants.CURRENT_VERSION, Thread.currentThread().getName());

        DozerClassLoader defaultClassLoader = getClassLoader();
        Settings settings = getSettings(defaultClassLoader);
        DozerClassLoader settingsClassLoader = getClassLoaderFromSettings(settings, defaultClassLoader);

        DozerClassLoader classLoader = settingsClassLoader == null ? defaultClassLoader : settingsClassLoader;

        DozerProxyResolver proxyResolver = getProxyResolver(settings, classLoader);

        CacheManager cacheManager = getCacheManager(settings);
        ELEngine elEngine = getELEngine();
        ElementReader elementReader = getElementReader(elEngine);

        BeanContainer beanContainer = new BeanContainer();
        beanContainer.setElEngine(elEngine);
        beanContainer.setElementReader(elementReader);
        beanContainer.setClassLoader(classLoader);
        beanContainer.setProxyResolver(proxyResolver);

        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        destBeanCreator.setStoredFactories(beanFactories);

        PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();
        BeanMappingGenerator beanMappingGenerator = new BeanMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory);
        DestBeanBuilderCreator destBeanBuilderCreator = new DestBeanBuilderCreator();

        loadDozerModules(beanContainer, destBeanBuilderCreator, beanMappingGenerator, propertyDescriptorFactory, destBeanCreator);

        List<MappingFileData> mappingsFileData = new ArrayList<>();
        if (settings.getUseJaxbMappingEngine()) {
            mappingsFileData.addAll(buildXmlMappings(beanContainer, destBeanCreator, propertyDescriptorFactory, elEngine));
        } else {
            XMLParser xmlParser = new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
            XMLParserFactory xmlParserFactory = new XMLParserFactory(beanContainer);

            mappingsFileData.addAll(readXmlMappings(xmlParserFactory, xmlParser));
            mappingsFileData.addAll(loadFromFiles(mappingFiles, xmlParserFactory, xmlParser, beanContainer));
        }

        mappingsFileData.addAll(buildGenericMappings(beanContainer, destBeanCreator, propertyDescriptorFactory));
        mappingsFileData.addAll(createMappingsWithBuilders(beanContainer, destBeanCreator, propertyDescriptorFactory));

        loadCustomMappings(mappingsFileData, beanContainer, propertyDescriptorFactory, beanMappingGenerator, destBeanCreator);

        return new DozerBeanMapper(mappingFiles,
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
                                   globalConfiguration,
                                   cacheManager);
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

    private List<MappingFileData> buildXmlMappings(BeanContainer beanContainer, DestBeanCreator destBeanCreator,
                                                   PropertyDescriptorFactory propertyDescriptorFactory, ELEngine elEngine) {
        BeanMappingXMLBuilder builder = new BeanMappingXMLBuilder(beanContainer, elEngine);
        builder.loadFiles(mappingFiles);
        builder.loadInputStreams(xmlMappingSuppliers);

        return builder.build(beanContainer, destBeanCreator, propertyDescriptorFactory);
    }

    private List<MappingFileData> buildGenericMappings(BeanContainer beanContainer, DestBeanCreator destBeanCreator,
                                                       PropertyDescriptorFactory propertyDescriptorFactory) {
        return beanMappingsBuilders.stream()
                .map(m -> m.build(beanContainer, destBeanCreator, propertyDescriptorFactory))
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    private DozerClassLoader getClassLoader() {
        if (fluentDefinedClassLoader == null) {
            if (RuntimeUtils.isOSGi()) {
                return new OSGiClassLoader(Activator.getBundle().getBundleContext());
            } else {
                return new DefaultClassLoader(DozerBeanMapperBuilder.class.getClassLoader());
            }
        } else {
            return fluentDefinedClassLoader;
        }
    }

    private DozerClassLoader getClassLoaderFromSettings(Settings settings, DozerClassLoader defaultClassLoader) {
        DozerClassLoader answer = null;

        String classLoaderName = settings.getClassLoaderBeanName();
        if (!SettingsDefaults.CLASS_LOADER_BEAN.equalsIgnoreCase(classLoaderName)) {
            answer = ReflectionUtils.newInstance(loadBeanType(classLoaderName, defaultClassLoader, DozerClassLoader.class));
        }

        return answer;
    }

    private Settings getSettings(DozerClassLoader classLoader) {
        if (settingsProcessor == null) {
            settingsProcessor = new DefaultSettingsProcessor(classLoader);
            return settingsProcessor.process();
        } else {
            return settingsProcessor.process();
        }
    }

    private DozerProxyResolver getProxyResolver(Settings settings, DozerClassLoader dozerClassLoader) {
        DozerProxyResolver answer;

        String proxyResolverName = settings.getProxyResolverBeanName();
        if (SettingsDefaults.PROXY_RESOLVER_BEAN.equalsIgnoreCase(proxyResolverName)) {
            answer = new DefaultProxyResolver();
        } else {
            answer = ReflectionUtils.newInstance(loadBeanType(proxyResolverName, dozerClassLoader, DozerProxyResolver.class));
        }

        return answer;
    }

    private <T> Class<? extends T> loadBeanType(String classLoaderName, DozerClassLoader classLoader, Class<T> iface) {
        Class<?> beanType = classLoader.loadClass(classLoaderName);
        if (beanType != null && !iface.isAssignableFrom(beanType)) {
            MappingUtils.throwMappingException("Incompatible types: " + iface.getName() + " and " + classLoaderName);
        }

        return (Class<? extends T>)beanType;
    }

    private CacheManager getCacheManager(Settings settings) {
        if (cacheManager == null) {
            // Initialize any bean mapper caches. These caches are only visible to the bean mapper instance and
            // are not shared across the VM.
            CacheManager cacheManager = new DefaultCacheManager();
            cacheManager.putCache(DozerCacheType.CONVERTER_BY_DEST_TYPE.name(), settings.getConverterByDestTypeCacheMaxSize());
            cacheManager.putCache(DozerCacheType.SUPER_TYPE_CHECK.name(), settings.getSuperTypesCacheMaxSize());

            return cacheManager;
        } else {
            return cacheManager;
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

    private void loadCustomMappings(List<MappingFileData> allMappings, BeanContainer beanContainer,
                                    PropertyDescriptorFactory propertyDescriptorFactory, BeanMappingGenerator beanMappingGenerator,
                                    DestBeanCreator destBeanCreator) {
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

    private void loadDozerModules(BeanContainer beanContainer,
                    DestBeanBuilderCreator destBeanBuilderCreator, BeanMappingGenerator beanMappingGenerator, PropertyDescriptorFactory propertyDescriptorFactory,
                    DestBeanCreator destBeanCreator) {
        try {
            ServiceLoader<DozerModule> services = ServiceLoader.load(DozerModule.class);
            for (DozerModule module : services) {
                module.init();
                module.init(beanContainer, destBeanCreator, propertyDescriptorFactory);

                destBeanBuilderCreator.addPluggedStrategies(module.getBeanBuilderCreationStrategies());
                beanMappingGenerator.addPluggedFieldDetectors(module.getBeanFieldsDetectors());
                propertyDescriptorFactory.addPluggedPropertyDescriptorCreationStrategies(module.getPropertyDescriptorCreationStrategies());
            }
        } catch (ServiceConfigurationError ex) {
            LOG.error("{}", ex.getMessage());
        }
    }
}
