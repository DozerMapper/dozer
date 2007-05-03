package net.sf.dozer.util.mapping;

import junit.framework.TestCase;

public class DozerInitializerTest extends TestCase {
  
  public void testIsInitialized() {
    DozerInitializer.init();
    assertTrue("isInitialized attribute should be set to true", DozerInitializer.isInitialized());
  }

}
