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
