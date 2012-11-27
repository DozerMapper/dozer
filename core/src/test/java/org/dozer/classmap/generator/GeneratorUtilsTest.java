package org.dozer.classmap.generator;

import org.dozer.functional_tests.proxied.ProxyDataObjectInstantiator;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Dmitry Spikhalskiy
 * @since 27.11.12
 */
public class GeneratorUtilsTest {
  @Test
  public void ignoreTechnicalFields() throws Exception {
    Object proxy = ProxyDataObjectInstantiator.INSTANCE.newInstance(Object.class);

    assertTrue(GeneratorUtils.shouldIgnoreField("callback", proxy.getClass(), Object.class));
    assertFalse(GeneratorUtils.shouldIgnoreField("callback", Object.class, Object.class));
    assertTrue(GeneratorUtils.shouldIgnoreField("callbacks", proxy.getClass(), Object.class));
    assertFalse(GeneratorUtils.shouldIgnoreField("callbacks", Object.class, Object.class));
    assertTrue(GeneratorUtils.shouldIgnoreField("class", Object.class, Object.class));
    assertFalse(GeneratorUtils.shouldIgnoreField("a", proxy.getClass(), Object.class));
  }
}
