/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.sf.dozer.classmap.ClassMap;
import net.sf.dozer.classmap.Configuration;
import net.sf.dozer.classmap.MappingFileData;
import net.sf.dozer.converters.CustomConverterContainer;
import net.sf.dozer.converters.CustomConverterDescription;

import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Internal class that loads and parses custom xml mapping files into ClassMap objects. The ClassMap objects returned
 * from the load method are fully decorated and ready to be used by the internal mapping engine. Only intended for
 * internal use.
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class CustomMappingsLoader {

  private static final Log log = LogFactory.getLog(CustomMappingsLoader.class);

  private static final MappingsParser mappingsParser = MappingsParser.getInstance();

  public LoadMappingsResult load(List<String> mappingFiles) {
    ClassMappings customMappings = new ClassMappings();
    ListOrderedSet customConverterDescriptions = ListOrderedSet.decorate(new ArrayList<CustomConverterDescription>());
    Configuration globalConfiguration = null;
    List<MappingFileData> mappingFileDataList = new ArrayList<MappingFileData>();
    if (mappingFiles != null && mappingFiles.size() > 0) {
      InitLogger.log(log, "Using the following xml files to load custom mappings for the bean mapper instance: " + mappingFiles);
      for (String mappingFileName : mappingFiles) {
        InitLogger.log(log, "Trying to find xml mapping file: " + mappingFileName);
        URL url = MappingValidator.validateURL(MapperConstants.DEFAULT_PATH_ROOT + mappingFileName);
        InitLogger.log(log, "Using URL [" + url + "] to load custom xml mappings");
        MappingFileReader mappingFileReader = new MappingFileReader(url);
        MappingFileData mappingFileData = mappingFileReader.read();
        InitLogger.log(log, "Successfully loaded custom xml mappings from URL: [" + url + "]");

        if (mappingFileData.getConfiguration() != null) {
          //Only allow 1 global configuration          
          if (globalConfiguration != null) {
            MappingUtils
                .throwMappingException("More than one global configuration found.  "
                    + "Only one global configuration block (<configuration></configuration>) can be specified across all mapping files.  "
                    + "You need to consolidate all global configuration blocks into a single one.");
          }
          globalConfiguration = mappingFileData.getConfiguration();
        }
        mappingFileDataList.add(mappingFileData);
      }
    }

    //If global configuration was not specified, use defaults
    if (globalConfiguration == null) {
      globalConfiguration = new Configuration();
    }

    // Decorate the raw ClassMap objects and create ClassMap "prime" instances
    for (MappingFileData mappingFileData : mappingFileDataList) {
      customMappings.addAll(mappingsParser.processMappings(mappingFileData.getClassMaps(), globalConfiguration));
    }

    // Add default mappings using matching property names if wildcard policy
    // is true. The addDefaultFieldMappings will check the wildcard policy of each classmap
    ClassMapBuilder.addDefaultFieldMappings(customMappings, globalConfiguration);

    // build up custom converter description objects
    if (globalConfiguration.getCustomConverters() != null && globalConfiguration.getCustomConverters().getConverters() != null) {
      for (CustomConverterDescription cc : globalConfiguration.getCustomConverters().getConverters()) {
        customConverterDescriptions.add(cc);
      }
    }

    // iterate through the classmaps and set all of the customconverters on them
    for (Entry<String, ClassMap> entry : customMappings.getAll().entrySet()) {
      ClassMap classMap = (ClassMap) entry.getValue();
      if (classMap.getCustomConverters() != null) {
        classMap.getCustomConverters().setConverters(customConverterDescriptions.asList());
      } else {
        classMap.setCustomConverters(new CustomConverterContainer());
        classMap.getCustomConverters().setConverters(customConverterDescriptions.asList());
      }
    }
    return new LoadMappingsResult(customMappings, globalConfiguration);
  }

}
