/**
 * Copyright 2005-2013 Dozer Project
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

import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.MappingFileData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

public class CustomMappingsLoaderTest extends AbstractDozerTest{

  CustomMappingsLoader loader;
  ArrayList<MappingFileData> data;

  @Before
  public void setUp() {
    loader = new CustomMappingsLoader();
    data = new ArrayList<MappingFileData>();
  }

  @Test(expected=MappingException.class)
  public void testLoad_MultipleGlobalConfigsFound() {
      data.add(createMappingData(true));
      data.add(createMappingData(true));

      loader.load(data);

      fail("should have thrown exception");
  }

  private MappingFileData createMappingData(boolean hasConfiguration) {
    MappingFileData mappingFileData = new MappingFileData();
    if (hasConfiguration) {
      mappingFileData.setConfiguration(new Configuration());
    }
    mappingFileData.addClassMap(mock(ClassMap.class));
    return mappingFileData;
  }

}
