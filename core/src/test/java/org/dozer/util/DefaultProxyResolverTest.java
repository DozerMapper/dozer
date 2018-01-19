/*
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
package org.dozer.util;

import java.util.Calendar;

import org.dozer.functional_tests.runner.ProxyDataObjectInstantiator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class DefaultProxyResolverTest extends Assert {

  private DefaultProxyResolver resolver;

  @Before
  public void setUp() throws Exception {
    resolver = new DefaultProxyResolver();
  }

  @Test
  public void testUnenhanceObject() throws Exception {
    Object obj = new Object();
    Object result = resolver.unenhanceObject(obj);
    assertSame(obj, result);
  }

  @Test
  public void testGetRealClass() throws Exception {
    Object proxy = ProxyDataObjectInstantiator.INSTANCE.newInstance(Calendar.class);
    assertFalse(proxy.getClass().equals(Calendar.class));

    Class<?> realClass = resolver.getRealClass(proxy.getClass());
    assertTrue(realClass.equals(Calendar.class));
  }

}
