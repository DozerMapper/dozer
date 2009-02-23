package org.dozer.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;


import org.dozer.MappingException;
import org.dozer.functional_tests.support.SampleDefaultBeanFactory;
import org.dozer.loader.CustomMappingsLoader;
import org.junit.Test;

public class CustomMappingsLoaderTest {
  private CustomMappingsLoader loader = new CustomMappingsLoader();

  @Test
  public void testLoad() {
    LoadMappingsResult result = loader.load(Arrays.asList(new String[] { "customMappingsLoaderTest.xml" }));
    assertNotNull("result should not be null", result);
    assertEquals("wrong # of mappings", 4, result.getCustomMappings().size());
  }

  @Test
  public void testLoadWithGlobalConfig() {
    LoadMappingsResult result = loader.load(Arrays.asList(new String[] { "customMappingsLoaderWithGlobalConfigTest.xml" }));
    assertNotNull("result should not be null", result);
    assertEquals("wrong # of mappings", 4, result.getCustomMappings().size());
    assertEquals("wrong value in global config", SampleDefaultBeanFactory.class.getName(), result.getGlobalConfiguration()
        .getBeanFactory());
  }

  @Test
  public void testLoad_MultipleGlobalConfigsFound() {
    try {
      loader.load(Arrays.asList(new String[] { "global-configuration.xml", "customMappingsLoaderWithGlobalConfigTest.xml" }));
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertTrue("invalid exception thrown", e.getMessage().contains("More than one global configuration found"));
    }
  }

}
