/**
 * Copyright 2005-2017 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.dozer.config.BeanContainer;
import org.dozer.config.GlobalSettings;
import org.dozer.util.DozerConstants;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DozerInitializerTest extends AbstractDozerTest {

  private DozerInitializer instance;

  @Before
  public void setUp() throws Exception {
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

  @Test(expected=MappingException.class)
  public void testBeanisMissing() {
    GlobalSettings settings = mock(GlobalSettings.class);
    when(settings.getClassLoaderName()).thenReturn(DozerConstants.DEFAULT_CLASS_LOADER_BEAN);
    when(settings.getProxyResolverName()).thenReturn("no.such.class.Found");

    instance.initialize(settings, getClass().getClassLoader());
    fail();
  }

  @Test(expected=MappingException.class)
  public void testBeanIsNotAssignable() {
    GlobalSettings settings = mock(GlobalSettings.class);
    when(settings.getClassLoaderName()).thenReturn("java.lang.String");
    when(settings.getProxyResolverName()).thenReturn(DozerConstants.DEFAULT_PROXY_RESOLVER_BEAN);

    instance.initialize(settings, getClass().getClassLoader());
    fail();
  }

  @After
  public void tearDown() throws Exception {
    instance.destroy();
  }
}
