package org.dozer.loader;

import java.util.Arrays;
import java.util.Collections;

import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.dozer.classmap.MappingFileData;
import org.dozer.functional_tests.support.SampleDefaultBeanFactory;
import org.junit.Test;

public class CustomMappingsLoaderTest extends AbstractDozerTest{
  private CustomMappingsLoader loader = new CustomMappingsLoader();

  @Test
  public void testLoad() {
    LoadMappingsResult result = loader.load(Arrays.asList("customMappingsLoaderTest.xml"), Collections.<MappingFileData>emptyList());
    assertNotNull("result should not be null", result);
    assertEquals("wrong # of mappings", 4, result.getCustomMappings().size());
  }

  @Test
  public void testLoadWithGlobalConfig() {
    LoadMappingsResult result = loader.load(Arrays.asList("customMappingsLoaderWithGlobalConfigTest.xml"), Collections.<MappingFileData>emptyList());
    assertNotNull("result should not be null", result);
    assertEquals("wrong # of mappings", 4, result.getCustomMappings().size());
    assertEquals("wrong value in global config", SampleDefaultBeanFactory.class.getName(), result.getGlobalConfiguration()
        .getBeanFactory());
  }

  @Test(expected=MappingException.class)
  public void testLoad_MultipleGlobalConfigsFound() {
      loader.load(Arrays.asList("global-configuration.xml", "customMappingsLoaderWithGlobalConfigTest.xml"), Collections.<MappingFileData>emptyList());
      fail("should have thrown exception");
  }

}
