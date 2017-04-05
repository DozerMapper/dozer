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
package org.dozer.stats;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Proxy;

import org.dozer.AbstractDozerTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class StatisticsInterceptorTest extends AbstractDozerTest {

  private StatisticsInterceptor interceptor;

  private Service service = mock(Service.class);
  private StatisticsManager manager = mock(StatisticsManager.class);

  @Before
  public void setUp() throws Exception {
    interceptor = new StatisticsInterceptor(service, manager);
  }

  @Test
  public void testInvoke() {
    Service proxy = (Service) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Service.class}, interceptor);
    proxy.method("a", "b");

    verify(service).method("a", "b");
    verify(manager).increment(StatisticType.MAPPING_SUCCESS_COUNT);
  }

  @Test
  public void testException() {
    Service proxy = (Service) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Service.class}, interceptor);
    doThrow(new NullPointerException()).when(service).method("a", "b");

    try {
      proxy.method("a", "b");
      fail();
    } catch (NullPointerException e) {
    }

    verify(service).method("a", "b");
    verify(manager).increment(StatisticType.MAPPING_FAILURE_COUNT);
  }

  private interface Service {
    void method(String src, String dest);
  }

}
