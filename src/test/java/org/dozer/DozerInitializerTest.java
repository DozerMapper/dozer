package org.dozer;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class DozerInitializerTest extends TestCase {

  private DozerInitializer instance;

  @Before
  protected void setUp() throws Exception {
    instance = DozerInitializer.getInstance();
    instance.destroy();
  }

  @Test
  public void testIsInitialized() {
    assertFalse(instance.isInitialized());
    instance.init();
    assertTrue(instance.isInitialized());
    instance.destroy();
    assertFalse(instance.isInitialized());
  }

  @Test
  public void testDoubleCalls() {
    instance.destroy();
    assertFalse(instance.isInitialized());
    instance.init();
    instance.init();
    assertTrue(instance.isInitialized());
    instance.destroy();
    instance.destroy();
    assertFalse(instance.isInitialized());
  }

  @After
  protected void tearDown() throws Exception {
    instance.destroy();
  }
}
