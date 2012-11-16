/*
 * Copyright 2005-2010 the original author or authors.
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
package org.dozer.loader;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMapBuilder;
import org.dozer.classmap.ClassMappings;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingFileData;
import org.dozer.converters.CustomConverterContainer;
import org.dozer.converters.CustomConverterDescription;
import org.dozer.loader.xml.MappingFileReader;
import org.dozer.loader.xml.XMLParserFactory;
import org.dozer.util.MappingUtils;
import org.dozer.util.MappingValidator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Internal class that loads and parses custom xml mapping files into ClassMap objects. The ClassMap objects returned
 * from the load method are fully decorated and ready to be used by the internal mapping engine. Only intended for
 * internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class CustomMappingsLoader {

  private static final Logger log = LoggerFactory.getLogger(CustomMappingsLoader.class);

  private static final MappingsParser mappingsParser = MappingsParser.getInstance();
  private final MappingFileReader mappingFileReader = new MappingFileReader(XMLParserFactory.getInstance());

  public LoadMappingsResult load(List<String> mappingFiles, List<MappingFileData> builderMappings) {
    
    List<MappingFileData> mappingFileDataList = loadFromFiles(mappingFiles);

    mappingFileDataList.addAll(builderMappings);

    Configuration globalConfiguration = findConfiguration(mappingFileDataList);

    ClassMappings customMappings = new ClassMappings();
    // Decorate the raw ClassMap objects and create ClassMap "prime" instances
    for (MappingFileData mappingFileData : mappingFileDataList) {
      List<ClassMap> classMaps = mappingFileData.getClassMaps();
      ClassMappings customMappingsPrime = mappingsParser.processMappings(classMaps, globalConfiguration);
      customMappings.addAll(customMappingsPrime);
    }

    // Add default mappings using matching property names if wildcard policy
    // is true. The addDefaultFieldMappings will check the wildcard policy of each classmap
    ClassMapBuilder.addDefaultFieldMappings(customMappings, globalConfiguration);

    Set<CustomConverterDescription> customConverterDescriptions = new LinkedHashSet<CustomConverterDescription>();

    // build up custom converter description objects
    if (globalConfiguration.getCustomConverters() != null && globalConfiguration.getCustomConverters().getConverters() != null) {
      for (CustomConverterDescription cc : globalConfiguration.getCustomConverters().getConverters()) {
        customConverterDescriptions.add(cc);
      }
    }    

    // iterate through the classmaps and set all of the customconverters on them
    for (Entry<String, ClassMap> entry : customMappings.getAll().entrySet()) {
      ClassMap classMap = entry.getValue();
      if (classMap.getCustomConverters() != null) {
        classMap.getCustomConverters().setConverters(new ArrayList<CustomConverterDescription>(customConverterDescriptions));
      } else {
        classMap.setCustomConverters(new CustomConverterContainer());
        classMap.getCustomConverters().setConverters(new ArrayList<CustomConverterDescription>(customConverterDescriptions));
      }
    }
    return new LoadMappingsResult(customMappings, globalConfiguration);
  }

  private List<MappingFileData> loadFromFiles(List<String> mappingFiles) {
    List<MappingFileData> mappingFileDataList = new ArrayList<MappingFileData>();
    if (mappingFiles != null && mappingFiles.size() > 0) {
      log.info("Using the following xml files to load custom mappings for the bean mapper instance: {}", mappingFiles);
      for (String mappingFileName : mappingFiles) {
        log.info("Trying to find xml mapping file: {}", mappingFileName);
        URL url = MappingValidator.validateURL(mappingFileName);
        log.info("Using URL [" + url + "] to load custom xml mappings");
        MappingFileData mappingFileData = mappingFileReader.read(url);
        log.info("Successfully loaded custom xml mappings from URL: [{}]", url);

        mappingFileDataList.add(mappingFileData);
      }
    }
    return mappingFileDataList;
  }

  /**
	 * Merge all found configurations. All conflicted single settings will be
	 * resolved randomly (from last read configuration), all other settings will
	 * grouped
	 * 
	 * @param mappingFileDataList
	 * @return
	 */
	private static Configuration findConfiguration(List<MappingFileData> mappingFileDataList) {
		Configuration globalConfiguration = new Configuration();
		for (MappingFileData mappingFileData : mappingFileDataList) {
			Configuration configuration = mappingFileData.getConfiguration();
			if (configuration != null) {
				globalConfiguration.setBeanFactory(configuration.getBeanFactory());
				globalConfiguration.setDateFormat(configuration.getDateFormat());
				globalConfiguration.setRelationshipType(configuration.getRelationshipType());
				globalConfiguration.setStopOnErrors(configuration.getStopOnErrors());
				globalConfiguration.setTrimStrings(configuration.getTrimStrings());
				globalConfiguration.setWildcard(configuration.getWildcard());
				if (configuration.getAllowedExceptions().getExceptions() != null) {
					globalConfiguration.getAllowedExceptions().getExceptions().addAll(configuration.getAllowedExceptions().getExceptions());
				}
				if (configuration.getCopyByReferences().getCopyByReferences() != null) {
					globalConfiguration.getCopyByReferences().getCopyByReferences().addAll(configuration.getCopyByReferences().getCopyByReferences());
				}
				if (configuration.getCustomConverters().getConverters() != null) {
					globalConfiguration.getCustomConverters().getConverters().addAll(configuration.getCustomConverters().getConverters());
				}
			}
		}
		return globalConfiguration;
	}
}
