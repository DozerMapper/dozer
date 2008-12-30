package net.sf.dozer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DozerInitializerTest  {

  @Test
  public void testIsInitialized() {
    DozerInitializer.init();
    assertTrue("isInitialized attribute should be set to true", DozerInitializer.isInitialized());
  }

}
