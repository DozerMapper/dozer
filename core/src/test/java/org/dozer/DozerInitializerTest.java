package org.dozer;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.dozer.config.BeanContainer;
import org.dozer.config.GlobalSettings;
import org.dozer.util.DozerConstants;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

  @Test
  public void testBeanisMissing() {
    GlobalSettings settings = mock(GlobalSettings.class);
    when(settings.getClassLoaderName()).thenReturn(DozerConstants.DEFAULT_CLASS_LOADER_BEAN);
    when(settings.getProxyResolverName()).thenReturn("no.such.class.Found");

    try {
      instance.initialize(settings);
      fail();
    } catch (MappingException e) {
    }
  }

  @Test
  public void testBeanIsNotAssignable() {
    GlobalSettings settings = mock(GlobalSettings.class);
    when(settings.getClassLoaderName()).thenReturn("java.lang.String");
    when(settings.getProxyResolverName()).thenReturn(DozerConstants.DEFAULT_PROXY_RESOLVER_BEAN);

    try {
      instance.initialize(settings);
      fail();
    } catch (MappingException e) {
    }

  }

  @Test
  public void testBeanInstantiated() {
    BeanContainer.getInstance().setClassLoader(null);
    BeanContainer.getInstance().setProxyResolver(null);
    instance.init();
    assertNotNull(BeanContainer.getInstance().getClassLoader());
    assertNotNull(BeanContainer.getInstance().getProxyResolver());
  }

  @After
  protected void tearDown() throws Exception {
    instance.destroy();
  }
}
