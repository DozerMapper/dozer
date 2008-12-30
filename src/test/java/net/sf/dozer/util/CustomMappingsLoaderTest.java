package net.sf.dozer.util;

import static org.junit.Assert.*;
import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;
import net.sf.dozer.MappingException;

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
    assertEquals("wrong value in global config", "net.sf.dozer.factories.SampleDefaultBeanFactory", result
        .getGlobalConfiguration().getBeanFactory());
  }

  @Test
  public void testLoad_MultipleGlobalConfigsFound() {
    try {
      loader.load(Arrays.asList(new String[] { "global-configuration.xml", "customMappingsLoaderWithGlobalConfigTest.xml" }));
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertTrue("invalid exception thrown", e.getMessage().indexOf("More than one global configuration found") != -1);
    }
  }

}
