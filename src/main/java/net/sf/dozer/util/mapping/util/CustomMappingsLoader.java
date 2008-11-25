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
package net.sf.dozer.util.mapping.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.classmap.Configuration;
import net.sf.dozer.util.mapping.classmap.Mappings;
import net.sf.dozer.util.mapping.converters.CustomConverterContainer;
import net.sf.dozer.util.mapping.converters.CustomConverterDescription;

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

  public LoadMappingsResult load(List mappingFiles) {
    Map customMappings = new HashMap();
    ListOrderedSet customConverterDescriptions = ListOrderedSet.decorate(new ArrayList());
    Configuration globalConfiguration = new Configuration();

    if (mappingFiles != null && mappingFiles.size() > 0) {
      InitLogger.log(log, "Using the following xml files to load custom mappings for the bean mapper instance: " + mappingFiles);
      Iterator iter = mappingFiles.iterator();
      while (iter.hasNext()) {
        String mappingFileName = (String) iter.next();
        InitLogger.log(log, "Trying to find xml mapping file: " + mappingFileName);
        URL url = MappingValidator.validateURL(MapperConstants.DEFAULT_PATH_ROOT + mappingFileName);
        InitLogger.log(log, "Using URL [" + url + "] to load custom xml mappings");
        MappingFileReader mappingFileReader = new MappingFileReader(url);
        Mappings mappings = mappingFileReader.read();
        InitLogger.log(log, "Successfully loaded custom xml mappings from URL: [" + url + "]");

        // the last configuration is the 'global' configuration
        globalConfiguration = mappings.getConfiguration();
        // build up the custom converters to make them global
        if (mappings.getConfiguration() != null && mappings.getConfiguration().getCustomConverters() != null
            && mappings.getConfiguration().getCustomConverters().getConverters() != null) {
          Iterator iterator = mappings.getConfiguration().getCustomConverters().getConverters().iterator();
          while (iterator.hasNext()) {
            CustomConverterDescription cc = (CustomConverterDescription) iterator.next();
            customConverterDescriptions.add(cc);
          }
        }
        // Decorate the raw ClassMap objects and create ClassMap "prime" instances
        MappingsParser mappingsParser = new MappingsParser();
        customMappings.putAll(mappingsParser.processMappings(mappings));
      }
    }

    // Add default mappings using matching property names if wildcard policy
    // is true. The addDefaultFieldMappings will check the wildcard policy of each classmap
    ClassMapBuilder.addDefaultFieldMappings(customMappings, globalConfiguration);

    // iterate through the classmaps and set all of the customconverters on them
    Iterator entries = customMappings.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry entry = (Map.Entry) entries.next();
      ClassMap classMap = (ClassMap) entry.getValue();
      if (classMap.getCustomConverters() != null) {
        classMap.getCustomConverters().setConverters(customConverterDescriptions.asList());
      } else {
        classMap.setCustomConverters(new CustomConverterContainer());
        classMap.getCustomConverters().setConverters(customConverterDescriptions.asList());
      }
    }
    return new LoadMappingsResult(Collections.synchronizedMap(customMappings), globalConfiguration);
  }

}
