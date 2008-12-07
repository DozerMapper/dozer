package net.sf.dozer.util.mapping.util;

import java.util.Arrays;

import junit.framework.TestCase;
import net.sf.dozer.util.mapping.MappingException;

public class CustomMappingsLoaderTest extends TestCase {
  private CustomMappingsLoader loader = new CustomMappingsLoader();

  public void testLoad() {
    LoadMappingsResult result = loader.load(Arrays.asList(new String[] { "customMappingsLoaderTest.xml" }));
    assertNotNull("result should not be null", result);
    assertEquals("wrong # of mappings", 4, result.getCustomMappings().size());
  }

  public void testLoadWithGlobalConfig() {
    LoadMappingsResult result = loader.load(Arrays.asList(new String[] { "customMappingsLoaderWithGlobalConfigTest.xml" }));
    assertNotNull("result should not be null", result);
    assertEquals("wrong # of mappings", 4, result.getCustomMappings().size());
    assertEquals("wrong value in global config", "net.sf.dozer.util.mapping.factories.SampleDefaultBeanFactory", result
        .getGlobalConfiguration().getBeanFactory());
  }

  public void testLoad_MultipleGlobalConfigsFound() {
    try {
      loader.load(Arrays.asList(new String[] { "global-configuration.xml", "customMappingsLoaderWithGlobalConfigTest.xml" }));
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertTrue("invalid exception thrown", e.getMessage().indexOf("More than one global configuration found") != -1);
    }
  }

}
